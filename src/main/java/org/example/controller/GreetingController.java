package org.example.controller;

import javax.xml.namespace.QName;
import org.example.annotations.GetMapping;
import org.example.annotations.RequestParam;
import org.example.annotations.RestController;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {
    private  static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "world") String name){
        return "Hola" + name;
    }

    @GetMapping("/pi")
    public String pi(String value){
        return Double.toString(Math.PI);
    }
}
