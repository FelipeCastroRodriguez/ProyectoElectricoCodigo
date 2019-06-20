/**********
 * Paquete
 **********/
/*
 * El paquete es un identificador
 * unico que permite diferenciar la
 * aplicacion de todas las demas
 */
package felipe.castro.rodriguez.articuno;

/************
 * Librerias
 * **********/
/*
 * Android Studio incluye de forma automatica
 * los paquetes de las librearias utilizadas
 * pero es necesario declarar que librearias
 * se van a usar en el archivo
 * build.gradle (Module app)
 */
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**********************
 * Actividad principal
 **********************/
 /*
 * extends AppCompatActivity: extends significa que hereda de la clase
 * AppCompatActivity, esta es la clase padre o "super".
 *
 * implements BeaconConsumer: implements significa que se va a usar la
 * interface BeaconConsumer, BeaconConsumer permite interactuar
 * con los beacons encontrados, en particular para usar el metodo "onBeaconServiceConnect()"
 */
public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    /*********************
     * Atributos Globales
     *********************/
    /*
     * El atributo scanNumber representa
     * la cantidad de escaneos que se han
     * realizado en busqueda de beacons
     */
    public int scanNumber = 0;

    /*
     * beaconsEncontrados es un diccionario
     * que contiene todos los beacons encontrados
     * el "key" es un string que representa el
     * Instance del beacon y el "value" es un objeto
     * de la clase FoundBeacon
     *
     * Se crea un metodo para obtener un string con
     * los contenidos del diccionario
     */
    public HashMap<String,FoundBeacon> beaconsEncontrados;
    public String printBeaconsEncontrados(HashMap<String,FoundBeacon> lista){
        String resultado = "";
        Set<String> keys = lista.keySet();
        for(String key: keys){
            resultado = resultado + "[" + key + ", {" +
                    lista.get(key).getInstance() + ", " +
                    lista.get(key).getDistance() + ", " +
                    lista.get(key).getIteration() + "}]\n ";
        }
        return resultado;
    }

    /*
     * beaconManager: Objeto de la clase BeaconManager.
     * En conjunto con la interface BeaconConsumer permite
     * realizar el ranging en busqueda de beacons
     */
    public BeaconManager beaconManager;
    public BeaconManager getBeaconManager(){
        return this.beaconManager;
    }

    /***********
     * onCreate
     ***********/
    /*
     * Metodo que se ejecuta cuando inicia
     * la aplicacion por primera vez
     *
     * Bundle savedInstaceState: permite
     * iniciar la actividad con informacion
     * previamente obtenida en caso de que
     * esta haya sido destruida
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.main_activity_name));
        /*
         * setsetContentView: indica que UI utilizar
         * verifyBluetooth: revisa si el Bluetooth
         * esta habilitado
         */
        setContentView(R.layout.activity_main);
        verificarBluetooth();

        /**************************
         * Inicializacion tributos
         *************************/
        /*
         * Los parsers son los encargados
         * de decodificar los paquetes de advertising
         *
         * getBeaconParser.clear: elimina el parser
         * predefinido
         * getBeaconParser.add: agrega un nuevo parser
         * para paquetes Eddystone UID
         * setForegroundScanPeriod: se define el tiempo
         * de duracion de un escaneo en milisegundos
         * beaconManager.bind: vincula esta actividad con el
         * BeaconService para poder hacer busqueda de beacons
         *
         * new HashMap: se crea un diccionario vacio
         */
        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        this.beaconManager.getBeaconParsers().clear();
        this.beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        this.beaconManager.setForegroundScanPeriod(4400);
        this.beaconManager.bind(this);
        this.beaconsEncontrados = new HashMap<String,FoundBeacon>();

        /*
         * En Android 6 (Marshmallow) y versiones superiores
         * es necesario solicitar permisos de ubicacion
         * para utilizar Bluetooth low energy
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Esta app requiere acceso a la ubicacion");
                builder.setMessage("Se requiere permisos de ubicacion para poder detectar Beacons BLE");
                builder.setPositiveButton(getString(R.string.alert_ok), null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
                builder.show();
            }
        }
    }

    /***********
     * onResume
     ***********/
    /*
     * Metodo que se ejecuta cuando la
     * aplicacion vuelve a ser mostrada
     */
    @Override
    protected void onResume(){
        super.onResume();
        this.beaconManager.bind(this);
    }

    /**********
     * onPause
     **********/
    /*
     * Metodo que se ejecuta cuando el usuario
     * sale de la actividad
     */
    @Override
    protected void onPause(){
        super.onPause();
        // se libera el Bluetooth
        this.beaconManager.unbind(this);
    }

    /************
     * onDestroy
     ************/
    /*
     * Metodo que se ejecuta cuando se cierra
     * la actividad
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.beaconManager.unbind(this);
    }

    /*************************
     * onBeaconServiceConnect
     *************************/
    /*
     * Metodo de la interface BeaconConsumer
     * Despues de haber hecho el bind, se puede utilizar
     * el BeaconService por medio del BeaconManager
     */
    @Override
    public void onBeaconServiceConnect() {
        /*
         * Se obtiene variables que hacen referencias
         * a los cuadros de texto en el layout y al
         * boton
         */
        final TextView viewListaBeacons = (TextView) MainActivity.this.findViewById(R.id.viewListaBeacons);
        viewListaBeacons.setMovementMethod(new ScrollingMovementMethod());
        final TextView viewNivelActualNumero = (TextView) MainActivity.this.findViewById(R.id.viewNivelActualNumero);
        final Button btnMap = (Button) findViewById(R.id.btnMap);

        /*
         * El Identifier es un objeto que representa
         * el UUID, el NameSpace o el Instance de un
         * beacon, son utilizados por las regions
         *
         * El Region es un objeto que contiene
         * identificadores y permite discriminar los
         * beacons que no son de inteneres de la
         * coleccion de beacons encontrados
         */
        String nameSpaceUcr = "0x9a8ea7974cc1efa9756b";
        final Identifier escuelaEieId = Identifier.parse(nameSpaceUcr);
        Region escuelaEie = new Region("escuelaEie", escuelaEieId,null,null);

        /*
         * RangeNotifier es una interface llamada cada vez
         * que se encuentran beacons en una region
         */

        RangeNotifier rangeNotifier = new RangeNotifier() {
            /**************************
             * didRangeBeaoonsInRegion
             **************************/
            /*
             * Este metodo es llamado cada vez que se encuentran
             * beacons que concuerdan con los criterios de la region
             * dada.
             * Los parametros son:
             * Collection<Beacon> beacons: coleccion de objetos de tipo
             * Beacon
             * Region region: region utilizada como criterio para la
             * busqueda de beacons
             */
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                viewListaBeacons.setText("Numero de escaneo: " + scanNumber + "\n");
                for (Beacon x : beacons) {
                    if (x.getId1().toString().equals("0x9a8ea7974cc1efa9756b")){
                        /*
                         * Se agrega cada beacon encontrado a la lista global
                         * de beacons encontrados, si ya existia en la lista se
                         * actualizan sus valores
                         */
                        beaconsEncontrados.put(x.getId2().toString(), new FoundBeacon(x.getId2().toString(), x.getDistance(), scanNumber));
                        // Se imprimen los datos del beacon encontrado
                        viewListaBeacons.append("Datos \n" +
                                "Namespace: " + x.getId1().toString() + "\n" +
                                "Instance: " + x.getId2().toString() + "\n" +
                                "Distance: " + x.getDistance() + " m\n" +
                                "Paquetes encontrados: " + x.getPacketCount() + "\n\n");
                    }
                }
                /*
                 * Se determina en cual nivel del edificio
                 * se encuentra el usuario
                 */
                final String nivelActual = beaconMasCercano(beaconsEncontrados);
                // Se imprime la lista de beacons encontrados
                viewListaBeacons.append(printBeaconsEncontrados(beaconsEncontrados) + "\n");
                // Se muestra el numero del nivel en que se encuentra el usuario
                viewNivelActualNumero.setText(nivelActual);
                /*
                 * Si se presiona el boton se le muestra al usuario
                 * una actividad que contiene un mapa del nivel en
                 * el que se encuentra
                 */
                btnMap.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent startIntent = new Intent(getApplicationContext(), ImageDisplayActivity.class);
                        startIntent.putExtra("Key",nivelActual);
                        startActivity(startIntent);
                    }
                });
                scanNumber = scanNumber + 1;
            }
        };
        try {
            /*
             * startRangingBeaconsInRegion: le indica al BeaconService
             * que busque Beacons en el area que concuerdan con los criterios de la
             * region especificada
             *
             * addRangeNotifier: indica la clase que de ser llamada cada vez que se
             * encuentran Beacons
             */
            beaconManager.startRangingBeaconsInRegion(escuelaEie);
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {
            /*
             * Esta exception ocurre si no se usa el
             * .bind() para el BeaconManager
             */
        }
    }

    /*********************
     * verificarBluetooth
     *********************/
    /*
     * Metodo para verificar:
     * 1. El Bluetooth del dispositivo esta encendido
     * 2. El dispositivo posee capacidades de BLE
     */
    private void verificarBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Bluetooth deshabilitado");
                builder.setMessage("Por favcor habilitar Bluetooth y reiniciar la app");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

            builder.setTitle("Bluetooth LE no disponible");
            builder.setMessage("Este dispositivo no cuenta con Bluetooth LE");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    System.exit(0);
                }

            });
            builder.show();
        }
    }

    /*******************
     * beaconMasCercano
     *******************/
    /*
     * Metodo para determinar cual beacon
     * de la lista de beacons mas cercanos
     * posee la menor distancia
     */
    public String beaconMasCercano(HashMap<String,FoundBeacon> beaconsEncontrados){
        // En caso de que la lista este vacia
        if(beaconsEncontrados.size() == 0){
            return "N/A";
        }
        // Se toma el primer elemento
        Set<String> keys = beaconsEncontrados.keySet();
        FoundBeacon beaconCheck;
        FoundBeacon beaconResultado = beaconsEncontrados.get(keys.toArray()[0].toString());
        /*
         * Se compara cada beacon de la lista
         * para encontrar cual posee la menor distancia
         * Tambien se toma en cuenta hace cuanto se agrego
         * el beacon a la lista
         */
        for(String key : keys){
            beaconCheck = beaconsEncontrados.get(key);
            if(beaconResultado.getDistance()>beaconCheck.getDistance() || (beaconCheck.getIteration()-beaconResultado.getIteration())>5){
                beaconResultado = beaconCheck;
            }
        }
        /*
         * Se asocia el Instance del beacon
         * a su nivel correspondiente
         */
        switch(beaconResultado.getInstance()){
            case "0x111111111111":
                return "1";
            case "0x222222222222":
                return "2";
            default:
                return "Error: beacon no registrado";
        }
    }

    /**********************
     * FIN DE LA ACTIVIDAD
     **********************/
}