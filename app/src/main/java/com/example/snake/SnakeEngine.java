package com.example.snake;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Intent;
//import java.util.Timer;

//certain pieces borrowed from game code school
class SnakeEngine extends SurfaceView implements Runnable {

    private Thread thread = null;
    private Context context;
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    private Direction direction = Direction.RIGHT;
    private int screenX;
    private int screenY;
    private int sLength;
    private int foodX;
    private int foodY;
    private int bombX;
    private int bombY;
    private int bSize;
    private final int B_WIDTH = 40;
    private int height;
    private long nextFrameTime;
    private final long FPS = 10;
    private final long MILLIS_PER_SECOND = 1000;
    private int score;
    private int[] snakeXs;
    private int[] snakeYs;
    private volatile boolean playing;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private boolean bombs;

    public SnakeEngine(Context context, Point size, boolean in) {
        super(context);

        context = context;
        bombs = in;
        screenX = size.x;
        screenY = size.y;

        bSize = screenX / B_WIDTH;
        height = screenY / bSize;

        surfaceHolder = getHolder();
        paint = new Paint();

        snakeXs = new int[200];
        snakeYs = new int[200];

        newGame();
    }

    @Override
    public void run() {

        while (playing) {

            if(updateRequired()) {
                update();
                draw();
            }

        }
    }
    public void pause() {
        playing = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        thread = new Thread(this);
        thread.start();
    }
    public void newGame() {
        sLength = 1;
        snakeXs[0] = B_WIDTH / 2;
        snakeYs[0] = height / 2;

        createFood();
        createBomb(foodX, foodY);

        score = 0;

        nextFrameTime = System.currentTimeMillis();
    }

    public void createFood() {
        Random random = new Random();
        foodX = random.nextInt(B_WIDTH - 1) + 1;
        foodY = random.nextInt(height - 1) + 1;
    }

    public void createBomb(int x, int y) {
        if (bombs == true) {
                Random random = new Random();
                bombX = random.nextInt(B_WIDTH - 1) + 1;
                bombY = random.nextInt(height - 1) + 1;
                if (bombX == x && bombY == y) {
                    createBomb(foodX, foodY);
                }
 //               Timer.scheduleAtFixedRate(run(), 0, 10000);
        }
    }

    private void eatFood(){
        sLength++;
        createFood();
        score = score + 1;
    }

    private void moveSnake(){
        for (int i = sLength; i > 0; i--) {
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];
        }

        switch (direction) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath(){
        boolean dead = false;
        if (snakeXs[0] == -1) dead = true;
        if (snakeXs[0] >= B_WIDTH) dead = true;
        if (snakeYs[0] == -1) dead = true;
        if (snakeYs[0] == height) dead = true;

        for (int i = sLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }
        if (snakeXs[0] == bombX && snakeYs[0] == bombY) {
            dead = true;
        }

        return dead;
    }

    public void update() {
        if (snakeXs[0] == foodX && snakeYs[0] == foodY) {
            eatFood();
        }

        moveSnake();

        if (detectDeath()) {

            SharedPreferences pref = getContext().getSharedPreferences("Scores", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("last", score);
            editor.apply();
            Intent intent = new Intent(getContext(), Leaderboard.class);
            getContext().startActivity(intent);
        }
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.argb(255, 255, 255, 255));

            paint.setTextSize(90);
            canvas.drawText("Score: " + score, 10, 80, paint);
            paint.setColor(Color.argb(255, 0, 255, 0));

            for (int i = 0; i < sLength; i++) {
                canvas.drawRect(snakeXs[i] * bSize,
                        (snakeYs[i] * bSize),
                        (snakeXs[i] * bSize) + bSize,
                        (snakeYs[i] * bSize) + bSize,
                        paint);
            }

            paint.setColor(Color.argb(255, 255, 255, 0));

            canvas.drawRect(foodX * bSize,
                    (foodY * bSize),
                    (foodX * bSize) + bSize,
                    (foodY * bSize) + bSize,
                    paint);

            paint.setColor(Color.argb(255, 0, 0, 0));

            canvas.drawRect(bombX * bSize,
                    (bombY * bSize),
                    (bombX * bSize) + bSize,
                    (bombY * bSize) + bSize,
                    paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {
        if(nextFrameTime <= System.currentTimeMillis()){

            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= screenX / 2) {
                    switch(direction){
                        case UP:
                            direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            direction = Direction.DOWN;
                            break;
                        case DOWN:
                            direction = Direction.LEFT;
                            break;
                        case LEFT:
                            direction = Direction.UP;
                            break;
                    }
                } else {
                    switch(direction){
                        case UP:
                            direction = Direction.LEFT;
                            break;
                        case LEFT:
                            direction = Direction.DOWN;
                            break;
                        case DOWN:
                            direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            direction = Direction.UP;
                            break;
                    }
                }
        }
        return true;
    }
}