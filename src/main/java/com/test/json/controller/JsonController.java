package com.test.json.controller;

import com.test.json.model.APIResponse;
import com.test.json.service.JsonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonController {

    private JsonService jsonService;

    public JsonController(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    @PostMapping("/json")
    public APIResponse<Void> getJsonFromURl() {
        return this.jsonService.test();
    }
}
