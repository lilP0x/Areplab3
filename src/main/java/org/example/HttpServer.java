package org.example;

import java.net.*;
import java.io.*;

public class HttpServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        FileReader fileReader = null;        
        try {
            serverSocket = new ServerSocket(35000);
            fileReader = new FileReader();
            
        } catch (IOException e) {
            System.err.println("No pude escuchar en el puerto: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
                fileReader.handleRequest(serverSocket, clientSocket);

            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            } 
        }
        serverSocket.close();
    }
}
