package felipe.castro.rodriguez.articuno;

/**************
 * FoundBeacon
 **************/
/*
 * Esta clase es utilizada para
 * guardar valores relevantes de
 * los beacons encontrados
 */
public class FoundBeacon {
    // Instance del beacon
    private String instance;
    // Distancia del beacon al dispositivo del usuario
    private double distance;
    // Iteracion en que el beacon fue descubierto
    private int iteration;

    // Constructor
    public FoundBeacon(String instance, double distance, int iteration){
        this.instance = instance;
        this.distance = distance;
        this.iteration = iteration;
    }

    // Metodos para acceder a los atributos
    public String getInstance(){
        return this.instance;
    }
    public double getDistance(){
        return this.distance;
    }
    public int getIteration(){
        return this.iteration;
    }

}
