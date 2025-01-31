package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;

public class Hide implements BasicEvent {

    private boolean hideJudgment;
    
    private int floor;
    private boolean hideTileIcon;

    public Hide(boolean hideJudgment, int floor, boolean hideTileIcon) {
        this.hideJudgment = hideJudgment;
        this.floor = floor;
        this.hideTileIcon = hideTileIcon;
    }

    public boolean getHideJudgment() {
        return hideJudgment;
    }

    public EventType getEventType() {
        return EventType.Hide;
    }

    public int getFloor() {
        return floor;
    }

    public boolean getHideTileIcon() {
        return hideTileIcon;
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