package thercn.adofai.helper;

import org.json.JSONArray;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static javax.sound.sampled.AudioFileFormat.*;

public class AudioMerger {
    public static void mixAudio(File baseFile, List<AudioInsert> inserts, File output)
            throws IOException, UnsupportedAudioFileException {

        // 读取基础音频
        AudioData base = readWav(baseFile);
        AudioFormat format = base.format;
        int sampleRate = (int) format.getSampleRate();
        int frameSize = format.getFrameSize(); // 每帧字节数

        // 将时间戳转换为字节位置
        for (AudioInsert insert : inserts) {
            insert.position = (int) (insert.timestamp * sampleRate / 1000.0) * frameSize;
            insert.position = Math.min(insert.position, base.pcmData.length); // 确保插入点不超过基础音频范围
        }

        // 按时间戳排序插入点
        inserts.sort(Comparator.comparingDouble(insert -> insert.timestamp));

        // 使用内存映射文件处理大数据
        File tempFile = File.createTempFile("audio_mix", ".dat");
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
             FileChannel channel = raf.getChannel()) {

            // 初始化缓冲区
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0,
                    base.pcmData.length); // 确保预留空间足够

            // 写入基础音频
            buffer.put(base.pcmData);
            buffer.force(); // 强制将数据写入磁盘

            // 批量处理插入
            Map<String, AudioData> clipCache = new HashMap<>();
            for (AudioInsert insert : inserts) {
                if (!clipCache.containsKey(insert.filePath)) {
                    clipCache.put(insert.filePath, readWav(new File(insert.filePath)));
                }
            }

            inserts.stream().forEach(insert -> {
                AudioData clip = clipCache.get(insert.filePath);
                int pos = insert.position;

                // 限制插入音频的长度，防止超出基础音频范围
                int insertLength = Math.min(clip.pcmData.length, base.pcmData.length - pos);
                if (insertLength <= 0) {
                    throw new IllegalArgumentException("插入点超出基础音频范围");
                }
                clip.pcmData = Arrays.copyOfRange(clip.pcmData, 0, insertLength);

                synchronized (buffer) {
                    buffer.position(pos);
                    for (int i = 0; i < clip.pcmData.length && pos + i < buffer.limit(); i++) {
                        byte mixed = addSamples(buffer.get(pos + i), clip.pcmData[i]);
                        buffer.put(pos + i, mixed);
                    }
                }
            });

            // 写入最终文件
            writeWav(output, buffer, format);
        } finally {
            tempFile.delete();
        }
    }

    /**
     * 创建一个指定时长的静音 WAV 文件
     *
     * @param file      输出文件路径
     * @param duration  静音时长（秒）
     * @param format    音频格式（AudioFormat）
     */
    public static void createSilentWav(File file, double duration, AudioFormat format)
            throws IOException, UnsupportedAudioFileException {

        // 计算帧数
        int frameCount = (int) (duration * format.getFrameRate());
        if (frameCount <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        // 创建音频流
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 写入静音数据
            byte[] silentData = new byte[frameCount * format.getFrameSize()];
            AudioInputStream silenceStream = new AudioInputStream(
                    new ByteArrayInputStream(silentData),
                    format,
                    frameCount
            );

            // 将静音数据写入文件
            AudioSystem.write(silenceStream, AudioFileFormat.Type.WAVE, file);
        }
    }

    /**
     * 创建一个默认格式的静音 WAV 文件
     * 默认格式：44100 Hz, 16-bit, PCM_SIGNED, 2 channels
     */
    public static void createSilentWav(File file, double duration) throws IOException, UnsupportedAudioFileException {
        AudioFormat format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100.0F, // 采样率
                16,       // 位深度
                2,        // 声道数
                4,        // 帧大小（2 channels * 16 bits = 4 bytes per frame）
                44100.0F, // 帧速率
                false     // 小端字节序
        );
        createSilentWav(file, duration, format);
    }
    // 写入WAV文件
    private static void writeWav(File file, MappedByteBuffer buffer, AudioFormat format)
            throws IOException {

        // 创建可访问的字节数组
        byte[] outputData = new byte[buffer.limit()]; // 使用缓冲区的总长度
        buffer.rewind(); // 重置缓冲区位置
        buffer.get(outputData);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(outputData);
             AudioInputStream ais = new AudioInputStream(bais, format, outputData.length)) {

            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
        }
    }
    // 音频数据容器
    private static class AudioData {
        AudioFormat format;
        byte[] pcmData;
    }

    // 插入点定义（基于时间戳）
    public static class AudioInsert {
        double timestamp; // 时间戳（毫秒）
        String filePath; // 音频文件路径
        int position;    // 计算后的字节位置
        public AudioInsert(double timestamp, String filePath) {
            this.timestamp = timestamp;
            this.filePath = filePath;
        }
    }

    // 读取WAV文件
    private static AudioData readWav(File file)
            throws UnsupportedAudioFileException, IOException {

        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = ais.getFormat();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[4096];
            int bytesRead;
            while ((bytesRead = ais.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }

            AudioData audio = new AudioData();
            audio.format = format;
            audio.pcmData = buffer.toByteArray();
            return audio;
        }
    }

    // 样本混合（16位小端处理）
    private static byte addSamples(byte b1, byte b2) {
        return (byte) Math.min(Byte.MAX_VALUE, Math.max(Byte.MIN_VALUE, b1 + b2));
    }

    public static Clip export(String hitSoundPath, List<Double> hitSoundTimes, String outputPath) throws Exception {
        File file = new File(hitSoundPath);
        File inputFile = new File("input.wav");
        inputFile.deleteOnExit();
        // 构建插入点列表（单位：采样数）
        double first = hitSoundTimes.get(0);
        hitSoundTimes.replaceAll(aDouble -> aDouble - first);
        List<AudioInsert> inserts = new ArrayList<>();
        for (int i = 0; i < hitSoundTimes.size(); i++) {
            if (i == 0) {
                createSilentWav(inputFile, hitSoundTimes.get(hitSoundTimes.size() -1) / 1000 + 10);
            }
            inserts.add(new AudioInsert(hitSoundTimes.get(i) , file.toString())); // 假设44.1kHz采样率
        }
        // 执行混合
        mixAudio(inputFile, inserts, new File(outputPath));
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(outputPath)));
        audioClip = clip;
        return clip;
    }
    public static Clip audioClip;
}
