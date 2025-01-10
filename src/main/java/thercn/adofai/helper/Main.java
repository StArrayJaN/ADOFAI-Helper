package thercn.adofai.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.github.kwhat.jnativehook.NativeInputEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;

public class Main {

    static String platform = System.getProperty("os.name").toLowerCase();
    public native static void start(double[] keyTimeList, String keys, double minClickInterval);

    public static void main(String[] args) {
        String file = "D:\\netease\\Downloads\\Hello_bpm_2024_By_QJsummer(2)\\2\\level.adofai";
        //file = null;
        try {

            if (platform.contains("windows")) {
            	System.loadLibrary("native");
            }
            Level level = Level.readLevelFile(file);
            System.out.println("当前文件为" + level.currentLevelFile);
            System.out.println("获取到" + level.getCharts().size() + "个轨道");
            System.out.println("BPM:" + level.getBPM());
            System.out.println("偏移:" + level.getOffset());
            System.out.println("获取到" + level.events.length() + "个事件");
            //运行任务，removeEffects为删除特效，convertToOld为转成旧版，runMacro为运行宏
            convertToOld(level);
            runMacro(level);
            //保存，删特效和转旧版需要，运行宏不需要
            level.saveFile(null);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


	static double currentTime() {
		return System.nanoTime() / 1E6;
	}

	static class StartMacro implements NativeKeyListener {

		Double[] bpmList;
		Robot bot;
		public StartMacro(Double[] list) {
			bpmList = list;
		}

		public void prepare() {
			try {
				GlobalScreen.registerNativeHook();
				GlobalScreen.addNativeKeyListener(this);
			} catch (NativeHookException e) {}

			try {
				bot = new Robot();
			} catch (AWTException e) {}
		}
		
		@Override
		public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
			if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_W) {
				double start = currentTime();
				int events = 1;
				while (events < bpmList.length) {
					double cur = currentTime();
					double timeMilliseconds = (cur - start) + bpmList[0];
					while (events < bpmList.length && bpmList[events] <= timeMilliseconds) {
						bot.keyPress(KeyEvent.VK_E);
						bot.keyRelease(KeyEvent.VK_E);
                        System.out.println(events);
						events++;
					}
				}
                System.exit(0);
			}
		}

	}
    static void runMacro(Level l) throws JSONException {
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
                parsedChart.put(i - midrCount, temp);
            }

        }
        double angle = fmod(angleDataList.get(angleDataList.size() - 1), 360);
        //创建一个json节点
		JSONObject temp = new JSONObject();
		temp.put("angle", angle);
		temp.put("bpm", "unSet");
		temp.put("direction", 0);
		temp.put("extraHold", 0);
		temp.put("midr", "false");
		parsedChart.put(parsedChart.length(), temp);

        double bpm = l.getBPM();
        float pitch = l.getPitch() / 100;
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
                    bpm = o.getDouble("bpmMultiplier") * bpm * pitch;
                } else {
                    bpm = o.getDouble("beatsPerMinute") * pitch;
                }
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
        }

        double BPM = l.getBPM();
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
                BPM = ob.getDouble("bpm");
            }


        }
        List<Double> noteTime = new ArrayList<>(),
			noteOffset = new ArrayList<>();
        {
            double curAngle = 0;
            double curBPM = l.getBPM();
            double curTime = 0; //核心:按键时间
            for (int i = 0; i < parsedChart.length(); i++) {
                JSONObject o = parsedChart.getJSONObject(i);
                //设置角度
                curAngle = fmod(curAngle - 180, 360);
                curBPM = o.getDouble("bpm");
                double destAngle = o.getDouble("angle");
                double pAngle = 0; 
                if (Math.abs(destAngle - curAngle) <= 0.001) {
                    //(疑似)取整
                    pAngle = 360;
                } else {
                    pAngle = fmod((curAngle - destAngle) * o.getInt("direction"), 360);
                }
                pAngle += o.getDouble("extraHold") * 360;
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


        double[] n = new double[noteTime.size()];
		Double[] v = new Double[noteTime.size()];
        for (int i = 0; i < noteTime.size(); i++) {
            n[i] = noteTime.get(i);
			v[i] = noteTime.get(i);
        }
		System.out.println("处理完成,按W开始");
		StartMacro start =	new StartMacro(v);
		start.prepare();
        /*
        if (platform.contains("windows")) {
        	start(n,"ABCDEFGH",0.1);
        } else {
			start_j(v);
		}*/
    }

    public static String readFile(String filePath) {
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
			return content.toString();
        } catch (Exception err) {
        	err.printStackTrace();
        }
        return null;
    }

    public static String scannerInput() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext("请输入一个文件路径:")) {
            String input = scanner.nextLine();
            return input;
        }
        return null;
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
        	l.removeAllEvent(effectEvents[i], true);
            l.removeAllEvent(effectEvents[i], false);
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
            //System.out.println(level.hasSetting(newSettingValue[i]));
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
                System.out.println(level.getSetting(newSettingValue[i]).toString().equals("true"));
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
                } catch (Exception err) {}
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

        level.removeAllEvent("setFloorIcon", false);
        level.removeAllEvent("AddObject", false);
        level.removeAllEvent("MoveObject", false);
        //System.out.println(level.settings.toString(2));
		level.setLevelSetting("version", 12);
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
    //伪模运算
    public static double fmod(double a, double b) {
        double t = Math.floor(a / b);
        return a - b * t;
    }
    //看什么注释，看方法名字↓
    public static double angleToTime(double angle, double bpm) {
        return (angle / 180) * (60 / bpm) * 1000;
    }
}
