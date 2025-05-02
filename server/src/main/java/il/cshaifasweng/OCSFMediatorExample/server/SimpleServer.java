package il.cshaifasweng.OCSFMediatorExample.server;


import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.entities.PlayerJoinedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.GameUpdateEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>(2);
	private int[][] board = new int[3][3];
	//private boolean gameStarted = false;


	private void resetBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = 0;
			}
		}
	}

	public SimpleServer(int port) {
		super(port);
		System.out.println("Server started on port " + port);
	}


	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		System.out.println(msgString);
		try {
			if (msgString.startsWith("#warning")) {        //maybe need to fix this
				Warning warning = new Warning("Warning From Server!");
				client.sendToClient(warning);
				System.out.format("[WARNING] SERVER Sent Warning to CLIENT %s\n", client.getInetAddress().getHostAddress());
			} else if (msgString.startsWith("#addClient")) {
				if (SubscribersList.size() == 2) {
					Warning warning = new Warning("Warning From Server: Game is full!");
					client.sendToClient(warning);
					System.out.format("[WARNING] SERVER sent Warning to CLIENT %s\n", client.getInetAddress().getHostAddress());
					return;
				}
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				System.out.format("[SERVER] CLIENT Joined Successfully %s\n", client.getInetAddress().getHostAddress());

			} else if (msgString.startsWith("#joined")) {
				if (SubscribersList.size() == 2) {
					sendToAllClients(new PlayerJoinedEvent(true));
					resetBoard();
					Thread.sleep(800);
					//gameStarted = true;
					// SubscribersList.get(0) = X , SubscribersList.get(1) = O
					SubscribedClient playerX = SubscribersList.get(0);
					SubscribedClient playerO = SubscribersList.get(1);

					playerX.getClient().sendToClient(new GameUpdateEvent("[GAME INFO] You are player X"));
					playerO.getClient().sendToClient(new GameUpdateEvent("[GAME INFO] You are player O"));

					Random rand = new Random();
					int startFirst = rand.nextInt(1, 3);        //1 or 2

					//if(startFirst == 1) , X is the first player, else O is the first player.
					GameUpdateEvent startFirstEvent = new GameUpdateEvent("[GAME INFO] Start First");
					SubscribersList.get(startFirst - 1).getClient().sendToClient(startFirstEvent);
					//System.out.println("[GAME INFO] " + ((startFirst==1) ? "X" : "O" ) + " Start First");


					GameUpdateEvent startSecondEvent = new GameUpdateEvent("[GAME INFO] Start Second");
					SubscribersList.get(1 - (startFirst - 1)).getClient().sendToClient(startSecondEvent);
					//System.out.println("[GAME INFO] " + ((startFirst==1)? "O" : "X" ) + " Start Second");
				}
			} else if (msgString.startsWith("#updateBoard")) {

				String[] parts = msgString.split(" "); // [ "#updateBoard", "X", "12" ]
				String playerSymbol = parts[1]; // "X" OR "O"
				String pos = parts[2];    // "12"

				int row = Character.getNumericValue(pos.charAt(0));
				int col = Character.getNumericValue(pos.charAt(1));
				int playerId = playerSymbol.equals("X") ? 1 : 2;
				int opponentId = playerId == 1 ? 2 : 1;

				board[row][col] = playerId;        //update board
				for (int i = 0; i < SubscribersList.size(); i++) {
					SubscribedClient sc = SubscribersList.get(i);
					boolean isMyTurn = i == opponentId - 1;
					GameUpdateEvent event = new GameUpdateEvent("[GAME UPDATE] Update Board", playerSymbol, row, col, isMyTurn);
					sc.getClient().sendToClient(event);
				}

				//check for victory/tie
				int winner = getWinner();

				if (winner != 0) {        //Check if there is a winner, if so, send a message to all clients and stop the game.
                    GameUpdateEvent event;
                    if (winner == 1) {
                        event = new GameUpdateEvent("[GAME INFO] Win X");
						sendToAllClients(event);
						//System.out.println("[GAME INFO] Win X");
                    } else {		//winner == 2
                        event = new GameUpdateEvent("[GAME INFO] Win O");
						sendToAllClients(event);
						//System.out.println("[GAME INFO] Win O");
                    }
                    return;
                    //resetBoard();

				}
				if (isBoardFull()) {
					GameUpdateEvent event = new GameUpdateEvent("[GAME INFO] Tie");
					sendToAllClients(event);
					//System.out.println("[GAME INFO] Tie");
					//resetBoard();
				}

			} else if (msgString.startsWith("#removeClient")) {
				SubscribedClient toRemove = null;

				System.out.println("Client disconnected: " + client.getInetAddress().getHostAddress());


				for (SubscribedClient sc : SubscribersList) {
					if (sc.getClient() == client) {
						toRemove = sc;
						break;
					}
				}

				if (toRemove != null) {
					SubscribersList.remove(toRemove);
				}

					if (SubscribersList.size() == 1) {
						GameUpdateEvent disconnectEvent = new GameUpdateEvent("[GAME INFO] Client Disconnected");
						SubscribersList.getFirst().getClient().sendToClient(disconnectEvent);
					}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error sending message : " + e.getMessage());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isBoardFull() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == 0) return false;
			}
		}
		return true;
	}

	private int getWinner() {
		for (int i = 0; i < 3; i++) {
			if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != 0) {
				return board[i][0];
			} else if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != 0) {
				return board[0][i];
			}
		}
		if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != 0) {
			return board[0][0];
		} else if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != 0) {
			return board[0][2];
		} else return 0;
	}

	@Override
	public void sendToAllClients(Object message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

