package at.wifi.notenmanager.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordValidator {

    //Hash generieren mit BCrypt
    public static String hashPassword(String txtPassword){
        return BCrypt.hashpw(txtPassword, BCrypt.gensalt());
    }

    //Vergleiche Hashed PW mit eingegebenen PW
    public static boolean checkPassword(String txtPassword, String hashedPassword){
        return BCrypt.checkpw(txtPassword, hashedPassword);
    }

    //Validierung = nicht null + Bedingungen nicht leer, 1 Kleinbuchstaben, 1 Großbuchstaben 1 Sonderzeichen mind. 8 Zeichen und max. 20 Zeichen lang.
    public static boolean isValid(String password){
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#!$?%^&*-]).{8,20}$");
    }
}
