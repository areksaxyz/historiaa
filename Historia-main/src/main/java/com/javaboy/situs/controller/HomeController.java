package com.javaboy.situs.controller;

import com.javaboy.situs.service.SitusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private SitusService situsService;

    @GetMapping("/") 
    public String viewHomePage(Model model) {
        model.addAttribute("latestSitus", situsService.getLatestSitus());
        return "index";
    }
}