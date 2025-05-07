package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.image.Image;




public class SecondaryController {

    @FXML
    private ImageView btn0;

    @FXML
    private ImageView btn1;

    @FXML
    private ImageView btn2;

    @FXML
    private ImageView btn3;

    @FXML
    private ImageView btn4;

    @FXML
    private ImageView btn5;

    @FXML
    private ImageView btn6;

    @FXML
    private ImageView btn7;

    @FXML
    private ImageView btn8;

    @FXML
    private Label playLbl;

    @FXML
    private Label playerXOLbl;

    private ImageView[] btns;

    private Image getPlayerImage(int playerId) {
        String path;
        switch(playerId)
        {
            case 2:
                path = "/Images/circle.png";
                break;
            case 1:
                path= "/Images/xpic.png";
                break;
            default:
                return null; // player hasn't been initialized yet
        }
        try{return new Image(getClass().getResourceAsStream(path));}catch(Exception e){
            System.out.println("Error loading image: " + e.getMessage());
            return null;
        }

    }

    @Subscribe
    public void winnerWinnerChickenDinner(String winner) {
        Platform.runLater(() -> {
            if (winner.equals("#winner X")) {
                playLbl.setText("Player X wins!");
                playLbl.setFont(Font.font("System", 22));
                try{SimpleClient.getClient().closeConnection();}
                catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (winner.equals("#winner O")) {
                playLbl.setText("Player O wins!");
                playLbl.setFont(Font.font("System", 22));
                try{SimpleClient.getClient().closeConnection();}
                catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(winner.equals("[GAME INFO] DRAW!")) {
                playLbl.setText("It's a draw!");
                playLbl.setFont(Font.font("System", 22));
            }
            for (ImageView btn : btns) {
                btn.setDisable(true); // disable all buttons because someone won
            }
        });
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");

    }

    // now if we post anything on the event bus that is of type GameUpdateEvent, this method will be called automatically
    @Subscribe
    public void updateGameGrid(GameUpdateEvent event) { // we get a GameUpdateEvent with the new game grid.
        if(event.getEventString().contains("#"))
        Platform.runLater(() -> {
        int[] gameGrid = event.getGrid(); // get the new game grid
        for (int i = 0; i < gameGrid.length; i++) {
            if (gameGrid[i] == 1) { // if the button is clicked by player 1
                btns[i].setImage(getPlayerImage(1)); // set X
            } else if (gameGrid[i] == 2) { // if the button is clicked by player 2
                btns[i].setImage(getPlayerImage(2)); // set O
            }
        }});
    }

    @FXML
    void btnClicked(MouseEvent event) {
        Platform.runLater(() -> { // I hope it works ( it does :) )
        Node src = (Node) event.getSource(); // source of the event (who caused it)
        int btnIndex = Integer.parseInt(src.getId().charAt(3)+"");// the index of the button (btn0, btn1, ... btn8)

        int playerId = SimpleClient.getClient().getId();

        if(btns[btnIndex].getImage() == null && SimpleClient.getClient().isMyTurn()) { // if the button is empty

            String playerType = (playerId == 1) ? "X" : "O";
            btns[btnIndex].setImage(getPlayerImage(playerId));
            try {
                SimpleClient.getClient().sendToServer("#updateGameGrid " + playerType + btnIndex);
                btns[btnIndex].setImage(getPlayerImage(playerId));
                SimpleClient.getClient().setMyTurn(false); // after we clicked, it's not our turn anymore (IMPORTANT!)
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("This button is already clicked");
        }});

    }

    @FXML
    void initialize() {
        btns = new ImageView[]{btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8}; // initialize the buttons array
        EventBus.getDefault().register(this);

        int playerId = SimpleClient.getClient().getId();
        String playerType = playerId == 1 ? "X" : "O";
        playerXOLbl.setText("You are player: " + playerType);
        Platform.runLater(() -> {
            Stage stage = (Stage) btn0.getScene().getWindow(); // choose a random button and get its source
            stage.setWidth(500);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.setTitle("Tic-Tac-Toe! | Match Started");
        });
    }


}