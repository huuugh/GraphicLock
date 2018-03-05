package com.example.hugh.lock;

/**
 * Created by 60352 on 2018/3/5.
 */

public class Utils {

    public static int distanceBetweenPoints(int x,int y,float movingX,float MovingY){
        return (int)(Math.sqrt(Math.pow(movingX-x,2) + Math.pow(MovingY-y,2)));
    }
}
