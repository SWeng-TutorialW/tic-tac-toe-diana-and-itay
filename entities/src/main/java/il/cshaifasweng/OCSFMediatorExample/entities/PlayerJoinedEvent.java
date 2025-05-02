package il.cshaifasweng.OCSFMediatorExample.entities;
import java.io.Serializable;

public class PlayerJoinedEvent implements Serializable{
    private boolean hasJoined;

    public PlayerJoinedEvent(boolean hasJoined) {
        this.hasJoined = hasJoined;
    }
    public boolean getHasJoined() {
        return hasJoined;
    }
}
