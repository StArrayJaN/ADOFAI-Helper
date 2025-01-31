package thercn.adofai.helper.Event.LevelEvent;

import thercn.adofai.helper.Event.BasicEvent;
import thercn.adofai.helper.Event.EventType;
import thercn.adofai.helper.Event.LevelEventEnum.Vector2;

public class PositionTrack implements BasicEvent {

    private boolean justThisTile;
    private boolean editorOnly;
    private Vector2 relativeTo;
    
    private Vector2 positionOffset;
    private int floor;
    private int opacity;

    public PositionTrack(boolean justThisTile, boolean editorOnly, Vector2 relativeTo, Vector2 positionOffset, int floor, int opacity) {
        this.justThisTile = justThisTile;
        this.editorOnly = editorOnly;
        this.relativeTo = relativeTo;
        this.positionOffset = positionOffset;
        this.floor = floor;
        this.opacity = opacity;
    }

    public boolean getJustThisTile() {
        return justThisTile;
    }

    public boolean getEditorOnly() {
        return editorOnly;
    }

    public Vector2 getRelativeTo() {
        return relativeTo;
    }

    public EventType getEventType() {
        return EventType.PositionTrack;
    }

    public Vector2 getPositionOffset() {
        return positionOffset;
    }

    public int getFloor() {
        return floor;
    }

    public int getOpacity() {
        return opacity;
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