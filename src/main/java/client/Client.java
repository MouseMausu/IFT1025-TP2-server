package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private ServerSocket server;
    public static void main(String[] args) {
        try {
            Socket client = new Socket("127.0.0.1", 1337);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);

            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***\n" +
                    "Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:\n" +
                    "1. Automne\n" +
                    "2. Hiver\n" +
                    "3. Été");
            Scanner reader = new Scanner(System.in);
            System.out.print("> Choix: ");

            String session = "";
            switch (reader.nextInt()) {
                case 1:
                    session += "Automne";
                    writer.append(session + " CHARGER");
                    break;
                case 2:
                    session += "Hiver";
                    writer.append(session + " CHARGER");
                    break;
                case 3:
                    session += "Été";
                    writer.append(session + " CHARGER");
                    break;
                default:
                    break;
            }
            writer.flush();
            if (! session.contains("")) {
                System.out.println("Les cours offerts pendant la session d'" + session.toLowerCase() + " sont:");
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

}
