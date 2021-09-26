package com.saxakiil.eventshubbackend.util;

public class RoleNormalizer {
    public static String normalize(String rawRole) {
        return rawRole.split("_", 2)[1].toLowerCase();
    }
}
