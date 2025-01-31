package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;

public class RepeatEvents implements BasicEvent {

    private boolean executeOnCurrentFloor;
    private int floorCount;
    private String repeatType;
    private int interval;
    
    private String tag;
    private int floor;
    private int repetitions;

    public RepeatEvents(boolean executeOnCurrentFloor, int floorCount, String repeatType, int interval, String tag, int floor, int repetitions) {
        this.executeOnCurrentFloor = executeOnCurrentFloor;
        this.floorCount = floorCount;
        this.repeatType = repeatType;
        this.interval = interval;
        this.tag = tag;
        this.floor = floor;
        this.repetitions = repetitions;
    }

    public boolean getExecuteOnCurrentFloor() {
        return executeOnCurrentFloor;
    }

    public int getFloorCount() {
        return floorCount;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public int getInterval() {
        return interval;
    }

    public EventType getEventType() {
        return EventType.RepeatEvents;
    }

    public String getTag() {
        return tag;
    }

    public int getFloor() {
        return floor;
    }

    public int getRepetitions() {
        return repetitions;
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