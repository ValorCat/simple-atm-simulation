package satm;

public class Terminal {

    public static String lastInput = "";
    public static String cardId = "";
    public static boolean depositJammed = false;
    public static boolean withdrawJammed = false;

    public static boolean isCardValid() {
        return cardId.equals("1234");
    }

    public static boolean isPINCorrect() {
        return lastInput.equals("1234");
    }

    public static boolean depositAvailable() {
        return !depositJammed;
    }

    public static boolean withdrawalAvailable() {
        return !withdrawJammed;
    }

    public static boolean hasEnoughFunds() {
        return Integer.parseInt(lastInput) < 1000;
    }

    public static boolean exceedsBalance() {
        return Integer.parseInt(lastInput) > 200;
    }

    public static boolean exceedsDailyLimit() {
        return Integer.parseInt(lastInput) > 100;
    }

}
