//Jhon Alejandro Parraga Mogollon
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.util.Iterator;
import java.util.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * @file Xarxa.java
 * @brief Programa principal on modificarem el graph i on tindrem tot relacionat amb aquest
 */

/**
 * @class Xarxa
 * @brief Xarxa de distribució d'aigua, no necessàriament connexa (graf dirigit de Node)
 * Descripció general: Xarxa de distribució d'aigua, no necessàriament connexa (graf dirigit de Node)
 */
public class Xarxa {

    private final Graph graph;
    private final Deque<String[]> operacions = new LinkedList<>(); //guardar les operacions per fer el backtrack
    private final Map<Node, Float> demandas = new LinkedHashMap<>(); //Aqui guardarem les demandes de cada node
    private final Map<Node, Float> cabals = new LinkedHashMap<>(); //Aqui guardarem els cabals de cada node
    private final Map<Canonada, Float> canonades = new HashMap<>(); //Aqui guardarem les canonades i les demandas que porten
    private final Map<Canonada, Float> canonadaCabals = new HashMap<>(); //Aqui guardarem les canonades i els cabals que porten
    private final Set<Node> nodos = new HashSet<>(); //Aqui guardarem les nodos del graph
    public Xarxa() {
        //Pre: ---
        //Post: Crea una xarxa de distribució d'aigua buida
        System.setProperty("org.graphstream.ui", "swing");
        graph = new SingleGraph("Xarxa");
        String styleSheet =
                "node { " +
                        "text-mode: normal; " +
                        "text-color: black; " +
                        "text-size: 20; " +
                        "fill-color: yellow; " +
                        "size: 50px, 30px; " +
                        "} " +
                        "edge { " +
                        "fill-color: green; " +
                        "size: 2px; " +
                        "}";
        graph.setAttribute("ui.stylesheet", styleSheet);
    }
    /**
     * @brief Retorna el node de la xarxa amb l'identificador donat.
     * @param id Identificador del node.
     * @return Node corresponent al identificador, o null si no existeix.
     * @pre: ---
     * @post: Retorna el node de la xarxa amb identificador id
     */
    public Node node(String id) {

        if(graph.getNode(id) != null) {
            if (graph.getNode(id).hasAttribute("origen")) {
                return (Node) graph.getNode(id).getAttribute("origen");
            }
            if (graph.getNode(id).hasAttribute("terminal")) {
                return (Node) graph.getNode(id).getAttribute("terminal");
            }
            if (graph.getNode(id).hasAttribute("connexio")) {
                return (Node) graph.getNode(id).getAttribute("connexio");
            }
        }
        return null;
    }
    
    /**
     * @brief Retorna un iterador que permet recórrer totes les canonades que surten del node.
     * @param node Node del qual es volen obtenir les canonades sortints.
     * @return Iterador de les canonades sortints del node.
     * @pre: node pertany a la xarxa
     * @post: Retorna un iterador que permet recórrer totes les canonades que surten del node
     */
    public Iterator<Canonada> sortides(Node node) {

        ArrayList<Canonada> sortides = new ArrayList<>();
        Iterator<Edge> itEdge = graph.getNode(node.id()).leavingEdges().iterator();
        while(itEdge.hasNext()){
            Canonada canonada = (Canonada) itEdge.next().getAttribute("Canonada");
            sortides.add(canonada);
        }

        return sortides.iterator();
    }
    /**
     * @brief Retorna un iterador que permet recórrer totes les canonades que entren al node.
     * @param node Node del qual es volen obtenir les canonades entrants.
     * @return Iterador de les canonades entrants del node.
     * @pre: node pertany a la xarxa
     * @post: Retorna un iterador que permet recórrer totes les canonades que entren al node
     */
    public Iterator<Canonada> entrades(Node node) {
        ArrayList<Canonada> entrades = new ArrayList<>();
        if(node != null) {
            Iterator<Edge> itEdge = graph.getNode(node.id()).enteringEdges().iterator();
            while (itEdge.hasNext()) {
                Canonada canonada = (Canonada) itEdge.next().getAttribute("Canonada");
                entrades.add(canonada);
            }
        }
        return entrades.iterator();
    }
    /**
     * @brief Afegeix un node d'origen a la xarxa.
     * @param nodeOrigen Node d'origen a afegir.
     * @throws IllegalArgumentException Si ja existeix un node amb el mateix id.
     * @pre: No existeix cap node amb el mateix id que nodeOrigen a la xarxa
     * @post: S'ha afegit nodeOrigen a la xarxa
     */
    public void afegir(Origen nodeOrigen) throws IllegalArgumentException {
        String ori = nodeOrigen.id();
        float longitud = nodeOrigen.coordenades().Longitud();
        float latitud = nodeOrigen.coordenades().Latitud();
        if (graph.getNode(ori) != null) {
            throw new IllegalArgumentException("Ja existeix un node amb aquest id: " + ori);
        }
        graph.addNode(ori).setAttribute("origen", nodeOrigen);
        graph.getNode(ori).setAttribute("ui.label", ori);
        graph.getNode(ori).setAttribute("ui.style", "fill-color: yellow; size: 25px; text-size: 15;");
        graph.getNode(ori).setAttribute("xy",longitud,latitud);
        actualizarEtiqueta(nodeOrigen);
    }
    /**
     * @brief Afegeix un node terminal a la xarxa.
     * @param nodeTerminal Node terminal a afegir.
     * @throws IllegalArgumentException Si ja existeix un node amb el mateix id.
     * @pre: No existeix cap node amb el mateix id que nodeTerminal a la xarxa
     * @post: S'ha afegit nodeTerminal a la xarxa
     */
    public void afegir(Terminal nodeTerminal) {
        nodos.add(nodeTerminal);
        String ter = nodeTerminal.id();
        float longitud = nodeTerminal.coordenades().Longitud();
        float latitud = nodeTerminal.coordenades().Latitud();
        if (graph.getNode(ter) != null) {
            throw new IllegalArgumentException("Ja existeix un node amb aquest id: " + ter);
        }
        graph.addNode(ter).setAttribute("terminal", nodeTerminal);
        graph.getNode(ter).setAttribute("ui.label",ter);
        graph.getNode(ter).setAttribute("ui.style", "fill-color: green; size: 25px; text-size: 15;");
        graph.getNode(ter).setAttribute("xy",longitud,latitud);
        actualizarEtiqueta(nodeTerminal);
    }
    /**
     * @brief Afegeix un node connexió a la xarxa.
     * @param nodeConnexio Node connexió a afegir.
     * @throws IllegalArgumentException Si ja existeix un node amb el mateix id.
     * @pre: No existeix cap node amb el mateix id que nodeConnexio a la xarxa
     * @post: S'ha afegit nodeConnexio a la xarxa
     */
    public void afegir(Connexio nodeConnexio) {
        String cone = nodeConnexio.id();
        float longitud = nodeConnexio.coordenades().Longitud();
        float latitud = nodeConnexio.coordenades().Latitud();
        if (graph.getNode(cone) != null) {
            throw new IllegalArgumentException("Ja existeix un node amb aquest id: " + cone);
        }
        graph.addNode(cone).setAttribute("connexio", nodeConnexio);
        graph.getNode(cone).setAttribute("ui.label",cone);
        graph.getNode(cone).setAttribute("ui.style", "fill-color: red; size: 25px; text-size: 15;");
        graph.getNode(cone).setAttribute("xy",longitud,latitud);
        actualizarEtiqueta(nodeConnexio);
    }
    /**
     * @brief Connecta dos nodes amb una canonada de capacitat donada.
     * @param node1 Node d'origen de la connexió.
     * @param node2 Node de destinació de la connexió.
     * @param c Capacitat de la canonada.
     * @pre: node1 i node2 pertanyen a la xarxa, no estan connectats, i node1 no és un node terminal
     * @post: S'han connectat els nodes amb una canonada de capacitat c, amb sentit de l'aigua de node1 a node2
     * @throws NoSuchElementException Si un o tots dos nodes no pertanyen a la xarxa.
     * @throws IllegalArgumentException Si els nodes ja estan connectats o si node1 és un node terminal.
     */
    public void connectarAmbCanonada(Node node1, Node node2, float c) {
        if (graph.getEdge(node1.id() + "-" + node2.id()) != null || graph.getEdge(node2.id() + "-" + node1.id()) != null) {
            throw new IllegalArgumentException("Ja existeix una connexio entre aquests nodes");
        }
        if (graph.getNode(node1.id()) == null || graph.getNode(node2.id()) == null) {
            throw new NoSuchElementException("Un o els dos nodes no pertanyen a la xarxa");
        }
        if (node1 instanceof Terminal) {
            throw new IllegalArgumentException("El node1 es tracta de un node terminal");
        }

        Canonada canonada = new Canonada(node1, node2, c);

        if (node2 instanceof Origen) {
            graph.getNode(node2.id()).setAttribute("connexio", new Connexio(node2.id(), node2.coordenades()));
            actualizarConexiones(node2, (Connexio) graph.getNode(node2.id()).getAttribute("connexio"));
            graph.getNode(node2.id()).removeAttribute("origen");
            graph.getNode(node2.id()).setAttribute("ui.label", node2.id());
            graph.getNode(node2.id()).setAttribute("ui.style", "fill-color: red; size: 25px; text-size: 15;");
            actualizarEtiqueta((Connexio) graph.getNode(node2.id()).getAttribute("connexio"));
            canonada = new Canonada(node1, (Connexio) graph.getNode(node2.id()).getAttribute("connexio"), c);
        }
            graph.addEdge(node1.id() + "-" + node2.id(), node1.id(), node2.id(), true).setAttribute("Canonada", canonada);
            graph.getEdge(node1.id() + "-" + node2.id()).setAttribute("ui.style", "fill-color: blue; size: 3px;");
    }
    /**
     * @brief Actualitza les connexions d'un node antic a un node nou de connexió.
     * @param antiguo Node antic.
     * @param nuevo Node nou de connexió.
     * @pre: antiguo és un node d'origen i nou és un node de connexió
     * @post: Totes les connexions sortints i entrants del node antiguo s'actualitzen per apuntar al node nou.
     */
    private void actualizarConexiones(Node antiguo, Connexio nuevo) {
        Iterator<Canonada> itSalientes = sortides(antiguo);
        while (itSalientes.hasNext()) {
            Canonada canonada = itSalientes.next();
            Canonada nuevaCanonada = new Canonada(nuevo, canonada.node2(), canonada.capacitat());
            graph.getEdge(antiguo.id() + "-" + canonada.node2().id()).setAttribute("Canonada", nuevaCanonada);
        }

        Iterator<Canonada> itEntrantes = entrades(antiguo);
        while (itEntrantes.hasNext()) {
            Canonada canonada = itEntrantes.next();
            Canonada nuevaCanonada = new Canonada(canonada.node1(), nuevo, canonada.capacitat());
            graph.getEdge(canonada.node2().id() + "-" + antiguo.id()).setAttribute("Canonada", nuevaCanonada);
        }
    }
    /**
     * @brief Retorna la canonada amb la capacitat corresponent a l'identificador donat.
     * @param canonada Identificador de la canonada.
     * @return La canonada corresponent a l'identificador.
     * @pre: La canonada ha d'existir en la xarxa.
     * @post: Retorna la canonada associada amb l'identificador proporcionat.
     */
    public Canonada retornarCapacitat(String canonada){
        return (Canonada) graph.getEdge(canonada).getAttribute("Canonada");
    }
    /**
     * @brief Abona un cliente a un terminal.
     * @param idClient Identificador del cliente.
     * @param nodeTerminal Nodo terminal al que se quiere abonar el cliente.
     * @pre: nodeTerminal pertany a la xarxa
     * @post: El client identificat amb idClient queda abonat al node terminal, i diu si ja ho estava
     * @return true si el cliente ya estaba abonado, false en caso contrario.
     * @throws NoSuchElementException Si el nodo terminal no pertenece a la red.
     */
    public boolean abonar(String idClient, Terminal nodeTerminal) {
        if (graph.getNode(nodeTerminal.id()) == null) {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }
        boolean Abonat = nodeTerminal.abonat(idClient);
        Terminal terminal = (Terminal) graph.getNode(nodeTerminal.id()).getAttribute("terminal");
        terminal.afegirAbonat(idClient);
        return Abonat;
    }
    /**
     * @brief Obtiene el caudal actual del punto de abastecimiento de un cliente.
     * @param idClient Identificador del cliente.
     * @return El caudal actual en el punto de abastecimiento del cliente.
     * @pre: Existeix un client identificat amb idClient a la xarxa
     * @post: Retorna el cabal actual al punt d'abastament del client identificat amb idClient7
     */
    public float cabalAbonat(String idClient) {
        float cabal = 0;
        for(Node node : nodos){
            if (node instanceof Terminal){
                Terminal terminal = (Terminal) graph.getNode(node.id()).getAttribute("terminal");
                if(abonar(idClient,terminal)){
                    cabal = cabals.get(node);
                }
            }
        }
        return cabal;
    }
    /**
     * @brief Abre la válvula de un nodo.
     * @param node Nodo cuya válvula se quiere abrir.
     * @throws NoSuchElementException Si el nodo no pertenece a la red.
     * @pre: node pertany a la xarxa
     * @post: L'aixeta del node està oberta
     */
    public void obrirAixeta(Node node) {
        if (graph.getNode(node.id()) == null) {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }
        boolean estabaTancada;
        estabaTancada = !node.aixetaOberta();
        if(estabaTancada){
            operacions.push(new String[]{node.id(),"tancat"});
        }
        else{
            operacions.push(new String[]{node.id(),"obert"});
        }
        node.obrirAixeta();
        actualizarEtiqueta(node);
    }
    /**
     * @brief Cierra la válvula de un nodo.
     * @param node Nodo cuya válvula se quiere cerrar.
     * @throws NoSuchElementException Si el nodo no pertenece a la red.
     * @pre: node pertany a la xarxa
     * @post: L'aixeta del node està tancada
     */
    public void tancarAixeta(Node node) {
        if (graph.getNode(node.id()) == null) {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }
        boolean estabaOberta;
        estabaOberta = node.aixetaOberta();
        if(estabaOberta){
            operacions.push(new String[]{node.id(),"obert"});
        }
        else{
            operacions.push(new String[]{node.id(),"tancat"});
        }
        node.tancarAixeta();
        actualizarEtiqueta(node);
    }
    /**
     * @brief Desfer un nombre de passos en la seqüència d'operacions realitzades d'obrir i tancar vàlvules.
     * @param nPassos Nombre de passos a desfer.
     * @pre: nPassos >= 1
     * @post: S'ha reculat nPassos passos en la seqüència d'operacions realitzades d'obrir i tancar aixetes
     * @throws IllegalArgumentException Si nPassos es negativo o cero.
    */
    public void recular(int nPassos){
        if (nPassos <= 0) {
            throw new IllegalArgumentException("El nombre de passos ha de ser positiu");
        }
        for (int i = 0; i < nPassos; i++) {
            if (operacions.isEmpty()) {
                throw new NoSuchElementException("No hi ha més operacions per desfer");
            }
            String[] operacio = operacions.pop();
            Node node = node(operacio[0]);
            if (operacio[1].equals("obert")) {
                node.obrirAixeta();
            } else {
                node.tancarAixeta();
            }
            actualizarEtiqueta(node);
        }
    }
    /**
     * @brief Estableix el cabal d'un node d'origen.
     * @param nodeOrigen Node d'origen.
     * @param cabal Cabal a establir.
     * @throws NoSuchElementException Si el node d'origen no pertany a la xarxa.
     * @throws IllegalArgumentException Si el cabal és negatiu.
     */
    public void establirCabal(Origen nodeOrigen, float cabal) {
        if (graph.getNode(nodeOrigen.id()) == null) {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }
        if ( cabal < 0) {
            throw new IllegalArgumentException("El cabal es negatiu");
        }
        nodeOrigen.establirCabal(cabal);
    }
    /**
     * @brief Estableix la demanda d'un node terminal.
     * @param nodeTerminal Node terminal.
     * @param demanda Demanda a establir.
     * @throws NoSuchElementException Si el node terminal no pertany a la xarxa.
     * @throws IllegalArgumentException Si la demanda és negativa.
     */
    public void establirDemanda(Terminal nodeTerminal, float demanda) {
        if (graph.getNode(nodeTerminal.id()) == null) {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }
        if ( demanda < 0) {
            throw new IllegalArgumentException("La demanda es negativa");
        }
        nodeTerminal.establirDemandaActual(demanda);
        actualizarEtiqueta(nodeTerminal);
    }
    /**
     * @brief Obté la demanda teòrica d'un node.
     * @param node Node la demanda del qual es vol obtenir.
     * @return La demanda teòrica del node segons la configuració actual de la xarxa.
     * @pre: node pertany a la xarxa
     * @post: Retorna la demanda teorica al node segons la configuració actual de la red
     * @throws NoSuchElementException Si el node no pertany a la xarxa.
     */
    public float demanda(Node node) {
        List<Node> nodesGraph = getNodes(node);
        for(Node node1 : nodesGraph){
            Iterator<Canonada> it = entrades(node1);
            while(it.hasNext()){
                Canonada canonada = it.next();
                canonades.put(canonada, 0f);
            }
        }
        for(Node node1 : nodesGraph){
            if (node1 instanceof Terminal){
                demandas.put(node1, ((Terminal) node1).demandaActual());
            }
            else{
                demandas.put(node1, 0f);
            }
        }
        for(Node nodeNou : nodesGraph){
            if (nodeNou instanceof Terminal){
                float demandaPrimera = demandas.get(nodeNou);
                dfs(nodeNou, demandaPrimera, canonades);
            }
        }
        Float demanda = demandas.get(node);
        return demanda == null ? 0 : demanda;
    }
    /**
     * @brief Realitza una cerca en profunditat (DFS) per distribuir la demanda a través de la xarxa de nodes.
     * @param node Node actual des del qual s'inicia la DFS.
     * @param demandaPrimera La demanda inicial que s'ha de distribuir des del node actual.
     * @param canonades Map que associa cada canonada amb la seva demanda actual.
     * @pre La demandaPrimera és no negativa i node pertany a la xarxa.
     * @post Es distribueix la demanda a través de la xarxa de nodes seguint les capacitats de les canonades.
     */
    public void dfs(Node node, float demandaPrimera, Map<Canonada, Float> canonades) {
        Iterator<Canonada> it = entrades(node);
        int count = 0;
        float total = 0;
        List<Float> capacidades = new ArrayList<>();

        while(it.hasNext()) {
            Canonada canonada = it.next();
            capacidades.add(canonada.capacitat());
            total += canonada.capacitat();
            count++;
        }
        boolean capacidadesIguales = new HashSet<>(capacidades).size() == 1;
        it = entrades(node);
        if (demandaPrimera != 0 && count > 0) {
            while (it.hasNext()) {
                Canonada canonada = it.next();
                Node nodeReceptor = canonada.node1();
                demandas.putIfAbsent(nodeReceptor, 0f);
                if (!canonada.node1().aixetaOberta()) {
                    dfs(nodeReceptor, 0, canonades);
                } else {
                    float demandaNova;
                    if (count > 1 && capacidadesIguales) {
                        demandaNova = demandaPrimera / count;
                    } else {
                        demandaNova = (demandaPrimera / total) * canonada.capacitat();
                    }
                    calculadoraDemanda(nodeReceptor, demandaNova, canonada,canonades);
                }
            }
        }
    }
    /**
     * @brief Calcula i ajusta la demanda d'un node receptor en funció de la capacitat de la canonada.
     * @pre El node receptor i la canonada han d'estar inicialitzats i presents en les estructures de dades.
     * @pre La demandaNova ha de ser un valor positiu.
     * @pre El mapa de canonades ha de contenir la canonada proporcionada.
     * @post La demanda del node receptor es veurà incrementada en funció de la capacitat de la canonada.
     * @post El valor associat a la canonada en el mapa de canonades serà actualitzat.
     * @param nodeReceptor El node receptor al qual es vol assignar la nova demanda.
     * @param demandaNova La quantitat de nova demanda que es vol assignar al node receptor.
     * @param canonada La canonada a través de la qual es vol assignar la demanda.
     * @param canonades Un mapa de les canonades i les seves demandes actuals.
     */
    public void calculadoraDemanda(Node nodeReceptor, float demandaNova, Canonada canonada,Map<Canonada, Float> canonades){
        float capacidad = canonada.capacitat();
        float demandaAjustada = Math.min(demandaNova, capacidad);
        float demandaActual = demandas.get(nodeReceptor);
        float demandaFinal = demandaActual + demandaAjustada;
        demandas.put(nodeReceptor, demandaFinal);
        float demandaCanonada = canonades.get(canonada);
        float demandaCanonadaNova = demandaCanonada + demandaAjustada;
        if (capacidad == demandaCanonada) {
            demandas.put(nodeReceptor, demandaActual);
            demandaAjustada = 0;
        } else {
            if (capacidad == demandaCanonadaNova) {
                canonades.put(canonada, demandaCanonadaNova);
            } else if (capacidad < demandaCanonadaNova) {
                canonades.put(canonada, demandaAjustada + demandaCanonada);
                demandas.put(nodeReceptor, (demandaAjustada - demandaCanonada) + demandaActual);
                demandaAjustada = 0;
            } else {
                canonades.put(canonada, demandaCanonadaNova);
            }
        }
        dfs(nodeReceptor, demandaAjustada, canonades);
    }
    /**
     * @brief Calcula la demanda teòrica al node segons la configuració actual de la xarxa.
     * @param node Node del qual es vol obtenir la demanda teòrica.
     * @return La demanda teòrica al node.
     * @throws NoSuchElementException Si el node no pertany a la xarxa.
     * @pre node pertany a la xarxa.
     * @post Retorna la demanda teòrica al node segons la configuració actual de la xarxa.
     */
    public float cabal(Node node){
        //Pre: node pertany a la xarxa
        //Post: Retorna la demanda teòrica al node segons la configuració actual de la xarxa
        //Excepcions: NoSuchElementException si node no pertany a la xarxa
        if (graph.getNode(node.id()) == null) {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }
        List<Node> nodeGraph = getNodes(node);
        for(Node node1 : nodeGraph){
            Iterator<Canonada> it = entrades(node1);
            while(it.hasNext()){
                Canonada canonada = it.next();
                canonadaCabals.put(canonada, 0f);
            }
        }
        for(Node node1 : nodeGraph){
            if (node1 instanceof Origen){
                float cabalAjustada = Math.min(((Origen) node1).cabal(), demanda(node1));
                cabals.put(node1, cabalAjustada);
            }
            else{
                cabals.put(node1, 0f);
            }
        }
        for(Node nodeNou : nodeGraph){
            if(nodeNou instanceof Origen){
                float cabalOrigen = cabals.get(nodeNou);
                dfsCabals(nodeNou,cabalOrigen);
            }
        }
        return cabals.get(node);
    }
    /**
     * @brief Realitza una cerca en profunditat (DFS) per distribuir el cabal a través de la xarxa de nodes.
     * @param node Node actual des del qual s'inicia la DFS.
     * @param cabalOrigen El cabal inicial que s'ha de distribuir des del node actual.
     * @pre cabalOrigen és no negatiu i node pertany a la xarxa.
     * @post Es distribueix el cabal a través de la xarxa de nodes seguint les capacitats de les canonades.
     */
    public void dfsCabals(Node node, float cabalOrigen){
        Iterator <Canonada> it = sortides(node);
        if (cabalOrigen != 0){
            while(it.hasNext()) {
                Canonada canonada = it.next();
                Node nodeReceptor = canonada.node2();
                cabals.putIfAbsent(nodeReceptor, 0f);
                if (!canonada.node2().aixetaOberta()) {
                    dfs(nodeReceptor, 0, canonadaCabals);
                } else {
                    float diferencia = cabalOrigen / demanda(node);
                    float nouCabalCanonada = diferencia * canonades.get(canonada);
                    float cabalCanonada = canonadaCabals.get(canonada);
                    canonadaCabals.put(canonada, nouCabalCanonada + cabalCanonada);
                    float cabalActual = cabals.get(nodeReceptor);
                    cabals.put(nodeReceptor, cabalActual + nouCabalCanonada);
                    dfsCabals(nodeReceptor, nouCabalCanonada);
                }
            }
        }
    }
    public void dibuixar(Origen nodeOrigen) {
        //Pre: ---        //Post: Dibuixa la xarxa de distribució d'aigua
        demanda(nodeOrigen);
        cabal(nodeOrigen);
        for (Map.Entry<Canonada, Float> entry : canonadaCabals.entrySet()) {
            Canonada canonada = entry.getKey();
            float capacitat = canonada.capacitat();
            Float value = entry.getValue();
            Edge edge = graph.getEdge(canonada.node1().id()  + "-" + canonada.node2().id());
            String etiqueta = String.format(" %.5f / %.5f", value, capacitat);
            edge.setAttribute("ui.label", etiqueta);
        }
        graph.display();
    }
    /**
     * @brief Obté la llista de nodes visitats a partir d'un node donat.
     * @param node Node des del qual es vol iniciar la cerca.
     * @return Llista de nodes visitats ordenats.
     * @pre node pertany a la xarxa.
     * @post Retorna una llista de nodes visitats ordenats segons el tipus de node.
     */
    public List<Node> getNodes(Node node) {
        Set<Node> visited = new HashSet<>();
        if (node != null) {
            Node node1 = null;
            if (!(node instanceof Origen)) {
                if (node instanceof Terminal) {
                    if(graph.getNode(node.id()) != null){
                        node1 = (Node) graph.getNode(node.id()).getAttribute("terminal");
                    }
                } else if (node instanceof Connexio) {
                    if(graph.getNode(node.id()) != null){
                        node1 = (Node) graph.getNode(node.id()).getAttribute("connexio");
                    }
                }
            } else {
                if(graph.getNode(node.id()) != null){
                    node1 = (Node) graph.getNode(node.id()).getAttribute("origen");
                }
            }
            dfs(node1, visited);
        }
        List<Node> visitatsOrdenats = new ArrayList<>(visited);
        visitatsOrdenats.sort(this::compare);
        return visitatsOrdenats;
    }
    /**
     * @brief Realitza una cerca en profunditat (DFS) per visitar tots els nodes connectats.
     * @param actual Node actual en la cerca.
     * @param visitados Conjunt de nodes ja visitats.
     * @pre actual és un node vàlid i visitados és un conjunt buit o parcialment complet.
     * @post Afegeix els nodes visitats al conjunt visitados.
     */
    private void dfs(Node actual, Set<Node> visitados) {
        visitados.add(actual);
        if (actual != null) {
            Iterator<Edge> salientes = graph.getNode(actual.id()).leavingEdges().iterator();
            while (salientes.hasNext()) {
                Edge edge = salientes.next();
                String nom = edge.getOpposite(graph.getNode(actual.id())).getId();
                Node destino = getNodeNou(nom);
                if (destino != null && !visitados.contains(destino)) {
                    dfs(destino, visitados);
                }
            }
        }
        if(actual != null) {
            Iterator<Edge> entrantes = graph.getNode(actual.id()).enteringEdges().iterator();
            while (entrantes.hasNext()) {
                Edge edge = entrantes.next();
                String nom = edge.getOpposite(graph.getNode(actual.id())).getId();
                Node origen = getNodeNou(nom);
                if (origen != null && !visitados.contains(origen)) {
                    dfs(origen, visitados);
                }
            }
        }
    }
    /**
     * @brief Obté un node nou basat en el seu nom.
     * @param nom Nom del node a obtenir.
     * @return El node corresponent al nom, o null si no existeix.
     * @pre nom és un identificador vàlid per a un node.
     * @post Retorna el node corresponent al nom si existeix, o null en cas contrari.
     */
    private Node getNodeNou(String nom) {
        if (graph.getNode(nom).hasAttribute("origen")) {
            return (Node) graph.getNode(nom).getAttribute("origen");
        } else if (graph.getNode(nom).hasAttribute("terminal")) {
            return (Node) graph.getNode(nom).getAttribute("terminal");
        } else if (graph.getNode(nom).hasAttribute("connexio")) {
            return (Node) graph.getNode(nom).getAttribute("connexio");
        }
        return null;
    }
    /**
     * @brief Ordena els nodes segons el seu tipus.
     * @param node Node a ordenar.
     * @return Un enter representant la prioritat de l'ordre (0 per Terminal, 1 per Connexio, 2 per Origen, 4 per altres).
     * @pre node és un node vàlid.
     * @post Retorna un valor enter per ordenar els nodes segons el seu tipus.
     */
    private int ordenarPerTipus(Node node) {
        if (node instanceof Terminal) {
            return 0;
        } else if (node instanceof Connexio) {
            return 1;
        } else if (node instanceof Origen) {
            return 2;
        }
        return 4;
    }
    /**
     * @brief Actualitza l'etiqueta d'un node amb el seu estat actual.
     * @param node Node del qual s'ha d'actualitzar l'etiqueta.
     * @pre node pertany a la xarxa.
     * @post L'etiqueta del node reflecteix l'estat actual de la seva aixeta i la seva demanda (si és un terminal).
     */
    public void actualizarEtiqueta(Node node) {
        String estadoAixeta = node.aixetaOberta() ? "Oberta" : "Tancada";
        String demandaInfo = "";
        if (node instanceof Terminal terminal) {
            demandaInfo = String.format("  %.2f / %.2f",terminal.demandaActual() ,terminal.demanda());
        }
        String label = node.id() + " (" + estadoAixeta + ")"+ demandaInfo;
        graph.getNode(node.id()).setAttribute("ui.label", label);
    }

    /**
     * @brief Prova el flux entre dos nodes i ajusta la xarxa en conseqüència.
     * @param node1 Node inicial per a la prova de flux.
     * @param nou Nou node a afegir i provar.
     * @pre node1 pertany a la xarxa i nou és un node vàlid.
     * @post La xarxa s'ajusta per a incloure el nou node i redistribuir el flux.
     */
    public void provaFlux(Node node1, Node nou){
        List<Node> nodesEliminar = new ArrayList<>();
        Map<Node, Float> mapCapacitats = new HashMap<>();

        if(nou instanceof Origen origen) {
            if (graph.getNode(nou.id()) == null) {
                afegir(origen);
            }
            for (Node node : getNodes(node1)) {
                if (node instanceof Origen) {
                    Iterator<Canonada> it = sortides(node);
                    while (it.hasNext()) {
                        Canonada canonada = it.next();
                        mapCapacitats.put(canonada.node2(), canonada.capacitat());
                    }
                    nodesEliminar.add(node);
                }
            }
            for (Map.Entry<Node, Float> entry : mapCapacitats.entrySet()) {
                crearCanonadesFlux(nou, entry.getKey(), entry.getValue());
            }
        }
        else if (nou instanceof Terminal terminal){
            if (graph.getNode(nou.id()) == null) {
                afegir(terminal);
            }
            for (Node node : getNodes(node1)) {
                if (node instanceof Terminal) {
                    Iterator<Canonada> it = entrades(node);
                    while (it.hasNext()) {
                        Canonada canonada = it.next();
                        mapCapacitats.put(canonada.node1(), canonada.capacitat());
                    }
                    nodesEliminar.add(node);
                }
            }
            for (Map.Entry<Node, Float> entry : mapCapacitats.entrySet()) {
                crearCanonadesFlux(entry.getKey(), nou, entry.getValue()); // Invertim l'ordre dels nodes
            }
        }

        for (Node node : nodesEliminar) {
            graph.removeNode(node.id());
        }
    }
    /**
     * @brief Crea una canonada per a dirigir el flux entre dos nodes.
     * @param node1 Node d'origen de la canonada.
     * @param node2 Node de destí de la canonada.
     * @param c Capacitat de la canonada.
     * @pre node1 i node2 pertanyen a la xarxa i c és no negativa.
     * @post La canonada es crea i s'afegeix a la xarxa amb l'estil adequat.
     */
    public void crearCanonadesFlux(Node node1, Node node2, float c){
        Canonada canonada = new Canonada(node1, node2, c);
        graph.addEdge(node1.id() + "-" + node2.id(), node1.id(), node2.id(), true).setAttribute("Canonada", canonada);
        graph.getEdge(node1.id() + "-" + node2.id()).setAttribute("ui.style", "fill-color: blue; size: 3px;");
    }
    /**
     * @brief Compara dos nodes per ordre de tipus.
     * @param n1 Primer node a comparar.
     * @param n2 Segon node a comparar.
     * @return Un valor negatiu, zero o positiu si el primer node és menor, igual o més gran que el segon node.
     * @pre n1 i n2 són nodes vàlids.
     * @post Retorna el resultat de la comparació segons el tipus de node.
     */
    private int compare(Node n1, Node n2) {
        return Integer.compare(ordenarPerTipus(n1), ordenarPerTipus(n2));
    }
    /**
     * @brief Canvia l'etiqueta de les canonades amb el flux màxim.
     * @param canonadas Map que conté els nodes i els seus nodes de destí amb els fluxos corresponents.
     * @pre canonadas és un mapa vàlid de nodes i fluxos.
     * @post Canvia l'etiqueta de les canonades al grafo per indicar el flux màxim.
     */
    public void canviarEtiquetaFluxMaxim(Map<Node, Map<Node, Float>> canonadas) {
        for (Map.Entry<Node, Map<Node, Float>> entry : canonadas.entrySet()) {
            Node nodeOrigen = entry.getKey();
            Map<Node, Float> destins = entry.getValue();

            for (Map.Entry<Node, Float> subEntry : destins.entrySet()) {
                Node nodeDesti = subEntry.getKey();
                Float flux = subEntry.getValue();

                // Busca l'aresta (canonada) en el grafo
                Edge edge = graph.getEdge(nodeOrigen.id() + "-" + nodeDesti.id());
                if (edge == null) {
                    edge = graph.getEdge(nodeDesti.id() + "-" + nodeOrigen.id());
                }

                // Si es troba l'aresta, actualitza l'etiqueta
                if (edge != null) {
                    Canonada canonada = (Canonada) edge.getAttribute("Canonada");
                    if (canonada != null) {
                        float capacitat = canonada.capacitat();
                        String etiqueta = String.format("Flux: %.2f / %.2f", flux, capacitat);
                        edge.setAttribute("ui.label", etiqueta);
                    }
                }
            }
        }
        graph.display();
    }
}
