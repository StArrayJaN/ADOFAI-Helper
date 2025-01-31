package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.AngleOffset;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.EventType;
import thercn.adofai.helper.Event.LevelEventEnum.Vector2;

public class ScreenScroll implements BasicEvent, AngleOffset, EventTag {

    private String eventTag;
    private Vector2 scroll;
    private double angleOffset;
    
    private int floor;

    public ScreenScroll(String eventTag, Vector2 scroll, double angleOffset, int floor) {
        this.eventTag = eventTag;
        this.scroll = scroll;
        this.angleOffset = angleOffset;
        this.floor = floor;
    }

    public String getEventTag() {
        return eventTag;
    }

    public Vector2 getScroll() {
        return scroll;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public EventType getEventType() {
        return EventType.ScreenScroll;
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