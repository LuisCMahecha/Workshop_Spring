package com.luismahecha.validador.controller;

import com.luismahecha.validador.model.FileType;
import com.luismahecha.validador.model.ValidationResponse;
import com.luismahecha.validador.service.ValidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/process")
public class ValidateController {
    private final ValidateService validateService;

    public ValidateController(ValidateService validateService) {
        this.validateService = validateService;
    }

    @GetMapping
    public ResponseEntity<String> holamundo(){
        return ResponseEntity.ok("Hola mundo");
    }

    @PostMapping(value = "/validate")
    public ValidationResponse proccssFile(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo no encontrado");
        }
        if (file.getOriginalFilename().endsWith(".csv")){
            return validateService.processCSV(file);
        }
        if (file.getOriginalFilename().endsWith(".xlsx")){
            return validateService.processExcel(file);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo no soportado, asegurese de ser un archivo: "+ FileType.csv +", "+ FileType.xlsx);
        }

    }
}