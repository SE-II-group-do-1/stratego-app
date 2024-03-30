package com.example.stratego_app.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameBoardView extends View {

    private Paint paint;
    private boolean isConfigMode = false; // to configure Board in GameFragment

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

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row == 4 || row == 5) && (col == 2 || col == 3 || col == 6 || col == 7)) {
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setColor(Color.LTGRAY);
                    }

                canvas.drawRect(
                        col * cellWidth,
                        row * cellHeight,
                        (col + 1) * cellWidth,
                        (row + 1) * cellHeight,
                        paint);
            }
        }
    }

    private void drawGridLines(Canvas canvas) {
        int cellWidth = getWidth() / 10;
        int cellHeight = getHeight() / 10;

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);

        for (int i = 0; i <= 10; i++) {
            canvas.drawLine(cellWidth * i, 0, cellWidth * i, getHeight(), paint);
            canvas.drawLine(0, cellHeight * i, getWidth(), cellHeight * i, paint);
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
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
    }

    //method to make squares on board clickable


}