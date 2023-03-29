package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    /**
     * Commande d'inscription.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Commande de chargement.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Construction du squelette du serveur i.e initialisation de son socket, d'une référence de gestionnaires d'événements
     * et ajout d'un premier gestionnaire pouvant traiter chaque requête d'un client (événement).
     * @param port Port du serveur.
     * @throws IOException Traitement d'erreur en lien avec le socket du serveur, soit les exceptions en lien avec
     * l'entrée et la sortie d'un client dans le serveur.
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajoute, à notre référence, un gestionnaire d'événements spécifiques. Celle-ci aide le serveur
     * à mieux procéder les requêtes en ayant une diversité de gestionnaires d'événement.
     * @param h Gestionnaire spécifique
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Méthode utilisant une référence (tableau dynamique) qui permet l'automatisation du choix de traitement à effectuer
     * selon la requête du client.
     * @param cmd Commande et donc la requête du client
     * @param arg Argument/Précision.
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Lancement du serveur i.e accepter et confirmer la connection du client au serveur.
     * Assure que le serveur aurait la possibilité de recevoir une requête et par la suite envoyer une réponse au client.
     * Le serveur atteint son terminus suite à l'envoie de sa réponse au client, basée sur sa requête.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Écoute la requête envoyée par le client et par la suite décortique celle-ci de sorte que le serveur puisse
     * passer à l'automatisation du type de gestionnaire.
     * @throws IOException Traitement d'erreur sur l'absence d'une requête.
     * @throws ClassNotFoundException Traitement d'erreur sur la possibilité au SERVEUR de lire de la requête du client.
     * Le serveur connait-il la classe de la requête ?
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Déconstruit la chaine de caractère décrivant la requête du client, afin d'en faire ressortir les mots clés
     * utile au protocole de gestionnaire d'événement.
     * @param line Requête du client sous forme de chaine de caractère
     * @return Mots clés conformes au protocole
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Terminus, donc fin du traitement et déconnection du client au serveur.
     * @throws IOException Traitement d'erreur au cas où le serveur ferme ses entrées et sorties non vides (aucun flush).
     * Même traitement avec le client (fermeture d'une connection perdue).
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Centre de traitement pour une requête d'inscription ou de chargement de cours disponibles durant la session
     * précisée en argument.
     * @param cmd Inscription/chargement
     * @param arg Session en disponibilité, soit celle d'automne, d'été ou d'hiver
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transformer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        try {
            FileReader fileReader = new FileReader( "src/main/java/server/data/cours.txt");
            BufferedReader reader = new BufferedReader(fileReader);

            String stringInfoCours;
            ArrayList<Course> listeCours = new ArrayList<>();

            while ((stringInfoCours = reader.readLine()) != null) {
                if (stringInfoCours.contains(arg)) {
                    String[] infoCours = stringInfoCours.split("\t");
                    listeCours.add(new Course(infoCours[1], infoCours[0], infoCours[2]));
                }
            }
            reader.close();
            objectOutputStream.writeObject(listeCours);
            objectOutputStream.flush();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        try {
            RegistrationForm ficheIns = (RegistrationForm) objectInputStream.readObject();

            FileReader fileReader = new FileReader("src/main/java/server/data/inscription.txt");
            BufferedReader reader = new BufferedReader(fileReader);

            String sauvegardeDonneeIns = "";
            String lecture;
            while ((lecture = reader.readLine()) != null) {
                sauvegardeDonneeIns += lecture + "\n";
            }
            reader.close();

            FileWriter fileWriter = new FileWriter("src/main/java/server/data/inscription.txt");
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.append(sauvegardeDonneeIns + ficheIns.getCourse().getSession() + "\t" + ficheIns.getCourse().getCode() + "\t" + ficheIns.getMatricule() + "\t"
                    + ficheIns.getPrenom() + "\t" + ficheIns.getNom() + "\t" + ficheIns.getEmail() + "\n");

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

