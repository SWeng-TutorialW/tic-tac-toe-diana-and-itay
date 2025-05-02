package il.cshaifasweng.OCSFMediatorExample.client;


import java.io.IOException;
import il.cshaifasweng.OCSFMediatorExample.entities.GameUpdateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Sample Skeleton for 'secondary.fxml' Controller Class
 */


public class SecondaryController {

    @FXML // fx:id="button00"
    private Button button00; // Value injected by FXMLLoader

    @FXML // fx:id="button01"
    private Button button01; // Value injected by FXMLLoader

    @FXML // fx:id="button02"
    private Button button02; // Value injected by FXMLLoader

    @FXML // fx:id="button10"
    private Button button10; // Value injected by FXMLLoader

    @FXML // fx:id="button11"
    private Button button11; // Value injected by FXMLLoader

    @FXML // fx:id="button12"
    private Button button12; // Value injected by FXMLLoader

    @FXML // fx:id="button20"
    private Button button20; // Value injected by FXMLLoader

    @FXML // fx:id="button21"
    private Button button21; // Value injected by FXMLLoader

    @FXML // fx:id="button22"
    private Button button22; // Value injected by FXMLLoader

    @FXML // fx:id="headlineLabel"
    private Label headlineLabel; // Value injected by FXMLLoader

    //@FXML // fx:id="newGameButton"
    //private Button newGameButton; // Value injected by FXMLLoader

    @FXML // fx:id="statusLabel"
    private Label statusLabel; // Value injected by FXMLLoader


    private Button[][] buttons;

    private static boolean gameEnded = false;

    //private String serverIp;


    @FXML
    void initialize() {
        buttons = new Button[][]{
                {button00, button01, button02},
                {button10, button11, button12},
                {button20, button21, button22}
        };

        EventBus.getDefault().register(this);
        System.out.println("Registered to EventBus!");

        Platform.runLater(() -> {
            Stage stage = (Stage) button00.getScene().getWindow();
            stage.setTitle("Tic Tac Toe");
            stage.setResizable(false);
            stage.setWidth(500);
            stage.setHeight(360);
        });
        newGame();
    }

    private void newGame() {
        for (int i = 0; i < 3; i++) {           //initialize the board with empty buttons
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setDisable(false);
            }
        }
        gameEnded = false;
    }


    @FXML
    public void handleMove(ActionEvent event) {

        Button clicked = (Button) event.getSource();
        int playerId = SimpleClient.getClient().getId();
        String buttonIndex = clicked.getId().substring(6); // "12"
        int row = Character.getNumericValue(buttonIndex.charAt(0));
        int col = Character.getNumericValue(buttonIndex.charAt(1));
        boolean myTurn = SimpleClient.getClient().getMyTurn();

        if (myTurn && buttons[row][col].getText().isEmpty() && !gameEnded) {

            String playerSymbol = playerId == 1 ? "X" : "O";

            try {
                SimpleClient.getClient().sendToServer("#updateBoard " + playerSymbol + " " + buttonIndex);
               // System.out.println("SECONDARY CONTROLLER sent message to SERVER: #updateBoard " + playerSymbol + " " + buttonIndex);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error sending message to server: " + e.getMessage());
            }
        }

    }

    @Subscribe
    public void updateBoard(GameUpdateEvent event) {
        Platform.runLater(() -> {
            if (!gameEnded) {
                int row = event.getRow();
                int col = event.getCol();
                String symbol = event.getSymbol();
                boolean myTurn = event.getMyTurn();

                System.out.println("[GAME UPDATE] " + symbol  + " made a move : [" + row + "][" + col + "]."  );
                buttons[row][col].setText(symbol);
                buttons[row][col].setDisable(true);
                SimpleClient.getClient().setMyTurn(myTurn);
                statusLabel.setText(myTurn ? "Your turn." : "Opponent's turn.");
            }
        });
    }

    @Subscribe
    public void GameMessageEvent(String message) {

        Platform.runLater(() -> {
            if (message.startsWith("#startFirst")) {
                statusLabel.setText("Your turn.");
            } else if (message.startsWith("#startSecond")) {
                statusLabel.setText("Opponent's turn.");
            } else if (message.startsWith("#playerSymbol")) {
                String playerSymbol = message.substring("#playerSymbol".length()).trim();
                headlineLabel.setText("Game started! You are " + playerSymbol + ".");
            } else if (message.startsWith("#winner X")) {
                statusLabel.setText("Player X won!");
                headlineLabel.setText("End of game!");
                endGame();
            } else if (message.startsWith("#winner O")) {
                statusLabel.setText("Player O won!");
                headlineLabel.setText("End of game!");
                endGame();
            } else if (message.startsWith("#tie")) {
                statusLabel.setText("It's a tie!");
                headlineLabel.setText("End of game!");
                endGame();
            } else if (message.startsWith("#clientDisconnected")) {
                headlineLabel.setText("Game ended.");
                headlineLabel.setText("Please exit the game by clicking the 'Exit' button.");
                endGame();
            } else {
                System.out.println( message + " is not a valid message");
            }
        });
    }

    private void endGame() {
        gameEnded = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setDisable(true);
            }
        }
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

}