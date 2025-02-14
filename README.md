# Proyecto: Servidor Web en Java con Framework 

## Descripción
Este proyecto consiste en la implementación de un servidor web simple en Java que soporta la entrega de páginas HTML e imágenes PNG, javascript. Además, proporciona un mini framework que permite la construcción de aplicaciones web a partir de POJOs utilizando anotaciones personalizadas similares a Spring Boot.

El servidor soporta peticiones GET y permite definir controladores con rutas personalizadas mediante las anotaciones `@RestController`, `@GetMapping` y `@RequestParam`.

## Características Principales
- Servidor HTTP que escucha en el puerto 35000.
- Capacidad para manejar rutas dinámicas con anotaciones.
- Soporte para `@RestController` y `@GetMapping` para definir controladores.
- Soporte para `@RequestParam` para recibir parámetros en las peticiones GET.
- Descubrimiento automático de controladores en el paquete `org.example.controller`.

## Requisitos
Para ejecutar este proyecto, asegúrate de tener instalados los siguientes componentes:
- **Java 21** (o compatible con tu entorno).
- **Maven** (para la gestión de dependencias y compilación del proyecto).

## Estructura del Proyecto
```
|-- src/main/java/org/example
|   |-- annotations/   # Anotaciones personalizadas (@RestController, @GetMapping, @RequestParam)
|   |-- controller/    # Controladores de ejemplo
|   |-- server/        # Implementación del servidor y manejador de rutas
|-- pom.xml           # Archivo de configuración de Maven
```

## Instalación y Ejecución
### 1. Clonar el Repositorio
```sh
$ git clone https://github.com/lilP0x/Areplab3
```

### 2. Compilar el Proyecto
```sh
$ mvn clean package
```

### 3. Ejecutar el Servidor
```sh
$ java -cp target/classes org.example.server.HttpServer
```
El servidor quedará escuchando en el puerto **35000**.

### 4. Probar Endpoints
#### Saludo Simple:
```sh
http://localhost:35000/app/hi?name=Juan
```
**Respuesta:**
```
Hola Juan
```

#### Suma de Dos Valores:
```sh
http://localhost:35000/app/add?value=5&value2=3
```
**Respuesta:**
```
8
```

#### Resta de Dos Valores:
```sh
http://localhost:35000/app/sub?value=10&value2=4
```
**Respuesta:**
```
6
```

## Explicación de la Solución
1. **Anotaciones Personalizadas**: Se definen `@RestController`, `@GetMapping` y `@RequestParam` para identificar clases y métodos que manejarán peticiones HTTP.
2. **Exploración de Clases**: `FileReader` busca clases anotadas con `@RestController` y registra los métodos `@GetMapping`.
3. **Servidor HTTP**: `HttpServer` maneja las conexiones y delega las peticiones al `FileReader`, que resuelve la ruta y ejecuta el método correspondiente.
4. **Inyección de Dependencias Básica**: Se instancian los controladores y se invocan dinámicamente sus métodos, pasando los parámetros adecuados.

---


