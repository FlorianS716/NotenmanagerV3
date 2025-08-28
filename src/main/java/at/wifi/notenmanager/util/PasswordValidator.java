package at.wifi.notenmanager.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordValidator {
    public static String hashPassword(String txtPassword){
        return BCrypt.hashpw(txtPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String txtPassword, String hashedPassword){
        return BCrypt.checkpw(txtPassword, hashedPassword);
    }

    public static boolean isValid(String password){
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*-]).{8,20}$");
    }
}
