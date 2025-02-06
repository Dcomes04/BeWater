/**
 * @file Connexio.java
 * @brief Fitxer que conté la classe Connexio
 */

/**
 * @class Connexio
 * @brief Node de connexió d'una xarxa de distribució d'aigua
 */
public class Connexio extends Node {
    //Descripció general: Node de connexió d'una xarxa de distribució d'aigua

    /**
     * @brief Constructor de la classe Connexio
     * @param id Identificador del node de connexió
     * @param c Coordenades del node de connexió
     * @pre ---
     * @post S'ha creat un nou node de connexió amb identificador id i coordenades c
     */
    public Connexio(String id, Coordenades c){
    //Pre:  ---
    //Post: S'ha creat un nou node de connexió amb identificador id i coordenades c
        super(id,c);//Super invoca el constructor de la classe Node. Després de crida a super, totes les atres inicialitzacions de Connexio podrien anar aquí.
    }
}
