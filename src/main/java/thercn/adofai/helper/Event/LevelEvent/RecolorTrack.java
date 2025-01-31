package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;
import thercn.adofai.helper.Event.LevelEventEnum.Vector2;

public class RecolorTrack implements BasicEvent {

    private String ease;
    private String trackColorType;
    private Vector2 startTile;
    private int trackGlowIntensity;
    private double angleOffset;
    
    private int trackPulseLength;
    private String trackColor;
    private int duration;
    private int trackColorAnimDuration;
    private String eventTag;
    private String secondaryTrackColor;
    private Vector2 endTile;
    private int gapLength;
    private int floor;
    private String trackColorPulse;
    private String trackStyle;

    public RecolorTrack(String ease, String trackColorType, Vector2 startTile, int trackGlowIntensity, double angleOffset, int trackPulseLength, String trackColor, int duration, int trackColorAnimDuration, String eventTag, String secondaryTrackColor, Vector2 endTile, int gapLength, int floor, String trackColorPulse, String trackStyle) {
        this.ease = ease;
        this.trackColorType = trackColorType;
        this.startTile = startTile;
        this.trackGlowIntensity = trackGlowIntensity;
        this.angleOffset = angleOffset;
        this.trackPulseLength = trackPulseLength;
        this.trackColor = trackColor;
        this.duration = duration;
        this.trackColorAnimDuration = trackColorAnimDuration;
        this.eventTag = eventTag;
        this.secondaryTrackColor = secondaryTrackColor;
        this.endTile = endTile;
        this.gapLength = gapLength;
        this.floor = floor;
        this.trackColorPulse = trackColorPulse;
        this.trackStyle = trackStyle;
    }

    public String getEase() {
        return ease;
    }

    public String getTrackColorType() {
        return trackColorType;
    }

    public Vector2 getStartTile() {
        return startTile;
    }

    public int getTrackGlowIntensity() {
        return trackGlowIntensity;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public EventType getEventType() {
        return EventType.RecolorTrack;
    }

    public int getTrackPulseLength() {
        return trackPulseLength;
    }

    public String getTrackColor() {
        return trackColor;
    }

    public int getDuration() {
        return duration;
    }

    public int getTrackColorAnimDuration() {
        return trackColorAnimDuration;
    }

    public String getEventTag() {
        return eventTag;
    }

    public String getSecondaryTrackColor() {
        return secondaryTrackColor;
    }

    public Vector2 getEndTile() {
        return endTile;
    }

    public int getGapLength() {
        return gapLength;
    }

    public int getFloor() {
        return floor;
    }

    public String getTrackColorPulse() {
        return trackColorPulse;
    }

    public String getTrackStyle() {
        return trackStyle;
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