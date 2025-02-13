package org.example.server;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.annotations.GetMapping;
import org.example.annotations.RequestParam;
import org.example.annotations.RestController;

public class FileReader {

    private final Map<String, Method> routeMappings = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();

    public FileReader() {
        loadComponents("org.example.controller");
        loadComponents("org.example.annotations");
    }

    public void loadComponents(String pathPack) {
        try {
            List<Class<?>> classes = findAllClasses(pathPack);

            for (Class<?> c : classes) {
                if (hasAnnotation(c, RestController.class)) {
                    System.out.println("Cargando controlador: " + c.getName());
                    Object instance = c.getDeclaredConstructor().newInstance();
                    controllers.put(c.getName(), instance);

                    for (Method method : c.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(GetMapping.class)) {
                            String route = method.getAnnotation(GetMapping.class).value();
                            routeMappings.put(route, method);
                            System.out.println("Registrando ruta GET: " + route);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Class<?>> findAllClasses(String packageName) throws IOException, ClassNotFoundException {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        var resource = classLoader.getResource(path);

        if (resource == null) {
            throw new IOException("No se encontró el paquete: " + packageName);
        }

        File directory = new File(resource.getFile());
        List<Class<?>> classes = new ArrayList<>();

        if (directory.exists()) {
            for (String file : directory.list()) {
                if (file.endsWith(".class")) {
                    String className = packageName + '.' + file.substring(0, file.length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }

    private boolean hasAnnotation(Class<?> c, Class<? extends Annotation> annotation) {
        return c.isAnnotationPresent(annotation);
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
                String[] requestParts = inputLine.split(" ");
                method = requestParts[0];
                file = requestParts[1];
                isFirstLine = false;
                System.out.println(requestParts);

               
            }

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

        URI requestFile = new URI(file);
        String filePath = requestFile.getPath(); 

         
        if (method.equals("GET") && filePath.startsWith("/app")) {
            handleGetRequest(file, out);
        } else {
            filePath = filePath.substring(1);
            serveFile(filePath, out);
        }

        out.close();
        in.close();
        clientSocket.close();
    }

    private void handleGetRequest(String path, OutputStream out) throws Exception {
        System.out.println("Request Path: " + path);

        String route = path.split("\\?")[0];
        System.out.println();

        Method handlerMethod = routeMappings.get(route);

        System.out.println(handlerMethod);

        if (handlerMethod != null) {
            Object controller = controllers.get(handlerMethod.getDeclaringClass().getName());

            Map<String, String> queryParams = new HashMap<>();

            if (path.contains("?")) {
                String queryString = path.split("\\?")[1];
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        queryParams.put(keyValue[0], keyValue[1]);
                    }
                }
            }

            // Obtener parámetros del método
            Parameter[] parameters = handlerMethod.getParameters();
            Object[] args = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                RequestParam requestParam = param.getAnnotation(RequestParam.class);

                if (requestParam != null) {
                    String paramName = requestParam.value();
                    args[i] = queryParams.getOrDefault(paramName, "default");
                }
            }

            String result = (String) handlerMethod.invoke(controller, args);

            PrintWriter writer = new PrintWriter(out, true);
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/plain");
            writer.println("Content-Length: " + result.length());
            writer.println();
            writer.println(result);
        } else {
            PrintWriter writer = new PrintWriter(out, true);
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Content-Type: text/plain");
            writer.println();
            writer.println("Error 404: Endpoint no encontrado");
        }
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

}
