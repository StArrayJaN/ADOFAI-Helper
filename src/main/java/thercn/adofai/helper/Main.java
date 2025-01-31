package thercn.adofai.helper;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.json.JSONArray;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private JTextField filePathField;
    private HintTextField keyList;
    private JTextArea info;
    private JButton selectFileButton;
    private JButton processButton;
    private JProgressBar progressBar;
    private JFrame consoleFrame;
    private JToggleButton toggleButton;
    private JCheckBox saveDelayTableCheckBox;
    private boolean saveDelayTable = false;
    private Point initialClick = new Point(0, 0);
    private Dimension size = new Dimension(470, 100);
    private LastOpenManager lastOpenManager = LastOpenManager.getInstance();
    public static boolean enableConsole = false;

    public Main() throws IOException {
        initializeUI();
        addComponents();
        addListeners();

    }

    private void initializeUI() {
        setTitle("ADOFAI Helper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 200));
        setLayout(null);
    }

    private void addComponents() {
        filePathField = new JTextField();
        filePathField.setBounds(50, 20, 200, 30);
        add(filePathField);

        keyList = new HintTextField("请输入键位");
        keyList.setBounds(50, 60, 200, 30);
        add(keyList);

        selectFileButton = new JButton("选择文件");
        selectFileButton.setBounds(260, 20, 100, 30);
        add(selectFileButton);

        processButton = new JButton("运行宏");
        processButton.setBounds(150, 100, 100, 30);
        add(processButton);

        saveDelayTableCheckBox = new JCheckBox("保存延迟表");
        saveDelayTableCheckBox.setBounds(260, 60, 100, 30);
        add(saveDelayTableCheckBox);

        progressBar = new JProgressBar();
        progressBar.setBounds(0, 140, getWidth(), 20);
        progressBar.setMaximum(100);
        progressBar.setVisible(false);
        add(progressBar);

        info = new JTextArea();
        info.setSize(size);
        info.setEditable(false);
        info.setText("欢迎使用ADOFAI Helper\n");

        if (!lastOpenManager.getLastOpenFile().equals("") || !lastOpenManager.getKeyList().equals("")) {
            filePathField.setText(lastOpenManager.getLastOpenFile());
            keyList.setText(lastOpenManager.getKeyList());
        }

        JScrollPane scrollPane = new JScrollPane(info);
        scrollPane.setAutoscrolls(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 设置 JScrollPane 的首选大小
        scrollPane.setSize(size);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
        add(scrollPane);

        LevelUtils.processListener = new LevelUtils.ProcessListener() {
            @Override
            public void onProcessDone(String message, State state) {
                info.append(message + "\n");
                if (state == State.STOPPED) {
                    System.exit(0);
                }
            }

            @Override
            public void onProcessChange(String message, int progress) {
                if (progress > 0 && !progressBar.isVisible()) {
                    progressBar.setVisible(true);
                }
                info.append(message + "\n");
                progressBar.setValue(progress);
            }
        };

        consoleFrame = new JFrame("控制台");
        consoleFrame.setUndecorated(true);
        consoleFrame.setSize(size);
        consoleFrame.setLocationRelativeTo(null);
        consoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addConsoleFramePopupMenu();
        addConsoleFrameMouseListeners();
        consoleFrame.add(scrollPane);
        consoleFrame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        consoleFrame.setVisible(false);

        toggleButton = new JToggleButton("显示控制台");
        toggleButton.setBounds(260, 100, 110, 30);
        add(toggleButton);
    }

    private void addListeners() {
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setCurrentDirectory(new File(getRuntimePath()));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ADOFAI Level File", "adofai"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ADOFAI Helper Delay Table", "ahjson"));
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (saveDelayTable && selectedFile.getName().endsWith(".ahjson")) {
                        JOptionPane.showMessageDialog(null, "请选择一个adofai文件");
                        return;
                    }
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                if (filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请选择一个文件");
                }
                if (keyList.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入键位");
                } else if (!keyListCanUse(keyList.getText().toUpperCase())) {
                    JOptionPane.showMessageDialog(null, "键位不能包含Q、W、→、←");
                    return;
                }
                lastOpenManager.setLastOpenFile(filePath);
                lastOpenManager.setKeyList(keyList.getText());
                try {
                    new Thread(() -> {
                        try {
                            progressBar.setVisible(true);
                            runMacro(keyList.getText().toUpperCase(), filePath);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }).start();
                    lastOpenManager.save();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!toggleButton.isSelected()) {
                    consoleFrame.dispose();
                } else {
                    System.setOut(new SwingPrintStream(info));
                    consoleFrame.setVisible(true);
                }
                enableConsole = toggleButton.isSelected();
            }
        });

        saveDelayTableCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filePathField.getText().endsWith(".ahjson")) {
                    saveDelayTableCheckBox.setSelected(false);
                    JOptionPane.showMessageDialog(null, "请选择一个adofai文件");
                    return;
                }
                saveDelayTable = saveDelayTableCheckBox.isSelected();
            }
        });
    }

    public void runMacro(String keyList, String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) return;
        List<Double> noteTimes = new ArrayList<>();
        if (file.getName().endsWith(".adofai")) {
            JSONArray delayTable = new JSONArray();
            Level level = Level.readLevelFile(file.toString());
            noteTimes = LevelUtils.getNoteTimes(level);
            if (saveDelayTable) {
                for (var item : noteTimes) {
                    delayTable.put(item);
                }
                try {
                    file = new File(getLevelDirectory(level),"delayTable.ahjson");
                    Files.write(file.toPath(), delayTable.toString(2).getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (file.getName().endsWith(".ahjson")) {
            genericHitSound(file.toString(),file.toString());
            JSONArray array = new JSONArray(new String(Files.readAllBytes(file.toPath())));
            noteTimes = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                noteTimes.add(array.getDouble(i));
            }
        }

        System.out.println("运行宏:" + file);

        LevelUtils.runMacro(noteTimes, keyList);
    }

    public static String getLevelDirectory(Level level) {
        return new File(level.currentLevelFile).getParent();
    }

    private void addConsoleFramePopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem alwaysOnTop = new JMenuItem("置顶");
        JMenuItem close = new JMenuItem("关闭");
        alwaysOnTop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consoleFrame.setAlwaysOnTop(!consoleFrame.isAlwaysOnTop());
            }
        });
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consoleFrame.dispose();
                toggleButton.setSelected(false);
            }
        });
        popupMenu.add(alwaysOnTop);
        popupMenu.add(close);
        info.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(consoleFrame, e.getX(), e.getY());
                }
            }
        });
    }

    private void addConsoleFrameMouseListeners() {
        info.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = consoleFrame.getLocation().x;
                int thisY = consoleFrame.getLocation().y;
                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                consoleFrame.setLocation(X, Y);
            }
        });
    }

    private boolean keyListCanUse(String keyList) {
        char[] chars = keyList.toCharArray();
        for (char c : chars) {
            if (c == 'Q' || c == 'W' || c == '→' || c == '←') {
                return false;
            }
        }
        return true;
    }

    public static String getRuntimePath() {
        String path = Main.class.getResource("Main.class").getPath();
        return new File(path.split("!")[0].replace("file:/", "")).getParent();
    }

    public static void exportResources(String path) throws IOException {
        String runtimePath = getRuntimePath();
        File file = new File(runtimePath,path);
        if (!file.exists()) {
            Files.write(file.toPath(), Main.class.getResourceAsStream("/" + path).readAllBytes());
        }
    }

    public static void genericHitSound(String string,String ahjson) throws Exception {
        exportResources("hit.wav");
        File file = new File(getRuntimePath(), "hit.wav");
        AudioMerger.export(file.toString(), ahjson, new File(string).getParent() + File.separator + "hitSound.wav");
    }

    public static void main(String[] args) {
        FlatLaf.setup(new FlatMacDarkLaf());
        SwingUtilities.invokeLater(() -> {
            try {
                Main main = new Main();
                main.setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
