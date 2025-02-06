//Jhon Alejandro Parraga Mogollon

/**
 * @file Canonada.java
 * @brief Programa on crearem i agafarem l'infomrmacio de la canonada.
 */
/**
 * @class Canonada
 * @brief Canonada de la xarxa de distribució d'aigua.
 */
public class Canonada {
    //Descripció general: Canonada de la xarxa de distribució d'aigua

    private final Node node1;
    private final Node node2;
    private final float capacitat;
    /**
     * @brief Crea una canonada que connecta node1 i node2 amb la capacitat indicada.
     * @param node1 Node d'inici de la canonada.
     * @param node2 Node de destí de la canonada.
     * @param capacitat Capacitat de la canonada.
     * @pre capacitat > 0.
     * @post Crea una canonada que connecta node1 i node2 amb la capacitat indicada.
     */
    public Canonada(Node node1, Node node2, float capacitat){
        this.node1 = node1;
        this.node2 = node2;
        this.capacitat = capacitat;
    }
    /**
     * @brief Retorna el node d'inici de la canonada.
     * @return Node d'inici de la canonada.
     * @pre ---
     * @post Retorna el node d'inici de la canonada.
     */
    public Node node1() {
        return this.node1;
    }
    /**
     * @brief Retorna el node de destí de la canonada.
     * @return Node de destí de la canonada.
     * @pre ---
     * @post Retorna el node de destí de la canonada.
     */
    public Node node2() {
        return this.node2;
    }
    /**
     * @brief Retorna la capacitat de la canonada.
     * @return Capacitat de la canonada.
     * @pre ---
     * @post Retorna la capacitat de la canonada.
     */
    public float capacitat(){
            return this.capacitat;
    }
}
