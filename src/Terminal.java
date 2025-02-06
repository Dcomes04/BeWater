import java.util.Set;
import java.util.HashSet;
/**
 * @file Terminal.java
 * @brief Fitxer que conté la classe Terminal
 */

/**
 * @class Terminal
 * @brief Node terminal d'una xarxa de distribució d'aigua
 */
public class Terminal extends Node {
    //Descripció general: Node terminal d'una xarxa de distribució d'aigua
    private final float demandaPunta;
    private float demandaActual;

    private final Set<String> abonats;

    /**
     * @brief Constructor de la classe Terminal
     * @param id Identificador del node terminal
     * @param c Coordenades del node terminal
     * @param demandaPunta Demanda punta del node terminal
     * @pre ---
     * @post S'ha creat un nou terminal amb identificador id, coordenades c i demanda punta demanda en l/s
     */
    public Terminal(String id, Coordenades c, float demandaPunta){
        //Pre:  ---
        //Post: S'ha creat un nou terminal amb identificador id, coordenades c i demanda punta demanda en l/s
        super(id,c);
        this.demandaPunta = demandaPunta;
        this.demandaActual = demandaPunta;//Per defecte, la demandaActual s'inicialitza com a demandaPunta.
        this.abonats = new HashSet<>();//Inicialitzem l'estructura de dades abonats com un HashSet.
    }

    /**
     * @brief Retorna la demanda punta d'aigua del terminal
     * @pre ---
     * @post Retorna la demanda punta d'aigua del terminal
     * @return La demanda punta d'aigua del terminal
     */
    public float demanda() {
        //Pre: ---
        //Post: Retorna la demanda punta d'aigua del terminal
        return this.demandaPunta;
    }

    /**
     * @brief Retorna la demanda actual d'aigua del terminal
     * @pre ---
     * @post Retorna la demanda actual d'aigua del terminal
     * @return La demanda actual d'aigua del terminal
     */
    public float demandaActual() {
        //Pre: ---
        //Post: Retorna la demanda actual d'aigua del terminal
        return this.demandaActual;
    }

    /**
     * @brief Estableix la demanda actual d'aigua del terminal
     * @param demanda La demanda actual d'aigua del terminal
     * @pre demanda >= 0
     * @post La demanda d'aigua actual del terminal és demanda
     * @throws IllegalArgumentException si demanda < 0
     */
    public void establirDemandaActual(float demanda) {
        //Pre: demanda >= 0
        //Post: La demanda d'aigua actual del terminal és demanda
        //Excepcions: IllegalArgumentException si demanda < 0
        if(demanda < 0) throw new IllegalArgumentException("La demanda actual no pot ser negativa.");
        this.demandaActual = demanda; //Si demanda és superior o igual a 0 s'actualitza demandaActual.
    }

    /**
     * @brief Afegeix un abonat al conjunt d'abonats del terminal
     * @param abonat Abonat a afegir
     * @pre abonat != null
     * @post S'ha afegit l'abonat al conjunt d'abonats
     * @throws IllegalArgumentException si abonat és null
     */
    public void afegirAbonat(String abonat) {
        //Pre: abonat no és null
        //Post: S'ha afegit l'abonat al conjunt d'abonats
        //Excepcions: IllegalArgumentException si abonat és null
        if(abonat == null) throw new IllegalArgumentException("L'abonat no pot ser null.");
        this.abonats.add(abonat);
    }

    /**
     * @brief Diu si un abonat és abonat del terminal
     * @param abonat Abonat a comprovar
     * @pre abonat != null
     * @post Retorna si abonat és abonat del terminal
     * @return True si abonat és abonat del terminal, false en cas contrari
     */
    public boolean abonat(String abonat){
        return abonats.contains(abonat);
    }
}