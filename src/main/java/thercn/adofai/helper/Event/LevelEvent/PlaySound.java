package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.AngleOffset;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.EventType;

public class PlaySound implements BasicEvent, AngleOffset, EventTag {

    private String eventTag;
    private boolean customHitSound;
    private int hitsoundVolume;
    private double angleOffset;
    
    private int floor;
    private String hitsound;
    private String selectAudioFile;

    public PlaySound(String eventTag, boolean customHitSound, int hitsoundVolume, double angleOffset, int floor, String hitsound, String selectAudioFile) {
        this.eventTag = eventTag;
        this.customHitSound = customHitSound;
        this.hitsoundVolume = hitsoundVolume;
        this.angleOffset = angleOffset;
        this.floor = floor;
        this.hitsound = hitsound;
        this.selectAudioFile = selectAudioFile;
    }

    public String getEventTag() {
        return eventTag;
    }

    public boolean getCustomHitSound() {
        return customHitSound;
    }

    public int getHitsoundVolume() {
        return hitsoundVolume;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public EventType getEventType() {
        return EventType.PlaySound;
    }

    public int getFloor() {
        return floor;
    }

    public String getHitsound() {
        return hitsound;
    }

    public String getSelectAudioFile() {
        return selectAudioFile;
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