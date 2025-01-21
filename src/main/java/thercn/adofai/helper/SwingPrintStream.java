package thercn.adofai.helper;

import javax.swing.*;
import java.io.PrintStream;

public class SwingPrintStream extends PrintStream {
    private final JTextArea textArea;

    public SwingPrintStream(JTextArea textArea) {
        super(System.out);
        this.textArea = textArea;
    }

    @Override
    public void println(String x) {
        textArea.append(x + "\n");
    }

    @Override
    public void println(Object x) {
        textArea.append(x + "\n");
    }

    @Override
    public void println() {
        textArea.append("\n");
    }

    @Override
    public void print(String x) {
        textArea.append(x);
    }

    @Override
    public void print(Object x) {
        textArea.append(x.toString());
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        textArea.append(String.format(format, args));
        return this;
    }
}
