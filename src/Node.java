/**
 * @file Node.java
 * @brief Fitxer que conté la classe Node
 */

/**
 * @class Node
 * @brief Representa un node en una xarxa de distribució d'aigua
 */
public class Node {
    //Descripció general: Node d'una xarxa de distribució d'aigua
    private final String id;
    private final Coordenades c;
    private boolean aixetaOberta;

    /**
     * @brief Constructor de la classe Node
     * @param id Identificador del node
     * @param c Coordenades del node
     * @pre ---
     * @post S'ha creat un nou node amb identificador id i coordenades c
     */
    public Node(String id, Coordenades c) {
        //Pre:  ---
        //Post: S'ha creat un nou node amb identificador id i coordenades c
        this.id = id;
        this.c = c;
        this.aixetaOberta = true;
    }

    /**
     * @brief Retorna l'identificador del node
     * @pre ---
     * @post Retorna l'identificador del node
     * @return L'identificador del node
     */
    public String id() {
        //Pre:  ---
        //Post: Retorna l'identificador del node
        return this.id;
    }

    /**
     * @brief Retorna les coordenades del node
     * @pre ---
     * @post Retorna les coordenades del node
     * @return Les coordenades del node
     */
    public Coordenades coordenades() {
        //Pre:  ---
        //Post: Retorna les coordenades del node
        return this.c;
    }

    /**
     * @brief Diu si l'aixeta del node està oberta
     * @pre ---
     * @post Diu si l'aixeta del node està oberta
     * @return True si l'aixeta està oberta, false en cas contrari
     */
    public boolean aixetaOberta() {
        //Pre:  ---
        //Post: Diu si l'aixeta del node està oberta
        return this.aixetaOberta;
    }

    /**
     * @brief Obre l'aixeta del node
     * @pre ---
     * @post L'aixeta del node està oberta
     */
    public void obrirAixeta() {
        //Pre:  ---
        //Post: L'aixeta del node està oberta
        this.aixetaOberta = true;
    }

    /**
     * @brief Tanca l'aixeta del node
     * @pre ---
     * @post L'aixeta del node està tancada
     */
    public void tancarAixeta() {
        //Pre:  ---
        //Post: L'aixeta del node està tancada
        this.aixetaOberta = false;
    }
}