package com.example.hugh.lock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 *
 * Created by 60352 on 2018/3/5.
 */

public class GraphicLock extends View {

    private Bitmap NormalPoint;
    private Bitmap PressedPoint;
    private Bitmap SelectPoint;

    Point[][] points = new Point[3][3];
    private boolean isInit = false;
    private Paint paint = new Paint();
    private int bitmapR;
    private ArrayList<Point> selectedPoints = new ArrayList<>();
    private float current_x;
    private float current_y;
    private boolean isFinished = false;

    public GraphicLock(Context context) {
        super(context);
    }

    public GraphicLock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphicLock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void initData(){

        //初始化图片资源
        Drawable point_normal = getResources().getDrawable(R.drawable.point_normal, null);
        NormalPoint = drawableToBitmap(point_normal);
        Drawable point_pressed = getResources().getDrawable(R.drawable.point_pressed, null);
        PressedPoint = drawableToBitmap(point_pressed);
        Drawable point_select = getResources().getDrawable(R.drawable.point_select, null);
        SelectPoint = drawableToBitmap(point_select);

        //获取Bitmap的半径
        bitmapR = NormalPoint.getHeight() / 2;

        //获取屏幕的尺寸信息
        int height = getHeight();
        int width = getWidth();
        int offsetY = 0;
        int offsetX = 0;
        //竖屏
        if (height > width){
            offsetY = (height - width)/2;
            height = width;
        //横屏
        }else {
            offsetX = (width - height)/2;
            width = height;
        }

        points[0][0] = new Point(offsetX + width/4, offsetY + width/4);
        points[0][1] = new Point(offsetX + width/2, offsetY + width/4);
        points[0][2] = new Point(offsetX + 3*width/4, offsetY + width/4);

        points[1][0] = new Point(offsetX + width/4, offsetY + width/2);
        points[1][1] = new Point(offsetX + width/2, offsetY + width/2);
        points[1][2] = new Point(offsetX + 3*width/4, offsetY + width/2);

        points[2][0] = new Point(offsetX + width/4, offsetY + 3*width/4);
        points[2][1] = new Point(offsetX + width/2, offsetY + 3*width/4);
        points[2][2] = new Point(offsetX + 3*width/4, offsetY + 3*width/4);

        isInit = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isFinished = false;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (Point point:selectedPoints){
                    point.State = Point.STATE_NORMAL;
                }
                selectedPoints.clear();
                judgePoint(event);
                break;
            case MotionEvent.ACTION_MOVE:
                judgePoint(event);
                break;
            case MotionEvent.ACTION_UP:
                isFinished = true;
                finishSelect();
                break;
        }
        postInvalidate();
        return true;
    }

    private void finishSelect() {
        if (selectedPoints.size() < 4){
            for (Point point:selectedPoints){
                point.State = Point.STATE_ERROR;
            }
        }
    }

    private void judgePoint(MotionEvent event) {
        current_x = event.getX();
        current_y = event.getY();
        for (int i=0;i<points.length;i++){
            for (int j=0;j<points.length;j++){
                Point point = points[i][j];
                if (Utils.distanceBetweenPoints(point.x,point.y, current_x, current_y) < bitmapR){
                    point.State = Point.STATE_PRESSED;
                    if (!selectedPoints.contains(point)){
                        selectedPoints.add(point);
                    }
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit){
            initData();
        }
        drawPoints(canvas);
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLUE);
        if (isFinished){
            if (selectedPoints.size() >= 2){
                for (int i=0;i<selectedPoints.size() - 1;i++){
                    canvas.drawLine(selectedPoints.get(i).x,selectedPoints.get(i).y,selectedPoints.get(i+1).x,selectedPoints.get(i+1).y,paint);
                }
            }
        }else {
            if (selectedPoints.size() >= 2){
                for (int i=0;i<selectedPoints.size() - 1;i++){
                    canvas.drawLine(selectedPoints.get(i).x,selectedPoints.get(i).y,selectedPoints.get(i+1).x,selectedPoints.get(i+1).y,paint);
                }
                canvas.drawLine(selectedPoints.get(selectedPoints.size() - 1).x,selectedPoints.get(selectedPoints.size() - 1).y,current_x,current_y,paint);
            }else if (selectedPoints.size() == 1){
                canvas.drawLine(selectedPoints.get(0).x,selectedPoints.get(0).y,current_x,current_y,paint);
            }
        }
    }

    private void drawPoints(Canvas canvas) {
        for (int i=0;i<points.length;i++){
            for (int j=0;j<points.length;j++){
                Point point = points[i][j];
                if (point.State == Point.STATE_NORMAL){
                    canvas.drawBitmap(NormalPoint,point.x - bitmapR,point.y - bitmapR,paint);
                }else if (point.State == Point.STATE_ERROR){
                    canvas.drawBitmap(PressedPoint,point.x - bitmapR,point.y - bitmapR,paint);
                }else if (point.State == Point.STATE_PRESSED){
                    canvas.drawBitmap(SelectPoint,point.x - bitmapR,point.y - bitmapR,paint);
                }
            }
        }
    }

    public static class Point {

        public Point() {
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int x;
        public int y;
        public int State = 0;
        public static int STATE_NORMAL = 0;
        public static int STATE_PRESSED = 1;
        public static int STATE_ERROR = 2;

    }
}
