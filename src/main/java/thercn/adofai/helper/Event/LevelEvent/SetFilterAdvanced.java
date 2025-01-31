package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.AngleOffset;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.EventType;

public class SetFilterAdvanced implements BasicEvent, AngleOffset, EventTag {

    private String plane;
    private String ease;
    private double angleOffset;
    private String targetType;
    private String targetTag;
    
    private boolean enabled;
    private boolean disableOthers;
    private String filter;
    private int duration;
    private String filterProperties;
    private String eventTag;
    private int floor;

    public SetFilterAdvanced(String plane, String ease, double angleOffset, String targetType, String targetTag, boolean enabled, boolean disableOthers, String filter, int duration, String filterProperties, String eventTag, int floor) {
        this.plane = plane;
        this.ease = ease;
        this.angleOffset = angleOffset;
        this.targetType = targetType;
        this.targetTag = targetTag;
        this.enabled = enabled;
        this.disableOthers = disableOthers;
        this.filter = filter;
        this.duration = duration;
        this.filterProperties = filterProperties;
        this.eventTag = eventTag;
        this.floor = floor;
    }

    public String getPlane() {
        return plane;
    }

    public String getEase() {
        return ease;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetTag() {
        return targetTag;
    }

    public EventType getEventType() {
        return EventType.SetFilterAdvanced;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public boolean getDisableOthers() {
        return disableOthers;
    }

    public String getFilter() {
        return filter;
    }

    public int getDuration() {
        return duration;
    }

    public String getFilterProperties() {
        return filterProperties;
    }

    public String getEventTag() {
        return eventTag;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}