package il.cshaifasweng.OCSFMediatorExample.client;


import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.PlayerJoinedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Sample Skeleton for 'primary.fxml' Controller Class
 */

public class PrimaryController {

	@FXML // fx:id="joinButton"
	private Button joinButton; // Value injected by FXMLLoader

	@FXML // fx:id="ipField"
	private TextField ipField; // Value injected by FXMLLoader

	@FXML // fx:id="portField"
	private TextField portField; // Value injected by FXMLLoader

	@FXML // fx:id="welcomeLbl"
	private Label welcomeLbl; // Value injected by FXMLLoader

	private SimpleClient client;
	private boolean isWaiting = false;

	@FXML
	void switchToGame(ActionEvent event) {

		String ip = ipField.getText().trim();        //get the text from the ipField and trim any leading or trailing spaces
		String portTxt = portField.getText().trim();
		int port = Integer.parseInt(portTxt);
		client = SimpleClient.createClient(ip, port);

		if (ip.isEmpty() || portTxt.isEmpty()) {        //check if the fields are empty
			welcomeLbl.setText("The IP address and port number are required. Please enter them and try again.");
		} else {        //else, it is not empty, so we can connect to the server
			try {

				//check if the ip entered is valid.
			//	InetAddress address = InetAddress.getByName(ip);
				client.openConnection();
				if (!client.isConnected()) {
					throw new IOException("Could not connect to server");
				}
				client.sendToServer("#addClient");
				Thread.sleep(500);
				isWaiting = true;
				joinButton.setDisable(true);
				welcomeLbl.setText("Waiting for opponent...");
				client.sendToServer(new Warning("I'm waiting!!!"));
				client.sendToServer("#joined");

			} catch (Exception e) {
				welcomeLbl.setText(e.toString());
			}
		}
	}


	@Subscribe
	public void joinGame(PlayerJoinedEvent event) {
		Platform.runLater(() -> {
			if (event.getHasJoined() && isWaiting) {
				try {
					EventBus.getDefault().unregister(this);
					App.setRoot("secondary");
					//client.sendToServer("switchToGame");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
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
	void initialize() {

		EventBus.getDefault().register(this);
	}
}


//try {
//			//joinButton.setDisable(false);
//			SimpleClient.getClient().sendToServer("#add client");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}