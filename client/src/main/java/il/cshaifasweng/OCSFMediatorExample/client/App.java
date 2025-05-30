package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private Stage stage;
    @Override
    public void start(Stage stage) throws IOException {

    	EventBus.getDefault().register(this);

        scene = new Scene(loadFXML("primary"), 450, 380);
        this.stage = stage;
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Tic-Tac-Toe!");
        stage.show();


    }

 /*   @Subscribe
    public void onResult(GameUpdateEvent event) {
        	Platform.runLater(() -> {
                try {

                    if(event.getEventString().contains("DRAW"))
                    {
                        Alert alert = new Alert(AlertType.INFORMATION, "It's a draw!");
                        alert.setTitle("Game Over");
                        alert.show();

                        client.sendToServer("#removeClient");
                        client.closeConnection();

                        Thread.sleep(5000);
                        stage.setScene(scene);
                        EventBus.getDefault().unregister(this);
                    }
                    else if(event.getEventString().contains("VICTORY")){
                        String winner = event.getEventString().substring(event.getEventString().length()-1);
                        String alertStr="";
                        if(winner.equals("X") && client.getId() == 1){
                            alertStr = "You win!";
                        }
                        else if(winner.equals("O") && client.getId() == 2){
                            alertStr = "You win!";
                        }
                        else{
                            alertStr = "YOU LOST!";
                        }
                        Alert alert = new Alert(AlertType.INFORMATION, alertStr);
                        alert.setTitle("Game Over");
                        alert.show();
                        client.sendToServer("#removeClient");
                        client.closeConnection();
                        Thread.sleep(5000);
                        stage.setScene(scene);
                        EventBus.getDefault().unregister(this);
                    }


                }
            catch (Exception e) {
                e.printStackTrace();
            }});
    }*/
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    


    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub

    	EventBus.getDefault().unregister(this);

        SimpleClient.getClient().sendToServer("#removeClient");
        SimpleClient.getClient().closeConnection();
		super.stop();
	}
    
    @Subscribe
    public void onWarningEvent(WarningEvent event) {
    	Platform.runLater(() -> {
    		Alert alert = new Alert(AlertType.WARNING,
        			String.format("Message: %s\nTimestamp: %s\n",
        					event.getWarning().getMessage(),
        					event.getWarning().getTime().toString())
        	);
        	alert.show();
    	});
    	
    }

	public static void main(String[] args) {
        launch();
    }

}