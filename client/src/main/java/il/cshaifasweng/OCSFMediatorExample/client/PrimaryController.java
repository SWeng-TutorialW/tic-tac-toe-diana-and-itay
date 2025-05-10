package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.control.TextField;
import java.util.concurrent.TimeUnit;


public class PrimaryController {
	private SimpleClient client=null;
	@FXML
	public Button joinGameBtn;
	@FXML
	public Label welcomeLbl;
	private boolean isWaiting = false;
	@FXML
	private TextField gameAddTxtBox;
	@FXML
	private TextField portTxtBox;

	@FXML
	void switchToGame(ActionEvent event) throws Exception {
		String gameAddress = gameAddTxtBox.getText(); // ip
		client = SimpleClient.createClient(gameAddress, Integer.parseInt(portTxtBox.getText()));
		try{
			client.openConnection();
			if(!client.isConnected()){
				throw new IOException("Could not connect to server");}
			client.sendToServer("add client"); // the client is going to be one of the subscribers of SimpleServer
			Thread.sleep(100); // wait for the server to add the client
			isWaiting = true;
			client.sendToServer("#joined");
			joinGameBtn.setDisable(true);
			welcomeLbl.setText("Waiting for other player to join...");


		}catch(Exception e){
			welcomeLbl.setText(e.toString());
			SimpleClient.removeClient();
			client=null;
		}
	}

	@Subscribe
	public void joinGame(PlayerJoinedEvent event) {
		Platform.runLater(() -> {
			if (event.getHasJoined() && isWaiting) { // if we are waiting and the other player joined, start the game
			try {
				EventBus.getDefault().unregister(this);
				App.setRoot("secondary");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}});

	}



	@FXML
    void sendWarning(ActionEvent event) {
    	try {
			SimpleClient.getClient().sendToServer("#warning");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@FXML
	void initialize(){
		EventBus.getDefault().register(this);
	}
}
