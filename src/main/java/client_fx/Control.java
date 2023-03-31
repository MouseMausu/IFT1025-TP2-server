package client_fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Control {
    private Socket client;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String session;
    private ArrayList<Course> listeCoursRecu;
    private Course trouverCours;
    private RegistrationForm formulaireInscription;

    public ObservableList<Course> charger() throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject("CHARGER" + " " + session);
        objectOutputStream.flush();
        listeCoursRecu = (ArrayList<Course>) objectInputStream.readObject();
        ObservableList<Course> listeCoursVu = FXCollections.observableArrayList(); //TODO CITER SOURCE
        for (Course cours: listeCoursRecu) {
            listeCoursVu.add(cours);
        }
        return listeCoursVu;

    }

    public void selectionnerCours(String infoSelection) {
        String infoInconnu;
        if (infoSelection.contains("Text") & !infoSelection.contains("Bold")){
            infoInconnu = infoSelection.substring(infoSelection.indexOf("\"") + 1, infoSelection.lastIndexOf("\""));
        } else if (infoSelection.contains("TableColumn") & !infoSelection.contains("\'null\'")) {
            infoInconnu = infoSelection.substring(infoSelection.indexOf("\'") + 1, infoSelection.lastIndexOf("\'"));
        } else {
            trouverCours = null;
            return;
        }
        for (Course cours: listeCoursRecu) {
            if (cours.toString().contains(infoInconnu)){
                trouverCours = cours;
            }
        }
    }

    public void inscrire(String prenom, String nom, String courriel, String matricule) throws IOException{
        if (matricule.length() < 8) {
            throw new InvalidParameterException("Matricule Invalide");
        } else {
            formulaireInscription = new RegistrationForm(prenom, nom, courriel, matricule, trouverCours);
            System.out.println(formulaireInscription.toString());
            objectOutputStream.writeObject("INSCRIRE" + " " + session);
            objectOutputStream.flush();
            objectOutputStream.writeObject(formulaireInscription);
            objectOutputStream.flush();
        }

    }

    public void connect() throws IOException {
        client = new Socket("127.0.0.1", 1337);
        objectOutputStream = new ObjectOutputStream(client.getOutputStream());
        objectInputStream = new ObjectInputStream(client.getInputStream());
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Course getTrouverCours(){
        return trouverCours;
    }
}
