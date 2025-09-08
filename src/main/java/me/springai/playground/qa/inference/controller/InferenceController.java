package me.springai.playground.qa.inference.controller;


import me.springai.playground.qa.inference.controller.dto.AskRequest;
import me.springai.playground.qa.inference.service.InferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/inference")
public class InferenceController {

    private final InferenceService service;

    public InferenceController(InferenceService service) {
        this.service = service;
    }


    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> ask(@RequestBody AskRequest req) {
        return ResponseEntity.ok(service.answer(req.question()));
    }

}
