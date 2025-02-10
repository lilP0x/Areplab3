package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class FileReader {

    public FileReader() {
    }

    public void handleRequest(ServerSocket socket, Socket clientSocket) throws Exception {
        OutputStream out = clientSocket.getOutputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        boolean isFirstLine = true;
        String file = "";
        String method = "";
        StringBuilder requestBody = new StringBuilder();
        boolean isPost = false;

        while ((inputLine = in.readLine()) != null) {
            if (isFirstLine) {
                // Extraer el método HTTP y la ruta
                String[] requestParts = inputLine.split(" ");
                method = requestParts[0];  // "GET" o "POST"
                file = requestParts[1];    
                isFirstLine = false;

                if (method.equals("POST")) {
                    isPost = true;  // Marcar como una solicitud POST
                }
            }

            // Leer el cuerpo de la solicitud POST
            if (isPost && inputLine.isEmpty()) {
                while (in.ready()) {
                    requestBody.append((char) in.read());
                }
                break;
            }

            System.out.println("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }

        if (method.equals("GET")) {
            URI requestFile = new URI(file);
            String filePath = requestFile.getPath().substring(1);
            serveFile(filePath, out);
        } else if (method.equals("POST")) {
            handlePostRequest(file, requestBody.toString(), out);
        }

        out.close();
        in.close();
        clientSocket.close();
    }

    
    private static void serveFile(String filePath, OutputStream output) throws IOException {
        boolean isError = false;
        InputStream fileStream = FileReader.class.getClassLoader().getResourceAsStream(filePath);

        if (fileStream == null) {
            fileStream = FileReader.class.getClassLoader().getResourceAsStream("400badrequest.html");
            isError = true;
        }

        byte[] fileBytes = fileStream.readAllBytes();
        String contentType = "application/octet-stream";

        if (filePath.endsWith(".html")) {
            contentType = "text/html";
        } else if (filePath.endsWith(".png")) {
            contentType = "image/png";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (filePath.endsWith(".gif")) {
            contentType = "image/gif";
        } else if (filePath.endsWith(".css")) {
            contentType = "text/css";
        } else if (filePath.endsWith(".js")) {
            contentType = "application/javascript";
        }

        PrintWriter writer = new PrintWriter(output, true);
        if (isError) {
            writer.println("HTTP/1.1 400 Bad Request");
            contentType = "text/html";
        } else {
            writer.println("HTTP/1.1 200 OK");
        }

        writer.println("Content-Type: " + contentType);
        writer.println("Content-Length: " + fileBytes.length);
        writer.println();
        output.write(fileBytes);
        output.flush();
    }

    private void handlePostRequest(String path, String body, OutputStream output) throws IOException {
        PrintWriter writer = new PrintWriter(output, true);

        if ("/save".equals(path)) {
            System.out.println("Datos recibidos en POST: " + body);

            String response = "{ \"message\": \"Datos guardados correctamente\" }";

            writer.println("HTTP/1.1 201 OK");
            writer.println("Content-Type: application/json");
            writer.println("Content-Length: " + response.length());
            writer.println();
            writer.println(response);
        } else {
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Content-Type: text/plain");
            writer.println();
            writer.println("Error 404: Endpoint no encontrado");
        }

        writer.flush();
    }
}