import java.util.*;
/**
 * @file GestorXarxes.java
 * @brief Fitxer que conté la classe GestorXarxes
 */

/**
 * @class GestorXarxes
 * @brief Gestiona les operacions relacionades amb les xarxes de distribució d'aigua
 */
public abstract class GestorXarxes {
    //Descripció general: Mòdul funcional amb funcions per a la gestió de xarxes de distribució d'aigua

    /**
     * @brief Comprova si la xarxa x que conté nodeOrigen té cicles
     * @param x La xarxa on es realitza la comprovació
     * @param nodeOrigen El node origen de la xarxa
     * @pre x és una xarxa vàlida i nodeOrigen és un node a x
     * @post Retorna cert si la xarxa x que conté nodeOrigen té cicles, fals en cas contrari
     * @return Retorna cert si la xarxa x que conté nodeOrigen té cicles, fals en cas contrari
     */
    public static boolean teCicles(Xarxa x, Node nodeOrigen) {
        Set<Node> visitats = new HashSet<>();
        Set<Node> enProces = new HashSet<>();
        // Empezar la búsqueda desde cada nodo no visitado
        for (Node node : x.getNodes(nodeOrigen)) {
            if (!visitats.contains(node) && teCiclesAux(x, node, null, visitats, enProces)) {
                return true; // Se encontró un ciclo
            }
        }
        return false; // No se encontraron ciclos en el grafo
    }


    /**
    * @brief Funció auxiliar per a la funció teCicles
    * @param x La xarxa on es realitza la comprovació
    * @param nodeActual El node actual en la recerca de cicles
    * @param nodePare El node pare del node actual
    * @param visitats Conjunt de nodes visitats
    * @param enProces Conjunt de nodes en procés de visita
    * @pre x és una xarxa vàlida i nodeActual, nodePare són nodes a x
    * @post Retorna cert si es troba un cicle, fals en cas contrari
    * @return Retorna cert si es troba un cicle, fals en cas contrari
    */
    private static boolean teCiclesAux(Xarxa x, Node nodeActual, Node nodePare, Set<Node> visitats, Set<Node> enProces) {
        if (enProces.contains(nodeActual)) {
            return true; // Se encontró un ciclo
        }
        if (visitats.contains(nodeActual)) {
            return false; // El nodo ya fue visitado y no forma parte de un ciclo en este camino
        }

        visitats.add(nodeActual);
        enProces.add(nodeActual);

        // Usar el método sortides para obtener las aristas salientes
        Iterator<Canonada> itCanonades = x.sortides(nodeActual);
        while (itCanonades.hasNext()) {
            Canonada canonada = itCanonades.next();
            Node nodeDesti = canonada.node2(); // Suponiendo que hay un método getNodeDesti en Canonada

            if (nodeDesti != nodePare && teCiclesAux(x, nodeDesti, nodeActual, visitats, enProces)) {
                return true; // Se encontró un ciclo
            }
        }

        enProces.remove(nodeActual); // Se completa la exploración del nodo actual
        return false; // No se encontró un ciclo en este camino
    }

    /**
     * @brief Comprova si la component connexa de la xarxa x que conté nodeOrigen és un arbre
     * @param x La xarxa on es realitza la comprovació
     * @param nodeOrigen El node origen de la xarxa
     * @pre x és una xarxa vàlida i nodeOrigen és un node a x
     * @post Retorna cert si la component connexa de la xarxa x que conté nodeOrigen és un arbre, fals en cas contrari
     * @return Retorna cert si la component connexa de la xarxa x que conté nodeOrigen és un arbre, fals en cas contrari
     */
    public static boolean esArbre(Xarxa x, Origen nodeOrigen) {
        //Pre: nodeOrigen pertany a la xarxa x
        //Post: Diu si la component connexa de la xarxa x que conté nodeOrigen és un arbre
        for(Node node : x.getNodes(nodeOrigen)){
            int entrades = 0;
            for (Iterator<Canonada> it = x.entrades(node); it.hasNext(); ) {
                it.next();
                entrades++;
            }
            if(entrades > 1) return false;
        }
        return true;
    }

    /**
     * @brief Retorna el cabal mínim que hauria d'haver entre tots els nodes d'origen de la component connexa
     * de la xarxa x que conté nodeOrigen, per tal que cap node terminal de la mateixa component, d'entre aquells
     * on arribi aigua, no rebi menys d'un percentatgeDemandaSatisfet de la seva demanda
     * @param x La xarxa on es realitza la comprovació
     * @param nodeOrigen El node origen de la xarxa
     * @param percentatgeDemandaSatisfet El percentatge de demanda satisfeta
     * @pre x és una xarxa vàlida, nodeOrigen és un node a x, la component connexa de la xarxa x que conté nodeOrigen no té cicles,
     * i percentatgeDemandaSatisfet > 0
     * @post Retorna el cabal mínim que hauria d'haver entre tots els nodes d'origen de la component connexa
     * de la xarxa x que conté nodeOrigen, per tal que cap node terminal de la mateixa component, d'entre aquells
     * on arribi aigua, no rebi menys d'un percentatgeDemandaSatisfet de la seva demanda
     * @return Retorna el cabal mínim
     */
    public static float cabalMinim(Xarxa x, Origen nodeOrigen, float percentatgeDemandaSatisfet) {
        //Pre: nodeOrigen pertany a la xarxa x, la component connexa de la xarxa x que conté nodeOrigen no té cicles,
        // i percentatgeDemandaSatisfet > 0
        //Post: Retorna el cabal mínim que hi hauria d’haver entre tots els nodes d’origen de la component connexa
        // de la xarxa x que conté nodeOrigen, per tal que cap node terminal de la mateixa component, d'entre aquells
        // on arribi aigua, no rebi menys d'un percentatgeDemandaSatisfet% de la seva demanda
        if(teCicles(x, nodeOrigen)) throw new IllegalArgumentException("La xarxa té cicles");
        float demandaTotal = 0;

        for(Node node : x.getNodes(nodeOrigen)){
            if(node instanceof Terminal) {
                Set<Node> recorregut = camiObert(x, node);
                if(recorregut != null) {
                    demandaTotal += ((Terminal) node).demandaActual();
                }
            }
        }

        return demandaTotal * percentatgeDemandaSatisfet / 100;
    }

    /**
     * @brief Retorna un conjunt de nodes que representen el camí des del nodeInici fins a qualsevol node d'origen obert, passant només per nodes oberts
     * @param x La xarxa on es realitza la comprovació
     * @param nodeInici El node inici de la xarxa
     * @pre nodeInici és un node a x
     * @post Retorna un conjunt de nodes que representen el camí des del nodeInici fins a qualsevol node d'origen obert, passant només per nodes oberts
     * @return Retorna un conjunt de nodes
     */
    private static LinkedHashSet<Node> camiObert(Xarxa x, Node nodeInici) {
        //Pre: nodeInici pertany a la xarxa x
        //Post: Retorna un conjunt de nodes que representen el camí des del nodeInici fins a qualsevol node d'origen obert, passant només per nodes oberts
        if(!nodeInici.aixetaOberta()) return null;

        Map<Node, Node> prev = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visitats = new HashSet<>();
        queue.add(nodeInici);
        Node nodeFinal = trobarCamiObert(x, queue, prev, visitats);

        if (nodeFinal == null) {
            return null; // No hi ha cap camí obert des del nodeInici fins a qualsevol node d'origen obert
        }
        List<Node> cami = new ArrayList<>();
        for (Node node = nodeFinal; node != null; node = prev.get(node)) {
            cami.add(node);
        }

        Collections.reverse(cami);
        return new LinkedHashSet<>(cami);
    }

    /**
     * @brief Troba un camí obert dins la xarxa x
     * @param x La xarxa on es realitza la recerca
     * @param queue La cua de nodes a visitar
     * @param prev El mapa de nodes previs de cada node
     * @param visitats El conjunt de nodes visitats
     * @pre x és una xarxa vàlida
     * @post Retorna el node final del camí obert si es troba, null en cas contrari
     * @return Retorna el node final del camí obert si es troba, null en cas contrari
     */
    private static Node trobarCamiObert(Xarxa x, Queue<Node> queue, Map<Node, Node> prev, Set<Node> visitats){
        Node nodeFinal = null;
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            visitats.add(node);
            if (node instanceof Origen && node.aixetaOberta() && !x.entrades(node).hasNext()) {
                nodeFinal = node;
                break;
            }
            Iterator<Canonada> itCanonades = x.entrades(node);
            while (itCanonades.hasNext()) {
                Node vei = itCanonades.next().node1();
                if (!visitats.contains(vei) && vei.aixetaOberta()) {
                    queue.add(vei);
                    prev.put(vei, node);
                }
            }
        }
        return nodeFinal;
    }

    /**
     * @brief Retorna el subconjunt de canonades de cjtCanonades tals que, si es satisfés la demanda de tots els nodes
     * terminals de la mateixa component, es sobrepassaria la seva capacitat
     * @param x La xarxa on es realitza la comprovació
     * @param cjtCanonades El conjunt de canonades
     * @pre Les canonades de cjtCanonades pertanyen a una mateixa component connexa, sense cicles, de la xarxa x
     * @post Retorna el subconjunt de canonades de cjtCanonades tals que, si es satisfés la demanda de tots els nodes
     * @return Retorna un conjunt de canonades
     */
    public static Set<Canonada> excesCabal(Xarxa x, Set<Canonada> cjtCanonades) {
        //Pre: Les canonades de cjtCanonades pertanyen a una mateixa component connexa, sense cicles, de la xarxa x
        //Post: Retorna el subconjunt de canonades de cjtCanonades tals que, si es satisfés la demanda de tots els nodes
        // terminals de la mateixa component, es sobrepassaria la seva capacitat
        if(teCicles(x, cjtCanonades.iterator().next().node1())) throw new IllegalArgumentException("La xarxa té cicles");

        Map<Node, Boolean> visitats = new HashMap<>();
        Map<Canonada, Float> demandaCanonades = new HashMap<>();
        Map<Node, Float> demandaNodes = new HashMap<>();
        muntarDemandaNodes(x,cjtCanonades,visitats,demandaNodes);

        //demanda de cada node
        calcularDemanda(x, visitats, demandaCanonades, demandaNodes);

        Map<Node, Float> cabalNodes = new HashMap<>();
        muntarCabalNodes(x,visitats,cabalNodes);

        //canonades amb demanda excedent (= calcular cabal)
        Set<Canonada> canonadesExcedents = new HashSet<>();
        Map<Canonada, Float> cabalCanonades = new HashMap<>();
        calcularCabal(x, visitats, cabalCanonades, cabalNodes, demandaCanonades, demandaNodes);

        for(Canonada entry : cjtCanonades){
            for(Map.Entry<Canonada, Float> entry2 : cabalCanonades.entrySet()){
                if(entry2.getKey() == entry){
                    if(entry2.getValue() > entry.capacitat()) canonadesExcedents.add(entry);
                }
            }
        }
        return canonadesExcedents;
    }

    /**
     * @brief Inicialitza el mapa de demanda de nodes
     * @param x La xarxa on es realitza la comprovació
     * @param cjtCanonades El conjunt de canonades
     * @param visitats El mapa de nodes visitats
     * @param demandaNodes El mapa de demanda de nodes
     * @pre Les canonades de cjtCanonades pertanyen a una mateixa component connexa, sense cicles, de la xarxa x
     * @post Inicialitza el mapa de demanda de nodes
     */
    private static void muntarDemandaNodes(Xarxa x, Set<Canonada> cjtCanonades, Map<Node, Boolean> visitats,Map<Node, Float> demandaNodes){
        for (Node node : x.getNodes(cjtCanonades.iterator().next().node1())) {
            boolean aixetaOberta = node.aixetaOberta();
            boolean camiObert = camiObert(x, node) != null;
            if(aixetaOberta && camiObert){
                if(node instanceof Terminal){
                    visitats.put(node, true);
                    demandaNodes.put(node, ((Terminal) node).demandaActual());
                }
                else{
                    visitats.put(node, false);
                }
            }
        }
    }

    /**
     * @brief Inicialitza el mapa de cabal de nodes
     * @param x La xarxa on es realitza la comprovació
     * @param visitats El mapa de nodes visitats
     * @param cabalNodes El mapa de cabal de nodes
     * @pre ---
     * @post Inicialitza el mapa de cabal de nodes
     */
    private static void muntarCabalNodes(Xarxa x, Map<Node, Boolean> visitats, Map<Node, Float> cabalNodes){
        for (Map.Entry<Node, Boolean> entry : visitats.entrySet()) {
            Node node = entry.getKey();
            if (!(node instanceof Origen) || x.entrades(node).hasNext()) {
                visitats.put(node, false);
            }
            else{
                if(((Origen) node).cabal() != 0)cabalNodes.put(node, ((Origen) node).cabal());
                else{
                    Iterator<Canonada> itCanonades = x.sortides(node);
                    float max = 0f;
                    while (itCanonades.hasNext()) {
                        Canonada canonada = itCanonades.next();
                        if (canonada.capacitat() > max) max = canonada.capacitat();
                    }
                    cabalNodes.put(node, max);
                }
            }
        }
    }

    /**
     * @brief Calcula la demanda de cada node
     * @param x La xarxa on es realitza la comprovació
     * @param visitats El mapa de nodes visitats
     * @param demandaCanonades El mapa de demanda de canonades
     * @param demandaNodes El mapa de demanda de nodes
     * @pre ---
     * @post Calcula la demanda de cada node
     */
    private static void calcularDemanda(Xarxa x, Map<Node, Boolean> visitats, Map<Canonada, Float> demandaCanonades, Map<Node, Float> demandaNodes) {
        // Primer tractem els nodes que ja estan marcats com a visitats
        for (Map.Entry<Node, Boolean> entry : visitats.entrySet()) {
            Node node = entry.getKey();
            Boolean visitat = entry.getValue();

            if (visitat) {
                omplirMapsDemandaCabal(x, node, visitats, demandaCanonades, demandaNodes);
            }
        }

        // Ara tractem la resta de nodes
        boolean totsVisitats = false;

        while (!totsVisitats) {
            totsVisitats = muntarDemanda(x,visitats,demandaCanonades,demandaNodes);
        }
    }

    /**
     * @brief Calcula la demanda de cada node
     * @param x La xarxa on es realitza la comprovació
     * @param visitats El mapa de nodes visitats
     * @param demandaCanonades El mapa de demanda de canonades
     * @param demandaNodes El mapa de demanda de nodes
     * @pre --
     * @post Calcula la demanda de cada node
     * @return Retorna si tots els nodes han estat visitats
     */
    private static boolean muntarDemanda(Xarxa x, Map<Node, Boolean> visitats, Map<Canonada, Float> demandaCanonades, Map<Node, Float> demandaNodes){
        boolean totsVisitats = true;
        for (Map.Entry<Node, Boolean> entry : visitats.entrySet()) {
            Node node = entry.getKey();
            Boolean visitat = entry.getValue();

            if (!visitat) {
                totsVisitats = false;
                Iterator<Canonada> itCanonades = x.sortides(node);
                Iterator<Canonada> finalItCanonades = itCanonades;

                boolean totesCanonadesSuperiorsAZero = canonadesSuperiorsZero(finalItCanonades, visitats, demandaCanonades);

                if (totesCanonadesSuperiorsAZero) {
                    float demanda = 0f;
                    itCanonades = x.sortides(node);

                    while (itCanonades.hasNext()) {
                        Canonada canonada = itCanonades.next();
                        if(visitats.containsKey(canonada.node2())){
                            demanda += demandaCanonades.get(canonada);
                        }
                    }

                    demandaNodes.put(node, demanda);
                    visitats.put(node, true);

                    omplirMapsDemandaCabal(x, node, visitats, demandaCanonades, demandaNodes);
                }
            }
        }
        return totsVisitats;
    }

    /**
     * @brief Comprova si totes les canonades tenen una demanda superior a zero
     * @param finalItCanonades L'iterador de canonades a comprovar
     * @param visitats El mapa de nodes visitats
     * @param demandaCanonades El mapa de demanda de canonades
     * @pre --
     * @post Comprova si totes les canonades tenen una demanda superior a zero
     * @return Retorna true si totes les canonades tenen una demanda superior a zero, false en cas contrari
     */
    private static boolean canonadesSuperiorsZero(Iterator<Canonada> finalItCanonades, Map<Node, Boolean> visitats, Map<Canonada, Float> demandaCanonades){
        boolean totesCanonadesSuperiorsAZero = true;
        for (Canonada canonada : (Iterable<Canonada>)() -> finalItCanonades) {
            for(Map.Entry<Node, Boolean> entry2 : visitats.entrySet()){
                if(entry2.getKey() == canonada.node2()) {
                    if (demandaCanonades.get(canonada) == null) {
                        totesCanonadesSuperiorsAZero = false;
                    }
                }
            }
        }
        return totesCanonadesSuperiorsAZero;
    }

    /**
     * @brief Omple els mapes de demanda i cabal
     * @param x La xarxa on es realitza la comprovació
     * @param node El node actual
     * @param visitats El mapa de nodes visitats
     * @param demandaCanonades El mapa de demanda de canonades
     * @param demandaNodes El mapa de demanda de nodes
     * @pre --
     * @post Omple els mapes de demanda i cabal
     */
    private static void omplirMapsDemandaCabal(Xarxa x, Node node, Map<Node, Boolean> visitats, Map<Canonada, Float> demandaCanonades, Map<Node, Float> demandaNodes){
        Iterator<Canonada> itCanonades = x.entrades(node);
        float sumaCapacitats = 0f;
        while (itCanonades.hasNext()) {
            Canonada canonada = itCanonades.next();
            if (visitats.containsKey(canonada.node1())) {
                sumaCapacitats += canonada.capacitat();
            }
        }
        float demandaPerCapacitat = demandaNodes.get(node) / sumaCapacitats;

        itCanonades = x.entrades(node);
        while (itCanonades.hasNext()) {
            Canonada canonada = itCanonades.next();
            if (visitats.containsKey(canonada.node1())) {
                float demandaCanonada = demandaPerCapacitat * canonada.capacitat();
                demandaCanonades.put(canonada, demandaCanonada);
            }
        }
    }

    /**
     * @brief Omple els mapes de demanda i cabal
     * @param x La xarxa on es realitza la comprovació
     * @param node El node actual
     * @param visitats El mapa de nodes visitats
     * @param demandaCanonades El mapa de demanda de canonades
     * @param demandaNodes El mapa de demanda de nodes
     * @param cabalNodes El mapa de cabal de nodes
     * @param cabalCanonades El mapa de cabal de canonades
     * @pre --
     * @post Omple els mapes de demanda i cabal
     */
    private static void omplirMapDemanda(Xarxa x, Node node, Map<Node, Boolean> visitats, Map<Canonada, Float> demandaCanonades, Map<Node, Float> demandaNodes, Map<Node, Float> cabalNodes, Map<Canonada, Float> cabalCanonades){
        Iterator<Canonada> itCanonades = x.sortides(node);

        while (itCanonades.hasNext()) {
            Canonada canonada = itCanonades.next();

            if (visitats.containsKey(canonada.node2())) {
                float demandaNode = demandaNodes.get(node);
                float cabalNode = cabalNodes.get(node);
                float demandaPerCabal = demandaNode / cabalNode;
                float cabalCanonada = demandaCanonades.get(canonada) / demandaPerCabal;
                cabalCanonades.put(canonada, cabalCanonada);
            }
        }
    }

    /**
     * @brief Calcula el cabal de cada node
     * @param x La xarxa on es realitza la comprovació
     * @param visitats El mapa de nodes visitats
     * @param cabalCanonades El mapa de cabal de canonades
     * @param cabalNodes El mapa de cabal de nodes
     * @param demandaCanonades El mapa de demanda de canonades
     * @param demandaNodes El mapa de demanda de nodes
     * @pre --
     * @post Calcula el cabal de cada node
     */
    private static void calcularCabal(Xarxa x, Map<Node, Boolean> visitats, Map<Canonada, Float> cabalCanonades, Map<Node, Float> cabalNodes, Map<Canonada, Float> demandaCanonades, Map<Node, Float> demandaNodes) {
        // Primer tractem els nodes que ja estan marcats com a visitats
        for (Map.Entry<Node, Boolean> entry : visitats.entrySet()) {
            Node node = entry.getKey();
            Boolean visitat = entry.getValue();

            if (visitat) {
                omplirMapDemanda(x, node, visitats, demandaCanonades, demandaNodes, cabalNodes, cabalCanonades);
            }
        }

        // Ara tractem la resta de nodes
        boolean totsVisitats = false;

        while (!totsVisitats) {
            totsVisitats = muntarCabal(x,visitats,cabalCanonades,cabalNodes,demandaCanonades,demandaNodes);
        }
    }

    /**
     * @brief Calcula el cabal de cada node
     * @param x La xarxa on es realitza la comprovació
     * @param visitats El mapa de nodes visitats
     * @param cabalCanonades El mapa de cabal de canonades
     * @param cabalNodes El mapa de cabal de nodes
     * @param demandaCanonades El mapa de demanda de canonades
     * @param demandaNodes El mapa de demanda de nodes
     * @return Retorna si tots els nodes han estat visitats
     * @pre --
     * @post Calcula el cabal de cada node
     */
    private static boolean muntarCabal(Xarxa x, Map<Node, Boolean> visitats, Map<Canonada, Float> cabalCanonades, Map<Node, Float> cabalNodes, Map<Canonada, Float> demandaCanonades, Map<Node, Float> demandaNodes){
        boolean totsVisitats = true;

        for (Map.Entry<Node, Boolean> entry : visitats.entrySet()) {
            Node node = entry.getKey();
            Boolean visitat = entry.getValue();
            if (!visitat) {
                totsVisitats = false;
                Iterator<Canonada> itCanonades = x.entrades(node);
                Iterator<Canonada> finalItCanonades = itCanonades;
                boolean totesCanonadesSuperiorsAZero = canonadesSuperiorsZeroCabal(finalItCanonades, visitats, cabalCanonades);

                if (totesCanonadesSuperiorsAZero) {
                    float cabal = 0f;
                    itCanonades = x.entrades(node);
                    while (itCanonades.hasNext()) {
                        Canonada canonada = itCanonades.next();
                        if(visitats.containsKey(canonada.node1())) cabal += cabalCanonades.get(canonada);
                    }

                    cabalNodes.put(node, cabal);
                    visitats.put(node, true);

                    omplirMapDemanda(x, node, visitats, demandaCanonades, demandaNodes, cabalNodes, cabalCanonades);
                }
            }
        }
        return totsVisitats;
    }

    /**
     * @brief Comprova si totes les canonades tenen un cabal superior a zero
     * @param finalItCanonades L'iterador de canonades a comprovar
     * @param visitats El mapa de nodes visitats
     * @param cabalCanonades El mapa de cabal de canonades
     * @pre --
     * @post Comprova si totes les canonades tenen un cabal superior a zero
     * @return Retorna true si totes les canonades tenen un cabal superior a zero, false en cas contrari
     */
    private static boolean canonadesSuperiorsZeroCabal(Iterator<Canonada> finalItCanonades, Map<Node, Boolean> visitats, Map<Canonada, Float> cabalCanonades){
        boolean totesCanonadesSuperiorsAZero = true;
        for (Canonada canonada : (Iterable<Canonada>)() -> finalItCanonades) {
            if(visitats.containsKey(canonada.node1())) {
                if (cabalCanonades.get(canonada) == null) {
                    totesCanonadesSuperiorsAZero = false;
                }
            }
        }
        return totesCanonadesSuperiorsAZero;
    }

    /**
     * @brief Retorna un conjunt de nodes que representen les aixetes a tancar
     * @param x La xarxa on es realitza la comprovació
     * @param aiguaArriba El mapa de terminals i si arriba aigua o no
     * @return Retorna un conjunt de nodes
     * @pre La xarxa x és un arbre
     * @post Retorna un conjunt de nodes que representen les aixetes a tancar
     */
    public static Set<Node> aixetesTancar(Xarxa x, Map<Terminal, Boolean> aiguaArriba) {
        Terminal terminal = aiguaArriba.keySet().iterator().next();
        if(!esArbre(x, trobarNodeOrigen(x,terminal))) throw new IllegalArgumentException("La xarxa no té forma d'arbre");
        Set<Node> aixetesTancar = new HashSet<>();
        Map<Node, Set<Node>> recorreguts = new HashMap<>();

        for(Map.Entry<Terminal, Boolean> entry : aiguaArriba.entrySet()) {
            if(entry.getValue()) {
                Set<Node> recorregut = camiObert(x, entry.getKey());
                if (recorregut != null) {
                    recorreguts.put(entry.getKey(), recorregut);
                }
            }
        }

        muntarAixetesTancar(x,aiguaArriba,recorreguts,aixetesTancar);

        for(Node node : aixetesTancar) node.tancarAixeta();
        return aixetesTancar;
    }

    /**
     * @brief Omple el conjunt de nodes que representen les aixetes a tancar
     * @param x La xarxa on es realitza la comprovació
     * @param aiguaArriba El mapa de terminals i si arriba aigua o no
     * @param recorreguts El mapa de nodes i els seus recorreguts
     * @param aixetesTancar El conjunt de nodes que representen les aixetes a tancar
     * @pre ---
     * @post Omple el conjunt de nodes que representen les aixetes a tancar
     */
    private static void muntarAixetesTancar(Xarxa x, Map<Terminal, Boolean> aiguaArriba, Map<Node, Set<Node>> recorreguts, Set<Node> aixetesTancar){
        for(Map.Entry<Terminal, Boolean> entry : aiguaArriba.entrySet()) {
            if(!entry.getValue()) {
                Node aixeta = entry.getKey();
                Set<Node> recorregut = camiObert(x, aixeta);

                if (recorregut != null) {
                    for(Map.Entry<Node, Set<Node>> recorregutEntry : recorreguts.entrySet()) {
                        LinkedHashSet<Node> recorregutCopy = new LinkedHashSet<>(recorregut);
                        recorregutCopy.retainAll(recorregutEntry.getValue());
                        afegirAAixetesTancar(x, recorregutCopy, aixetesTancar);
                    }
                }
            }
        }
    }

    /**
     * @brief Afegeix les aixetes a tancar al conjunt d'aixetes a tancar
     * @param x La xarxa on es realitza la comprovació
     * @param recorregutCopy El conjunt de nodes que formen el recorregut copiat
     * @param aixetesTancar El conjunt de nodes que representen les aixetes a tancar
     * @pre ---
     * @post Afegeix les aixetes a tancar al conjunt d'aixetes a tancar
     */
    private static void afegirAAixetesTancar(Xarxa x, LinkedHashSet<Node> recorregutCopy, Set<Node> aixetesTancar){
        for(Node node : recorregutCopy){
            if(x.entrades(node).hasNext() && x.sortides(node).hasNext()){
                if(aixetesTancar.isEmpty()) aixetesTancar.add(node);
                else {
                    for (Node existingNode : aixetesTancar) {
                        Set<Node> existingPath = camiObert(x, existingNode);
                        Set<Node> newPath = camiObert(x, node);

                        if(existingPath != null && newPath != null) {
                            if (!existingPath.contains(node)) {
                                if (newPath.contains(existingNode)) {
                                    if (existingNode.aixetaOberta()) {
                                        aixetesTancar.remove(existingNode);
                                        aixetesTancar.add(node);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * @brief Troba el node origen dins la xarxa x
     * @param x La xarxa on es realitza la comprovació
     * @param terminal El node a partir del qual es comença la recerca
     * @return Retorna el node origen si es troba, null en cas contrari
     * @pre ---
     * @post Retorna el node origen si es troba, null en cas contrari
     */
    private static Origen trobarNodeOrigen(Xarxa x, Terminal terminal) {
        for (Node node : x.getNodes(terminal)) {
            if (node instanceof Origen) {
                return (Origen) node;
            }
        }
        return null; // Retorna null si no es troba cap node Origen
    }

    /**
     * @brief Retorna una llista de nodes ordenats segons les seves coordenades
     * @param c Les coordenades a partir de les quals es realitza l'ordenació
     * @param cjtNodes El conjunt de nodes a ordenar
     * @pre ---
     * @post Retorna una llista de nodes ordenats segons les seves coordenades
     * @return Retorna una llista de nodes ordenats
     */
    public static List<Node> nodesOrdenats(Coordenades c, Set<Node> cjtNodes) {
        //Pre: ---
        //Post: Retorna una llista amb els nodes de cjtNodes ordenats segons la seva distància a c i, en cas d'empat,
        // en ordre alfabètic dels seus identificadors
        List<Node> nodesOrdenats = new ArrayList<>(cjtNodes);
        nodesOrdenats.sort((node1, node2) -> {
            float distancia1 = (float) c.distancia(node1.coordenades());
            float distancia2 = (float) c.distancia(node2.coordenades());
            if(distancia1 != distancia2) return Float.compare(distancia1, distancia2);
            return node1.id().compareTo(node2.id());
        });
        return nodesOrdenats;
    }

    /**
     * @brief Dibuixa el flux màxim que pot circular per la xarxa x, tenint en compte la capacitat de les canonades
     * @param x La xarxa on es realitza la comprovació
     * @param nodeOrigen El node origen de la xarxa
     * @pre nodeOrigen pertany a la xarxa x
     * @post Dibuixa el flux màxim que pot circular per la xarxa x, tenint en compte la capacitat de les canonades
     */
    public static void fluxMaxim(Xarxa x, Origen nodeOrigen) {
        //Pre: nodeOrigen pertany a la xarxa x
        //Post: Dibuixa el flux màxim que pot circular per la xarxa x, tenint en compte la capacitat de les canonades

        //En el cas de que hi hagi més d'un origen o més d'una terminal, fer que només hi hagi una terminal i un origen
        Origen nouOrigen = ajustarXarxa(x, nodeOrigen);

        //Map de maps creat i inicialitzat amb els nodes i les seves adjacències
        Map<Node,Map<Node,Float>> fluxSortides = new HashMap<>();
        Map<Node,Map<Node,Float>> fluxEntrades = new HashMap<>();

        fluxEntradesSortides(x, nouOrigen, fluxSortides, fluxEntrades);

        //Buscar el camí més curt entre el cabal i la terminal
        Terminal nodeTerminal = trobarNodeTerminal(x, nouOrigen);
        boolean trobat = true;
        while(trobat){
            List<Node> cami = camiMesCurt(nouOrigen, nodeTerminal, fluxSortides, fluxEntrades);
            if(cami == null) trobat = false;
            else actualitzarFlux(cami, fluxSortides, fluxEntrades);
        }
        //Dibuixar el flux màxim
        x.canviarEtiquetaFluxMaxim(fluxEntrades);
    }

    /**
     * @brief Inicialitza els mapes de flux d'entrada i sortida per a cada node de la xarxa
     * @param x La xarxa on es realitza la comprovació
     * @param nouOrigen El node origen de la xarxa
     * @param fluxSortides El mapa de flux de sortida per a cada node
     * @param fluxEntrades El mapa de flux d'entrada per a cada node
     * @pre ---
     * @post Inicialitza els mapes de flux d'entrada i sortida per a cada node de la xarxa
     */
    private static void fluxEntradesSortides(Xarxa x, Origen nouOrigen, Map<Node, Map<Node, Float>> fluxSortides, Map<Node, Map<Node, Float>> fluxEntrades){
        for(Node node : x.getNodes(nouOrigen)){
            if(node.aixetaOberta()) {
                Map<Node, Float> adjacentsSortides = new HashMap<>();
                // Per a fluxSortides
                Iterator<Canonada> itCanonadesSortides = x.sortides(node);
                while (itCanonadesSortides.hasNext()) {
                    Canonada canonada = itCanonadesSortides.next();
                    if(canonada.node2().aixetaOberta()) adjacentsSortides.put(canonada.node2(), canonada.capacitat());
                }
                fluxSortides.put(node, adjacentsSortides);

                Map<Node, Float> adjacentsEntrades = new HashMap<>();
                // Per a fluxEntrades
                Iterator<Canonada> itCanonadesEntrades = x.entrades(node);
                while (itCanonadesEntrades.hasNext()) {
                    Canonada canonada = itCanonadesEntrades.next();
                    if(canonada.node1().aixetaOberta()) adjacentsEntrades.put(canonada.node1(), 0f);
                }
                fluxEntrades.put(node, adjacentsEntrades);
            }
        }
    }

    /**
     * @brief Ajusta la xarxa x segons un conjunt de regles o paràmetres
     * @param x La xarxa a ajustar
     * @param nodeOrigen El node origen de la xarxa
     * @return El nou node origen després de l'ajust de la xarxa
     * @pre ---
     * @post Ajusta la xarxa x segons un conjunt de regles o paràmetres
     */
    private static Origen ajustarXarxa(Xarxa x, Origen nodeOrigen){
        List<Origen> origens = new ArrayList<>();
        List<Terminal> terminals = new ArrayList<>();
        Node origen = null;
        Node terminal = null;
        Origen nouOrigen = nodeOrigen;


        // Comptar el nombre d'origens i terminals
        for (Node node : x.getNodes(nodeOrigen)) {
            if (node instanceof Origen && !x.entrades(node).hasNext()) {
                origens.add((Origen) node);
                origen = node;
            } else if (node instanceof Terminal && !x.sortides(node).hasNext()) {
                terminals.add((Terminal) node);
                terminal = node;
            }
        }

        // Si hi ha més d'un origen o més d'una terminal, fer que només hi hagi una terminal i un origen
        if (origens.size() > 1) {
            Origen nodeOrigenNou = new Origen("0", new Coordenades(0,0));
            x.provaFlux(origen,nodeOrigenNou);
            nouOrigen = nodeOrigenNou;
        }

        if (terminals.size() > 1) {
            Terminal nodeTerminalNou = new Terminal("T", new Coordenades(2,2), 0);
            x.provaFlux(terminal,nodeTerminalNou);
        }

        return nouOrigen;
    }

    /**
     * @brief Troba el node terminal dins la xarxa x
     * @param x La xarxa on es realitza la comprovació
     * @param nodeOrigen El node a partir del qual es comença la recerca
     * @pre nodeOrigen pertany a la xarxa x
     * @post Troba el node terminal dins la xarxa x
     * @return Retorna el node terminal si es troba, null en cas contrari
     */
    private static Terminal trobarNodeTerminal(Xarxa x, Origen nodeOrigen) {
        for (Node node : x.getNodes(nodeOrigen)) {
            if (node instanceof Terminal && !x.sortides(node).hasNext()) {
                return (Terminal) node;
            }
        }
        return null; // Retorna null si no es troba cap node Terminal
    }

    /**
     * @brief Troba el camí més curt entre el node origen i el node terminal dins la xarxa x
     * @param nodeOrigen El node origen del camí
     * @param nodeTerminal El node terminal del camí
     * @param fluxSortides El mapa de fluxos de sortida de cada node
     * @param fluxEntrades El mapa de fluxos d'entrada de cada node
     * @pre ---
     * @post Troba el camí més curt entre el node origen i el node terminal dins la xarxa x
     * @return Retorna una llista de nodes que representen el camí més curt, null si no es troba cap camí
     */
    private static List<Node> camiMesCurt(Origen nodeOrigen, Terminal nodeTerminal, Map<Node, Map<Node, Float>> fluxSortides, Map<Node, Map<Node, Float>> fluxEntrades) {
        Map<Node, Node> prev = new HashMap<>();
        Map<Node, Integer> dist = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        for (Node node : fluxSortides.keySet()) {
            dist.put(node, node.equals(nodeOrigen) ? 0 : Integer.MAX_VALUE);
            queue.add(node);
        }

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            // Comprovar els nodes adjacents a través de fluxSortides
            nodesAdjacents(fluxSortides, node, queue, dist, prev);
            // Comprovar els nodes adjacents a través de fluxEntrades
            nodesAdjacents(fluxEntrades, node, queue, dist, prev);
        }

        List<Node> cami = new ArrayList<>();
        Set<Node> nodesVisitats = new HashSet<>();
        for (Node node = nodeTerminal; node != null; node = prev.get(node)) {
            if (nodesVisitats.contains(node)) {
                break; // Si el node ja ha estat visitat, surt del bucle
            }
            cami.add(node);
            nodesVisitats.add(node);
        }
        Collections.reverse(cami);

        if(cami.getFirst() != nodeOrigen) return null;
        return cami;
    }

    /**
     * @brief Actualitza les distàncies i els nodes previs dels nodes adjacents al node actual
     * @param flux El mapa de fluxos de sortida o entrada de cada node
     * @param node El node actual
     * @param queue La cua de prioritat dels nodes a visitar
     * @param dist El mapa de distàncies des del node origen a cada node
     * @param prev El mapa de nodes previs de cada node
     * @pre --
     * @post Actualitza les distàncies i els nodes previs dels nodes adjacents al node actual
     */
    private static void nodesAdjacents(Map<Node,Map<Node,Float>> flux, Node node, PriorityQueue<Node> queue, Map<Node, Integer> dist, Map<Node, Node> prev){
        for (Map.Entry<Node, Float> entry : flux.get(node).entrySet()) {
            Node vei = entry.getKey();
            Float fluxSortida = entry.getValue();

            if (fluxSortida > 0 && dist.get(node) + 1 < dist.get(vei)) {
                queue.remove(vei);
                dist.put(vei, dist.get(node) + 1);
                prev.put(vei, node);
                queue.add(vei);
            }
        }
    }

    /**
     * @brief Actualitza el flux de sortida i entrada després de trobar un camí més curt
     * @param cami La llista de nodes que formen el camí més curt
     * @param fluxSortides El mapa de fluxos de sortida de cada node
     * @param fluxEntrades El mapa de fluxos d'entrada de cada node
     * @pre ---
     * @post Actualitza el flux de sortida i entrada després de trobar un camí més curt
     */
    private static void actualitzarFlux(List<Node> cami, Map<Node, Map<Node, Float>> fluxSortides, Map<Node, Map<Node, Float>> fluxEntrades) {
        // Trobar el flux mínim en el camí
        float fluxMinim = Float.MAX_VALUE;
        for (int i = 0; i < cami.size() - 1; i++) {
            Node nodeActual = cami.get(i);
            Node nodeSeguent = cami.get(i + 1);
            float flux;
            if(fluxSortides.get(nodeActual).containsKey(nodeSeguent)) flux = fluxSortides.get(nodeActual).get(nodeSeguent);
            else flux = fluxEntrades.get(nodeActual).get(nodeSeguent);

            if (flux < fluxMinim) fluxMinim = flux;
        }

        // Actualitzar fluxSortides i fluxEntrades
        for (int i = 0; i < cami.size() - 1; i++) {
            Node nodeActual = cami.get(i);
            Node nodeSeguent = cami.get(i + 1);

            if (fluxSortides.get(nodeActual).containsKey(nodeSeguent)) {
                // El nodeSeguent és una sortida del nodeActual
                float fluxSortida = fluxSortides.get(nodeActual).get(nodeSeguent);
                fluxSortides.get(nodeActual).put(nodeSeguent, fluxSortida - fluxMinim);
                float fluxEntrada = fluxEntrades.get(nodeSeguent).get(nodeActual);
                fluxEntrades.get(nodeSeguent).put(nodeActual, fluxEntrada + fluxMinim);
            }
            else if (fluxEntrades.get(nodeActual).containsKey(nodeSeguent)) {
                // El nodeSeguent és una entrada del nodeActual
                float fluxEntrada = fluxEntrades.get(nodeActual).get(nodeSeguent);
                fluxEntrades.get(nodeActual).put(nodeSeguent, fluxEntrada - fluxMinim);
                float fluxSortida = fluxSortides.get(nodeSeguent).get(nodeActual);
                fluxSortides.get(nodeSeguent).put(nodeActual, fluxSortida + fluxMinim);
            }
        }
    }
}