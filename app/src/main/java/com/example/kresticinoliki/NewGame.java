package com.example.kresticinoliki;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Random;

/**
 * Created by Kate on 03.02.2017.
 */

public class NewGame extends Activity {

    static final int GAME_MODE_HVSAI = 0;
    static final int GAME_MODE_HVSH = 1;
    static final int EMPTY_CELL = 0;
    static final int HUMAN_DOT = 1;
    static final int AI_DOT = 2;
    static final int DOTS_PADDING = 15;
    static final int STATE_DRAW = 0;
    static final int STATE_HUMAN = 1;
    static final int STATE_AI = 2;
    static final String MSG_DRAW = "Сыграли вничью";
    static final String MSG_HUMAN = "Победил игрок";
    static final String MSG_AI = "Победил компьютер";

    static int[][] gameField;

    Random compRand = new Random();
    Display display;

    int sizeFieldX;
    int sizeFieldY;
    int winLength;
    int cellWidth;
    int cellHeight;
    int stateGameOver;
    int width;
    int height;

    boolean init = false;
    boolean gameOver;

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
//        setContentView(R.layout.map);

        startNewGame(1, 3, 3, 3);
    }


    void startNewGame(int mode, int sizeFieldX, int sizeFieldY, int winLength){

        this.sizeFieldX = sizeFieldX;
        this.sizeFieldY = sizeFieldY;
        this.winLength = winLength;

        init = true;

        /**
         * Создаем массив
         */
        gameField = new int[sizeFieldX][sizeFieldX];

        gameOver = false;

    }

    class DrawView extends View {

        Paint p;
        Rect rect;

        public DrawView(Context context) {
            super(context);
            p = new Paint();
            rect = new Rect();
        }

        public DrawView(Context context, AttributeSet attrs) {
            super(context, attrs);
            p = new Paint();
            rect = new Rect();
        }

        public DrawView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            p = new Paint();
            rect = new Rect();
        }

        @Override
        protected void onDraw(Canvas canvas){

            canvas.drawRGB(47, 79, 79);
            render(canvas, p);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int touchX = (int)event.getX();
            int touchY = (int)event.getY();
            update(touchX, touchY);
            invalidate();
            return true;
        }
    }


    public void render(Canvas canvas, Paint p){

        if (!init) return;
        /**
         * Получаем ширину поля
         */
        display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        cellWidth = width/sizeFieldX;
        cellHeight = height/sizeFieldY;

        p.setARGB(255, 0, 0, 0);
        p.setStrokeWidth(10);

        /**
         * Создаем цикл для отрисовки определенного количества ячеек, выбираемого в слайдере
         */
        for (int i = 1; i < sizeFieldX; i++) {
            canvas.drawLine(0,(height/sizeFieldX)*i, height,(height/sizeFieldX)*i, p);
            canvas.drawLine((width/sizeFieldX)*i, 0,(width/sizeFieldX)*i, height, p);

        }

//        for (int i = 1; i < sizeFieldX; i++) {
//            p.setColor(Color.BLACK);
//            canvas.drawRect(cellWidth * i - 30, cellHeight * i - 30, cellWidth * i + 30, cellHeight * 1 + 30, p);
////            canvas.drawRect(cellWidth * i - 30, cellHeight * i - 30, cellWidth * i + 30, cellHeight * 1 + 30, p);
//            canvas.rotate(45);
//        }

        for (int i = 1; i < sizeFieldX; i++) {
            for (int j = 1; j < sizeFieldX; j++) {
                p.setColor(Color.BLACK);

                canvas.rotate(45, cellWidth * j, cellHeight * i);
                canvas.drawRect(cellWidth * j - 30, cellHeight * i - 30, cellWidth * j + 30, cellHeight * i + 30, p);

                canvas.rotate(-45, cellWidth * j, cellHeight * i);
            }
        }

        for (int i = 0; i < sizeFieldX; i++) {
            for (int j = 0; j < sizeFieldY; j++) {
                if (yourCellIsEmpty(i, j)) continue;
                if (gameField[i][j] == HUMAN_DOT){

                    Drawable d = getResources().getDrawable(R.drawable.fishx);
                    d.setBounds(i * cellWidth + DOTS_PADDING, j * cellHeight + DOTS_PADDING,
                            cellWidth * i - DOTS_PADDING * 2, cellHeight * j - DOTS_PADDING * 2);
                    d.draw(canvas);
//                    p.setARGB(255, 107, 142, 35);
                }
                else if (gameField[i][j] == AI_DOT){

                    Drawable f2 = getResources().getDrawable(R.drawable.fishcircle);
                    f2.setBounds(i * cellWidth + DOTS_PADDING, j * cellHeight + DOTS_PADDING,
                            cellWidth * i - DOTS_PADDING * 2, cellHeight * j - DOTS_PADDING * 2);
                    f2.draw(canvas);
//                    p.setARGB(255, 255, 160, 122);
                }
                else {
                    throw new RuntimeException("Что это?" + gameField[i][j]);
                }
//                canvas.drawCircle(
//                        cellWidth * i + cellWidth / 2,
//                        cellHeight * j + cellHeight / 2,
//                        (cellWidth - DOTS_PADDING) / 2,
//                        p
//                );
//                Drawable d = getResources().getDrawable(R.drawable.fishx);
//                d.setBounds(i * cellWidth + DOTS_PADDING, j * cellHeight + DOTS_PADDING,
//                        cellWidth - DOTS_PADDING * 2, cellHeight - DOTS_PADDING * 2);
//                d.draw(canvas);
//                canvas.drawRect(i * cellWidth + DOTS_PADDING, j * cellHeight + DOTS_PADDING,
//                        cellWidth - DOTS_PADDING * 2, cellHeight - DOTS_PADDING * 2, p);
                if (gameOver){
                    messageGameOver(canvas, p);
                }
            }
        }
    }

    private void update(int x, int y){

        if (gameOver) return;
        int cellX = x / cellWidth;
        int cellY = y / cellHeight;

        if (!yourCellIsEmpty(cellX, cellY) || !isValidCell(cellX, cellY)) return;
        putHere(cellX, cellY, HUMAN_DOT);
        if (checkWin(HUMAN_DOT)){
            stateGameOver = STATE_HUMAN;
            gameOver = true;
            return;
        }
        if (gameFildFull()){
            stateGameOver = STATE_DRAW;
            gameOver = true;
            return;
        }
        computerRandom();
        if (checkWin(AI_DOT)){
            stateGameOver = STATE_AI;
            gameOver = true;
            return;
        }
        if (gameFildFull()){
            stateGameOver = STATE_DRAW;
            gameOver = true;
            return;
        }
    }

    public boolean yourCellIsEmpty(int x, int y){
        if (x<0 || x>sizeFieldX-1 || y<0 || y>sizeFieldX-1)
            return false;
        if (gameField[x][y] == EMPTY_CELL)
            return true;
        else
            return false;
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < sizeFieldX && y >= 0 && y < sizeFieldY;
    }


    public static void putHere(int x, int y, int dot){
        gameField[x][y] = dot;
    }

     public void computerRandom() {

        int x;
        int y;

        do {
            x = compRand.nextInt(sizeFieldX);
            y = compRand.nextInt(sizeFieldY);
        }while (!yourCellIsEmpty(x,y));

        putHere(x , y , AI_DOT);
    }


    private boolean checkWin(int sign) {
        for(int i = 0; i < sizeFieldX; ++i) {
            for(int j = 0; j < sizeFieldY; ++j) {
                if(checkLine(i, j, 1, 0, winLength, sign)) {
                    return true;
                }

                if(checkLine(i, j, 1, 1, winLength, sign)) {
                    return true;
                }

                if(checkLine(i, j, 0, 1, winLength, sign)) {
                    return true;
                }

                if(checkLine(i, j, 1, -1, winLength, sign)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkLine(int x, int y, int vx, int vy, int len, int sign) {
        int far_x = x + (len - 1) * vx;
        int far_y = y + (len - 1) * vy;
        if(!isValidCell(far_x, far_y)) {
            return false;
        } else {
            for(int i = 0; i < len; ++i) {
                if(gameField[y + i * vy][x + i * vx] != sign) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean gameFildFull(){

        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField.length; j++) {
                if (gameField[i][j] == EMPTY_CELL)
                    return false;
            }
        }
        return true;
    }

    public void messageGameOver(Canvas canvas, Paint p){

        p.setARGB(80, 112, 128, 144);
        if (sizeFieldX % 3 == 0) {
            canvas.drawRect(0, height / 3, width, height / 3 * 2, p);
        }
        else if (sizeFieldX % 3 == 2){
            p.setARGB(80, 112, 128, 144);
            canvas.drawRect(0, cellHeight * sizeFieldX / 2  - cellHeight, width, height - (cellHeight * sizeFieldX / 2 - cellHeight), p);
        }
        else {
//            canvas.drawRect(0, cellHeight * i, width, cellHeight * i * 3, p);

        }
        p.setARGB(255, 238, 207, 161);
        p.setTextSize(68);

        switch (stateGameOver){
            case (STATE_DRAW):
                canvas.drawText(MSG_DRAW, 70, height/2, p);
                break;
            case (STATE_HUMAN):
                canvas.drawText(MSG_HUMAN, 100, height/2, p);
                break;
            case (STATE_AI):
                canvas.drawText(MSG_AI, 10, height/2, p);
                break;
            default:
                throw new RuntimeException("Что тут происходит? " + stateGameOver);
        }

    }
}
