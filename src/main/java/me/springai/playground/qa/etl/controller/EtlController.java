package me.springai.playground.qa.etl.controller;

import me.springai.playground.qa.etl.service.EtlService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/v1/etl")
public class EtlController {

    private final EtlService service;

    public EtlController(EtlService service) {
        this.service = service;
    }

    @PostMapping(path = "/load", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> load(
            @RequestPart("file") @NotNull MultipartFile file) throws Exception {
        return ResponseEntity.ok(service.ingest(file));
    }
}