package satm;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.function.Function;

public enum Screen {

    WELCOME_SCREEN("Welcome\nPlease insert your\nATM card", Screen::awaitCard),
    ENTER_PIN_SCREEN("Please enter your PIN\n%s", Screen::awaitPIN, 1),
    BAD_PIN_SCREEN("Your PIN is incorrect.\nPlease try again.\n%s", Screen::awaitPIN, 2),
    BAD_CARD_SCREEN("Invalid ATM card. It will\nbe retained.", Screen::doNothing),
    CHOOSE_TRANSACTION_SCREEN("Select transaction:\nbalance >\ndeposit >\nwithdrawal >", Screen::awaitTransaction),
    BALANCE_SCREEN("Balance is $200.00", Screen::doNothing),
    ENTER_DEPOSIT_AMOUNT_SCREEN("Enter amount.\nWithdrawals must\nbe multiples of $10\n$ %s", Screen::awaitDepositAmount, 3),
    ENTER_WITHDRAWAL_AMOUNT_SCREEN("Enter amount.\nWithdrawals must\nbe multiples of $10\n$ %s", Screen::awaitWithdrawalAmount, 3),
    NOT_ENOUGH_BALANCE_SCREEN("Insufficient Funds!\nPlease enter a new amount\n$ %s", Screen::awaitWithdrawalAmount, 2),
    AMOUNT_NOT_MULTIPLE_10_SCREEN("Machine can only\ndispense $10 notes.\nPlease enter a new amount.\n$ %s", Screen::awaitWithdrawalAmount, 3),
    WITHDRAWAL_ERROR_SCREEN("Temporarily unable\nto process withdrawals.\nAnother transaction?", Screen::awaitContinue),
    DISPENSED_CASH_SCREEN("Your balance is being\nupdated. Please take\ncash from dispenser.", Screen::awaitRemoveCash),
    DEPOSIT_ERROR_SCREEN("Temporarily unable\nto process deposits.\nAnother transaction?", Screen::awaitContinue),
    INSERT_DEPOSIT_SCREEN("Please insert deposit\ninto deposit slot.", Screen::awaitEnterDeposit),
    PRINT_BALANCE_SCREEN("Your new balance is\nbeing printed. Another\ntransaction?", Screen::awaitContinue),
    GOODBYE_SCREEN("Please take your\nreceipt and ATM card.\nThank you.", Screen::doNothing),

    ;

    private static int pinAttempts = 0;

    private final String[] displayText;
    private final Function<Event, Screen> transition;
    private final int numberLine;

    Screen(String text, Function<Event, Screen> transition) {
        this(text, transition, -1);
    }

    Screen(String text, Function<Event, Screen> transition, int numberLine) {
        this.displayText = text.split("\n");
        this.transition = transition;
        this.numberLine = numberLine;
    }

    public String[] getDisplayText() {
        return displayText;
    }

    public Screen getNextScreen(Event trigger) {
        return transition.apply(trigger);
    }

    public void updateNumber(VBox screenUI, String number) {
        if (numberLine >= 0) {
            String newLine = String.format(displayText[numberLine], number + "_");
            ((Text) screenUI.getChildren().get(numberLine)).setText(newLine);
        }
    }

    private static Screen awaitCard(Event event) {
        if (event != Event.ATM_CARD_SWIPE) return null;
        return Terminal.isCardValid() ? ENTER_PIN_SCREEN : BAD_CARD_SCREEN;
    }

    private static Screen awaitPIN(Event event) {
        if (event != Event.ENTER_NUMBER) return null;
        if (Terminal.isPINCorrect()) return CHOOSE_TRANSACTION_SCREEN;
        pinAttempts += 1;
        if (pinAttempts < 3) return BAD_PIN_SCREEN;
        else return BAD_CARD_SCREEN;
    }

    private static Screen doNothing(Event event) {
        if (event == Event.ATM_CARD_SWIPE) return WELCOME_SCREEN;
        return null;
    }

    private static Screen awaitTransaction(Event event) {
        switch (event) {
            case BUTTON_A: return BALANCE_SCREEN;
            case BUTTON_B: return Terminal.depositAvailable() ? ENTER_DEPOSIT_AMOUNT_SCREEN : DEPOSIT_ERROR_SCREEN;
            case BUTTON_C: return Terminal.withdrawalAvailable() ? ENTER_WITHDRAWAL_AMOUNT_SCREEN : WITHDRAWAL_ERROR_SCREEN;
            default: return null;
        }
    }

    private static Screen awaitDepositAmount(Event event) {
        return event == Event.ENTER_NUMBER ? INSERT_DEPOSIT_SCREEN : null;
    }

    private static Screen awaitEnterDeposit(Event event) {
        return event == Event.INSERT_DEPOSIT_ENVELOPE ? PRINT_BALANCE_SCREEN : null;
    }

    private static Screen awaitWithdrawalAmount(Event event) {
        if (event != Event.ENTER_NUMBER) return null;
        if (!Terminal.hasEnoughFunds()) return WITHDRAWAL_ERROR_SCREEN;
        if (Terminal.exceedsBalance()) return NOT_ENOUGH_BALANCE_SCREEN;
        if (Terminal.exceedsDailyLimit()) return WITHDRAWAL_ERROR_SCREEN;
        if (Integer.parseInt(Terminal.lastInput) % 10 != 0) return AMOUNT_NOT_MULTIPLE_10_SCREEN;
        return DISPENSED_CASH_SCREEN;
    }

    private static Screen awaitRemoveCash(Event event) {
        if (event == Event.REMOVE_CASH) return PRINT_BALANCE_SCREEN;
        return null;
    }

    private static Screen awaitContinue(Event event) {
        switch (event) {
            case BUTTON_A: return CHOOSE_TRANSACTION_SCREEN;
            case BUTTON_B: return GOODBYE_SCREEN;
            default: return null;
        }
    }

}
