package client_fx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import server.models.Course;

import java.io.IOException;
import java.security.InvalidParameterException;

public class Client_FX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        HBox root = new HBox(5);
        root.setMaxHeight(480);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 720, 480);

        // Texte précisant la zone de consultation des cours
        Text texteListeCours = new Text("Liste des cours");
        texteListeCours.setFont(Font.font(20));

        // Centrer le texte indiquant la zone d'inscription
        HBox centrerTexteLC = new HBox();
        centrerTexteLC.setPadding(new Insets(10, 0, 10, 0));
        centrerTexteLC.setAlignment(Pos.CENTER);
        centrerTexteLC.getChildren().add(texteListeCours);

        // Tableau d'affichage de cours
        //TODO CITER SOURCE
        TableView<Course> tabCours = new TableView<>();
        tabCours.setMinWidth(320);
        TableColumn colonneCode = new TableColumn("Code");
        colonneCode.setMinWidth(100);
        colonneCode.setCellValueFactory(new PropertyValueFactory<Course, String>("code"));
        TableColumn colonneNomCours = new TableColumn("Cours");
        colonneNomCours.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        colonneNomCours.setMinWidth(220);
        tabCours.getColumns().addAll(colonneCode, colonneNomCours);

        // Bouton charger
        Button charger = new Button("Charger");

        // Choix de session
        ChoiceBox choisirSession = new ChoiceBox();
        choisirSession.setMinWidth(100);
        choisirSession.getItems().addAll("Automne", "Hiver", "Ete");

        // Centrer le bouton de chargement de cours
        HBox centrerBoutChargement = new HBox(60);
        centrerBoutChargement.setAlignment(Pos.CENTER);
        centrerBoutChargement.getChildren().addAll(choisirSession, charger);

        // Zone de contrôle pour la consultation des listes de cours
        VBox zoneBoutChargement = new VBox();
        zoneBoutChargement.setAlignment(Pos.CENTER);
        zoneBoutChargement.setPadding(new Insets(20, 0, 20, 0));
        zoneBoutChargement.setBackground(new Background(new BackgroundFill(Color.rgb(235,235,225), CornerRadii.EMPTY, Insets.EMPTY)));
        zoneBoutChargement.getChildren().add(centrerBoutChargement);

        // GUI (droite) pour la zone de chargement
        VBox zoneListeCours = new VBox();
        zoneListeCours.setPadding(new Insets(0, 15, 10, 15));
        zoneListeCours.setBackground(new Background(new BackgroundFill(Color.rgb(235,235,225), CornerRadii.EMPTY, Insets.EMPTY)));
        zoneListeCours.getChildren().addAll(centrerTexteLC, tabCours);

        // Gui de gauche et donc celui responsable pour la consultation des cours
        VBox guiGauche = new VBox(5);
        guiGauche.setMaxHeight(480);
        guiGauche.getChildren().addAll(zoneListeCours, zoneBoutChargement);

        Text texteInscription = new Text("Formulaire d'inscription");
        texteInscription.setFont(Font.font(20));

        Text[] texteEntrees = new Text[4]; // Texte précisant la zone de formulaire d'inscription
        String[] etiquetteTE = {"Prénom", "Nom", "Courriel", "Matricule"}; // Étiquettes à associer aux textes ci-haut.
        TextField[] entrees = new TextField[4]; // Les cases d'entrée pour l'identification.

        // Création des identifiants
        for (int i = 0; i < texteEntrees.length; i++) {
            texteEntrees[i] = new Text(etiquetteTE[i]);
            texteEntrees[i].setFont(Font.font(15));
            entrees[i] = new TextField();
            entrees[i].setMinSize(180,30);
        }

        // Bouton envoyer
        Button envoyer = new Button("envoyer");
        envoyer.setMinWidth(80);

        // Centrer le texte indiquant la zone d'inscription
        HBox centrerTexteIns = new HBox();
        centrerTexteIns.setAlignment(Pos.CENTER);
        centrerTexteIns.setPadding(new Insets(10, 0, 40, 0));
        centrerTexteIns.getChildren().add(texteInscription);

        // Centrer les cases d'identification ainsi que le bouton "envoyer"
        HBox centrerBoutEnv = new HBox();
        centrerBoutEnv.setAlignment(Pos.CENTER);
        centrerBoutEnv.setPadding(new Insets(20, 0, 0, 0));
        centrerBoutEnv.getChildren().add(envoyer);

        // Groupement de textes précisant les identifiants
        VBox groupeTexteEntree = new VBox(20);
        groupeTexteEntree.setPadding(new Insets(5, 0, 0, 0));
        groupeTexteEntree.getChildren().addAll(texteEntrees[0], texteEntrees[1], texteEntrees[2], texteEntrees[3]);

        // Groupement de zones d'entrée pour identifiants et du bouton envoyer
        VBox groupeEntree = new VBox(10);
        groupeEntree.getChildren().addAll(entrees[0], entrees[1], entrees[2], entrees[3], centrerBoutEnv);

        //
        HBox zoneInscription = new HBox(20);
        zoneInscription.setAlignment(Pos.CENTER);
        zoneInscription.setPadding(new Insets(0, 50, 0, 50));
        zoneInscription.getChildren().addAll(groupeTexteEntree, groupeEntree);

        // GUI de droite (Interface graphique complet pour l'inscription)
        VBox guiDroite = new VBox();
        guiDroite.setMaxHeight(480);
        guiDroite.setBackground(new Background(new BackgroundFill(Color.rgb(235,235,225), CornerRadii.EMPTY, Insets.EMPTY)));
        guiDroite.getChildren().addAll(centrerTexteIns, zoneInscription);

        root.getChildren().addAll(guiGauche, guiDroite);

        primaryStage.setTitle("Inscription UdeM");
        primaryStage.setScene(scene);
        primaryStage.show();

        //------------------------------------------ Section event -----------------------------------------------------
        Control controler = new Control();


        choisirSession.setOnAction(actionEvent -> {
            controler.setSession((String) choisirSession.getValue());
        });

        charger.setOnAction(actionEvent -> {
            try{
                controler.connect();
                tabCours.setItems(controler.charger());
            } catch (IOException e){
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        tabCours.setOnMouseClicked((mouseEvent -> {
            controler.selectionnerCours(mouseEvent.getPickResult().getIntersectedNode().toString());
        }));

        envoyer.setOnAction(actionEvent -> {
            String prenom = entrees[0].getText();
            String nom = entrees[1].getText();
            String courriel = entrees[2].getText();
            String matricule = entrees[3].getText();
            try {
                controler.connect();
                controler.inscrire(prenom, nom, courriel, matricule);
                alerter("Succès", "Felicitaion! " + nom + " " + prenom +
                        " est inscrit(e) avec succès pour le cours de " + controler.getTrouverCours().getCode());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidParameterException e){
                System.out.println("...");
            }
        });
    }
    public void alerter(String type, String message){
        Alert alert = new Alert(Alert.AlertType.NONE);
        switch(type){
            case "Succès":
                alert = new Alert(Alert.AlertType.INFORMATION);
                break;
            case "Échec":
                alert = new Alert(Alert.AlertType.ERROR);
                break;
        }
        alert.setContentText(message);
        alert.showAndWait();
    }
}