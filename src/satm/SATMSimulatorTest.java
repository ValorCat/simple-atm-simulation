package satm;

import javafx.scene.layout.VBox;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SATMSimulatorTest {

    private Controller controller;

    @Before
    public void setUp() {
        controller = new Controller(new VBox());
        Terminal.depositJammed = false;
        Terminal.withdrawJammed = false;
    }

    @Test
    public void testValidATMCard() {
        controller.setScreen(Screen.WELCOME_SCREEN);
        controller.useCardSlot();
        assertEquals(Screen.ENTER_PIN_SCREEN, controller.getScreen());
    }

    @Test
    public void testInvalidATMCard() {
        controller.setScreen(Screen.WELCOME_SCREEN);
        controller.useCardSlotInvalid();
        assertEquals(Screen.BAD_CARD_SCREEN, controller.getScreen());
    }

    @Test
    public void testCorrectPIN() {
        controller.setScreen(Screen.ENTER_PIN_SCREEN);
        controller.setNumber("1234");
        controller.clickEnter();
        assertEquals(Screen.CHOOSE_TRANSACTION_SCREEN, controller.getScreen());
    }

    @Test
    public void testIncorrectPIN() {
        controller.setScreen(Screen.ENTER_PIN_SCREEN);
        for (int i = 0; i < 3; i++) {
            // fail three times
            controller.setNumber("5678");
            controller.clickEnter();
        }
        assertEquals(Screen.BAD_CARD_SCREEN, controller.getScreen());
    }

    @Test
    public void testBalanceInquiry() {
        controller.setScreen(Screen.CHOOSE_TRANSACTION_SCREEN);
        controller.clickButtonA();
        assertEquals(Screen.BALANCE_SCREEN, controller.getScreen());
    }

    @Test
    public void testDeposit() {
        controller.setScreen(Screen.CHOOSE_TRANSACTION_SCREEN);
        controller.clickButtonB();
        assertEquals(Screen.ENTER_DEPOSIT_AMOUNT_SCREEN, controller.getScreen());
        controller.clickEnter();
        assertEquals(Screen.INSERT_DEPOSIT_SCREEN, controller.getScreen());
        controller.useDepositSlot();
        assertEquals(Screen.PRINT_BALANCE_SCREEN, controller.getScreen());
        controller.clickButtonB();
        assertEquals(Screen.GOODBYE_SCREEN, controller.getScreen());
        controller.useCardSlot();
        assertEquals(Screen.WELCOME_SCREEN, controller.getScreen());
    }

    @Test
    public void testDepositWhenJammed() {
        controller.setScreen(Screen.CHOOSE_TRANSACTION_SCREEN);
        Terminal.depositJammed = true;
        controller.clickButtonB();
        assertEquals(Screen.DEPOSIT_ERROR_SCREEN, controller.getScreen());
        controller.clickButtonB();
        assertEquals(Screen.GOODBYE_SCREEN, controller.getScreen());
        controller.useCardSlot();
        assertEquals(Screen.WELCOME_SCREEN, controller.getScreen());
    }

    @Test
    public void testWithdrawal() {
        controller.setScreen(Screen.CHOOSE_TRANSACTION_SCREEN);
        controller.clickButtonC();
        assertEquals(Screen.ENTER_WITHDRAWAL_AMOUNT_SCREEN, controller.getScreen());
        controller.setNumber("100");
        controller.clickEnter();
        assertEquals(Screen.DISPENSED_CASH_SCREEN, controller.getScreen());
        controller.useCashSlot();
        assertEquals(Screen.PRINT_BALANCE_SCREEN, controller.getScreen());
        controller.clickButtonB();
        assertEquals(Screen.GOODBYE_SCREEN, controller.getScreen());
        controller.useCardSlot();
        assertEquals(Screen.WELCOME_SCREEN, controller.getScreen());
    }

    @Test
    public void testWithdrawalBadMultiple() {
        controller.setScreen(Screen.CHOOSE_TRANSACTION_SCREEN);
        controller.clickButtonC();
        assertEquals(Screen.ENTER_WITHDRAWAL_AMOUNT_SCREEN, controller.getScreen());
        controller.setNumber("42");
        controller.clickEnter();
        assertEquals(Screen.AMOUNT_NOT_MULTIPLE_10_SCREEN, controller.getScreen());
    }

    @Test
    public void testWithdrawalTooMuch() {
        controller.setScreen(Screen.CHOOSE_TRANSACTION_SCREEN);
        controller.clickButtonC();
        assertEquals(Screen.ENTER_WITHDRAWAL_AMOUNT_SCREEN, controller.getScreen());
        controller.setNumber("500");
        controller.clickEnter();
        assertEquals(Screen.NOT_ENOUGH_BALANCE_SCREEN, controller.getScreen());
    }

    @Test
    public void testWithdrawalExceedsDailyLimit() {
        controller.setScreen(Screen.CHOOSE_TRANSACTION_SCREEN);
        controller.clickButtonC();
        assertEquals(Screen.ENTER_WITHDRAWAL_AMOUNT_SCREEN, controller.getScreen());
        controller.setNumber("120");
        controller.clickEnter();
        assertEquals(Screen.WITHDRAWAL_ERROR_SCREEN, controller.getScreen());
        controller.clickButtonB();
        assertEquals(Screen.GOODBYE_SCREEN, controller.getScreen());
        controller.useCardSlot();
        assertEquals(Screen.WELCOME_SCREEN, controller.getScreen());
    }

}