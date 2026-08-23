package com.fluffb4ll.gameserver.controller;

import com.fluffb4ll.gameserver.dto.rest.request.AuthDto;
import com.fluffb4ll.gameserver.dto.rest.response.AuthResponseDto;
import com.fluffb4ll.gameserver.service.PlayerAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final PlayerAuthService authService;

    public AuthController(PlayerAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthDto authDto) {
        try {
            List<UUID> result = authService.login(authDto.nickname(), authDto.password());
            return ResponseEntity.ok(AuthResponseDto.loginOk(result));
        } catch (SecurityException e) {
            return ResponseEntity.badRequest().body(AuthResponseDto.error(e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody AuthDto authDto) {
        try {
            UUID token = authService.signup(authDto.nickname(), authDto.password());
            return ResponseEntity.ok(AuthResponseDto.signupOk(token));
        } catch (SecurityException e) {
            return ResponseEntity.badRequest().body(AuthResponseDto.error(e.getMessage()));
        }
    }
}
