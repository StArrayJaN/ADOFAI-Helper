package thercn.adofai.helper;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LastOpenManager {
    private File lastOpenFileJson;
    private JSONObject info = new JSONObject();;
    public static String PATH_KEY = "path";
    public static String KEY_LIST_KEY = "keyList";

    private LastOpenManager() throws IOException {
        init();
    }

    public static LastOpenManager getInstance() throws IOException {
        return new LastOpenManager();
    }

    private void init() throws IOException {
        lastOpenFileJson = new File(Main.getRuntimePath(),"lastOpenFile.json");
        if (!lastOpenFileJson.exists()) {
            lastOpenFileJson.createNewFile();
        } else {
            String fileContent = new String(Files.readAllBytes(lastOpenFileJson.toPath()));
            if (!fileContent.isEmpty()) {
                info = new JSONObject(fileContent);
            }
        }
    }

    public JSONObject getInfo() {
        return info;
    }

    public void setLastOpenFile(String path) {
        info.put(PATH_KEY, path);
    }

    public String getLastOpenFile() {
        return info.optString(PATH_KEY,"");
    }

    public String getKeyList() {
        return info.optString(KEY_LIST_KEY,"");
    }

    public void setKeyList(String keys) {
        info.put(KEY_LIST_KEY, keys);
    }

    public void save() throws IOException {
        if (info != null) {
            Files.write(lastOpenFileJson.toPath(), info.toString(2).getBytes());
        }
    }
}
