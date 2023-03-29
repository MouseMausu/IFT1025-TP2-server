package client_simple;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import server.models.Course;
import server.models.RegistrationForm;

public class Client_Simple {

    public static void main(String[] args) {
        try {
            Socket client = new Socket("127.0.0.1", 1337); // Première connection
            boolean reconnection = false;

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());

            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

            Scanner reader = new Scanner(System.in);

            while (true) {
                if (reconnection) { // Reconnection pour réconsultation des choix de session.
                    client = new Socket("127.0.0.1", 1337);
                    objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                    objectInputStream = new ObjectInputStream(client.getInputStream());
                }

                boolean terminus = false; // Termine le client suite au success de l'inscription.

                System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:\n" +
                        "1. Automne\n" +
                        "2. Hiver\n" +
                        "3. Été");
                System.out.print("> Choix: ");
                String session = "";
                switch (reader.nextInt()) {
                    case 1:
                        session += "Automne";
                        objectOutputStream.writeObject("CHARGER" + " " + session);
                        break;
                    case 2:
                        session += "Hiver";
                        objectOutputStream.writeObject("CHARGER" + " " + session);
                        break;
                    case 3:
                        session += "Ete";
                        objectOutputStream.writeObject("CHARGER" + " " + session);
                        break;
                    default:
                        System.out.println("Votre choix ne correspond pas à celle d'une des sessions proposées.");
                        reconnection = false; // Aucune requête --> aucune reconnection, car on refait le choix de session.
                        break;
                }

                if (!session.equals("")) {
                    objectOutputStream.flush(); // Envoyer la requête

                    System.out.println("Les cours offerts pendant la session d'" + session.toLowerCase() + " sont:");
                    ArrayList<Course> listeCours = (ArrayList<Course>) objectInputStream.readObject(); // Charger cours

                    for (int i = 0; i < listeCours.size(); i++) { // Énumérer cours
                        System.out.println((i + 1) + ". " + listeCours.get(i).getCode() + " " + listeCours.get(i).getName());
                    }

                    while (true) {
                        boolean retourChoixSession = false; // Retourner au choix des sessions
                        System.out.println("> Choix:\n" +
                                "1. Consulter les cours offerts pour une autre session\n" +
                                "2. Inscription à un cours");
                        System.out.print("> Choix: ");
                        int choix = reader.nextInt();
                        System.out.println("");
                        switch (choix) {
                            case 1:
                                retourChoixSession = true; // Retour au choix
                                break;
                            case 2:
                                reader.nextLine(); // Skip /n
                                System.out.print("Veuillez saisir votre prénom: ");
                                String prenom = reader.nextLine();
                                System.out.print("Veuillez saisir votre nom: ");
                                String nom = reader.nextLine();
                                System.out.print("Veuillez saisir votre email: ");
                                String email = reader.nextLine();
                                System.out.print("Veuillez saisir votre matricule: ");
                                // TODO Rappel matricule 8 chiffres sinon erreurs (INTERFACE)
                                String matricule = reader.nextLine();
                                System.out.print("Veuillez saisir le code du cours: ");
                                String code = reader.nextLine();

                                RegistrationForm infoEtudiant;
                                for (Course cours: listeCours) {
                                    if (cours.getCode().equals(code)) {
                                        terminus = true; // Inscription valide --> FIN après

                                        // Remplir le formulaire
                                        infoEtudiant = new RegistrationForm(prenom, nom, email, matricule, cours);

                                        client = new Socket("127.0.0.1", 1337); // Reconnection
                                        objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                                        objectInputStream = new ObjectInputStream(client.getInputStream());

                                        // Envois du formulaire d'inscription
                                        objectOutputStream.writeObject("INSCRIRE" + " " + session);
                                        objectOutputStream.flush();
                                        objectOutputStream.writeObject(infoEtudiant);
                                        objectOutputStream.flush();
                                        break;
                                    }
                                }

                                if (terminus) { // Inscription validée
                                    System.out.println("Félicitation! Inscription réussie de " + prenom + " au cours " + code.substring(0, 3) + "-" + code.substring(3));
                                    break;
                                } else { // TODO erreur/exception code/matricule mal saisie (INTERFACE)
                                    System.out.println("Échec à l'inscription du cours. Le code à été probablement été mal saisie.");
                                    reconnection = true; // Retour à l'étape consultation/inscription
                                    break;
                                }

                            default:
                                System.out.println("Votre choix ne correspond pas à ceux qui ont été proposés. Veuillez le repréciser");
                                reconnection = true; // Retour à l'étape consultation/inscription
                                break;
                        }

                        if (retourChoixSession || terminus) {
                            reconnection = true; // Consulter les sessions
                            break;
                        }
                    }
                }
                if (terminus) { // FIN
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
