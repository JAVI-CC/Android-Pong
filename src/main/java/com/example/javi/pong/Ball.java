package com.example.pong;

import android.graphics.RectF;

import java.util.Random;

/**
 * Created by javi on 6/05/20.
 */

public class Ball {
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    public Ball(int screenX, int screenY){

        // Hacer el tamaño de mBall relativo a la resolución de la pantalla
        mBallWidth = screenX / 100;
        mBallHeight = mBallWidth;

        /* Comienza la pelota viajando hacia arriba a un cuarto de la altura de la pantalla por segundo */
        mYVelocity = screenY / 4;
        mXVelocity = mYVelocity;

        // Inicializa el Rect que representa el mBall
        mRect = new RectF();

    }

    // Dar acceso al Rect
    public RectF getRect(){
        return mRect;
    }

    // Cambia la posición de cada cuadro
    public void update(long fps){
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top - mBallHeight;
    }

    // Invierte el rumbo vertical
    public void reverseYVelocity(){
        mYVelocity = -mYVelocity;
    }

    // Invierte el rumbo horizontal
    public void reverseXVelocity(){
        mXVelocity = -mXVelocity;
    }

    public void setRandomXVelocity(){

        // Genera un número aleatorio 0 o 1
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    // Acelera en un 10%
    // Una puntuación de más de 20 es bastante difícil
    // Reduce o aumenta 10 para hacer esto más fácil o más difícil
    public void increaseVelocity(){
        mXVelocity = mXVelocity + mXVelocity / 10;
        mYVelocity = mYVelocity + mYVelocity / 10;
    }

    public void clearObstacleY(float y){
        mRect.bottom = y;
        mRect.top = y - mBallHeight;
    }

    public void clearObstacleX(float x){
        mRect.left = x;
        mRect.right = x + mBallWidth;
    }

    public void reset(int x, int y){
        mRect.left = x / 2;
        mRect.top = y - 20;
        mRect.right = x / 2 + mBallWidth;
        mRect.bottom = y - 20 - mBallHeight;
    }

}

