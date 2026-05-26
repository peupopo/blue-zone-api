package com.bluezone.blue_zone_api.controller;

import com.bluezone.blue_zone_api.model.Item;
import com.bluezone.blue_zone_api.service.SheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ItemController {

    @Autowired
    private SheetsService sheetsService;

    @GetMapping("/")
    public String form(Model model) {
        model.addAttribute("item", new Item());
        return "index";
    }

    @PostMapping("/adicionar")
    public String formPost(@ModelAttribute Item item) {
        try {
            sheetsService.adicionarItem(item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }
}