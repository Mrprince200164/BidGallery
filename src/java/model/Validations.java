package model;

import java.util.Date;

public class Validations {

    public static Boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public static Boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }

    public static boolean isDouble(String text) {
        return text.matches("^\\d+(\\.\\d{2})?$");
    }

    public static boolean isInteger(String text) {
        return text.matches("^\\d+$");
    }

    public static boolean isMobileValid(String mobile) {
        return mobile.matches("^(0{1})(7{1})([0|1|2|4|5|6|7|8]{1})([0-9]{7})");
    }

    public static String generateCode() {

        int r = (int) (Math.random() * 1000000);
        return String.format("%06d", r);

    }
    public static boolean isDate(String date) {
        return date.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    }
    
    public static boolean isDateTime(String dateStr) {
    try {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        sdf.setLenient(false);
        sdf.parse(dateStr);
        return true;
    } catch (Exception e) {
        return false;
    }
}

public static Date parseDateTime(String dateStr) {
    try {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        return sdf.parse(dateStr);
    } catch (Exception e) {
        return null;
    }
}

}
