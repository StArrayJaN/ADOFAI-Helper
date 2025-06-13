package thercn.adofai.helper;

import thercn.adofai.helper.Event.EventType;

import java.io.IOException;

public class LevelProcessor {
    public static void main(String[] args) {
        Level level = Level.readLevelFile("C:\\Users\\DELL\\Downloads\\zsry_KreScent_-_Destined_for_Demise_Changhyeon\\error.adofai");
        var actions = level.events;
        for (int i = 0; i < actions.length(); i++) {
            if (actions.getJSONObject(i).getString("eventType").equals(EventType.ColorTrack.name()) ||
                    actions.getJSONObject(i).getString("eventType").equals(EventType.RecolorTrack.name()) ||
                    actions.getJSONObject(i).getString("eventType").equals(EventType.ScaleRadius.name()) ||
                    actions.getJSONObject(i).getString("eventType").equals(EventType.Hide.name()) ||
                    actions.getJSONObject(i).getString("eventType").equals(EventType.PositionTrack.name())) {
                actions.remove(i);
                i--;
            }
        }
        try {
            level.saveFile(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
