package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;

public class SetHoldSound implements BasicEvent {

    private String holdLoopSound;
    private String holdEndSound;
    private int holdSoundVolume;
    private double holdMidSoundDelay;
    private String holdMidSound;
    private String holdMidSoundType;
    
    private int floor;
    private String holdMidSoundTimingRelativeTo;
    private String holdStartSound;

    public SetHoldSound(String holdLoopSound, String holdEndSound, int holdSoundVolume, double holdMidSoundDelay, String holdMidSound, String holdMidSoundType, int floor, String holdMidSoundTimingRelativeTo, String holdStartSound) {
        this.holdLoopSound = holdLoopSound;
        this.holdEndSound = holdEndSound;
        this.holdSoundVolume = holdSoundVolume;
        this.holdMidSoundDelay = holdMidSoundDelay;
        this.holdMidSound = holdMidSound;
        this.holdMidSoundType = holdMidSoundType;
        this.floor = floor;
        this.holdMidSoundTimingRelativeTo = holdMidSoundTimingRelativeTo;
        this.holdStartSound = holdStartSound;
    }

    public String getHoldLoopSound() {
        return holdLoopSound;
    }

    public String getHoldEndSound() {
        return holdEndSound;
    }

    public int getHoldSoundVolume() {
        return holdSoundVolume;
    }

    public double getHoldMidSoundDelay() {
        return holdMidSoundDelay;
    }

    public String getHoldMidSound() {
        return holdMidSound;
    }

    public String getHoldMidSoundType() {
        return holdMidSoundType;
    }

    public EventType getEventType() {
        return EventType.SetHoldSound;
    }

    public int getFloor() {
        return floor;
    }

    public String getHoldMidSoundTimingRelativeTo() {
        return holdMidSoundTimingRelativeTo;
    }

    public String getHoldStartSound() {
        return holdStartSound;
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