package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.EventType;
import thercn.adofai.helper.Event.LevelEventEnum.Vector2;
import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventTag;
import thercn.adofai.helper.Event.AngleOffset;

public class CustomBackground implements BasicEvent, EventTag, AngleOffset {

    private boolean lockRot;
    private String color;
    private Vector2 parallax;
    private double angleOffset;
    private int scalingRatio;
    
    private String bgImage;
    private boolean loopBG;
    private String eventTag;
    private boolean imageSmoothing;
    private String bgDisplayMode;
    private int floor;
    private String imageColor;

    public CustomBackground(boolean lockRot, String color, Vector2 parallax, double angleOffset, int scalingRatio,
                            String bgImage, boolean loopBG, String eventTag, boolean imageSmoothing, String bgDisplayMode, int floor, String imageColor) {
        this.lockRot = lockRot;
        this.color = color;
        this.parallax = parallax;
        this.angleOffset = angleOffset;
        this.scalingRatio = scalingRatio;
        this.bgImage = bgImage;
        this.loopBG = loopBG;
        this.eventTag = eventTag;
        this.imageSmoothing = imageSmoothing;
        this.bgDisplayMode = bgDisplayMode;
        this.floor = floor;
        this.imageColor = imageColor;
    }

    public boolean getLockRot() {
        return lockRot;
    }

    public String getColor() {
        return color;
    }

    public Vector2 getParallax() {
        return parallax;
    }

    @Override
    public double getAngleOffset() {
        return angleOffset;
    }

    public int getScalingRatio() {
        return scalingRatio;
    }

    @Override
    public EventType getEventType() {
        return EventType.CustomBackground;
    }

    public String getBgImage() {
        return bgImage;
    }

    public boolean getLoopBG() {
        return loopBG;
    }

    @Override
    public String getEventTag() {
        return eventTag;
    }

    public boolean getImageSmoothing() {
        return imageSmoothing;
    }

    public String getBgDisplayMode() {
        return bgDisplayMode;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean getEnabled() {
        return true;
    }


    public String getImageColor() {
        return imageColor;
    }

    @Override
    public void setEnabled(boolean enabled) {
        return;
    }

}