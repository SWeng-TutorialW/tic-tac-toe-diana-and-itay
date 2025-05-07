package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;


public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	private static int id=-1; // id of the client (X or O ==> 1 or 0)
	private static boolean myTurn = false;
	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else if(msg.toString().contains("#myTurn")){myTurn = true;}
		else if (msg.getClass().equals(GameUpdateEvent.class)) { //
				if(msg.toString().contains("[GAME INFO] VICTORY!")){ // msg would be "[GAME INFO] VICTORY! X/O" - last char is the winner
					String winner = msg.toString().substring(msg.toString().length()-1);
					if(winner.equals("X")){
						EventBus.getDefault().post("#winner X");
					}
					else if(winner.equals("O")){
						EventBus.getDefault().post("#winner O");
					}
					else{
						throw new NumberFormatException();
					}

				}else {
					GameUpdateEvent event = (GameUpdateEvent) msg; // safe downcast
					EventBus.getDefault().post(event); // after we updated in the server, we update locally (for each client)
				}
			}
		else if(msg.getClass().equals(PlayerJoinedEvent.class)){
			EventBus.getDefault().post(new PlayerJoinedEvent(true));
		}
		else if(msg.toString().contains("[GAME INFO] You are player: ")){
			String player = msg.toString().substring(msg.toString().length()-1);
			if(player.equals("X")){
				id = 1;
			}
			else if(player.equals("O")){
				id = 2;
			}
			else{
				throw new NumberFormatException();
			}
			System.out.println("[GAME INFO] You are player: " + player + " With id: " + id);
		}
		else if(msg.toString().contains("[GAME INFO] Start First")){
			myTurn= true;
		}
		else{
			System.out.println(msg); // shouldn't happen but just in case.
		}
	}
	public boolean isMyTurn() {
		return myTurn;
	}
	public void setMyTurn(boolean myTurn) {
		SimpleClient.myTurn = myTurn;
	}

	public static SimpleClient getClient() { // pretty pointless if I have createClient
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


	public int getId() {
		return id;
	}
}



