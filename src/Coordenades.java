/**
 * @file GestorXarxes.java
 * @brief Fitxer que conté la classe GestorXarxes
 */

/**
 * @class GestorXarxes
 * @brief Gestiona les operacions relacionades amb les xarxes de distribució d'aigua
 */

public class Coordenades {
    //Descripció general: Coordenades geogràfiques (latitud, longitud)
    private float latitud;
    private float longitud;

    /**
     * @brief Constructor de la classe Coordenades
     * @param grausLatitud Graus de latitud
     * @param minutsLatitud Minuts de latitud
     * @param segonsLatitud Segons de latitud
     * @param direccioLatitud Direcció de latitud ('N' o 'S')
     * @param grausLongitud Graus de longitud
     * @param minutsLongitud Minuts de longitud
     * @param segonsLongitud Segons de longitud
     * @param direccioLongitud Direcció de longitud ('E' o 'W')
     * @pre 0 <= grausLatitud <= 60, 0 <= minutsLatitud <= 60, 0 <= segonsLatitud <= 60, direccioLatitud = 'N' o 'S', 0 <= grausLongitud <= 60, 0 <= minutsLongitud <= 60, 0 <= segonsLongitud <= 60, direccioLatitud = 'E' o 'W'
     * @post Crea unes coordenades amb els valors indicats
     */
    public Coordenades(int grausLatitud, int minutsLatitud, float segonsLatitud, char direccioLatitud,
                       int grausLongitud, int minutsLongitud, float segonsLongitud, char direccioLongitud) {
        //Pre: 0 <= grausLatitud <= 60, 0 <= minutsLatitud <= 60, 0 <= segonsLatitud <= 60, direccioLatitud = 'N' o 'S', 0 <= grausLongitud <= 60, 0 <= minutsLongitud <= 60, 0 <= segonsLongitud <= 60, direccioLatitud = 'E' o 'W'
        //Post: Crea unes coordenades amb els valors indicats
        //Excepcions: IllegalArgumentException si es viola la precondició

        // Validar les coordenades i direccions
        if (grausLatitud < 0 || grausLatitud > 90 || minutsLatitud < 0 || minutsLatitud > 60 || segonsLatitud < 0 || segonsLatitud > 60 || (direccioLatitud != 'N' && direccioLatitud != 'S') || grausLongitud < 0 || grausLongitud > 180 || minutsLongitud < 0 || minutsLongitud > 60 || segonsLongitud < 0 || segonsLongitud > 60 || (direccioLongitud != 'E' && direccioLongitud != 'W'))
            throw new IllegalArgumentException("Coordenades invàlides.");

        // Convertir els graus, minuts, segons a graus decimals
        this.latitud = convertirGrausDecimals(grausLatitud, minutsLatitud, segonsLatitud);
        this.longitud = convertirGrausDecimals(grausLongitud, minutsLongitud, segonsLongitud);

        // Passar la latitud a negatiu en el cas de que sigui cap al sud
        if (direccioLatitud == 'S') this.latitud *= -1;

        // Passar la longitud a negatiu en el cas de que sigui cap a l'oest
        if (direccioLongitud == 'W') this.longitud *= -1;
    }

    /**
     * @brief Constructor de la classe Coordenades
     * @param latitud Latitud en graus decimals
     * @param longitud Longitud en graus decimals
     * @pre -90 <= latitud <= 90, -180 <= longitud <= 180
     * @post Crea unes coordenades amb els valors indicats
     */
    public Coordenades(float latitud, float longitud) {
        //Pre: -90 <= latitud <= 90, -180 <= longitud <= 180
        //Post: Crea unes coordenades amb els valors indicats
        //Excepcions: IllegalArgumentException si es viola la precondició
        if(latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180) throw new IllegalArgumentException("Coordenades invàlides.");
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * @brief Retorna la distància entre aquestes coordenades i c, expressada en km
     * @param c Les coordenades a comparar
     * @pre c != null
     * @post Retorna la distància entre aquestes coordenades i c, expressada en km
     * @return La distància entre aquestes coordenades i c
     */
    public double distancia(Coordenades c) {
        //Pre: ---
        //Post: Retorna la distància entre aquestes coordenades i c, expressada en km
        double radiTerra = 6371.0; // Radi de la Terra en quilòmetres
        double dLat = Math.toRadians(c.latitud - this.latitud);
        double dLon = Math.toRadians(c.longitud - this.longitud);
        double lat1 = Math.toRadians(this.latitud);
        double lat2 = Math.toRadians(c.latitud);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double cHav = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radiTerra * cHav;
    }

    /**
     * @brief Converteix graus, minuts i segons a graus decimals
     * @param graus Graus
     * @param minuts Minuts
     * @param segons Segons
     * @pre 0 <= graus <= 60, 0 <= minuts <= 60, 0 <= segons <= 60
     * @post Retorna el valor en graus a decimals
     * @return El valor en graus decimals
     */
    private float convertirGrausDecimals(int graus, int minuts, float segons) {
        //Pre: 0 <= graus <= 60, 0 <= minuts <= 60, 0 <= segons <= 60
        //Post: Retorna el valor en graus a decimals.
        return graus + minuts / 60.0f + segons / 3600.0f;
    }

    /**
     * @brief Retorna la latitud
     * @pre ---
     * @post Retorna la latitud
     * @return La latitud
     */
    public float Latitud() {
        //Pre: ---
        //Post: Retorna la latitud
        return latitud;
    }

    /**
     * @brief Retorna la longitud
     * @pre ---
     * @post Retorna la longitud
     * @return La longitud
     */
    public float Longitud() {
        //Pre: ---
        //Post: Retorna la longitud
        return longitud;
    }

}
