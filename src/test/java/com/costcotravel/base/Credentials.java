package com.costcotravel.base;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Reads a Costco sign-in from the environment or a gitignored local file.
 * Values are never written into source, reports, or assertion messages.
 */
public final class Credentials {

    public static final String USERNAME_ENV = "COSTCO_USERNAME";
    public static final String PASSWORD_ENV = "COSTCO_PASSWORD";
    private static final Path LOCAL_FILE = Path.of("credentials.properties");

    private static Properties file;

    private Credentials() {
    }

    public static boolean arePresent() {
        return !username().isBlank() && !password().isBlank();
    }

    public static String username() {
        return value(USERNAME_ENV, "username");
    }

    public static String password() {
        return value(PASSWORD_ENV, "password");
    }

    public static String redact(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String user = username();
        String secret = password();
        String redacted = text;
        if (!user.isBlank()) {
            redacted = redacted.replace(user, "[redacted-username]");
        }
        if (!secret.isBlank()) {
            redacted = redacted.replace(secret, "[redacted-password]");
        }
        return redacted;
    }

    private static String value(String envName, String fileKey) {
        String fromEnv = System.getenv(envName);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        String fromFile = localProperties().getProperty(fileKey, "");
        return fromFile == null ? "" : fromFile.trim();
    }

    private static Properties localProperties() {
        if (file != null) {
            return file;
        }
        Properties loaded = new Properties();
        if (Files.isRegularFile(LOCAL_FILE)) {
            try (Reader reader = Files.newBufferedReader(LOCAL_FILE)) {
                loaded.load(reader);
            } catch (IOException ex) {
                throw new IllegalStateException("Could not read " + LOCAL_FILE, ex);
            }
        }
        file = loaded;
        return file;
    }
}
