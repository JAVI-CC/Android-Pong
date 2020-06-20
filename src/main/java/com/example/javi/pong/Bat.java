package com.example.javi.pong;

import android.graphics.RectF;

/**
 * Created by javi on 6/05/20.
 */

public class Bat {

    // RectF es un objeto que contiene cuatro coordenadas, justo lo que necesitamos
    private RectF mRect;

    // Que tan largo y alto será nuestro mBat
    private float mLength;
    private float mHeight;

    // X es el extremo izquierdo del rectángulo que forma nuestro mBat
    private float mXCoord;

    // Y es la coordenada superior
    private float mYCoord;

    // Esto mantendrá la velocidad de píxeles por segundo que
    // el mBat se moverá
    private float mBatSpeed;

    // ¿De qué maneras puede moverse el mBat?
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // ¿Se mueve el mBat y en qué dirección
    private int mBatMoving = STOPPED;

    // La longitud y el ancho de la pantalla en píxeles
    private int mScreenX;
    private int mScreenY;

    // Este es el método constructor
    // Cuando creamos un objeto de esta clase, pasaremos
    // en el ancho de la pantalla y mHeight
    public Bat(int x, int y){

        mScreenX = x;
        mScreenY = y;

        // 1/8 ancho de pantalla ancho
        mLength = mScreenX / 8;

        // 1/25 de pantalla mHeight high
        mHeight = mScreenY / 25;

        // Inicie mBat aproximadamente en el centro de pantalla
        mXCoord = mScreenX / 2;
        mYCoord = mScreenY - 20;

        mRect = new RectF(mXCoord, mYCoord, mXCoord + mLength, mYCoord + mHeight);

        // ¿Qué tan rápido es el mBat en píxeles por segundo?
        mBatSpeed = mScreenX;
        // Cubra toda la pantalla en 1 segundo
    }

    // Este es un método getter para hacer el rectángulo que
    // define nuestro bate disponible en la clase PongView
    public RectF getRect(){
        return mRect;
    }

    // Este método se usará para cambiar / establecer si el mBat va
    // izquierda, derecha o en ninguna parte
    public void setMovementState(int state){
        mBatMoving = state;
    }

    // Este método de actualización se llamará desde la actualización en PongView
    // Determina si el Murciélago necesita moverse y cambia las coordenadas
    // contenido en mRect si es necesario
    public void update(long fps){

        if(mBatMoving == LEFT){
            mXCoord = mXCoord - mBatSpeed / fps;
        }

        if(mBatMoving == RIGHT){
            mXCoord = mXCoord + mBatSpeed / fps;
        }

        // Asegúrate de que no salga de la pantalla
        if(mRect.left < 0){ mXCoord = 0; } if(mRect.right > mScreenX){
            mXCoord = mScreenX -
                    // El ancho del murciélago
                    (mRect.right - mRect.left);
        }

        // Actualiza los gráficos de Bat
        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }

}