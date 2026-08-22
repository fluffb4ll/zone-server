package com.fluffb4ll.gameserver.util;

import java.util.regex.Pattern;

public class RegexValidator {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[A-Za-z_.\\d-]{1,16}$");

    public static boolean isValidPassword(String rawPassword) {
        if (rawPassword == null)
            return false;
        return PASSWORD_PATTERN.matcher(rawPassword).matches();
    }

    public static boolean isValidNickname(String nickname) {
        if (nickname == null)
            return false;
        return NICKNAME_PATTERN.matcher(nickname).matches();
    }
}
