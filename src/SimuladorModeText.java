//Jhon Alejandro Parraga Mogollon
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.io.*;

/**
 * @file SimuladorModeText.java
 * @brief Programa principal on agafarem la informació del fitxer d'entrada i farem la simulació de la xarxa.
 */
/**
 * @class SimuladorModeText
 * @brief Aquí farem la simulació dels fitxers que be water entri per tal de poder fer el graph
 */
public class SimuladorModeText {
    private final Xarxa xarxa = new Xarxa();

    public void simular(String fitxerEntrada, String fitxerSortida) {
        Set<String> commandStarters = new HashSet<>(Arrays.asList("origen", "connexio", "terminal", "connectar", "cicles", "arbre", "cabal minim", "tancar", "obrir", "backtrack", "situacio", "proximitat", "demanda", "cabal", "demandas", "cabales", "exces", "abonar", "max-flow", "cabal abonat", "dibuixar"));

        try (BufferedReader reader = new BufferedReader(new FileReader(fitxerEntrada));
             PrintStream out = new PrintStream(new FileOutputStream(fitxerSortida))) {
            System.setOut(out); //Redirigir al fitxer de sortida

            String line;
            List<String> commandLines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    commandLines.add(line);
                    if (commandStarters.contains(line.split(" ")[0])) { // Asume que el comando es siempre la primera palabra
                        if (commandLines.size() > 1) {
                            procesarLinea(commandLines.subList(0, commandLines.size() - 1));
                            commandLines.clear();
                            commandLines.add(line);
                        }
                    }
                }
            }
            if (!commandLines.isEmpty()) {
                procesarLinea(commandLines); // Procesar el último comando
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private void procesarLinea(List<String> lines) {
        String command = lines.get(0);
        switch (command) {
            case "terminal":
                if (lines.size() < 4) {
                    System.out.println("Formato incorrecto para 'terminal'");
                    break;
                }
                afegirTerminal(lines.get(1), lines.get(2), Float.parseFloat(lines.get(3)));
                break;
            case "origen":
                afegirOrigen(lines.get(1), lines.get(2));
                break;
            case "connexio":
                afegirConnexio(lines.get(1), lines.get(2));
                break;
            case "connectar":
                connectarNodes(lines.get(1), lines.get(2), Float.parseFloat(lines.get(3)));
                break;
            case "cicles":
                comprovarCicles(lines.get(1));
                break;
            case "arbre":
                comprovarArbre(lines.get(1));
                break;
            case "cabal minim":
                float percentatge = Float.parseFloat(lines.get(2).replace("%", ""));
                comprovarCabalMinim(lines.get(1), percentatge);
                break;
            case "tancar":
                tancarAixeta(lines.get(1));
                break;
            case "obrir":
                obrirAixeta(lines.get(1));
                break;
            case "backtrack":
                backtrack(Integer.parseInt(lines.get(1)));
                break;
            case "situacio":
                aixetesTancar(lines);
                break;
            case "demandas":
                ordeDemanda(lines.get(1));
                break;
            case "cabales":
                ordeCabal(lines.get(1));
                break;
            case "proximitat":
                comprovarProximitat(lines);
                break;
            case "demanda":
                demanda(lines.get(1), Float.parseFloat(lines.get(2)));
                break;
            case "cabal":
                cabals(lines.get(1), Float.parseFloat(lines.get(2)));
                break;
            case "exces":
                excesCabal(lines);
                break;
            case "abonar":
                abonarClient(lines.get(1), lines.get(2));
                break;
            case "max-flow":
                fluxMaxim(lines.get(1));
                break;
            case "cabal abonat":
                System.out.println("cabal abonat");
                float cabal = xarxa.cabalAbonat(lines.get(1));
                System.out.println(cabal);
                break;
            case "dibuixar":
                dibuixarXarxa(lines.get(1));
                break;
            default:
                System.out.println("Comanda desconeguda: " + command);
        }
    }
    /**
     * @brief Parsea una cadena de texto para convertirla en coordenades.
     * @param coordenadaTexto Cadena de texto con les coordenades en format "graus:minuts:segonsDir,graus:minuts:segonsDir".
     * @return Un objecte Coordenades amb les coordenades parseades, o null si hi ha un error en el parseig.
     * @pre ---
     * @post Retorna un objecte Coordenades amb les coordenades parseades o null si hi ha un error.
     */
    private Coordenades parsearCoordenades(String coordenadaTexto) {
        try {
            String[] latLon = coordenadaTexto.split(",");
            String[] lat = latLon[0].split(":");
            String[] lon = latLon[1].split(":");

            int grausLat = Integer.parseInt(lat[0]);
            int minutsLat = Integer.parseInt(lat[1]);
            float segonsLat = Float.parseFloat(lat[2].substring(0, lat[2].length() - 1));
            char dirLat = lat[2].charAt(lat[2].length() - 1);

            int grausLon = Integer.parseInt(lon[0]);
            int minutsLon = Integer.parseInt(lon[1]);
            float segonsLon = Float.parseFloat(lon[2].substring(0, lon[2].length() - 1));
            char dirLon = lon[2].charAt(lon[2].length() - 1);

            return new Coordenades(grausLat, minutsLat, segonsLat, dirLat, grausLon, minutsLon, segonsLon, dirLon);
        } catch (Exception e) {
            System.out.println("Error parsing coordinates: " + coordenadaTexto);
            return null;
        }
    }
    /**
     * @brief Afegeix un terminal a la xarxa.
     * @param id Identificador del terminal.
     * @param coordenadesTexto Coordenades del terminal en format de text.
     * @param demanda Demanda del terminal.
     * @pre ---
     * @post Afegeix un terminal a la xarxa amb les coordenades i demanda especificades.
     */
    private void afegirTerminal(String id, String coordenadesTexto, float demanda) {
        Coordenades coordenades = parsearCoordenades(coordenadesTexto);
        if (coordenades != null) {
            Terminal terminal = new Terminal(id, coordenades, demanda);
            xarxa.afegir(terminal);
        }
    }
    /**
     * @brief Afegeix un origen a la xarxa.
     * @param id Identificador de l'origen.
     * @param coordenadesTexto Coordenades de l'origen en format de text.
     * @pre ---
     * @post Afegeix un origen a la xarxa amb les coordenades especificades.
     */
    private void afegirOrigen(String id, String coordenadesTexto) {
        Coordenades coordenades = parsearCoordenades(coordenadesTexto);
        if (coordenades != null) {
            Origen origen = new Origen(id, coordenades);
            xarxa.afegir(origen);
        }
    }
    /**
     * @brief Afegeix una connexió a la xarxa.
     * @param id Identificador de la connexió.
     * @param coordenadesTexto Coordenades de la connexió en format de text.
     * @pre ---
     * @post Afegeix una connexió a la xarxa amb les coordenades especificades.
     */
    private void afegirConnexio(String id, String coordenadesTexto) {
        Coordenades coordenades = parsearCoordenades(coordenadesTexto);
        if (coordenades != null) {
            Connexio connexio = new Connexio(id, coordenades);
            xarxa.afegir(connexio);
        }
    }
    /**
     * @brief Connecta dos nodes amb una canonada.
     * @param id1 Identificador del primer node.
     * @param id2 Identificador del segon node.
     * @param capacidad Capacitat de la canonada.
     * @pre ---
     * @post Connecta els dos nodes especificats amb una canonada de la capacitat indicada.
     */
    private void connectarNodes(String id1, String id2, float capacidad) {
        Node node1 = xarxa.node(id1);
        Node node2 = xarxa.node(id2);
        if (node1 != null && node2 != null) {
            xarxa.connectarAmbCanonada(node1, node2, capacidad);
        } else {
            System.out.println("Uno de los nodos no existe: " + id1 + " or " + id2);
        }
    }
    /**
     * @brief Comprova si un node té cicles.
     * @param id Identificador del node.
     * @pre ---
     * @post Comprova si el node especificat té cicles i imprimeix el resultat.
     */
    private void comprovarCicles(String id) {
        Node node = xarxa.node(id);
        Origen nodeOrigen = node instanceof Origen ? (Origen) node : null;
        if (nodeOrigen != null) {
            if (GestorXarxes.teCicles(xarxa,nodeOrigen)) {
                System.out.println(id + " té cicles");
            } else {
                System.out.println(id +" no té cicles");
            }
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
    /**
     * @brief Comprova si un node és un arbre.
     * @param id Identificador del node.
     * @pre ---
     * @post Comprova si el node especificat és un arbre i imprimeix el resultat.
     */
    private void comprovarArbre(String id) {
        Node node = xarxa.node(id);
        Origen nodeOrigen = node instanceof Origen ? (Origen) node : null;
        if (nodeOrigen != null) {
            if (GestorXarxes.esArbre(xarxa,nodeOrigen)) {
                System.out.println(id + " és un arbre");
            } else {
                System.out.println(id + " no és un arbre");
            }
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
    /**
     * @brief Comprova el cabal mínim d'un node.
     * @param id Identificador del node.
     * @param percentatge Percentatge de cabal mínim.
     * @pre ---
     * @post Comprova el cabal mínim del node especificat i imprimeix el resultat.
     */
    private void comprovarCabalMinim(String id, float percentatge) {
        Node node = xarxa.node(id);
        Origen nodeOrigen = node instanceof Origen ? (Origen) node : null;
        if (nodeOrigen != null) {
            float cabalMinim = GestorXarxes.cabalMinim(xarxa, nodeOrigen,percentatge);
            System.out.println("Cabal mínim");
            System.out.println(cabalMinim);
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
    /**
     * @brief Tanca l'aixeta d'un node.
     * @param id Identificador del node.
     * @pre ---
     * @post Tanca l'aixeta del node especificat.
     */
    private void tancarAixeta(String id) {
        Node node = xarxa.node(id);
        if (node != null) {
            xarxa.tancarAixeta(node);
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
    /**
     * @brief Obre l'aixeta d'un node.
     * @param id Identificador del node.
     * @pre ---
     * @post Obre l'aixeta del node especificat.
     */
    private void obrirAixeta(String id) {
        Node node = xarxa.node(id);
        if (node != null) {
            xarxa.obrirAixeta(node);
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
    /**
     * @brief Retrocedeix un cert nombre de passos en les operacions realitzades.
     * @param n Nombre de passos a retrocedir.
     * @pre n és un nombre positiu.
     * @post Retrocedeix n passos en les operacions realitzades.
     */
    private void backtrack(int n) {
        xarxa.recular(n);
    }
    /**
     * @brief Determina les aixetes que s'han de tancar per a complir amb les condicions donades.
     * @param lines Llista de línies amb els identificadors dels terminals i els seus estats d'aigua.
     * @pre ---
     * @post Imprimeix els nodes on s'han de tancar les aixetes.
     */
    private void aixetesTancar(List<String> lines) {
        HashMap<Terminal, Boolean> aiguaArriba = new HashMap<>();
        for (int i = 1; i < lines.size(); i++){
            String[] parts = lines.get(i).split(" ");
            String terminalId = parts[0];
            boolean estat = parts[1].equals("SI");
            Terminal terminal = (Terminal) xarxa.node(terminalId);
            aiguaArriba.put(terminal, estat);
        }
        Set<Node> nodes = GestorXarxes.aixetesTancar(xarxa, aiguaArriba);
        System.out.println("tancar");
        for (Node node : nodes) {
            System.out.println(node.id());
        }
    }
    /**
     * @brief Retorna la demanda d'un client.
     * @param IdClient Identificador del client.
     * @pre ---
     * @post Imprimeix la demanda del client especificat.
     */
    private void ordeDemanda(String IdClient){
        Node node = xarxa.node(IdClient);
        float demanda = xarxa.demanda(node);
        System.out.println(demanda);
    }
    /**
     * @brief Retorna el cabal d'un origen.
     * @param IdOrigen Identificador de l'origen.
     * @pre ---
     * @post Imprimeix el cabal de l'origen especificat.
     */
    private void ordeCabal(String IdOrigen){
        Node node = xarxa.node(IdOrigen);
        float cabal = xarxa.cabal(node);
        System.out.println(cabal);
    }
    /**
     * @brief Comprova la proximitat de nodes a unes coordenades especificades.
     * @param lines Llista de línies amb les coordenades i els identificadors dels nodes.
     * @pre ---
     * @post Imprimeix els nodes ordenats per proximitat a les coordenades especificades.
     */
    private void comprovarProximitat(List<String> lines){
        Coordenades coordenades = parsearCoordenades(lines.get(1));
        Set<Node> nodes = new HashSet<>();
        for (int i = 2; i < lines.size(); i++){
            Node node = xarxa.node(lines.get(i));
            if (node != null){
                nodes.add(node);
            }
        }
        List<Node> resultatNodes = GestorXarxes.nodesOrdenats(coordenades, nodes);
        System.out.println("proximitat");
        for (Node node : resultatNodes){
            System.out.println(node.id());
        }
    }
    /**
     * @brief Estableix la demanda d'un node terminal.
     * @param id Identificador del node terminal.
     * @param demanda Demanda a establir.
     * @pre ---
     * @post Estableix la demanda del node terminal especificat.
     */
    private void demanda(String id, float demanda){
        Node node = xarxa.node(id);
        Terminal nodeTerminal = node instanceof Terminal ? (Terminal) node : null;
        if (nodeTerminal != null){
            xarxa.establirDemanda(nodeTerminal, demanda);
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
    /**
     * @brief Estableix el cabal d'un node origen.
     * @param id Identificador del node origen.
     * @param cabal Cabal a establir.
     * @pre ---
     * @post Estableix el cabal del node origen especificat.
     */
    private void cabals (String id, float cabal){
        Node node = xarxa.node(id);
        Origen nodeOrigen = node instanceof Origen ? (Origen) node : null;
        if (nodeOrigen != null){
            xarxa.establirCabal(nodeOrigen, cabal);
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
    /**
     * @brief Determina les canonades amb excés de cabal.
     * @param lines Llista d'identificadors de les canonades.
     * @pre ---
     * @post Imprimeix les canonades amb excés de cabal.
     */
    private void excesCabal(List<String> lines){
        Set<Canonada> canonadas = new HashSet<>();
        for (int i = 1; i < lines.size(); i++){
            Canonada canonada = xarxa.retornarCapacitat(lines.get(i));
            if (canonada != null) {
                canonadas.add(canonada);
            }
        }
        Set<Canonada> excesoCanonadas = GestorXarxes.excesCabal(xarxa, canonadas);
        System.out.println("exces cabal");
        for (Canonada canonada : excesoCanonadas){
            System.out.println(canonada.node1().id() + "-" + canonada.node2().id());
        }
    }
    /**
     * @brief Abona un client a un terminal.
     * @param id Identificador del client.
     * @param idTerminal Identificador del terminal.
     * @pre ---
     * @post Abona el client especificat al terminal especificat.
     */
    private void abonarClient(String id,String idTerminal){
        Node node = xarxa.node(idTerminal);
        Terminal nodeTerminal = node instanceof Terminal ? (Terminal) node : null;
        if (nodeTerminal != null){
            xarxa.abonar(id,nodeTerminal);
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
    /**
     * @brief Calcula el flux màxim d'un node origen.
     * @param id Identificador del node origen.
     * @pre ---
     * @post Calcula el flux màxim del node origen especificat i actualitza el graf en conseqüència.
     */
    private void fluxMaxim(String id){
        Node node = xarxa.node(id);
        GestorXarxes.fluxMaxim(xarxa,(Origen)node);
    }
    private void dibuixarXarxa(String id){
        Node node = xarxa.node(id);
        Origen nodeOrigen = node instanceof Origen ? (Origen) node : null;
        if (node != null){
            xarxa.dibuixar(nodeOrigen);
        } else {
            System.out.println("El node no existeix: " + id);
        }
    }
}