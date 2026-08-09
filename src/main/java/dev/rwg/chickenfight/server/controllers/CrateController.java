package dev.rwg.chickenfight.server.controllers;

import dev.rwg.chickenfight.server.dto.CurrentCratesResponse;
import dev.rwg.chickenfight.server.services.CratesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crate")
public class CrateController {

    private final CratesService cratesService;

    public CrateController(CratesService cratesService) {
        this.cratesService = cratesService;
    }

    @GetMapping("/crates")
    public ResponseEntity<?> getCrates() {
        return ResponseEntity.status(HttpStatus.OK).body(new CurrentCratesResponse(cratesService.getCurrentCrates()));
    }
}
