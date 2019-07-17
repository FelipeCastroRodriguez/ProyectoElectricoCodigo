package felipe.castro.rodriguez.articuno;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jsibbold.zoomage.ZoomageView;

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
        setTitle("Mapa del nivel");
        setContentView(R.layout.activity_image_display);

        // Se obtiene una referencia al ImageView
        ZoomageView mapaNivel = (ZoomageView) findViewById(R.id.ZoomageViewMap);
        TextView viewNoBeacon = (TextView) this.findViewById(R.id.viewNoBeacon);

        // Se muestra el mapa correspondiente
        switch(getIntent().getExtras().getString("Key")){

            case "1":
                // Usuario en el nivel 1
                viewNoBeacon.setText("");
                mapaNivel.setImageResource(R.drawable.nivelunocolor);;
                break;
            case "2":
                // Usuario en el nivel 2
                viewNoBeacon.setText("");
                mapaNivel.setImageResource(R.drawable.niveldoscolor);
                break;

            case "3":
                // Usuario en el nivel 3
                viewNoBeacon.setText("");
                mapaNivel.setImageResource(R.drawable.niveltrescolor);
                break;
            case "4":
                // Usuario en el nivel 4
                viewNoBeacon.setText("");
                mapaNivel.setImageResource(R.drawable.toucan1);
                break;
            case "5":
                // Usuario en el nivel 5
                viewNoBeacon.setText("");
                mapaNivel.setImageResource(R.drawable.toucan1);
                break;
            case "6":
                // Usuario en el nivel 6
                viewNoBeacon.setText("");
                mapaNivel.setImageResource(R.drawable.toucan1);
                break;
            case "Error: baliza no registrada":
                // Beacon no registrado
                viewNoBeacon.setText("");
                mapaNivel.setImageResource(R.drawable.cat);
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
