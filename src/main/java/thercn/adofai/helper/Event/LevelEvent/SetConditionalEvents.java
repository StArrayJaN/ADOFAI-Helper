package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;

public class SetConditionalEvents implements BasicEvent {

    private String missTag;
    private String lossTag;
    private String veryLateTag;
    private String earlyPerfectTag;
    private String veryEarlyTag;
    private String tooLateTag;
    private String perfectTag;
    
    private String tooEarlyTag;
    private String hitTag;
    private String latePerfectTag;
    private String barelyTag;
    private String onCheckpointTag;
    private int floor;

    public SetConditionalEvents(String missTag, String lossTag, String veryLateTag, String earlyPerfectTag, String veryEarlyTag, String tooLateTag, String perfectTag, String tooEarlyTag, String hitTag, String latePerfectTag, String barelyTag, String onCheckpointTag, int floor) {
        this.missTag = missTag;
        this.lossTag = lossTag;
        this.veryLateTag = veryLateTag;
        this.earlyPerfectTag = earlyPerfectTag;
        this.veryEarlyTag = veryEarlyTag;
        this.tooLateTag = tooLateTag;
        this.perfectTag = perfectTag;
        this.tooEarlyTag = tooEarlyTag;
        this.hitTag = hitTag;
        this.latePerfectTag = latePerfectTag;
        this.barelyTag = barelyTag;
        this.onCheckpointTag = onCheckpointTag;
        this.floor = floor;
    }

    public String getMissTag() {
        return missTag;
    }

    public String getLossTag() {
        return lossTag;
    }

    public String getVeryLateTag() {
        return veryLateTag;
    }

    public String getEarlyPerfectTag() {
        return earlyPerfectTag;
    }

    public String getVeryEarlyTag() {
        return veryEarlyTag;
    }

    public String getTooLateTag() {
        return tooLateTag;
    }

    public String getPerfectTag() {
        return perfectTag;
    }

    public EventType getEventType() {
        return EventType.SetConditionalEvents;
    }

    public String getTooEarlyTag() {
        return tooEarlyTag;
    }

    public String getHitTag() {
        return hitTag;
    }

    public String getLatePerfectTag() {
        return latePerfectTag;
    }

    public String getBarelyTag() {
        return barelyTag;
    }

    public String getOnCheckpointTag() {
        return onCheckpointTag;
    }

    public int getFloor() {
        return floor;
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