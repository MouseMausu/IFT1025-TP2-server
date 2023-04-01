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

/**
 * Classe de vue et donc celle responsable pour l'interface du client.
 * Dans l'ensemble des lignes de code pour cette classe, certains ont dû être appris et découverts à partir du site web
 * @link <a href="https://docs.oracle.com/javase/8/javafx/api/toc.htm">...</a> (Voir ligne 66-67 aussi)
 */
public class Client_FX extends Application {

    private TableView<Course> tabCours;
    private TextField[] entrees;
    private ChoiceBox choisirSession;

    /**
     * Méthode principale de lancement du programme affichant le GUI
     * @param args Argument sur la console
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialise chaque composante graphique présent dans notre fenêtre, soit les zones de textes et entrées,
     * boutons de chargement, bouton d'envois, dimension de la fenêtre. L'interface graphique est divisée en deux parties
     * dont la première est celle permettant à l'utilisateur/trice de charger les cours disponibles durant telle session (GUI GAUCHE).
     * Celle-ci est subdivisée en deux parties dont une pour l'affichange et une autre pour le controle. La seconde partie est désignée
     * pour les entrées d'informations ainsi que l'envoie de celles-ci.
     *
     * @param primaryStage Fenêtre de présentation.
     */
    public void start(Stage primaryStage) {

        HBox root = new HBox(5);
        root.setMaxHeight(480);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 720, 480);

        //---------------------------------------------GUI GAUCHE-------------------------------------------------------

        // Texte précisant la zone de consultation des cours
        Text texteListeCours = new Text("Liste des cours");
        texteListeCours.setFont(Font.font(20));

        // Centrer le texte indiquant la zone d'inscription
        HBox centrerTexteLC = new HBox();
        centrerTexteLC.setPadding(new Insets(10, 0, 10, 0));
        centrerTexteLC.setAlignment(Pos.CENTER);
        centrerTexteLC.getChildren().add(texteListeCours);

        // Tableau d'affichage de cours
        // Dans cette partie, les lignes de code permettant d'initialiser les colonnes du tableau sont prises du site web
        // https://docs.oracle.com/javafx/2/ui_controls/table-view.htm, par la technicienne chez Oracle, Alla Redko.
        tabCours = new TableView<>();
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
        choisirSession = new ChoiceBox();
        choisirSession.setMinWidth(100);
        choisirSession.getItems().addAll("Automne", "Hiver", "Ete");

        // Centrer tous les éléments controlant le chargement des cours (choisir session + bouton charger)
        HBox centrerBoutChargement = new HBox(60);
        centrerBoutChargement.setAlignment(Pos.CENTER);
        centrerBoutChargement.getChildren().addAll(choisirSession, charger);

        // Centrer cette zone de contrôle (GUI en bas à gauche)
        VBox zoneBoutChargement = new VBox();
        zoneBoutChargement.setAlignment(Pos.CENTER);
        zoneBoutChargement.setPadding(new Insets(20, 0, 20, 0));
        zoneBoutChargement.setBackground(new Background(new BackgroundFill(Color.rgb(235,235,225), CornerRadii.EMPTY, Insets.EMPTY)));
        zoneBoutChargement.getChildren().add(centrerBoutChargement);

        // La zone d'affichage des cours (GUI en haut à gauche)
        VBox zoneListeCours = new VBox();
        zoneListeCours.setPadding(new Insets(0, 15, 10, 15));
        zoneListeCours.setBackground(new Background(new BackgroundFill(Color.rgb(235,235,225), CornerRadii.EMPTY, Insets.EMPTY)));
        zoneListeCours.getChildren().addAll(centrerTexteLC, tabCours);

        // Gui de gauche et donc celui responsable pour la consultation des cours
        VBox guiGauche = new VBox(5);
        guiGauche.setMaxHeight(480);
        guiGauche.getChildren().addAll(zoneListeCours, zoneBoutChargement);

        //---------------------------------------------GUI DROITE-------------------------------------------------------

        // Texte précisant la zone "Formulaire d'inscription"
        Text texteInscription = new Text("Formulaire d'inscription");
        texteInscription.setFont(Font.font(20));

        // Création des identifiants
        Text[] texteEntrees = new Text[4];
        String[] etiquetteTE = {"Prénom", "Nom", "Courriel", "Matricule"};
        entrees = new TextField[4];
        for (int i = 0; i < texteEntrees.length; i++) { // Création des éléments en entier sous une boucle.
            texteEntrees[i] = new Text(etiquetteTE[i]);
            texteEntrees[i].setFont(Font.font(15));
            entrees[i] = new TextField();
            entrees[i].setMinSize(180,30);
        }

        // Bouton envoyer
        Button envoyer = new Button("envoyer");
        envoyer.setMinWidth(80);

        // Centrer le texte "Formulaire d'inscription"
        HBox centrerTexteIns = new HBox();
        centrerTexteIns.setAlignment(Pos.CENTER);
        centrerTexteIns.setPadding(new Insets(10, 0, 40, 0));
        centrerTexteIns.getChildren().add(texteInscription);

        // Centrer le bouton "envoyer"
        HBox centrerBoutEnv = new HBox();
        centrerBoutEnv.setAlignment(Pos.CENTER);
        centrerBoutEnv.setPadding(new Insets(20, 0, 0, 0));
        centrerBoutEnv.getChildren().add(envoyer);

        // Regrouper les textes "nom", "prénom", etc...
        VBox groupeTexteEntree = new VBox(20);
        groupeTexteEntree.setPadding(new Insets(5, 0, 0, 0));
        groupeTexteEntree.getChildren().addAll(texteEntrees[0], texteEntrees[1], texteEntrees[2], texteEntrees[3]);

        // Regrouper les zones d'entrée pour identifiants et du bouton "envoyer"
        VBox groupeEntree = new VBox(10);
        groupeEntree.getChildren().addAll(entrees[0], entrees[1], entrees[2], entrees[3], centrerBoutEnv);

        // Centrer tous les éléments en un se retrouvant au côté droite de la fenêtre.
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

        // Choisir la session
        choisirSession.setOnAction(actionEvent -> {
            controler.setSession((String) choisirSession.getValue());
        });

        // Clic du bouton charger
        charger.setOnAction(actionEvent -> {
            try{
                reinitialiserBordure();
                controler.connecter();
                tabCours.setItems(controler.charger());
            } catch (IOException e){
                alerter("Erreur", "Échec au chargement\n " +
                        "Le serveur doit être ouvert tout en possédant le ficher cours.txt pour que le chargement ait lieu!");
            } catch (ClassNotFoundException e) {
               e.printStackTrace();
            } catch (NullPointerException e) {
                bordureErreur(choisirSession);
                alerter("Erreur", "Échec au chargement\n" +
                        "La session n'a pas encore été choisi");
            }
        });

        // CLick sur le tableau d'affichage
        tabCours.setOnMouseClicked((mouseEvent -> {
            controler.selectionnerCours(mouseEvent.getPickResult().getIntersectedNode().toString());
        }));

        // Bouton envoyer
        envoyer.setOnAction(actionEvent -> {
            String prenom = entrees[0].getText();
            String nom = entrees[1].getText();
            String courriel = entrees[2].getText();
            String matricule = entrees[3].getText();
            try {
                reinitialiserBordure();
                controler.connecter();
                controler.inscrire(prenom, nom, courriel, matricule);
                alerter("Succès", "Felicitation! " + nom + " " + prenom +
                        " est inscrit(e) avec succès pour le cours de " + controler.getCoursChoisi().getCode());
                for (int i = 0; i < entrees.length; i++){
                    entrees[i].clear();
                }
            } catch (IOException e) {
                alerter("Erreur", "Erreur: Le serveur doit être ouvert pour que le chargement ait lieu!");
            } catch (NullPointerException e) {
                bordureErreur(tabCours);
                alerter("Erreur", "Vous n'avez pas choisi de cours!");
            } catch (IllegalArgumentException e){
                switch (e.getMessage()){
                    case "Matricule et courriel invalide":
                        bordureErreur(entrees[2]);
                        bordureErreur(entrees[3]);
                        alerter("Erreur", "Le formulaire est invalide\n" +
                                "Le Champ \"Email\" est invalide\n" +
                                "Le champ \"Matricule\" est invalide");
                        break;
                    case "Courriel invalide":
                        bordureErreur(entrees[2]);
                        alerter("Erreur", "Le formulaire est invalide\n" +
                                "Le Champ \"Email\" est invalide\n");
                        break;
                    default:
                        bordureErreur(entrees[3]);
                        alerter("Erreur", "Le formulaire est invalide\n" +
                                "Le champ \"Matricule\" est invalide");
                        break;
                }
            }
        });
    }

    /**
     * Appelle automatiquement les alertes lorsque le programme observe une erreur ou succès d'inscription.
     * L'alerte est affichée dans une petite fenêtre précisant l'erreur sous un message.
     * @param type Type d'alerte sous une chaine de caractère succès/erreur.
     * @param message Message à afficher dans la fenêtre d'alerte.
     */
    public void alerter(String type, String message){
        Alert alert = new Alert(Alert.AlertType.NONE);
        switch(type){
            case "Succès":
                alert = new Alert(Alert.AlertType.INFORMATION);
                break;
            case "Erreur":
                alert = new Alert(Alert.AlertType.ERROR);
                break;
        }
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Ajout d'une bordure précisant une case d'entrée erronée.
     * @param textField Objet associé à une zone entrée texte.
     */
    public void bordureErreur(TextField textField){
        textField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
    }

    /**
     * Ajout d'une bordure précisant un manque de choix de session.
     * @param choiceBox Objet associé à une boite permettant de choisir une session.
     */
    public void bordureErreur(ChoiceBox choiceBox){
        choiceBox.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
    }

    /**
     * Ajout d'une bordure que l'utilisateur n'a pas choisi de cours parmis ceux dans le tableau.
     * @param tableView Objet associé aux tableaux d'affichage.
     */
    public void bordureErreur(TableView tableView){
        tableView.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
    }

    /**
     * Efface toutes les bordures d'erreur générées. Utilise 3 objets de l'interface susceptibles d'erreurs.
     * Soit le tableau, le choix de session ainsi que les entrées.
     */
    public void reinitialiserBordure(){
        tabCours.setBorder(Border.EMPTY);
        choisirSession.setBorder(Border.EMPTY);
        for (TextField entree: this.entrees){
            entree.setBorder(Border.EMPTY);
        }
    }
}