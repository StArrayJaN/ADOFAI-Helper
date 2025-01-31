package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;

public class ScaleRadius implements BasicEvent {

    private int scale;
    
    private int floor;

    public ScaleRadius(int scale, int floor) {
        this.scale = scale;
        this.floor = floor;
    }

    public int getScale() {
        return scale;
    }

    public EventType getEventType() {
        return EventType.ScaleRadius;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean getEnabled(){
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        return;
    }

}