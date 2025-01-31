package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.AngleOffset;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.EventType;

public class SetFilter implements BasicEvent, AngleOffset, EventTag {

    private String filter;
    private int intensity;
    private int duration;
    private String ease;
    private String eventTag;
    private double angleOffset;
    
    private int floor;
    private boolean enabled;
    private boolean disableOthers;

    public SetFilter(String filter, int intensity, int duration, String ease, String eventTag, double angleOffset, int floor, boolean enabled, boolean disableOthers) {
        this.filter = filter;
        this.intensity = intensity;
        this.duration = duration;
        this.ease = ease;
        this.floor = floor;
        this.enabled = enabled;
        this.disableOthers = disableOthers;
        this.angleOffset = angleOffset;
        this.eventTag = eventTag;

    }

    public String getFilter() {
        return filter;
    }

    public int getIntensity() {
        return intensity;
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
        return EventType.SetFilter;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    public boolean getDisableOthers() {
        return disableOthers;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}