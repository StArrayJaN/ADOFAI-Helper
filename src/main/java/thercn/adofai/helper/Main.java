package thercn.adofai.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONException;
public class Main {

//    static String platform = System.getProperty("os.name").toLowerCase();
//    public native static void start(double[] keyTimeList);
//    public native static boolean getKeyEvent();
    static String file = "F:\\ADOFAI\\HYPER ULTRA JACKPOT\\level.adofai";
    static String keyList = "ASDFGHJKLZXCVBNM";

    static {
        extractDll();
    }

    public static void main(String[] args) {
        if (checkIsJar()) {
            scanInput();
        }
        try {
            Level level = Level.readLevelFile(file);
            System.out.println("当前文件为" + level.currentLevelFile);
            System.out.println("获取到" + level.getCharts().size() + "个轨道");
            System.out.println("BPM:" + level.getBPM());
            System.out.println("偏移:" + level.getOffset());
            System.out.println("获取到" + level.events.length() + "个事件");
            LevelUtils.runMacro(level,keyList);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void scanInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入.adofai文件路径");
        String input = scanner.nextLine();
        if (Files.exists(Paths.get(input))) {
            file = input;
        }
        System.out.println("请输入键位(例如:ASDFG):");
        String keys = scanner.nextLine();
        keyList = keys.toUpperCase();
        System.out.println("键位:" + keyList);
        scanner.close();
    }

    public static void extractDll() {
        String dllName = "JNativeHook.x86_64.dll";
        try {
            var dllStream = Main.class.getResourceAsStream("/" + dllName);
            var jarFile = new File(String.valueOf(Main.class.getResource("Main.class")).split("!")[0].replace("jar:file:/", ""));
            Path destPath = Paths.get(jarFile.getParent(), dllName);
            if (!Files.exists(destPath)) {
                Files.copy(dllStream, destPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkIsJar() {
        String b = String.valueOf(Main.class.getResource("Main.class"));
        return b.startsWith("jar");
    }
}
