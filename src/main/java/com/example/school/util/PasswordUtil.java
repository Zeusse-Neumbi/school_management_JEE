package com.example.school.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String candidate, String hashed) {
        if (hashed == null || !hashed.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(candidate, hashed);
    }
}
