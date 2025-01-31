package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.AngleOffset;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.EventType;

public class SetText implements BasicEvent, AngleOffset, EventTag {

    private String eventTag;
    private double angleOffset;
    
    private String tag;
    private int floor;
    private String decText;

    public SetText(String eventTag, double angleOffset, String tag, int floor, String decText) {
        this.eventTag = eventTag;
        this.angleOffset = angleOffset;
        this.tag = tag;
        this.floor = floor;
        this.decText = decText;
    }

    public String getEventTag() {
        return eventTag;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public EventType getEventType() {
        return EventType.SetText;
    }

    public String getTag() {
        return tag;
    }

    public int getFloor() {
        return floor;
    }

    public String getDecText() {
        return decText;
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