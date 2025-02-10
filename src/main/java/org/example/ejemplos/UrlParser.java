package org.example.ejemplos;
import java.net.URL;

public class UrlParser {

    public static void main(String[] args) throws Exception {
        URL myUrl = new URL("https://cafecristetto.com:7777/index.html?val=10#popo");
        

        System.out.println("Protocolo:" + myUrl.getProtocol());
        System.out.println("Authority:" + myUrl.getAuthority());
        System.out.println("Host:" + myUrl.getHost());
        System.out.println("Port:" + myUrl.getPort());
        System.out.println("Path:" + myUrl.getPath());
        System.out.println("Query:" + myUrl.getQuery());
        System.out.println("File:" + myUrl.getFile());
        System.out.println("Ref:" + myUrl.getRef());
    }
}