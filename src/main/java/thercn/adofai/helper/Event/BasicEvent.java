package thercn.adofai.helper.Event;

import org.json.JSONObject;

import java.lang.reflect.Field;

public interface BasicEvent {
    EventType getEventType();
    int getFloor();
    boolean getEnabled();
    void setEnabled(boolean enabled);

    default JSONObject toJSON() throws RuntimeException{
        try {
            JSONObject json = new JSONObject();
            Class<?> clazz = this.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                json.put(field.getName(), field.get(this));
            }
            json.put("eventType",getEventType().toString());
            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
