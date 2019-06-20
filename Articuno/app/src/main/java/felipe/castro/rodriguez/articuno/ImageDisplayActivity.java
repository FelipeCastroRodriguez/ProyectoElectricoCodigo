package felipe.castro.rodriguez.articuno;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/***********************
 * ImageDisplayActivity
 ***********************/
/*
 * Esta actividad es utilizada para
 * mostrar el mapa del nivel en el
 * que se encuentra el usuario
 */
public class ImageDisplayActivity extends AppCompatActivity {
    /***********
     * onCreate
     ***********/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_image_display);
        // Se obtiene una referencia al ImageView
        ImageView img = (ImageView) findViewById(R.id.imageView);
        TextView viewNoBeacon = (TextView) this.findViewById(R.id.viewNoBeacon);

        // Se muestra el mapa correspondiente
        switch(getIntent().getExtras().getString("Key")){
            case "1":
                // Usuario en el nivel 1
                viewNoBeacon.setText("");
                img.setImageResource(R.drawable.cat);;
                break;
            case "2":
                // Usuario en el nivel 2
                viewNoBeacon.setText("");
                img.setImageResource(R.drawable.toucan1);;
                break;
            case "Error: beacon no registrado":
                // Beacon no registrado
                viewNoBeacon.setText("");
                img.setImageResource(R.drawable.error);;
                break;
        }
    }

    /************
     * onDestroy
     ************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**********************
     * FIN DE LA ACTIVIDAD
     **********************/
}
