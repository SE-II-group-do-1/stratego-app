package com.example.stratego_app.ui;

import static android.view.MotionEvent.ACTION_DOWN;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.ObserverModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Rank;
import java.util.HashMap;
import java.util.Map;

public class GameBoardView extends View implements ObserverModelService {
    ModelService modelService = ModelService.getInstance();

    private static final String TAG = "gbv";
    private Paint paint;
    private boolean isConfigMode = false;
    private boolean displayLowerHalfOnly = false;
    private Map<Rank, Drawable> drawableCache = new HashMap<>();

    private Piece selected;
    private int selectedX;
    private int selectedY;


    private int cellWidth;
    private int cellHeight;

    // Used when creating the view in code
    public GameBoardView(Context context) {
        super(context);
        init();
    }


    // Used when inflating the view from XML
    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Used when inflating the view from XML + applying style from theme
    public GameBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        loadDrawableCache();
        setupDragListener();
        setupClickListener();
        ModelService.subscribe(this);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ModelService.unsubscribe(this);
    }

    @Override
    public void update() {
        invalidate();
    }

    /**
     * loading and caching drawable pieces associated with different ranks
     */
    private void loadDrawableCache() {
        drawableCache.clear();
        for (Rank rank : Rank.values()) {
            int resId = getContext().getResources().getIdentifier(rank.name().toLowerCase(), "drawable", getContext().getPackageName());
            if (resId != 0) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
                if (drawable != null) {
                    drawableCache.put(rank, drawable);
                } else {
                    Log.e(TAG, "Drawable not found for rank: " + rank);
                }
            }
        }
    }


    public void setDisplayLowerHalfOnly(boolean displayLowerHalfOnly) {
        this.displayLowerHalfOnly = displayLowerHalfOnly;
        invalidate(); // Redraw the view
    }

    public void setConfigMode(boolean isConfigMode) {
        this.isConfigMode = isConfigMode;
        invalidate(); // Redraw the view with the new configuration
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);//DISPLAY PIECES

        if (isConfigMode) {
            drawConfigElements(canvas);
        }
        drawGridLines(canvas);
    }


    /**
     * methods drawBoard, draw GridLines, drawConfigElements, draw Boardframe, onMeasure
     * are used to display the gameboard in various fragements
     */
    private void drawBoard(Canvas canvas) {
        int widthCell = getWidth() / 10;
        int heightCell = getHeight() / 10;

        int startRow = displayLowerHalfOnly ? 5 : 0;

        for (int row = startRow; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row == 4 || row == 5) && (col == 2 || col == 3 || col == 6 || col == 7)) {
                    paint.setColor(Color.parseColor("#4169E1"));
                } else {
                    paint.setColor(Color.LTGRAY);
                }

                canvas.drawRect(
                        (float) col * widthCell,
                        (float) row * heightCell,
                        (float) (col + 1) * widthCell,
                        (float) (row + 1) * heightCell,
                        paint);

            }
        }
    }


    private void drawGridLines(Canvas canvas) {
        int widthCell = getWidth() / 10;
        int heightCell = getHeight() / 10;
        int startRow = displayLowerHalfOnly ? 5 : 0; // Start row for drawing
        int startY = displayLowerHalfOnly ? heightCell * 5 : 0; // Y-coordinate to start drawing horizontal lines

        paint.setColor(Color.parseColor("#B2BEB5"));
        paint.setStrokeWidth(7);

        for (int i = 0; i <= 10; i++) {
            canvas.drawLine((float) widthCell * i, startY, (float) widthCell * i, getHeight(), paint);
        }

        for (int i = startRow; i <= 10; i++) {
            float y = (float) heightCell * (i - startRow);
            canvas.drawLine(0, y + startY, getWidth(), y + startY, paint);
        }


        paint.setStrokeWidth(0);
    }

    private void drawConfigElements(Canvas canvas) {
        drawBoardFrame(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);  // Ensures the view remains square
        setMeasuredDimension(size, size);
    }


    private void drawBoardFrame(Canvas canvas) {
        paint.setColor(Color.parseColor("#B2BEB5"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
    }

    /**
     * method drawPieces adds pieces to the board fetching them from a drawable cache, so a constant reload from res files is not necessary
     */
    //use dependency injection, check saved setup beforehand

    private void drawPieces(Canvas canvas) {

        Piece[][] boardArray = ModelService.getInstance().getGameBoard().getBoard();


        for (int row = 0; row < boardArray.length; row++) {
            for (int col = 0; col < boardArray[row].length; col++) {
                Piece piece = boardArray[row][col];
                if (piece == null) {
                    continue;  // Skip drawing if no piece is present
                }

                if(piece.getColor() != ModelService.getInstance().getPlayerColor() && !piece.isVisible()) {
                    // TODO: Exchange piece.getRank() with a special drawable for not visible pieces
                    Drawable drawable = drawableCache.get(piece.getRank());
                    if (drawable != null) {
                        drawable.setBounds(col * cellWidth, row * cellHeight, (col + 1) * cellWidth, (row + 1) * cellHeight);
                        DrawableCompat.setTint(drawable, Color.parseColor("#006400"));
                        drawable.draw(canvas);  // Draw the piece
                    }
                    continue;
                }
                Drawable drawable = drawableCache.get(piece.getRank());
                if (drawable != null) {
                    drawable.setBounds(col * cellWidth, row * cellHeight, (col + 1) * cellWidth, (row + 1) * cellHeight);
                    DrawableCompat.setTint(drawable, getAndroidColorFromPiece(piece));
                    drawable.draw(canvas);  // Draw the piece
                }
            }
        }
    }

    /**
     * Move pieces on Board when selecting (clicking) them and then other field
     */
    public void setupClickListener(){
        this.setOnTouchListener((View v, MotionEvent e) -> {
            int action = e.getAction();
            switch (action) {
                case ACTION_DOWN:
                    onTouch(e);
                    return true;
                default:
                    return false;
            }
        });
    }

    private boolean onTouch(MotionEvent e) {

        int col = (int) (e.getX() / cellWidth);
        int row = (int) (e.getY() / cellHeight);

        Log.i(TAG, String.valueOf(col));
        Log.i(TAG, String.valueOf(row));

        if (selected == null) {
            selected = ModelService.getInstance().getGameBoard().getField(row, col);
            Log.i(TAG, "click1" + selected);
            selectedX = col;
            selectedY = row;
            return false;
        }
        Log.i(TAG, "click2" + selected);
        Log.i(TAG, String.valueOf(selectedX));
        Log.i(TAG, String.valueOf(selectedY));
        modelService.movePiece(selectedY, selectedX, row, col);
        selected = null;
        return true;
    }

    /*
     * Drag and Drop to facilitate an individual board set-up
     */
    public void setupDragListener() {
        this.setOnDragListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DROP:
                    handleDrop(event.getX(), event.getY(), event);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    invalidate(); // Redraw when drag ends to clean up any visual cues
                    return true;
                default:
                    return false;
            }
        });
    }


    private DropListener dropListener;
    private int draggedPosition = -1; // To track the position of the dragged item

    public void setDropListener(DropListener listener) {
        this.dropListener = listener;
    }

    public void setCurrentDragPosition(int position) {
        this.draggedPosition = position;
    }

    public interface DropListener {
        void onDrop(boolean success, int position);
    }

   private boolean handleDrop(float x, float y, DragEvent event) {
       Log.i("handleDrop", "handle");
       int col = (int) (x / cellWidth);
        int row = (int) (y / cellHeight);

        ClipData.Item item = event.getClipData().getItemAt(0);
        String pieceType = item.getText().toString();
        Piece droppedPiece = createPieceFromType(pieceType);

       boolean success = modelService.placePieceAtGameSetUp(col, row, droppedPiece);
       invalidate(); // Redraw the board

        if (dropListener != null && draggedPosition != -1) {
            dropListener.onDrop(success, draggedPosition);
            draggedPosition = -1; // Reset position after handling
        }

        return success;
    }


    private Piece createPieceFromType(String type) {
        Rank rank = Rank.valueOf(type.toUpperCase());
        return new Piece(rank, ModelService.getInstance().getPlayerColor());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Recalculate cell dimensions and refresh drawable cache if size changes
        if (w != oldw || h != oldh) {
            cellWidth = w / 10;
            cellHeight = h / 10;
            loadDrawableCache();
        }
    }

    private static int getAndroidColorFromPiece(Piece piece) {
        if(piece.getColor() != null) {
            switch (piece.getColor()) {
                case RED:
                    return Color.RED;
                case BLUE:
                    return Color.BLUE;
                default:
                    return Color.BLACK;
            }
        }
        return Color.BLACK;
    }



}