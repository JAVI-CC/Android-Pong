package com.example.javi.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by javi on 6/05/20.
 */

class PongView extends SurfaceView implements Runnable {

    // Este es nuestro hilo
    Thread mGameThread = null;

    // Necesitamos un objeto SurfaceHolder
    // Lo veremos en acción en el método de dibujo pronto.
    SurfaceHolder mOurHolder;

    // Un booleano que estableceremos y desarmaremos
    // cuando el juego se está ejecutando o no
    // Es volátil porque se accede desde dentro y fuera del hilo
    volatile boolean mPlaying;

    // El juego está en pausa al comienzo
    boolean mPaused = true;

    // Un objeto Canvas y Paint
    Canvas mCanvas;
    Paint mPaint;

    // Esta variable rastrea la velocidad de fotogramas del juego
    long mFPS;

    // El tamaño de la pantalla en píxeles
    int mScreenX;
    int mScreenY;

    // Los jugadores mBat
    Bat mBat;

    // A mBall
    Ball mBall;

    // Para efectos de sonido
    SoundPool sp;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;
    int explodeID = -1;

    // El mScore
    int mScore = 0;

    // Lives
    int mLives = 3;

    /* Cuando llamamos a new () en pongView Este constructor personalizado se ejecuta */
    public PongView(Context context, int x, int y) {

        /*La siguiente línea de código le pide a la clase SurfaceView que configure nuestro objeto.*/
        super(context);

        // Establecer el ancho y alto de la pantalla
        mScreenX = x;
        mScreenY = y;

        // Inicializa los objetos mOurHolder y mPaint
        mOurHolder = getHolder();
        mPaint = new Paint();

        // Nuevo mBat
        mBat = new Bat(mScreenX, mScreenY);

        // Crear a mBall
        mBall = new Ball(mScreenX, mScreenY);

        /* Crea una instancia de nuestro grupo de sonido dependiendo de qué versión de Android esté presente */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }


        try {
            // Crear objetos de las 2 clases requeridas
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Carga nuestro fx en memoria listo para usar
            descriptor = assetManager.openFd("beep1.ogg");
            beep1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("explode.ogg");
            explodeID = sp.load(descriptor, 0);

        } catch (IOException e) {
            // Imprime un mensaje de error en la consola
            Log.e("error", "failed to load sound files");
        }
        setupAndRestart();
    }

    public void setupAndRestart() {

        // Pon el mBall de vuelta al principio
        mBall.reset(mScreenX, mScreenY);

        // si el juego supera los puntajes de reinicio y mLives
        if (mLives == 0) {
            mScore = 0;
            mLives = 3;
        }

    }

    public void run() {
        while (mPlaying) {

            // Captura la hora actual en milisegundos en startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Actualizar el frame
            if (!mPaused) {
                update();
            }

            // Dibuja el marco
            draw();

            /* Calcule el FPS en este marco. Luego podemos utilizar el resultado para sincronizar las animaciones en los métodos de actualización. */
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame;
            }

        }

    }

    // Todoo lo que necesita ser actualizado entra aquí. Movimiento, detección de colisión, etc.
    public void update() {

        // Mueve el mBat si es necesario
        mBat.update(mFPS);
        mBall.update(mFPS);

        // Comprueba si mBall choca con mBat
    if(RectF.intersects(mBat.getRect(),mBall.getRect()))

    {
        mBall.setRandomXVelocity();
        mBall.reverseYVelocity();
        mBall.clearObstacleY(mBat.getRect().top - 2);

        mScore++;
        mBall.increaseVelocity();

        sp.play(beep1ID, 1, 1, 0, 0, 1);
    }

        // Rebota la pelota cuando toca la parte inferior de la pantalla
    if(mBall.getRect().bottom >mScreenY)

    {
        mBall.reverseYVelocity();
        mBall.clearObstacleY(mScreenY - 2);

        // Pierde una vida
        mLives--;
        sp.play(loseLifeID, 1, 1, 0, 0, 1);

        if (mLives == 0) {
            mPaused = true;
            setupAndRestart();
        }
    }

        // Devuelve la pelota cuando golpea la parte superior de la pantalla
    if(mBall.getRect().top< 0)

    {
        mBall.reverseYVelocity();
        mBall.clearObstacleY(12);

        sp.play(beep2ID, 1, 1, 0, 0, 1);
    }

        // Si la pelota golpea el rebote de la pared izquierda
    if(mBall.getRect().left< 0)

    {
        mBall.reverseXVelocity();
        mBall.clearObstacleX(2);

        sp.play(beep3ID, 1, 1, 0, 0, 1);
    }

        // Si la pelota golpea el rebote de la pared derecha
    if(mBall.getRect().right >mScreenX)

    {
        mBall.reverseXVelocity();
        mBall.clearObstacleX(mScreenX - 22);

        sp.play(beep3ID, 1, 1, 0, 0, 1);
    }

  }

    // Dibuja la escena recién actualizada
    public void draw() {

        // Asegúrese de que nuestra superficie de dibujo sea válida o nos bloqueemos
        if (mOurHolder.getSurface().isValid()) {

            // Dibuja todoo aquí
            // Bloquea los mCanvas listos para dibujar
            mCanvas = mOurHolder.lockCanvas();

            // Borrar la pantalla con mi color favorito
            mCanvas.drawColor(Color.argb(255, 120, 197, 87));

            // Elige el color del pincel para dibujar
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Dibuja el mBat
            mCanvas.drawRect(mBat.getRect(), mPaint);

            // Dibuja el mBall
            mCanvas.drawRect(mBall.getRect(), mPaint);

            // Cambia el color del dibujo a blanco
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Dibuja el mScore
            mPaint.setTextSize(40);
            mCanvas.drawText("Score: " + mScore + "   Lives: " + mLives, 10, 50, mPaint);

            // Dibuja todoo en la pantalla
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }

    }

    // Si la actividad está en pausa / detenida
    // apagamos nuestro hilo.
    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // Si la actividad comienza / reinicia
    // comienza nuestro hilo.
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    // La clase SurfaceView implementa onTouchListener
    // Entonces podemos anular este método y detectar toques de pantalla.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // El jugador ha tocado la pantalla
            case MotionEvent.ACTION_DOWN:

                mPaused = false;

                // ¿Se toca a la derecha o a la izquierda?
                if(motionEvent.getX() > mScreenX / 2){
                    mBat.setMovementState(mBat.RIGHT);
                }
                else{
                    mBat.setMovementState(mBat.LEFT);
                }

                break;

            // El jugador ha eliminado el dedo de la pantalla
            case MotionEvent.ACTION_UP:

                mBat.setMovementState(mBat.STOPPED);
                break;
        }
        return true;
    }



}