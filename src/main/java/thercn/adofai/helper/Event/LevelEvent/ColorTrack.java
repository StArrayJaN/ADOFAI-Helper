package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.EventType;
import thercn.adofai.helper.Event.BasicEvent;

public class ColorTrack implements BasicEvent{

    private String trackColorType;
    private String trackTexture;
    private int trackGlowIntensity;
    
    private int trackTextureScale;
    private int trackPulseLength;
    private String trackColor;
    private boolean justThisTile;
    private int trackColorAnimDuration;
    private String secondaryTrackColor;
    private int floor;
    private String trackColorPulse;
    private String trackStyle;

    public ColorTrack(String trackColorType, String trackTexture, int trackGlowIntensity, int trackTextureScale, int trackPulseLength, String trackColor, boolean justThisTile, int trackColorAnimDuration, String secondaryTrackColor, int floor, String trackColorPulse, String trackStyle) {
        this.trackColorType = trackColorType;
        this.trackTexture = trackTexture;
        this.trackGlowIntensity = trackGlowIntensity;
        this.trackTextureScale = trackTextureScale;
        this.trackPulseLength = trackPulseLength;
        this.trackColor = trackColor;
        this.justThisTile = justThisTile;
        this.trackColorAnimDuration = trackColorAnimDuration;
        this.secondaryTrackColor = secondaryTrackColor;
        this.floor = floor;
        this.trackColorPulse = trackColorPulse;
        this.trackStyle = trackStyle;
    }

    public String getTrackColorType() {
        return trackColorType;
    }

    public String getTrackTexture() {
        return trackTexture;
    }

    public int getTrackGlowIntensity() {
        return trackGlowIntensity;
    }

    public EventType getEventType() {
        return EventType.ColorTrack;
    }

    public int getTrackTextureScale() {
        return trackTextureScale;
    }

    public int getTrackPulseLength() {
        return trackPulseLength;
    }

    public String getTrackColor() {
        return trackColor;
    }

    public boolean getJustThisTile() {
        return justThisTile;
    }

    public int getTrackColorAnimDuration() {
        return trackColorAnimDuration;
    }

    public String getSecondaryTrackColor() {
        return secondaryTrackColor;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean getEnabled() {
        return true;
    }

    public String getTrackColorPulse() {
        return trackColorPulse;
    }

    public String getTrackStyle() {
        return trackStyle;
    }

    @Override
    public void setEnabled(boolean enabled) {
        return;
    }

}