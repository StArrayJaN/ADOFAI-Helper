package thercn.adofai.helper;

import com.github.kwhat.jnativehook.NativeHookException;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.JMarsDarkTheme;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Main extends JFrame {
    private JTextField filePathField;
    private HintTextField keyList;
    private JTextArea info;
    private JButton selectFileButton;
    private JButton processButton;
    private JProgressBar progressBar;

    public Main() {
        setTitle("ADOFAI Helper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setLayout(null);

        filePathField = new JTextField();
        filePathField.setBounds(50, 20, 200, 30);

        keyList = new HintTextField("请输入键位");
        keyList.setBounds(50, 60, 200, 30);

        add(filePathField);
        add(keyList);

        selectFileButton = new JButton("选择文件");
        selectFileButton.setBounds(260, 20, 100, 30);
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileFilter(new FileNameExtensionFilter("ADOFAI Level File", "adofai"));
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        add(selectFileButton);

        processButton = new JButton("运行宏");
        processButton.setBounds(150, 100, 100, 30);
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
                {
                    try {
                        Level level = Level.readLevelFile(filePath);
                        LevelUtils.runMacro(level, keyList.getText().toUpperCase());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        add(processButton);

        progressBar = new JProgressBar();
        progressBar.setBounds(50, 140, 300, 30);
        progressBar.setVisible(false);
        add(progressBar);

        info = new JTextArea();
        info.setMinimumSize(new Dimension(300, 60));
        info.setRows(10);

        JScrollPane scrollPane = new JScrollPane(info);
        scrollPane.setBounds(50, 180, 300, 60);
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
    public static void main(String[] args) throws NativeHookException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main main = new Main();
                main.setVisible(true);
                var theme = new JMarsDarkTheme() {
                    @Override
                    public void installFonts() {
                        this.fontItalic = new FontUIResource(Font.SANS_SERIF, Font.ITALIC, 12);
                        this.fontBold = new FontUIResource(Font.SANS_SERIF, Font.BOLD, 12);
                        this.fontRegular = new FontUIResource(Font.SANS_SERIF, Font.PLAIN, 12);
                        this.fontMedium = new FontUIResource(Font.SANS_SERIF, Font.PLAIN, 12);
                    }
                };
                MaterialLookAndFeel materialLookAndFeel = new MaterialLookAndFeel(theme);
                try {
                    UIManager.setLookAndFeel(materialLookAndFeel);
                } catch (UnsupportedLookAndFeelException e) {
                    throw new RuntimeException(e);
                }
                SwingUtilities.updateComponentTreeUI(main);
            }
        });
    }
}
