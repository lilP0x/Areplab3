package org.example.controller;

import org.example.annotations.GetMapping;
import org.example.annotations.RequestParam;
import org.example.annotations.RestController;


@RestController
public class LabController {
    

    @GetMapping("/hi")
    public String gr(@RequestParam(value = "name", defaultValue = "world") String name){
        return "Hola" + name;
    }

    @GetMapping("/add")
    public String add(String value){
        return Double.toString(Math.PI);
    }
}
