package util;

public class Validator {
    private Validator() {}
    public static boolean isBlank(String text) { return text == null || text.trim().isEmpty(); }
    public static boolean isEmail(String email) { return isBlank(email) || email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"); }
}
