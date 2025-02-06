/**
 * @file Origen.java
 * @brief Fitxer que conté la classe Origen
 */

/**
 * @class Origen
 * @brief Node origen d'una xarxa de distribució d'aigua
 */
public class Origen extends Node {
    //Descripció general: Node origen d'una xarxa de distribució d'aigua
    private float cabal;

    /**
     * @brief Constructor de la classe Origen
     * @param id Identificador del node origen
     * @param c Coordenades del node origen
     * @pre ---
     * @post S'ha creat un nou origen amb identificador id i coordenades c
     */
    public Origen(String id, Coordenades c) {
        //Pre:  ---
        //Post: S'ha creat un nou origen amb identificador id i coordenades c
        super(id,c); //Super invoca el constructor de la classe Node. Estableix el id i les coordenades a l'Origen determinat.
        this.cabal = 0; //Iniciar el cabal a 0 per l'objecte Origen.
    }

    /**
     * @brief Retorna el cabal d'aigua que surt de l'origen
     * @pre ---
     * @post Retorna el cabal d'aigua que surt de l'origen
     * @return El cabal d'aigua que surt de l'origen
     */
    public float cabal() {
        //Pre: ---
        //Post: Retorna el cabal d'aigua que surt de l'origen
        return this.cabal;
    }

    /**
     * @brief Estableix el cabal d'aigua que surt de l'origen
     * @param cabal El cabal d'aigua que surt de l'origen
     * @pre cabal >= 0
     * @post El cabal d'aigua que surt de l'origen és cabal
     * @throws IllegalArgumentException si cabal < 0
     */
    public void establirCabal(float cabal) {
        //Pre: cabal >= 0
        // Post: El cabal d'aigua que surt de l'origen és cabal
        //Excepcions: IllegalArgumentException si cabal < 0
        if(cabal < 0) throw new IllegalArgumentException("El cabal no pot ser negatiu");
        this.cabal = cabal; //Si el cabal és superior o igual a 0 l'actualitza.
    }
}