package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.client.GameUpdateEvent;
import il.cshaifasweng.OCSFMediatorExample.client.PlayerJoinedEvent;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static int totalFilled = 0; // how many presses were there
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>(2); // only 2 players
	private static int[] gameGrid = {0,0,0,0,0,0,0,0,0}; // init grid for the game


	public SimpleServer(int port) {
		super(port);
		System.out.println("[SERVER] Server is Running...\n");

	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();

		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning From Server!");
			try {
				client.sendToClient(warning);
				System.out.format("[WARNING] Sent Warning to Client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		else if(msgString.startsWith("add client")){
			if(SubscribersList.size() == 2){
				try {
					client.sendToClient("Game is full");
					System.out.format("Client %s tried to join but game is full\n", client.getInetAddress().getHostAddress());
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("[GAME INFO] You are player: " + (SubscribersList.size() == 1 ? "X" : "O"));
				System.out.format("[SERVER] Client Joined Successfully: %s\n", client.getInetAddress().getHostAddress());
				if(SubscribersList.size() == 2){
					Random rnd = new Random();
					int startsFirst = rnd.nextInt(1,3); // 1 or 2 (X or O)
					SubscribersList.get(startsFirst-1).getClient().sendToClient("[GAME INFO] Start First"); // send to the one who starts first
				}
			} catch (IOException e) {
				throw new RuntimeException(e); // if adding the client fails (if a 3rd client tries to add itself)
			}
		}

		else if(msgString.startsWith("#updateGameGrid")){ //message example: #updateGameGrid X6 / #updateGameGrid O5
			int placedOn = Integer.parseInt(String.valueOf(msgString.charAt(msgString.length()-1))); // 0-8
			char playerChar = msgString.charAt(msgString.length() - 2); // X or O
			int playerId = (playerChar == 'X') ? 1 : 2; // 1 or 2
			try {

				if (playerChar == 'X') {
					gameGrid[placedOn] = 1;
				} else if (playerChar == 'O') {
					gameGrid[placedOn] = 2;
				} else {
					throw new NumberFormatException(); // shouldn't happen

				}
				int oppositePlayer = (playerId == 1) ? 2 : 1; // opposite player
				SubscribersList.get(oppositePlayer - 1).getClient().sendToClient("#myTurn");
				sendToAllClients(new GameUpdateEvent("Placed an " + playerChar + " on " + placedOn, gameGrid)); // everyone updates their grid
				System.out.println("Placed an " + playerChar + " on " + placedOn);
				totalFilled++;
				// now check if anybody is a winner
				// Check for win condition
				// Check rows

				boolean isVictory = false;
					// rows
					for (int i = 0; i < 3; i++) {
						if (gameGrid[i * 3] == playerId && gameGrid[i * 3 + 1] == playerId && gameGrid[i * 3 + 2] == playerId) {
							isVictory=true;
							break;
						}
					}


					// columns
					for (int j = 0; j < 3; j++) {
						if (gameGrid[j] == playerId && gameGrid[j + 3] == playerId && gameGrid[j + 6] == playerId) {
							isVictory = true;
							break;
						}
					}

					// Check diagonals
					if (gameGrid[0] == playerId && gameGrid[4] == playerId && gameGrid[8] == playerId)
						isVictory = true;


					if (gameGrid[2] == playerId && gameGrid[4] == playerId && gameGrid[6] == playerId)
						isVictory = true;


					if(isVictory) sendToAllClients("[GAME INFO] VICTORY! " + ((playerId == 1) ? "X" : "O"));


					if(totalFilled == 9){
						sendToAllClients("[GAME INFO] DRAW!");
					}

			}
			catch(Exception e){System.out.println("Invalid placed on "+msgString);}
		}

		else if(msgString.startsWith("#joined")){
			if(SubscribersList.size() == 2){

				sendToAllClients(new PlayerJoinedEvent(true));
			}

		}
		else if(msgString.startsWith("#resetGameGrid")){
			gameGrid = new int[]{0,0,0,0,0,0,0,0,0};
			sendToAllClients(gameGrid);
		}
		else if(msgString.startsWith("#removeClient")){

			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						System.out.println("Client removed: " + client.getInetAddress().getHostAddress());
						break;
					}
				}
			}
		}

	}
	@Override // I overridden this
	public void sendToAllClients(Object msg){
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(msg);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
