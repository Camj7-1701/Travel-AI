package com.travel.ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping(value = {"/", "/login", "/register", "/map", "/strategy", "/scenic", "/scenic/detail/**", "/profile"})
    public String index() {
        return "forward:/index.html";
    }
}
