package org.example.server;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.*;

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

    private void loadComponents(String packagePath) {
        try {
            List<Class<?>> classes = findAllClasses(packagePath);
            for (Class<?> c : classes) {
                if (c.isAnnotationPresent(RestController.class)) {
                    Object instance = c.getDeclaredConstructor().newInstance();
                    controllers.put(c.getName(), instance);
                    for (Method method : c.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(GetMapping.class)) {
                            String route = method.getAnnotation(GetMapping.class).value();
                            routeMappings.put(route, method);
                            System.out.println("Ruta registrada: " + route);
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

    public void handleRequest(ServerSocket socket, Socket clientSocket) throws Exception {
        OutputStream out = clientSocket.getOutputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        boolean isFirstLine = true;
        String file = "";
        String method = "";

        while ((inputLine = in.readLine()) != null) {
            if (isFirstLine) {
                String[] requestParts = inputLine.split(" ");
                method = requestParts[0];
                file = requestParts[1];
                isFirstLine = false;
            }

            if (!in.ready()) {
                break;
            }
        }

        URI requestFile = new URI(file);
        String filePath = requestFile.getPath().replaceAll("/$", ""); 
        if (method.equals("GET") && filePath.startsWith("/app")) {
            handleGetRequest(file, out);
        } else {
            serveFile(filePath, out);
        }

        out.close();
        in.close();
        clientSocket.close();
    }

    private void handleGetRequest(String path, OutputStream out) throws Exception {
        System.out.println("Solicitud recibida en: " + path);

        String route = path.split("\\?")[0].replaceAll("/$", ""); // 🔥 Corrige la clave
        Method handlerMethod = routeMappings.get(route);

        if (handlerMethod != null) {
            Object controller = controllers.get(handlerMethod.getDeclaringClass().getName());

            Map<String, String> queryParams = new HashMap<>();
            if (path.contains("?")) {
                String queryString = path.split("\\?")[1];
                for (String param : queryString.split("&")) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        queryParams.put(keyValue[0], keyValue[1]);
                    }
                }
            }

            Parameter[] parameters = handlerMethod.getParameters();
            Object[] args = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                RequestParam requestParam = param.getAnnotation(RequestParam.class);

                if (requestParam != null) {
                    String paramName = requestParam.value();
                    String paramValue = queryParams.get(paramName);

                    if (param.getType().equals(int.class) && paramValue != null) {
                        args[i] = Integer.parseInt(paramValue);
                    } else {
                        args[i] = paramValue;
                    }
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
        InputStream fileStream = FileReader.class.getClassLoader().getResourceAsStream(filePath);
        boolean isError = (fileStream == null);

        if (isError) {
            fileStream = FileReader.class.getClassLoader().getResourceAsStream("400badrequest.html");
        }

        byte[] fileBytes = fileStream.readAllBytes();
        String contentType = "text/plain";

        PrintWriter writer = new PrintWriter(output, true);
        writer.println(isError ? "HTTP/1.1 400 Bad Request" : "HTTP/1.1 200 OK");
        writer.println("Content-Type: " + contentType);
        writer.println("Content-Length: " + fileBytes.length);
        writer.println();
        output.write(fileBytes);
        output.flush();
    }


    public Map<String, Method> getRouteMappings() {
        return routeMappings;
    }
    
}
