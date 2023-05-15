package rosegold.gumtuneclient.utils;

public class StringUtils {
    public static String removeFormatting(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }

    public static String millisecondFormatTime(long milliseconds) {
        long second = (milliseconds / 1000) % 60;
        long minute = (milliseconds / (1000 * 60)) % 60;
        long hour = (milliseconds / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
