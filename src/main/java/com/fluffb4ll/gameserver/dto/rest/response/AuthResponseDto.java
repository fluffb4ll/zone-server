package com.fluffb4ll.gameserver.dto.rest.response;


import java.util.List;
import java.util.UUID;

public record AuthResponseDto(UUID playerId, UUID authToken, String errorMessage) {
    public static AuthResponseDto loginOk(List<UUID> uuids) {
        return new AuthResponseDto(uuids.get(0), uuids.get(1), null);
    }

    public static AuthResponseDto signupOk(UUID playerId) {
        return new AuthResponseDto(playerId, null, null);
    }

    public static AuthResponseDto error(String errorMessage) {
        return new AuthResponseDto(null, null, errorMessage);
    }
}
