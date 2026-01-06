package lab.soa.domain;

public enum View {
    STREET,
    YARD,
    BAD,
    GOOD,
    ;

    public static boolean isValidValue(String value) {
        if (value == null) {
            return false;
        }
        try {
            View.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
