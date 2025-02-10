

import org.example.FileReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.*;

public class FileReaderTest {
    
    private static ServerSocket serverSocket;
    private static Thread serverThread;

    @BeforeAll
    static void startServer() throws Exception {
        serverSocket = new ServerSocket(8080);
        serverThread = new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    new FileReader().handleRequest(serverSocket, clientSocket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        serverSocket.close();
        serverThread.interrupt();
    }

    @Test
    void testGetExistingFile() throws Exception {
        String response = sendRequest("GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertTrue(response.contains("HTTP/1.1 200 OK"));
    }

    @Test
    void testGetNonExistingFile() throws Exception {
        String response = sendRequest("GET /noexist.html HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
    }

    @Test
    void testPostValid() throws Exception {
        String requestBody = "data=hello";
        String request = "POST /save HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: " + requestBody.length() + "\r\n" +
                "\r\n" + requestBody;
        String response = sendRequest(request);
        assertTrue(response.contains("HTTP/1.1 201 OK"));
    }

    @Test
    void testPostInvalid() throws Exception {
        String requestBody = "data=hello";
        String request = "POST /invalid HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: " + requestBody.length() + "\r\n" +
                "\r\n" + requestBody;
        String response = sendRequest(request);
        assertTrue(response.contains("HTTP/1.1 404 Not Found"));
    }

    private String sendRequest(String request) throws Exception {
        try (Socket socket = new Socket("127.0.0.1", 8080);
             OutputStream out = socket.getOutputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.write(request.getBytes());
            out.flush();
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            
            return response.toString();
        }
    }
}
