package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;

public class Pause implements BasicEvent {

    private int duration;
    private String angleCorrectionDir;
    
    private int floor;
    private int countdownTicks;

    public Pause(int floor, int duration, String angleCorrectionDir, int countdownTicks) {
        this.floor = floor;
        this.duration = duration;
        this.angleCorrectionDir = angleCorrectionDir;
        this.countdownTicks = countdownTicks;
    }

    public int getDuration() {
        return duration;
    }

    public String getAngleCorrectionDir() {
        return angleCorrectionDir;
    }

    public EventType getEventType() {
        return EventType.Pause;
    }

    public int getFloor() {
        return floor;
    }

    public int getCountdownTicks() {
        return countdownTicks;
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