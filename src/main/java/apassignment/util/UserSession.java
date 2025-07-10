package apassignment.util;

public class UserSession {
    private static String currentUserID;

    public static void setCurrentUserID(String userID) {
        currentUserID = userID;
    }

    public static String getCurrentUserID() {
        return currentUserID;
    }
}
