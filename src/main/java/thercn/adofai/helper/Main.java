package thercn.adofai.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Console;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.Timestamp;
import java.util.concurrent.TimeUnit;
import org.json.JSONTokener;

public class Main {

    public static void main(String[] args) {
       JSONTokener t;
        String file = "/storage/emulated/0/level(1).adofai";
        String string = "";
        try {
            //System.loadLibrary("native");
            Level level = Level.readLevelFile(file);
            System.out.println("当前文件为" + level.currentLevelFile);
            System.out.println("获取到" + level.getCharts().size() + "个轨道");
            System.out.println("BPM:" + level.getBPM());
            System.out.println("偏移:" + level.getOffset());
            System.out.println("获取到" + level.getEvents() + "个事件");
            removeEffects(level);
            level.save(null);
            System.out.println("删除完成");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static String catFile(String filePath){
    	File file = new File(filePath);
        BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), "UTF-8"));
            StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();
        int i = content.toString().indexOf("{");
        String newJSONStr = content.toString().substring(i);
        return newJSONStr;
        } catch(Exception err) {
        	err.printStackTrace();
        }
        return null;
        
        
    }
    public static String scannerInput() {
        Console console = System.console();
        String input = console.readLine("请输入内容：");
        if (console != null) {
            System.out.println("你输入的内容是：" + input);
        } else {
            System.out.println("无法读取控制台输入");
        }
        return input;
    }

    static void removeEffects(Level l) throws JSONException {
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
			"RepeatEvents",
			"Bloom",
			"ScreenScroll"
            };
        for (int i = 0; i < effectEvents.length; ++i) {
        	l.removeAllEvent(effectEvents[i],true);
            l.removeAllEvent(effectEvents[i],false);
        }
        
    }
    static void convertToOld(Level level) throws JSONException {
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
                } catch (Exception err) {}
            }
        } 
        level.removeAllEvent("setFloorIcon",false);
        level.removeAllEvent("AddObject",false);
        level.removeAllEvent("MoveObject",false);
		level.setLevelSetting("version", 12);
	}

    static void runMacro(Level l) throws JSONException {
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
        double angle = fmod(angleDataList.get(angleDataList.size() - 1), 360);
		JSONObject temp = new JSONObject();
		temp.put("angle", angle);
		temp.put("bpm", "unSet");
		temp.put("direction", 0);
		temp.put("extraHold", 0);
		temp.put("midr", "false");
		parsedChart.put(parsedChart.length(), temp);

        double bpm = l.getBPM();
        float pitch = l.getPitch() / 100;
        for (int i = 0; i < l.events.length(); i++) {
            JSONObject o = l.events.getJSONObject(i);
            int tile = o.getInt("floor");
            String event = o.get("eventType").toString();
            tile -= upperBound(midrId.toArray(new Integer[0]), tile);

            if (event.equals("SetSpeed")) {
                JSONObject ob = parsedChart.getJSONObject(tile);
                if (o.get("speedType").equals("Multiplier")) {
                    bpm = o.getDouble("bpmMultiplier") * bpm * pitch;
                } else {
                    bpm = o.getDouble("beatsPerMinute") * pitch;
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

        List<Double> noteTime = new ArrayList<>(),
			noteOffset = new ArrayList<>();
        {
            double curAngle = 0;
            double curBPM = l.getBPM();
            double curTime = 0;
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
                noteOffset.add(angleToTime(pAngle, curBPM));
                noteTime.add(curTime);
            }

        }
        System.out.println("处理完成,按W开始");
        double[] n = new double[noteTime.size()];
        for (int i = 0; i < noteTime.size(); i++) {
            n[i] = noteTime.get(i);
            //System.out.println("当前方块数量:" + i + ",当前方块BPM:" + 60 * 1000 / noteOffset.get(i) + "BPM");
        }
		final List<Double> a = noteOffset;
		Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					for (int i = 0; i < a.size(); i++) {
						System.out.println("当前方块数量:" + i + ",当前方块BPM:" + 60 * 1000 / a.get(i) + "BPM");
						Double b = a.get(i) * 1000;
						try {
							TimeUnit.MICROSECONDS.sleep(b.longValue());
						} catch (InterruptedException e) {}
					}
				}
			});
        t.start();
		System.out.println("我槽你吗");
        //start(n);
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
}
