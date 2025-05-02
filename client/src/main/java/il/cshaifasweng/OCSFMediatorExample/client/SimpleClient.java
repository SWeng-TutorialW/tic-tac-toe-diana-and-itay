package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.GameUpdateEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.PlayerJoinedEvent;
import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;


public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

//	private static int port = 3000;
//	private static String host = "localhost";

	private int id = -1 ;
	private static boolean myTurn = false ;

	private SimpleClient(String host, int port) {
		super(host, port);
	}


	@Override
	protected void handleMessageFromServer(Object msg) {
		String message = msg.toString();
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		if (msg.getClass().equals(GameUpdateEvent.class)) {
			if (message.startsWith("[GAME INFO] Tie")) {
				System.out.println(message);
				EventBus.getDefault().post("#tie");
			} else if (message.startsWith("[GAME INFO] Win")) {
				System.out.println(message);
				String winner = message.substring(message.length() - 1);
				if (winner.equals("X")) {
					EventBus.getDefault().post("#winner X");
				} else if (winner.equals("O")) {
					EventBus.getDefault().post("#winner O");
				}
			}else if (message.startsWith("[GAME INFO] Start First")) {
				System.out.println(message);
				SimpleClient.getClient().setMyTurn(true);
				EventBus.getDefault().post("#startFirst");
			}
			else if (message.startsWith("[GAME INFO] Start Second")) {
				System.out.println(message);
				SimpleClient.getClient().setMyTurn(false);
				EventBus.getDefault().post("#startSecond");
			}else if (message.startsWith("[GAME INFO] You are player")) {
				System.out.println(message);
				String playerSymbol = message.substring(message.length() - 1);
				if (playerSymbol.equals("X")) {
					id = 1;
				} else if (playerSymbol.equals("O")) {
					id = 2;
				}
				EventBus.getDefault().post("#playerSymbol " + playerSymbol);
			}
			else if (message.startsWith("[GAME INFO] Client Disconnected")) {
				System.out.println(message);
				SimpleClient.getClient().setMyTurn(false);
				EventBus.getDefault().post("#clientDisconnected");
			}

			else {
				GameUpdateEvent event = (GameUpdateEvent) msg;
				EventBus.getDefault().post(event);
			}
		}
		else if (msg.getClass().equals(PlayerJoinedEvent.class)) {
			EventBus.getDefault().post(msg);
				//EventBus.getDefault().post(new PlayerJoinedEvent(true));
		}
			else {
			System.out.println("[ERROR] Unrecognized message: " + message + "," + msg.getClass().getSimpleName());
		}

	}

		public boolean getMyTurn() {
		return myTurn;
		}

		public void setMyTurn(boolean myTurn) {
		SimpleClient.myTurn = myTurn;
		}


	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

	public static SimpleClient createClient(String host, int port) {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
		return client;
	}

	public int getId(){
		return id;
	}

//	public void setHostAndPort(String newHost, int newPort) {
//		port=newPort;
//		host=newHost;
//		client = new SimpleClient(host, port);
//	}

}
