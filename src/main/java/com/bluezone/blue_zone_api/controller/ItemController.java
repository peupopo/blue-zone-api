package com.bluezone.blue_zone_api.controller;

import com.bluezone.blue_zone_api.model.Item;
import com.bluezone.blue_zone_api.service.SheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItemController {

    @Autowired
    private SheetsService sheetsService;

    @GetMapping("/itens")
    public ResponseEntity<?> getItens() {
        try {
            List<Item> resultado = sheetsService.listarItens();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/itens")
    public ResponseEntity<?> postItens(@RequestBody Item item) {
        try {
            sheetsService.adicionarItem(item);
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/itens")
    public ResponseEntity<?> putItens(@RequestBody Item item) {
        try {
            sheetsService.atualizarItem(item);
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}