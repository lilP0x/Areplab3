package org.example.server;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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

                if (method.equals("POST")) {
                    isPost = true;  
                }
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

        if (method.equals("GET")) {
            handleGetRequest(file, out); 
        }

        out.close();
        in.close();
        clientSocket.close();
    }

    private void handleGetRequest(String path, OutputStream out) throws Exception {

        Method handlerMethod = routeMappings.get(path);
        System.out.println(handlerMethod);
        
        if (handlerMethod != null) {
            Object controller = controllers.get("org.example.controller.LabController");

            String name = "world";  

            if (path.contains("?")) {
                String queryString = path.split("\\?")[1];
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if ("name".equals(keyValue[0])) {
                        name = keyValue[1];
                    }
                }
            }

            String result = (String) handlerMethod.invoke(controller, name);

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
}
