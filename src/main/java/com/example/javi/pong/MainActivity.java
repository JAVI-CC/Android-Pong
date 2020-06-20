package com.example.javi.pong;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {

    // pongView será la vista del juego
    // También mantendrá la lógica del juego.
    // y responder a toques de pantalla también
    PongView pongView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtenga un objeto Display para acceder a los detalles de la pantalla
        Display display = getWindowManager().getDefaultDisplay();

        // Carga la resolución en un objeto Point
        Point size = new Point();
        display.getSize(size);

        // Inicializa pongView y configúralo como la vista
        pongView = new PongView(this, size.x, size.y);
        setContentView(pongView);
    }

    // Este método se ejecuta cuando el jugador comienza el juego.
    @Override
    protected void onResume() {
        super.onResume();

        // Dile al método de reanudar pongView para ejecutar
        pongView.resume();
    }

    // Este método se ejecuta cuando el jugador abandona el juego
    @Override
    protected void onPause() {
        super.onPause();

        // Dile al método de pausa pongView que se ejecute
        pongView.pause();
    }
}
