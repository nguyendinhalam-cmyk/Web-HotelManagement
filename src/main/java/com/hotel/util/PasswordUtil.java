package com.hotel.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {}

    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        return BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
    }

    public static boolean matches(String rawPassword, String hash) {
        if (rawPassword == null || hash == null || hash.isBlank()) return false;
        return BCrypt.verifyer().verify(rawPassword.toCharArray(), hash).verified;
    }
}
