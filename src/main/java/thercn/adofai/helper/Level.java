package thercn.adofai.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			Level level = Level.readLevelFile("/sdcard/levels/593/level.adofai");
			System.out.println("当前文件为" + level.currentLevelFile);
			System.out.println("获取到" + level.getCharts().size() + "个轨道");
			System.out.println("BPM:" + level.getBPM());
			System.out.println("偏移:" + level.getOffset());
			System.out.println("获取到" + level.getEvents() + "个事件");
            level.setLevelSetting("bpm",598);
            level.previewAndSave();
            
			//runMacro(level);
		} catch (IOException|JSONException e) {
			e.printStackTrace();
		}
    }
	static void runMacro(Level l) throws JSONException {
		JSONObject chart = l.toJSONObject();
		JSONArray parsedChart = new JSONArray();
		int midrCount = 0;
		List<Integer> midrId = new ArrayList<>();
		for (int i = 0; i < chart.getJSONArray("angleData").length(); i++) {
			double angleData = chart.getJSONArray("angleData").getDouble(i);
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
				temp.put("midr", "false");
				temp.put("direction", 0);
				temp.put("bpm", "unSet");
				temp.put("extraHold", 0);
				parsedChart.put(i - midrCount, temp);
			}
		}
		double bpm = l.getBPM();

		for (int i = 0; i < l.events.length(); i++) {
			JSONObject o = l.events.getJSONObject(i);
			int tile = o.getInt("floor");
			String event = o.get("eventType").toString();
			tile -= upperBound(midrId.toArray(new Integer[0]),tile);
			if (event.equals("SetSpeed")) {
				if (o.get("speedType").equals("Multiplier")) {
					JSONObject ob = parsedChart.getJSONObject(tile);
					bpm = o.getDouble("bpmMultiplier") * bpm;
					ob.put("bpm",  bpm);
					parsedChart.put(tile, ob);
				} else 	{
					JSONObject ob = parsedChart.getJSONObject(tile);
					bpm = o.getInt("beatsPerMinute");
					ob.put("bpm", o.getInt("beatsPerMinute"));
					parsedChart.put(tile, ob);
				}
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
		int direction=1;
		for (int i=0;i < parsedChart.length();i++) {
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
		if (true) {
			double curAngle = 0;
			double curBPM = l.getBPM();
			double curTime = angleToTime((double)l.settings.getInt("countdownTicks") * 180 - 180, curBPM);//《-这里需要修改
			for (int i = 0; i < parsedChart.length(); i++) {
				JSONObject o = parsedChart.getJSONObject(i);
				curAngle = fmod(curAngle - 180, 360);
				curBPM = o.getDouble("bpm");
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
				noteTime.add(curTime - 0.01);
			}

		}
		System.out.println("处理完成,按W开始");
		double[] n = new double[noteTime.size()];
		for (int i = 0; i < noteTime.size(); i++) {
			n[i] = noteTime.get(i);
		}
		start(n);
		//return;

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

    public static Level readLevelFile(String filePath) throws JSONException,IOException {
		currentLevelFile = filePath;
        File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
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
				eventObject = (JSONObject)events.get(i);
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
		JSONArray charts = level.getJSONArray("angleData");
		List<Double> chartArray = new ArrayList<>();
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

    public String getSetting(String setting) {
    	return settings.get(setting).toString();
    }
    
    public void setLevelSetting(String key,Object value) {
    	settings.put(key,value);
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
				speed.add(new String[]{String.valueOf(i),getSpeed(i)[0],getSpeed(i)[1]});
			}
		}
		return speed;
	}

	public String[] getSpeed(int chart) throws JSONException {
		JSONObject event;
		for (int a = 0; a < events.length(); a++) {
			event = (JSONObject)events.get(a);
			if ((int)event.get("floor") == chart && event.get("eventType").equals("SetSpeed")) {
				String isMultiplier = "false";
				if (event.get("speedType").equals("Multiplier")) {
					isMultiplier = "true";
					return new String[] {String.valueOf(chart),isMultiplier,event.get("bpmMultiplier").toString()};
				} else {
					return new String[] {String.valueOf(chart),isMultiplier,event.get("beatsPerMinute").toString()};
				}
			}
		}
		return null;
	}

	public List<JSONObject> getChartEvents(int chart) throws JSONException {
		JSONObject eventObject;
		List<JSONObject> chartEvents = new ArrayList<>();
		for (int a = 0; a < events.length(); a++) {
			eventObject = (JSONObject)events.get(a);
			if ((int)eventObject.get("floor") == chart) {
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
			eventObject = (JSONObject)events.get(a);
			if ((int)eventObject.get("floor") == chart && eventObject.get("eventType").equals(event)) {
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
			eventObject = (JSONObject)events.get(i);
			if (eventObject.get("eventType").equals(event)) {
				events.remove(i);
				i--;
			}
		}
	}

	public void previewAndSave() throws JSONException,IOException {
		System.out.println(level.toString(2));
		File file = new File(currentLevelFile.replace(".adofai", "-mod.adofai"));
		FileWriter writer = new FileWriter(file);
		writer.write(level.toString(2));
		writer.close();
	}

}