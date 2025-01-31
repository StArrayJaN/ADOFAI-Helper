package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;
import thercn.adofai.helper.Event.LevelEventEnum.Vector2;

public class FreeRoam implements BasicEvent{

    private int duration;
    private String hitsoundOffBeats;
    private String outEase;
    private String hitsoundOnBeats;
    private Vector2 size;
    private String angleCorrectionDir;
    
    private Vector2 positionOffset;
    private int floor;
    private int outTime;
    private int countdownTicks;

    public FreeRoam(int floor, int duration, String hitsoundOffBeats, String outEase, String hitsoundOnBeats, Vector2 size, Vector2 positionOffset, String angleCorrectionDir, int outTime, int countdownTicks) {
        this.floor = floor;
        this.duration = duration;
        this.outEase = outEase;
        this.hitsoundOffBeats = hitsoundOffBeats;
        this.hitsoundOnBeats = hitsoundOnBeats;
        this.size = size;
        this.angleCorrectionDir = angleCorrectionDir;
        this.positionOffset = positionOffset;
        this.outTime = outTime;
        this.countdownTicks = countdownTicks;
    }

    public int getDuration() {
        return duration;
    }

    public String getHitsoundOffBeats() {
        return hitsoundOffBeats;
    }

    public String getOutEase() {
        return outEase;
    }

    public String getHitsoundOnBeats() {
        return hitsoundOnBeats;
    }

    public Vector2 getSize() {
        return size;
    }

    public String getAngleCorrectionDir() {
        return angleCorrectionDir;
    }

    public EventType getEventType() {
        return EventType.FreeRoam;
    }

    public Vector2 getPositionOffset() {
        return positionOffset;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean getEnabled() {
        return true;
    }

    public int getOutTime() {
        return outTime;
    }

    public int getCountdownTicks() {
        return countdownTicks;
    }

    @Override
    public void setEnabled(boolean enabled) { return; }
}