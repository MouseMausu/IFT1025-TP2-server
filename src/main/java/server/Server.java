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

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Construction du squelette du serveur i.e initialisation de son socket, d'une trace des gestionnaires d'événements
     * et ajout d'un premier gestionnaire correspondant aux requêtes d'un client quelconque (l'événement).
     * @param port (port du serveur)
     * @throws IOException
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajout d'un gestionnaire d'événements à notre trace. Agit en tant que référence pour aider le serveur dans le
     * processus du choix traitement.
     * @param h (gestionnaire)
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Méthode utilisant une référence qui permet l'automatisation du choix de traitement à effectuer
     * selon la requête du client.
     * @param cmd (commande)
     * @param arg (argument)
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Lancement du serveur i.e accepter et confirmer la connection du client au serveur lorsque ceci a lieu.
     * Assurer que ses requêtes soient bien reçues et traitées par le serveur (sinon exception). Le serveur atteint
     * son terminus suite à l'envoie de sa réponse au client, basée sur sa requête.
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
     *
     * @throws IOException
     * @throws ClassNotFoundException
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
     * Déconstruit la chaine de caractère générée par les requêtes du client, afin d'en faire ressortir les mots clés
     * utile au protocole du traitement de tout événement. C'est ce qui permet de traduire les requêtes du client
     * au serveur.
     * @param line (chaine de caractère générée par les requêtes du client)
     * @return (chaine traduite en mots clés conformes au protocole)
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Terminus et donc fin du traitement. Fermeture des portes entrées/sorties signifiant
     * la déconnection du client au serveur.
     * @throws IOException
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Traitement de l'événement et donc de la requête du client, soit l'inscription ou le chargement des cours
     * qui sont disponibles durant la session précisée dans l'argument.
     * @param cmd (inscription/chargement)
     * @param arg (session en disponibilité, soit celle d'automne, d'été ou d'hiver)
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
            FileReader fileReader = new FileReader(new File("").getAbsolutePath() + "/src/main/java/server/data/cours.txt");
            BufferedReader reader = new BufferedReader(fileReader);

            String infoCours;
            ArrayList<Course> listeCours = new ArrayList<>();
            while ((infoCours = reader.readLine()) != null) {
                String[] infoCoursSep = infoCours.split("\t");
                if (infoCours.contains(arg)) {
                    listeCours.add(new Course(infoCoursSep[1], infoCoursSep[0], infoCoursSep[2]));
                }
            }
            reader.close();
            objectOutputStream.writeObject(listeCours);

        }catch (IOException e){
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

            FileWriter fileWriter = new FileWriter(new File("").getAbsolutePath() + "/src/main/java/server/data/inscription.txt");
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.append(ficheIns.getCourse().getSession() + ficheIns.getCourse().getCode() + ficheIns.getMatricule()
                    + ficheIns.getPrenom() + ficheIns.getNom() + ficheIns.getEmail());

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

