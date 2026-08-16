package com.fluffb4ll.gameserver.model.controllers;

import com.fluffb4ll.gameserver.engine.PlayerAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final PlayerAuthService authService;

    public AuthController(PlayerAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(String nickname, String rawPassword) {
        try {
            UUID token = authService.login(nickname, rawPassword);
            return ResponseEntity.ok(token.toString());
        } catch (SecurityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
