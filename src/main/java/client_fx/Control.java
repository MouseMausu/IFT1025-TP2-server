package client_fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.models.Course;
import server.models.RegistrationForm;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Classe de controle, un intermediaire entre la classe de vue Client_FX, les modèles Course et Registration ainsi
 * qu'un intermédiaire de communication entre la classe Server.
 */
public class Control {
    private Socket client;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String session;
    private ArrayList<Course> listeCoursRecu;
    private Course coursChoisi;
    private RegistrationForm formulaireInscription;

    /**
     * Controle de chargement qui réinitialise le cours choisi par l'utilisateur chaque fois que ce dernier appuie sur
     * le bouton charger. Envoie une demande de contenue au serveur pour la session choisie.
     * @return Envoie à la classe reponsable pour la vue du GUI la liste des cours demandées.
     * @throws IOException Traitement d'erreur pour la lecture du fichier txt et la prise du contenu demandé.
     * @throws ClassNotFoundException Traitement d'erreur pour la lisibilité de l'objet reçu. À-t-on la classe permettant
     * la lecture de l'objet reçu?
     * @throws NullPointerException (Lancée conditionnellement) Traitement d'erreur sur l'oublie d'un choix de session d'inscription.
     */
    public ObservableList<Course> charger() throws IOException, ClassNotFoundException, NullPointerException {
        coursChoisi = null;
        if (session == null){
            deconnecter();
            throw new NullPointerException("Aucune session en lecture");
        } else {
            objectOutputStream.writeObject("CHARGER" + " " + session);
            objectOutputStream.flush();
            listeCoursRecu = (ArrayList<Course>) objectInputStream.readObject();

            // Prise du même site web, voir classe Client_FX ligne 67.
            ObservableList<Course> listeCoursVu = FXCollections.observableArrayList();
            for (Course cours: listeCoursRecu) {
                listeCoursVu.add(cours);
            }
            return listeCoursVu;
        }
    }

    /**
     * Décortique infoSelection pour deduire le code ainsi que le nom du cours choisi par le clic de souris.
     * Trouve et enregistre l'information complete du cours choisi.
     * @param infoSelection Informations complexe sur l'événement clic de souris par l'usagée.
     */
    public void selectionnerCours(String infoSelection) {
        String infoInconnu;
        if (infoSelection.contains("Text") & !infoSelection.contains("Bold")){
            infoInconnu = infoSelection.substring(infoSelection.indexOf("\"") + 1, infoSelection.lastIndexOf("\""));
        } else if (infoSelection.contains("TableColumn") & !infoSelection.contains("\'null\'")) {
            infoInconnu = infoSelection.substring(infoSelection.indexOf("\'") + 1, infoSelection.lastIndexOf("\'"));
        } else {
            coursChoisi = null;
            return;
        }
        for (Course cours: listeCoursRecu) {
            if (cours.toString().contains(infoInconnu)){
                coursChoisi = cours;
            }
        }
    }

    /**
     * Vérifier la validité des informations d'inscription telle que la longueur de la matricule,
     * syntaxe des informations entrée par l'utilisateur. Envoie des infos au serveur.
     * @param prenom Prenom de l'utilisateur.
     * @param nom Nom de l'utilisateur.
     * @param courriel Courriel de l'utilisateur.
     * @param matricule Matricule de l'utilisateur.
     * @throws IOException Traitement d'erreur sur l'envoie de la requête.
     * @throws NullPointerException (Lancée conditionnellement) Traitement d'erreur sur l'oublie d'un choix de session d'inscription.
     * @throws IllegalArgumentException (Lancée conditionnellement) Traitement d'erreur sur la validité du couriel et matricule.
     */
    public void inscrire(String prenom, String nom, String courriel, String matricule) throws IOException, NullPointerException, IllegalArgumentException{

        boolean erreurMatricule = (matricule.length() != 8);
        boolean erreurCourriel = (courriel.indexOf("@") <= 0) || (courriel.indexOf("@") > courriel.lastIndexOf("."));

        if (coursChoisi == null) {
            deconnecter();
            throw new NullPointerException("Aucun cours choisi");
        } else if (erreurCourriel & erreurMatricule) {
            deconnecter();
            throw new IllegalArgumentException("Matricule et courriel invalide");
        }
        else if (erreurMatricule) {
            deconnecter();
            throw new IllegalArgumentException("Matricule invalide");
        }
        else if (erreurCourriel) {
            deconnecter();
            throw new IllegalArgumentException("Courriel invalide");
        }
        else {
            formulaireInscription = new RegistrationForm(prenom, nom, courriel, matricule, coursChoisi);
            objectOutputStream.writeObject("INSCRIRE" + " " + session);
            objectOutputStream.flush();
            objectOutputStream.writeObject(formulaireInscription);
            objectOutputStream.flush();
        }
    }

    /**
     * Connection au serveur
     * @throws IOException Traitement d'erreur en cas de connection sur un serveur deconnecté
     */
    public void connecter() throws IOException {
        client = new Socket("127.0.0.1", 1337);
        objectOutputStream = new ObjectOutputStream(client.getOutputStream());
        objectInputStream = new ObjectInputStream(client.getInputStream());
    }

    /**
     * Déconnection forcée pour la prévention d'une connection redondante.
     * @throws IOException Traitement d'erreur en cas de demande forcée sur un serveur deconnecté.
     */
    public void deconnecter() throws IOException{
        objectOutputStream.writeObject("");
        objectOutputStream.flush();
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Course getCoursChoisi(){
        return coursChoisi;
    }
}
