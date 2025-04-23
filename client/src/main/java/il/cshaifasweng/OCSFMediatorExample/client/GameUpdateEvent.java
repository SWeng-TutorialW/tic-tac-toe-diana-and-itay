package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.Serializable;
public class GameUpdateEvent implements Serializable{


    private String eventString;
    private int[] grid = {0,0,0,0,0,0,0,0,0};
    int id;

    public GameUpdateEvent(String eventString, int[] boardGrid) {
        this.eventString = eventString;
        this.grid = boardGrid;
    }
    public GameUpdateEvent(String eventString) {
        this.eventString = eventString;
    }
    public String getEventString() {
        return eventString;
    }

    public int[] getGrid() {
        return grid;
    }



}
