package thercn.adofai.helper.Event.LevelEventEnum;

import org.json.JSONArray;

public class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String ToString() {
        return String.format("[%f, %f]", x, y);
    }

    public JSONArray toJSONArray() {
        return new JSONArray().put(x).put(y);
    }
}
