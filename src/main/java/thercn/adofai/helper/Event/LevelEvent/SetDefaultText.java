package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;

public class SetDefaultText implements BasicEvent {

    private int duration;
    private String ease;
    private String eventTag;
    private double angleOffset;
    
    private int floor;

    public SetDefaultText(int duration, String ease, String eventTag, double angleOffset, int floor) {
        this.duration = duration;
        this.ease = ease;
        this.eventTag = eventTag;
        this.angleOffset = angleOffset;
        this.floor = floor;
    }

    public int getDuration() {
        return duration;
    }

    public String getEase() {
        return ease;
    }

    public String getEventTag() {
        return eventTag;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public EventType getEventType() {
        return EventType.SetDefaultText;
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