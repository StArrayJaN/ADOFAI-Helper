package thercn.adofai.helper;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LevelUtils {

    public static ProcessListener processListener;
    public static List<Double> delayList = new ArrayList<>();

    public interface ProcessListener {
        void onProcessDone(String message,State state);
        void onProcessChange(String message, int progress);
    }

    public static List<Double> getNoteTimes(Level l) throws JSONException {
        List<Double> angleDataList = l.getCharts();
        JSONArray levelEvents = l.events;
        //对带有变速和旋转的中旋进行处理
        for (int i = 0; i < angleDataList.size(); i++) {
            if (angleDataList.get(i) == 999) {
                if (l.hasEvent(i, "SetSpeed")) {
                    JSONObject a = levelEvents.getJSONObject(l.getEventIndex(i, "SetSpeed"));
                    a.put("floor", a.getInt("floor") + 1);
                } else if (l.hasEvent(i, "Twirl")) {
                    JSONObject a = levelEvents.getJSONObject(l.getEventIndex(i, "Twirl"));
                    a.put("floor", a.getInt("floor") + 1);
                }
            }
        }

        if (processListener != null) {
            SwingUtilities.invokeLater(() -> processListener.onProcessChange("处理轨道数据", 10));
        }

        JSONArray parsedChart = new JSONArray();
        int midrCount = 0;
        List<Integer> midrId = new ArrayList<>();
        //初步处理，获取轨道角度和中旋
        for (int i = 0; i < angleDataList.size(); i++) {
            double angleData = angleDataList.get(i);
            if (angleData == 999) {
                //中旋，删除掉多余的一个物量
                midrCount++;
                JSONObject temp = parsedChart.getJSONObject(i - midrCount);
                temp.put("midr", "true");
                parsedChart.put(i - midrCount, temp);
                midrId.add(i - 1);
            } else {
                //一般轨道
                double angle = fmod(angleData, 360);
                JSONObject temp = new JSONObject();
                temp.put("angle", angle);
                temp.put("bpm", "unSet");
                temp.put("direction", 0);
                temp.put("extraHold", 0);
                temp.put("midr", "false");
                temp.put("MultiPlanet", "-1");
                parsedChart.put(i - midrCount, temp);
            }

        }

        if (processListener != null) {
            SwingUtilities.invokeLater(() -> processListener.onProcessChange("处理轨道数据", 20));
        }

        double angle = fmod(angleDataList.get(angleDataList.size() - 1), 360);
        //创建一个json节点
        JSONObject temp = new JSONObject();
        temp.put("angle", angle);
        temp.put("bpm", "unSet");
        temp.put("direction", 0);
        temp.put("extraHold", 0);
        temp.put("midr", "false");
        temp.put("MultiPlanet", "-1");
        parsedChart.put(parsedChart.length(), temp);

        double bpm = l.getBPM();
        double pitch = l.getPitch() / 100;

        boolean a = true;
        //根据轨道事件修改json节点
        for (int i = 0; i < levelEvents.length(); i++) {
            JSONObject o = levelEvents.getJSONObject(i);
            int tile = o.getInt("floor");
            String event = o.get("eventType").toString();
            //upperBound用于获取轨道数量和事件数量的差异;
            tile -= upperBound(midrId.toArray(new Integer[0]), tile);
            //根据速度事件设置变速
            if (event.equals("SetSpeed")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                if (o.get("speedType").equals("Multiplier")) {
                    bpm = o.getDouble("bpmMultiplier") * bpm;
                } else if (o.get("speedType").equals("Bpm")) {
                    bpm = o.getDouble("beatsPerMinute") * pitch;
                    a = true;
                }
                //  System.out.println(bpm + new Boolean(a).toString());
                ob.put("bpm", bpm);
                parsedChart.put(tile, ob);
            }
            //旋转
            if (event.equals("Twirl")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                ob.put("direction", -1);
                parsedChart.put(tile, ob);
            }
            //暂停
            if (event.equals("Pause")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                ob.put("extraHold", o.getDouble("duration") / 2);
                parsedChart.put(tile, ob);
            }
            //长按
            if (event.equals("Hold")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                ob.put("extraHold", o.getDouble("duration"));
                parsedChart.put(tile, ob);
            }
            /* Huihui start */
            //三球
            if (event.equals("MultiPlanet")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                if (o.get("planets").equals("ThreePlanets")) {
                    ob.put("MultiPlanet", "1");
                } else {
                    ob.put("MultiPlanet", "0");
                }
                parsedChart.put(tile, ob);
            }
            /* Huihui  end  */
        }

        if (processListener != null) {
            SwingUtilities.invokeLater(() -> processListener.onProcessChange("处理事件数据", 40));
        }

        double BPM = l.getBPM() * pitch;
        int direction = 1;
        for (int i = 0; i < parsedChart.length(); i++) {
            //旋转处理
            if (parsedChart.getJSONObject(i).getInt("direction") == -1) {
                direction = -direction;
            }
            JSONObject ob = parsedChart.getJSONObject(i);
            ob.put("direction", direction);
            //将bpm应用到所有轨道
            if (parsedChart.getJSONObject(i).get("bpm").equals("unSet")) {
                ob.put("bpm", BPM);
            } else {
                BPM = (float) ob.getDouble("bpm");
            }
        }

        if (processListener != null) {
            SwingUtilities.invokeLater(() -> processListener.onProcessChange("处理轨道数据", 60));
        }

        List<Double> noteTime = new ArrayList<>(),
                noteOffset = new ArrayList<>();
        {
            double curAngle = 0;
            double curBPM = l.getBPM();
            double curTime = 0; //核心:按键时间

            /* Huihui start */
            boolean isMultiPlanet = false;
            /* Huihui  end  */

            for (int i = 0; i < parsedChart.length(); i++) {
                JSONObject o = parsedChart.getJSONObject(i);
                //设置角度
                curAngle = fmod(curAngle - 180, 360);
                curBPM = (float) o.getDouble("bpm");
                double destAngle = o.getDouble("angle");
                double pAngle = 0;
                if (Math.abs(destAngle - curAngle) <= 0.001) {
                    //(疑似)取整
                    pAngle = 360;
                } else {
                    pAngle = fmod((curAngle - destAngle) * o.getInt("direction"), 360);
                }
                pAngle += o.getDouble("extraHold") * 360;

                /* Huihui start */

                // 缓存角度
                double angleTemp = pAngle;
                // 处理三球
                if (isMultiPlanet) {
                    if (pAngle > 60) pAngle -= 60;
                    else pAngle += 300;

                }
                // 改变状态 -1表示状态不变
                if (o.get("MultiPlanet") != "-1") {
                    if (o.get("MultiPlanet") == "1") {
                        isMultiPlanet = true;
                        if (pAngle > 60) pAngle -= 60;
                        else pAngle += 300;
                    } else {
                        isMultiPlanet = false;
                        pAngle = angleTemp;
                    }
                }

                /* Huihui  end  */

                //按键时间增加
                curTime += angleToTime(pAngle, curBPM);
                curAngle = destAngle;
                //中旋处理
                if (o.getBoolean("midr")) {
                    curAngle = curAngle + 180;
                }
                noteOffset.add(angleToTime(pAngle, curBPM));
                //添加到数组
                noteTime.add(curTime);
            }

        }

        return noteTime;
    }

    public static void runMacro(List<Double> noteTime, String keyList) throws IOException {
        if (processListener != null) {
            SwingUtilities.invokeLater(() -> {
                processListener.onProcessChange("处理完成", 100);
                processListener.onProcessDone("""
                        处理完成，按W开始
                        按←和→来调整偏移
                        按Q退出""", State.FINISHED);
            });
        }
        StartMacro start = new StartMacro(noteTime.toArray(new Double[0]));
        start.setKeyList(keyList);
        try {
            start.startHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }

        private static int upperBound(Integer[] arr, int value) {
        int left = 0;
        int right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] >= value) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    //伪模运算
    public static double fmod(double a, double b) {
        double t = Math.floor(a / b);
        return a - b * t;
    }

    //看什么注释，看方法名字↓
    public static double angleToTime(double angle, double bpm) {
        return (angle / 180) * (60 / bpm) * 1000;
    }

    public static class StartMacro implements NativeKeyListener {

        Double[] bpmList;
        Robot bot;
        double offset = 0;
        String keyList = "A";
        Thread thread;
        boolean breaked;
        List<Integer> keys;

        public StartMacro(Double[] list) {
            bpmList = list;
            thread = getThread();
            try {
                bot = new Robot();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }

        public void setKeyList(String keyList) {
            this.keyList = keyList;
            char[] keyChars = keyList.toCharArray();
            keys = new ArrayList<>();
            for (char c : keyChars) {
                keys.add(KeyEvent.getExtendedKeyCodeForChar(c));
            }
        }

        public void startHook() throws NativeHookException {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        }

        public void stopHook() {
            breaked = true;
            GlobalScreen.removeNativeKeyListener(this);
        }

        public Thread getThread() {
            return new Thread(() -> {
                int keyIndex = 0;
                double start = currentTime();
                int events = 1;
                while (events < bpmList.length) {
                    double cur = currentTime();
                    double timeMilliseconds = (cur - start) + bpmList[0];
                    while (events < bpmList.length && bpmList[events] + offset <= timeMilliseconds) {
                        //根据bpm计算延迟
                        if (keyIndex >= keys.size()) keyIndex = 0;
                        bot.keyPress(keys.get(keyIndex));
                        bot.keyRelease(keys.get(keyIndex));
                        if (Main.enableConsole) {
                            System.out.printf("进度:%d/%d,BPM:%f,延迟:%fms,偏移:%f,键位:%s\n",events,
                                    bpmList.length,
                                    60000 / (bpmList[events] - bpmList[events -1]),
                                    bpmList[events] - bpmList[events -1],
                                    offset,
                                    KeyEvent.getKeyText(keys.get(keyIndex)));
                        }
                        events++;
                        keyIndex++;
                    }
                    if (breaked) break;
                }
                if (processListener != null) {
                    processListener.onProcessDone("已结束",State.FINISHED);
                }
            });
        }

        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
            switch (nativeEvent.getKeyCode()) {
                case NativeKeyEvent.VC_W:
                    thread.start();
                    break;
                case NativeKeyEvent.VC_LEFT:
                    offset -= 5;
                    break;
                case NativeKeyEvent.VC_RIGHT:
                    offset += 5;
                    break;
                case NativeKeyEvent.VC_Q:
                    breaked = true;
                    thread.interrupt();
                    if (processListener != null) {
                        processListener.onProcessDone("已退出",State.STOPPED);
                    }
                    break;
            }
        }
    }

    public static List<Double> genericDelayTable(Double[] bpmList) throws JSONException {
        List<Double> delayTable = new ArrayList<>();
        double start = currentTime();
        int events = 1;
        while (events < bpmList.length) {
            double cur = currentTime();
            double timeMilliseconds = (cur - start) + bpmList[0];
            System.out.println(bpmList[events]);
            delayTable.add(timeMilliseconds);
            events++;
        }
        return delayTable;
    }

    private static double currentTime() {
        return System.nanoTime() / 1E6;
    }

    private static void removeEffects(Level l) throws JSONException {
        String[] effectEvents = {
                "MoveCamera",
                "MoveTrack",
                "AddDecoration",
                "CustomBackground",
                "Flash",
                "SetFilter",
                "HallOfMirrors",
                "ShakeScreen",
                "MoveDecorations",
                "ScaleRadius",
                "RepeatEvents",
                "Bloom",
                "ScreenScroll"
        };

        for (int i = 0; i < effectEvents.length; ++i) {
            l.removeAllEvent(effectEvents[i], true);
            l.removeAllEvent(effectEvents[i], false);
        }
    }

    public static void convertToOld(Level level) throws JSONException {
        String[] removeSettings = {"speedTrialAim",
                "trackTexture",
                "trackTextureScale",
                "showDefaultBGTile",
                "defaultBGTileColor",
                "defaultBGShapeType",
                "defaultBGShapeColor",
                "defaultTextColor",
                "defaultTextShadowColor",
                "congratsText",
                "perfectText",
                "imageSmoothing",
                "scalingRatio"};

        String[] newSettingValue = {"showDefaultBGIfNoImage",
                "separateCountdownTime",
                "separateCountdownTime",
                "seizureWarning",
                "lockRot",
                "loopBG",
                "scalingRatio",
                "pulseOnFloor",
                "startCamLowVFX",
                "loopVideo",
                "floorIconOutlines",
                "stickToFloors"};
        String ntrue = "Enabled";
        String nfalse = "Disabled";
        for (int i = 0; i < newSettingValue.length; i++) {
            if (level.hasSetting(newSettingValue[i])) {
                if (newSettingValue[i].equals("scalingRatio")) {
                    level.setLevelSetting("unscaledSize", level.settings.getInt("scalingRatio"));
                }
                if (!newSettingValue[i].equals("scalingRatio") && level.getSetting(newSettingValue[i]).equals(true)) {
                    level.setLevelSetting(newSettingValue[i], ntrue);


                }
                if (!newSettingValue[i].equals("scalingRatio") && level.getSetting(newSettingValue[i]).equals(false)) {
                    level.setLevelSetting(newSettingValue[i], nfalse);
                }
            }
        }

        for (String reomveSetting : removeSettings) {
            level.removeLevelSetting(reomveSetting);
        }

        for (int i = 0; i < level.events.length(); ++i) {
            JSONObject o = level.events.getJSONObject(i);
            for (String key : o.keySet()) {
                try {
                    if (o.getBoolean(key)) {
                        o.put(key, "Enabled");
                    } else if (!o.getBoolean(key)) {
                        o.put(key, "Disabled");
                    }
                } catch (Exception err) {
                }
            }
            if (o.get("eventType").equals("ScalePlanets")) {
                o.remove("targetPlanet");
            }
        }

        for (int i = 0; i < level.decorations.length(); ++i) {
            JSONObject o = level.decorations.getJSONObject(i);
            for (String key : o.keySet()) {
                try {
                    if (o.getBoolean(key)) {
                        o.put(key, "Enabled");
                    } else if (!o.getBoolean(key)) {
                        o.put(key, "Disabled");
                    }
                } catch (Exception err) {
                }
            }
        }

        level.removeAllEvent("setFloorIcon", false);
        level.removeAllEvent("AddObject", false);
        level.removeAllEvent("MoveObject", false);
        level.setLevelSetting("version", 12);
    }
}
