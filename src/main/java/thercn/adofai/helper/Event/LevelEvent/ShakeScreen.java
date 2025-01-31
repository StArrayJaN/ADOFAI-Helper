package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.AngleOffset;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.EventType;

public class ShakeScreen implements BasicEvent, AngleOffset, EventTag {

    private int duration;
    private int intensity;
    private String ease;
    private boolean fadeOut;
    private String eventTag;
    private int strength;
    private double angleOffset;
    
    private int floor;

    public ShakeScreen(int duration, int intensity, String ease, boolean fadeOut, String eventTag, int strength, double angleOffset, int floor) {
        this.duration = duration;
        this.intensity = intensity;
        this.ease = ease;
        this.fadeOut = fadeOut;
        this.eventTag = eventTag;
        this.strength = strength;
        this.angleOffset = angleOffset;
        this.floor = floor;
    }

    public int getDuration() {
        return duration;
    }

    public int getIntensity() {
        return intensity;
    }

    public String getEase() {
        return ease;
    }

    public boolean getFadeOut() {
        return fadeOut;
    }

    public String getEventTag() {
        return eventTag;
    }

    public int getStrength() {
        return strength;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public EventType getEventType() {
        return EventType.ShakeScreen;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean getEnabled(){
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {return;}

}