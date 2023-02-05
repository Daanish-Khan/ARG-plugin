package arg.uottawa.arg.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KeyFoundEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final int eventNum;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public KeyFoundEvent(int eventNum) {
        this.eventNum = eventNum;
    }

    public int getEventNum() {
        return this.eventNum;
    }

}
