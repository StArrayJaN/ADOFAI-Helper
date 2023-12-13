package thercn.adofai.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Level {
    JSONObject level;
    JSONObject settings;
    JSONArray events;
    JSONArray decorations;
    static String currentLevelFile;

    public static void main(String[] args) {
        try {
            //System.loadLibrary("key");
            //
            //System.loadLibrary("项目1");
            Level level = Level.readLevelFile("/storage/emulated/0/levels/Solypsis VIP [All]/level.adofai");
            System.out.println("当前文件为" + level.currentLevelFile);
            System.out.println("获取到" + level.getCharts().size() + "个轨道");
            System.out.println("BPM:" + level.getBPM());
            System.out.println("偏移:" + level.getOffset());
            System.out.println("获取到" + level.getEvents() + "个事件");
            runMacro(level);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    static void convertToOld(Level level) throws JSONException,IOException{
		String reomveSettings[] = { "speedTrialAim",
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
			"scalingRatio" };

		String newSettingValue[] = {"showDefaultBGIfNoImage",
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
			"stickToFloors" };
		String ntrue = "Enabled";
		String nfalse = "Disabled";
		for (int i = 0; i < newSettingValue.length; i++) {
			if (level.hasSetting(newSettingValue[i])) {
				if (newSettingValue[i].equals("scalingRatio")) {
					level.setLevelSetting("unscaledSize", level.settings.getInt("scalingRatio"));
				}
				if (!newSettingValue[i].equals("scalingRatio") && level.getSetting(newSettingValue[i]).equals("true")) {
					level.setLevelSetting(newSettingValue[i], ntrue);
				}
				if (!newSettingValue[i].equals("scalingRatio") && level.getSetting(newSettingValue[i]).equals("false")) {
					level.setLevelSetting(newSettingValue[i], nfalse);
				}
			}
		}
		for (int i = 0; i < reomveSettings.length; i++) {
			level.removeLevelSetting(reomveSettings[i]);
		}
		level.setLevelSetting("version",12);
		level.previewAndSave();
	}
    static void runMacro(Level l) throws JSONException,IOException {
        JSONArray parsedChart = new JSONArray();
        int midrCount = 0;
        List<Integer> midrId = new ArrayList<>();

        List<Double> angleDataList = l.getCharts();

        for (int i = 0; i < angleDataList.size(); i++) {
            double angleData = angleDataList.get(i);
            if (Math.abs(angleData - 999) <= 0.01) {
                midrCount++;
                JSONObject temp = parsedChart.getJSONObject(i - midrCount);
                temp.put("midr", "true");
                parsedChart.put(i - midrCount, temp);
                midrId.add(i - 1);
            } else {
                double angle = fmod(angleData, 360);
                JSONObject temp = new JSONObject();
                temp.put("angle", angle);
                temp.put("bpm", "unSet");
                temp.put("direction", 0);
                temp.put("extraHold", 0);
                temp.put("midr", "false");
                parsedChart.put(i - midrCount, temp);
            }
        }
        double bpm = (float)l.getBPM();

        for (int i = 0; i < l.events.length(); i++) {
            JSONObject o = l.events.getJSONObject(i);
            int tile = o.getInt("floor");
            String event = o.get("eventType").toString();
            tile -= upperBound(midrId.toArray(new Integer[0]), tile);
            
            if (event.equals("SetSpeed")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                if (o.get("speedType").equals("Multiplier")) {
                    bpm = o.getDouble("bpmMultiplier") * bpm;
                } else {
                    bpm = o.getDouble("beatsPerMinute");
                }
                ob.put("bpm", bpm);
                parsedChart.put(tile, ob);
            }
            if (event.equals("Twirl")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                ob.put("direction", -1);
                parsedChart.put(tile, ob);
            }
            if (event.equals("Pause")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                ob.put("extraHold", o.getDouble("duration") / 2);
                parsedChart.put(tile, ob);
            }
            if (event.equals("Hold")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                ob.put("extraHold", o.getDouble("duration"));
                parsedChart.put(tile, ob);
            }
        }

        double BPM = l.getBPM();
        int direction = 1;
        for (int i = 0; i < parsedChart.length(); i++) {
            if (parsedChart.getJSONObject(i).getInt("direction") == -1) {
                direction = -direction;
            }
            JSONObject ob = parsedChart.getJSONObject(i);
            ob.put("direction", direction);
            if (parsedChart.getJSONObject(i).get("bpm").equals("unSet")) {
                ob.put("bpm", BPM);
            } else {
                BPM = ob.getDouble("bpm");
            }
        }

        List<Double> noteTime = new ArrayList<>();
        //noteTime.add(0.0);
        {
            double curAngle = 0;
            double curBPM = (float)l.getBPM();
            double curTime = 0;//《-这里需要修改
            for (int i = 0; i < parsedChart.length(); i++) {
                JSONObject o = parsedChart.getJSONObject(i);
                curAngle = fmod(curAngle - 180, 360);
                curBPM = o.getFloat("bpm");
                double destAngle = o.getDouble("angle");
                double pAngle = 0;
                if (Math.abs(destAngle - curAngle) <= 0.001) {
                    pAngle = 360;
                } else {
                    pAngle = fmod((curAngle - destAngle) * o.getInt("direction"), 360);
                }
                pAngle += o.getDouble("extraHold") * 360;
                curTime += angleToTime(pAngle, curBPM);
                curAngle = destAngle;
                if (o.getBoolean("midr")) {
                    curAngle = curAngle + 180;
                }
                noteTime.add(curTime);
            }

        }
        System.out.println("处理完成,按W开始");
        // double offsetTime = 0;
        double[] n = new double[noteTime.size()];
        for (int i = 0; i < noteTime.size(); i++) {
            n[i] = noteTime.get(i);
            //     n[i] = offsetTime;
        }
        FileWriter w = new FileWriter(new File("/sdcard/a.txt"));
        File file = new File("/sdcard/b.json");
        for(double t:noteTime){
	    	w.write(t + "\n");
            System.out.println(t);
    	}
	    w.close();
        writeJSONToFile(parsedChart,file);
      //  start(n);
        //return;

    }


    /**
     * generalize angle exclude 360
     *
     * @param angle not generalized angle
     * @return 0 <= angle < 360
     */
    public static double generalizeAngle(double angle) {
        angle = angle - ((int) (angle / 360)) * 360;
        return angle < 0 ? angle + 360 : angle;
    }

    public static int upperBound(Integer[] arr, int value) {
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

    public static double fmod(double a, double b) {
        double t = Math.floor(a / b);
        return a - b * t;
    }

    public static double angleToTime(double angle, double bpm) {
        return (angle / 180) * (60 / bpm) * 1000;
    }

    public static native void start(double[] bpmList);

    public Level(JSONObject level) throws JSONException {
        this.level = level;
        this.settings = level.getJSONObject("settings");
        this.events = level.getJSONArray("actions");
        if (settings.getInt("version") > 10) {
            this.decorations = level.getJSONArray("decorations");
        }
    }

    public static Level readLevelFile(String filePath) throws JSONException, IOException {
        currentLevelFile = filePath;
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), "UTF-8"));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();
        int i = content.toString().indexOf("{");
        String newJSONStr = content.toString().substring(i);
        return new Level(new JSONObject(newJSONStr));
    }

    public int getEvents() {
        return events.length();
    }

    public JSONObject toJSONObject() throws JSONException {
        return new JSONObject(level.toString());
    }

    public JSONObject findNearbyEvent(int chart, String event) throws JSONException {

        while (chart-- >= 0) {
            JSONObject eventObject;
            for (int i = 0; i < events.length(); i++) {
                eventObject = (JSONObject) events.get(i);
                if (eventObject.getInt("floor") == chart && eventObject.get("eventType").equals(event)) {
                    //System.out.println(eventObject);
                    return eventObject;
                }
            }
        }
        return null;
    }

    public List<Double> getCharts() throws JSONException {
        //预处理，有需要则在main方法进行更多处理
        JSONArray charts = level.optJSONArray("angleData");
        List<Double> chartArray = new ArrayList<>();

        if (charts == null) {
            String pathData = level.getString("pathData");

            List<TileAngle> parsedPathData = pathData
                    .chars()
                    .mapToObj(c -> (char) c)
                    .map(TileAngle.angleCharMap::get)
                    .collect(Collectors.toList());

            double staticAngle = 0d;

            for (TileAngle angle : parsedPathData) {
                if (angle == TileAngle.NONE) {
                    chartArray.add(angle.angle);
                    continue;
                } else {
                    if (angle.relative) {
                        staticAngle = generalizeAngle(staticAngle + 180 - angle.angle);
                    } else staticAngle = angle.angle;
                }
                chartArray.add(staticAngle);
            }

            return chartArray;
        }

        for (int i = 0; i < charts.length(); i++) {
            double chart = Double.parseDouble(charts.get(i).toString());
            chartArray.add(chart);
        }
        return chartArray;
    }

    public double getBPM() throws JSONException {
        return Double.parseDouble(settings.get("bpm").toString());
    }

    public int getOffset() throws JSONException {
        return settings.getInt("offset");
    }

    public int getPitch() throws JSONException {
        return settings.getInt("pitch");
    }

    public int getCountDownTicks() throws JSONException {
        return settings.getInt("countdownTicks");
    }

    public String getSetting(String setting) throws JSONException {
        return settings.get(setting).toString();
    }

    public void setLevelSetting(String key, Object value) throws JSONException {
        settings.put(key, value);
    }

    public void removeLevelSetting(String key) throws JSONException {
        settings.remove(key);
    }

	public boolean hasSetting(String key) {
		try
		{
			settings.get(key);
			return true;
		} catch (JSONException e) {
			return false;
		}
	}
	
    public Double[] bpmMultiplierToBPM(List<String[]> bpmList) throws JSONException {
        double bpm = getBPM();
        Double newbpmList[] = new Double[bpmList.size()];
        for (int i = 0; i < bpmList.size(); i++) {
            double value = Double.parseDouble(bpmList.get(i)[2]);
            if (bpmList.get(i)[1] == "true") {
                newbpmList[i] = bpm * value;
                bpm = bpm * value;
            } else {
                newbpmList[i] = value;
                bpm = value;
            }
        }
        return newbpmList;
    }

    public List<String[]> getAllSpeed() throws JSONException {
        List<String[]> speed = new ArrayList<>();
        for (int i = 0; i < getCharts().size(); i++) {
            if (getSpeed(i) != null) {
                speed.add(new String[]{String.valueOf(i), getSpeed(i)[0], getSpeed(i)[1]});
            }
        }
        return speed;
    }

    public String[] getSpeed(int chart) throws JSONException {
        JSONObject event;
        for (int a = 0; a < events.length(); a++) {
            event = (JSONObject) events.get(a);
            if ((int) event.get("floor") == chart && event.get("eventType").equals("SetSpeed")) {
                String isMultiplier = "false";
                if (event.get("speedType").equals("Multiplier")) {
                    isMultiplier = "true";
                    return new String[]{String.valueOf(chart), isMultiplier, event.get("bpmMultiplier").toString()};
                } else {
                    return new String[]{String.valueOf(chart), isMultiplier, event.get("beatsPerMinute").toString()};
                }
            }
        }
        return null;
    }

    public List<JSONObject> getChartEvents(int chart) throws JSONException {
        JSONObject eventObject;
        List<JSONObject> chartEvents = new ArrayList<>();
        for (int a = 0; a < events.length(); a++) {
            eventObject = (JSONObject) events.get(a);
            if ((int) eventObject.get("floor") == chart) {
                chartEvents.add(eventObject);
            }
        }
        return chartEvents;
    }

    public JSONObject getEvent(int chart, List<JSONObject> events, String event) throws JSONException {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).get("eventType").equals(event)) {
                if (hasEvent(chart, events.get(i).get("eventType").toString())) {
                    return events.get(i);
                }
            }
        }
        return null;
    }

    public boolean hasEvent(int chart, String event) throws JSONException {
        JSONObject eventObject;
        for (int a = 0; a < events.length(); a++) {
            eventObject = (JSONObject) events.get(a);
            if ((int) eventObject.get("floor") == chart && eventObject.get("eventType").equals(event)) {
                return true;
            }
        }
        return false;
    }

    public JSONArray getEventArray() {
        return events;
    }

    public void removeAllEvent(String event) throws JSONException {
        JSONObject eventObject;
        for (int i = 0; i < events.length(); i++) {
            eventObject = (JSONObject) events.get(i);
            if (eventObject.get("eventType").equals(event)) {
                events.remove(i);
                i--;
            }
        }
    }

    public void previewAndSave() throws JSONException, IOException {
        System.out.println(level.toString(2));
        File file = new File(currentLevelFile.replace(".adofai", "-mod.adofai"));
        writeJSONToFile(level,file);
    }
    
    public static void writeJSONToFile(JSONObject JSONString,File filePath) throws IOException,JSONException {
		FileWriter writer = new FileWriter(filePath);
        writer.write(JSONString.toString(2));
        writer.close();
	}

    public static void writeJSONToFile(JSONArray JSONString,File filePath) throws IOException,JSONException {
		FileWriter writer = new FileWriter(filePath);
        writer.write(JSONString.toString(3));
        writer.close();
	}

    enum TileAngle {

        _0('R', 0, false),
        _15('p', 15, false),
        _30('J', 30, false),
        _45('E', 45, false),
        _60('T', 60, false),
        _75('o', 75, false),
        _90('U', 90, false),
        _105('q', 105, false),
        _120('G', 120, false),
        _135('Q', 135, false),
        _150('H', 150, false),
        _165('W', 165, false),
        _180('L', 180, false),
        _195('x', 195, false),
        _210('N', 210, false),
        _225('Z', 225, false),
        _240('F', 240, false),
        _255('V', 255, false),
        _270('D', 270, false),
        _285('Y', 285, false),
        _300('B', 300, false),
        _315('C', 315, false),
        _330('M', 330, false),
        _345('A', 345, false),
        _5('5', 108, true),
        _6('6', 252, true),
        _7('7', 900.0 / 7.0, true),
        _8('8', 360 - 900.0 / 7.0, true),
        R60('t', 60, true),
        R120('h', 120, true),
        R240('j', 240, true),
        R300('y', 300, true),
        NONE('!', 999, true);

        public final char charCode;
        public final double angle;
        public final boolean relative;

        public static final Map<Character, TileAngle> angleCharMap = new HashMap<>();

        static {
            for (TileAngle value : TileAngle.values()) angleCharMap.put(value.charCode, value);
        }


        TileAngle(char charCode, double angle, boolean relative) {
            this.charCode = charCode;
            this.angle = angle;
            this.relative = relative;
        }


    }
}
