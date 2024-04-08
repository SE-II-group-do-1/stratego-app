package com.example.stratego_app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameBoardView extends View {

    private Paint paint;
    private boolean isConfigMode = false; // to configure Board in GameFragment
    private boolean displayLowerHalfOnly = false;

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
        paint = new Paint();
        paint.setAntiAlias(true);
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);

        if(isConfigMode) {
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
                        (float)col * cellWidth,
                        (float)row * cellHeight,
                        (float)(col + 1) * cellWidth,
                        (float)(row + 1) * cellHeight,
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
            canvas.drawLine((float)cellWidth * i, startY, (float)cellWidth * i, getHeight(), paint);
        }

        for (int i = startRow; i <= 10; i++) {
            float y = (float)cellHeight * (i - startRow);
            canvas.drawLine(0, y + startY, getWidth(), y + startY, paint);
        }


        paint.setStrokeWidth(0);
    }

    private void drawConfigElements(Canvas canvas) {
        drawBoardFrame(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    private void drawBoardFrame(Canvas canvas) {
        paint.setColor(Color.parseColor("#B2BEB5"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
    }

    //method to make squares on board clickable


}