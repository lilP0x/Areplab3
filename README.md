# Taller diseño y estructuración de aplicaciones distribuidas en internet

Este proyecto es un servidor web simple implementado en Java que permite manejar múltiples solicitudes HTTP de forma secuencial (no concurrente). El servidor puede servir archivos HTML, CSS, JavaScript e imágenes desde el disco local y manejar solicitudes REST como el método POST.

##  Características del Servidor
- Escucha en el puerto `35000`.
- Atiende solicitudes HTTP GET para servir archivos estáticos (`.html`, `.css`, `.js`, `.png`, `.jpg`, `.gif`).
- Soporta solicitudes HTTP POST para recibir datos desde un cliente web.
- No usa frameworks web como Spring.
- Implementado usando solo Java y las librerías estándar de manejo de red.

## Estructura del Proyecto
```
 src/main/java/org/example/
    ├── HttpServer.java       # Clase principal, inicia el servidor
    ├── FileReader.java       # Maneja las solicitudes HTTP y entrega archivos
 src/main/resources/
    ├── index.html            # Página web de prueba
    ├── styles.css            # Estilos para la aplicación web
    ├── script.js             # Lógica en JavaScript para la interacción
    ├── laminemipapa.png            # Imagen de prueba para verificar el servidor
```

## 1 Instalación y Ejecución
### Clonar el repositorio
```sh
git clone https://github.com/lilP0x/Areplab1.git
```

### 2️ Compilar el servidor
```sh
mvn clean package
```

### 3️ Ejecutar el servidor
```sh
java -cp target/classes org.example.HttpServer
```

![alt text](/src/main/resources/readmeImages/image-2.png)

##  Pruebas y Evaluación
###  Pruebas Manuales
1. **Abrir el navegador y acceder al servidor**
   - `http://localhost:35000/index.html` → Debe mostrar la página web de prueba.
   - `http://localhost:35000/styles.css` → Debe devolver la hoja de estilos.
   - `http://localhost:35000/script.js` → Debe devolver el script JavaScript.
   - `http://localhost:35000/laminemipapa.png` → Debe mostrar la imagen.

2. **Enviar una solicitud POST desde la consola**
```sh
curl -X POST -d "data=hello" http://localhost:35000/save
```
   - Respuesta esperada: `{"message": "Datos guardados correctamente"}`

3. **Solicitar un archivo inexistente**
   - `http://localhost:35000/noexist.html` → Debe devolver un error `400 Bad Request`.

###  Pruebas Automatizadas
Se ejecutaron pruebas unitarias utilizando `JUnit` verificando:
- La correcta respuesta a archivos existentes y no existentes.
- El manejo de solicitudes POST.


para ejecutarlas tendrea que ejecutar el siguiente comando desde la terminal 
```sh
mvn test
```

![alt text](/src/main/resources/readmeImages/image-1.png)




###  Arquitectura del Proyecto
El servidor sigue una arquitectura basada en sockets:
1. **`HttpServer`** inicia el servidor y espera conexiones.
2. **`FileReader`** maneja cada solicitud entrante:
   - Extrae la ruta del archivo solicitado.
   - Si es un GET, busca y devuelve el archivo.
   - Si es un POST, procesa los datos y responde en JSON.

Para una mejor ejemplificacion tendremos el siguiente diagrama de arquitectura.

![alt text](/src/main/resources/readmeImages/image.png)

##  Conclusión
Este proyecto demuestra cómo funciona un servidor web simple en Java sin frameworks. Permite explorar la arquitectura de aplicaciones distribuidas y la comunicación HTTP de bajo nivel.

# Build With 

- Maven 


# Made by: 

Juan Pablo Fernandez Gonzalez

