package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
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
    private SimpleClient client;
    private static Stage primaryStage;

    @Override
    //need to add a headline
    public void start(Stage stage) throws IOException {
    	EventBus.getDefault().register(this);
    	//client = SimpleClient.getClient();
    	//client.openConnection();
        scene = new Scene(loadFXML("primary"), 360, 250);
        //primaryStage = stage;
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe");
        stage.show();
    }

    void setClient(SimpleClient client) {
        this.client = client;
    }

    public static void setScene(Scene scene) {
            primaryStage.setScene(scene);
    }

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
        try{
            EventBus.getDefault().unregister(this);
            if(client==null) {
                client = SimpleClient.getClient();
            }
            if(client != null && client.isConnected()) {
                client.sendToServer("#removeClient");
                client.closeConnection();
            }
            super.stop();
        }catch(Exception e) {
            e.printStackTrace();
        }
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