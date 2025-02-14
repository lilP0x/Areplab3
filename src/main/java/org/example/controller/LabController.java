package org.example.controller;

import org.example.annotations.GetMapping;
import org.example.annotations.RequestParam;
import org.example.annotations.RestController;


@RestController
public class LabController {
    

    @GetMapping("/app/hi")
    public String gr(@RequestParam(value = "name", defaultValue = "world") String name){
        if (name == null){
            name = "world";
        }
        return "Hola" + name;
    }

    @GetMapping("/app/add")
    public String add(@RequestParam(value = "value") int value, 
                      @RequestParam(value = "value2") int value2) {
        return String.valueOf(value + value2);
    }


    @GetMapping("/app/sub")
    public String prime(@RequestParam(value = "value") int value, 
                      @RequestParam(value = "value2") int value2) {
        return String.valueOf(value - value2);
    }

}
