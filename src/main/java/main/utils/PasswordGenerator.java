package main.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = LOWER.toUpperCase();
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+";
    private static final String ALL_ALLOWED_CHARS = LOWER + UPPER + DIGITS + SPECIAL_CHARACTERS;
    private static SecureRandom random = new SecureRandom();

    private PasswordGenerator() {
    }

    public static String generateRandomPassword(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than 0");
        }

        List<Character> passwordChars = new ArrayList<>(length);
        // Ensure at least one character of each type is added
        passwordChars.add(LOWER.charAt(random.nextInt(LOWER.length())));
        passwordChars.add(UPPER.charAt(random.nextInt(UPPER.length())));
        passwordChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the remaining slots with random chars from all allowed
        for (int i = passwordChars.size(); i < length; i++) {
            passwordChars.add(ALL_ALLOWED_CHARS.charAt(random.nextInt(ALL_ALLOWED_CHARS.length())));
        }

        // Shuffle to prevent predictable sequences
        Collections.shuffle(passwordChars, random);

        // Build the final password string
        StringBuilder password = new StringBuilder(length);
        for (Character ch : passwordChars) {
            password.append(ch);
        }

        return password.toString();
    }
}

