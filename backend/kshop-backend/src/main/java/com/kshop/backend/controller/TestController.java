/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kshop.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Iris-PC
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Endpoint public OK";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "Endpoint privé OK";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "ADMIN OK";
    }
}
