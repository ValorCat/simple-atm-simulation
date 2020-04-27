package satm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class Controller {

    private Screen currentScreen = Screen.WELCOME_SCREEN;
    private String number = "";

    @FXML private VBox screenUI;

    public Controller() {}

    public Controller(VBox screenUI) {
        this.screenUI = screenUI;
    }

    public void handleEvent(Event event) {
        Screen nextScreen = currentScreen.getNextScreen(event);
        if (nextScreen != null) {
            setScreen(nextScreen);
        }
    }

    public Screen getScreen() {
        return currentScreen;
    }

    public void setScreen(Screen screen) {
        this.currentScreen = screen;
        updateScreen();
    }

    public void updateScreen() {
        List<Node> lines = screenUI.getChildren();
        lines.clear();
        for (String newLine : currentScreen.getDisplayText()) {
            lines.add(new Text(newLine.replace("%s", "_")));
        }
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void clickButtonA() {
        handleEvent(Event.BUTTON_A);
    }

    public void clickButtonB() {
        handleEvent(Event.BUTTON_B);
    }

    public void clickButtonC() {
        handleEvent(Event.BUTTON_C);
    }

    public void clickEnter() {
        Terminal.lastInput = number;
        handleEvent(Event.ENTER_NUMBER);
        this.number = "";
    }

    public void useCardSlot() {
        Terminal.cardId = "1234";
        handleEvent(Event.ATM_CARD_SWIPE);
    }

    public void useCardSlotInvalid() {
        Terminal.cardId = "5678";
        handleEvent(Event.ATM_CARD_SWIPE);
    }

    public void useDepositSlot() {
        handleEvent(Event.INSERT_DEPOSIT_ENVELOPE);
    }

    public void useCashSlot() {
        handleEvent(Event.REMOVE_CASH);
    }

    public void clickNumber(ActionEvent fxEvent) {
        this.number += ((Button) fxEvent.getTarget()).getText();
        this.currentScreen.updateNumber(screenUI, number);
    }

    public void clickClear() {
        this.number = "";
        this.currentScreen.updateNumber(screenUI, "");
    }

}
