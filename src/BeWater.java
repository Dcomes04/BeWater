/**
 * @file BeWater.java
 * @brief Programa principal de simulació de xarxes de distribució d'aigua.
 */

/**
 * @class BeWater
 * @brief Classe abstracta per al programa principal de simulació de xarxes de distribució d'aigua.
 *
 * La classe BeWater conté el mètode principal que inicia la simulació en mode text.
 */
public abstract class BeWater {
    /**
     * @brief Mètode principal que inicia la simulació en mode text.
     * @param args Arguments de la línia de comandes. args[0] ha de ser el fitxer de configuració de la xarxa i args[1] ha de ser el fitxer de resultats.
     */
    public static void main(String[] args) {
        SimuladorModeText simulador = new SimuladorModeText();
        System.out.println("Be water, my friend");
        simulador.simular(args[0],args[1]);
    }
}
