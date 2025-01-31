package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.AngleOffset;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.EventType;

public class SetParticle implements BasicEvent, AngleOffset, EventTag {

    private int duration;
    private String ease;
    private String eventTag;
    private String targetMode;
    private double angleOffset;
    
    private String tag;
    private int floor;

    public SetParticle(int duration, String ease, String eventTag, String targetMode, double angleOffset, String tag, int floor) {
        this.duration = duration;
        this.ease = ease;
        this.eventTag = eventTag;
        this.targetMode = targetMode;
        this.angleOffset = angleOffset;
        this.tag = tag;
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

    public String getTargetMode() {
        return targetMode;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public EventType getEventType() {
        return EventType.SetParticle;
    }

    public String getTag() {
        return tag;
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