package com.example.stratego_app.ui;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.stratego_app.R;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.pieces.Board;
import com.example.stratego_app.model.pieces.Piece;
import com.example.stratego_app.model.pieces.Rank;

import java.util.HashMap;
import java.util.Map;

public class GameBoardView extends View{

    private static final String TAG = "gbv";
    private Paint paint;
    private boolean isConfigMode = false;
    private boolean displayLowerHalfOnly = false;
    private Map<Rank, Drawable> drawableCache = new HashMap<>();
    private Board board = new Board();

    ModelService modelService = new ModelService();

    private int cellWidth, cellHeight; // Cache cell dimensions to improve performance

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
    }

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


    private void drawBoard(Canvas canvas) {
        int cellWidth = getWidth() / 10;
        int cellHeight = getHeight() / 10;

        int startRow = displayLowerHalfOnly ? 5 : 0;

        for (int row = startRow; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row == 4 || row == 5) && (col == 2 || col == 3 || col == 6 || col == 7)) {
                    paint.setColor(Color.parseColor("#4169E1"));
                } else {
                    paint.setColor(Color.LTGRAY);
                }

                canvas.drawRect(
                        (float) col * cellWidth,
                        (float) row * cellHeight,
                        (float) (col + 1) * cellWidth,
                        (float) (row + 1) * cellHeight,
                        paint);

            }
        }
    }


    private void drawGridLines(Canvas canvas) {
        int cellWidth = getWidth() / 10;
        int cellHeight = getHeight() / 10;
        int startRow = displayLowerHalfOnly ? 5 : 0; // Start row for drawing
        int startY = displayLowerHalfOnly ? cellHeight * 5 : 0; // Y-coordinate to start drawing horizontal lines

        paint.setColor(Color.parseColor("#B2BEB5"));
        paint.setStrokeWidth(7);

        for (int i = 0; i <= 10; i++) {
            canvas.drawLine((float) cellWidth * i, startY, (float) cellWidth * i, getHeight(), paint);
        }

        for (int i = startRow; i <= 10; i++) {
            float y = (float) cellHeight * (i - startRow);
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

    // ------- PIECES --------------
    private void drawPieces(Canvas canvas) {
        Piece[][] boardArray = modelService.getBoard().getBoard();

        for (int row = 0; row < boardArray.length; row++) {
            for (int col = 0; col < boardArray[row].length; col++) {
                Piece piece = boardArray[row][col];
                if (piece != null) {
                    Drawable drawable = drawableCache.get(piece.getRank());
                    if (drawable != null) {
                        drawable.setBounds(col * cellWidth, row * cellHeight, (col + 1) * cellWidth, (row + 1) * cellHeight);
                        drawable.draw(canvas);
                    }
                }
            }
        }
    }


    //----------------- Drag and Drop ------------
    public void setupDragListener() {
        this.setOnDragListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String pieceType = item.getText().toString();
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


    // ------------------- drop listener

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
        int col = (int) (x / cellWidth);
        int row = (int) (y / cellHeight);

        ClipData.Item item = event.getClipData().getItemAt(0);
        String pieceType = item.getText().toString();
        Piece droppedPiece = createPieceFromType(pieceType);

        // Log the coordinates and the piece trying to be dropped
        Log.d(TAG, "Trying to drop piece at row: " + row + ", col: " + col);
        if (droppedPiece != null) {
            Log.d(TAG, "Piece is: " + droppedPiece.getRank());
        } else {
            Log.d(TAG, "No piece found in drag event.");
        }

        boolean success = board.isValidLocation(row, col);
        Log.d(TAG, "Is valid location for drop: " + success);

        if (success) {
            board.setField(row, col, droppedPiece);
            invalidate(); // Redraw the board
            Log.d(TAG, "Piece placed on the board successfully.");
        } else {
            Log.d(TAG, "Drop failed: Location is not valid.");
        }

        if (dropListener != null && draggedPosition != -1) {
            dropListener.onDrop(success, draggedPosition);
            Log.d(TAG, "Drop listener notified: success = " + success + ", position = " + draggedPosition);
            draggedPosition = -1; // Reset position after handling
        } else {
            Log.d(TAG, "Drop listener is not set or drag position is invalid.");
        }

        return success;
    }

    private Piece createPieceFromType(String type) {
        // Example conversion logic
        Rank rank = Rank.valueOf(type.toUpperCase());
        return new Piece(rank, null, 0); // Assuming a constructor exists
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

    public static Drawable getDrawableForPiece(Context context, Piece piece) {
        int drawableId = getDrawableIdByRank(piece.getRank());
        return ContextCompat.getDrawable(context, drawableId);
    }

    private static int getDrawableIdByRank(Rank rank) {
        switch (rank) {
            case MARSHAL:
                return R.drawable.marshal; // Replace with actual drawable resource ID
            case GENERAL:
                return R.drawable.general; // and so forth for each rank
            // Add more cases as per your drawable resources
            default:
                return -1;
        }
    }








}