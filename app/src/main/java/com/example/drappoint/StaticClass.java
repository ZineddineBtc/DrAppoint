package com.example.drappoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticClass {

    public static int PICK_SINGLE_IMAGE = 1;
    public static String SHARED_PREFERENCES = "shared_preferences";
    public static String NAME = "username";
    public static String EMAIL = "email";
    public static String PHONE = "phone";
    public static String ADDRESS = "address";

    public static boolean isValidEmail(String email) {
        if(email.length()>4){
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }else{
            return false;
        }

    }
    public static boolean containsDigit(String s) {
        if(s.length()>2){
            boolean containsDigit = false;
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
            return containsDigit;
        }else{
            return false;
        }
    }
    public static String getCurrentTime(){
        return new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
    }

}
