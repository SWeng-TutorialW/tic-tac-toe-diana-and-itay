package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class GameUpdateEvent implements Serializable {
    private String eventString;
    private int[][] board = {{0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}};
    int id;

    private int row;
    private int col;
    private String symbol;
    private boolean myTurn;

    public int[][] getBoard() {
        return board;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean getMyTurn() {
        return myTurn;
    }

    public String getEventString() {
        return eventString;
    }
    public GameUpdateEvent(String eventString, String symbol, int row, int col, boolean myTurn) {
        this.eventString = eventString;
        this.symbol = symbol;
        this.row = row;
        this.col = col;
        this.myTurn = myTurn;

    }

    public GameUpdateEvent(String eventString) {
        this.eventString = eventString;
    }

    @Override
    public String toString() {
        return eventString;
    }

}