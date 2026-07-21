package smartparkingmanagementsystem.util;

import java.util.Locale;
import java.util.regex.Pattern;

/** Shared validation and normalization rules for Module 1. */
public final class ValidationUtils {
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9][0-9 ()-]{5,18}[0-9]$");
    private ValidationUtils() { }

    public static String requireText(String value, String label) {
        String result = value == null ? "" : value.trim();
        if (result.isEmpty()) throw new IllegalArgumentException(label + " is required.");
        if (result.indexOf('|') >= 0 || result.indexOf('\n') >= 0 || result.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(label + " cannot contain | or line breaks.");
        }
        return result;
    }

    public static String normalizeIdentifier(String value, String label) {
        return requireText(value, label).toUpperCase(Locale.ROOT);
    }

    public static String validatePhone(String value) {
        String phone = requireText(value, "Phone number");
        if (!PHONE.matcher(phone).matches()) throw new IllegalArgumentException("Enter a valid phone number.");
        return phone;
    }

    public static int validateFloor(int floor) {
        if (floor < 0) throw new IllegalArgumentException("Floor number must be zero or greater.");
        return floor;
    }

    public static double validateRate(double rate) {
        if (!Double.isFinite(rate) || rate <= 0) {
            throw new IllegalArgumentException("Hourly rate must be greater than zero.");
        }
        return rate;
    }
}
