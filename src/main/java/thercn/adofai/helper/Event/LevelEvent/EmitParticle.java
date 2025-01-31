package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.AngleOffset;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.EventType;

public class EmitParticle implements BasicEvent, AngleOffset, EventTag{

    private String eventTag;
    private int count;
    private double angleOffset;
    
    private String tag;
    private int floor;


    public EmitParticle(String tag, int floor, String eventTag, int count, double angleOffset) {
        this.tag = tag;
        this.floor = floor;
        this.eventTag = eventTag;
        this.count = count;
        this.angleOffset = angleOffset;
    }

    public String getEventTag() {
        return eventTag;
    }

    public int getCount() {
        return count;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public EventType getEventType() {
        return EventType.EmitParticle;
    }

    public String getTag() {
        return tag;
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