package util;

import java.time.LocalDateTime;
import model.Member;

public class LoginSession {
    private static Member currentUser;
    private static LocalDateTime loginTime;

    private LoginSession() {}

    public static void login(Member member) {
        currentUser = member;
        loginTime = LocalDateTime.now();
    }

    public static void logout() {
        currentUser = null;
        loginTime = null;
    }

    public static Member getCurrentUser() { return currentUser; }
    public static LocalDateTime getLoginTime() { return loginTime; }
    public static boolean isAdmin() { return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole()); }
}
