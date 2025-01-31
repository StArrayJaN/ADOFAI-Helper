package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;
import thercn.adofai.helper.Event.LevelEventEnum.Vector2;

public class FreeRoamRemove implements BasicEvent {

    private Vector2 size;
    
    private Vector2 position;
    private int floor;

    public FreeRoamRemove(Vector2 position, int floor, Vector2 size) {
        this.position = position;
        this.floor = floor;
        this.size = size;
    }

    public Vector2 getSize() {
        return size;
    }

    public EventType getEventType() {
        return EventType.FreeRoamRemove;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean getEnabled() {
        return true;
    }
    @Override
    public void setEnabled(boolean enabled) {
        return;
    }
}