package thercn.adofai.helper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class GradientLabel  extends JLabel {
    private final String text;
    private final int[] colors;
    private final int speed;
    private final GradientDirection direction;
    private float offset = 0;
    private final float delta;

    public GradientLabel(String text, int[] colors, int speed, GradientDirection direction) {
        super(text);
        this.text = text;
        this.colors = colors;
        this.speed = speed;
        this.direction = direction;
        setOpaque(false);

        // 确定滑动方向增量
        delta = (direction == GradientDirection.RIGHT_TO_LEFT || direction == GradientDirection.BOTTOM_TO_TOP) ? -0.01f : 0.01f;

        Timer timer = new Timer(speed, e -> {
            offset += delta;
            if (offset > 1.0f) offset = 0.0f; // 重置偏移量，确保循环
            repaint();
        });
        timer.start();
    }

    public static int[] getRainbowColors() {
        return new int[] {0xFF0000, 0xFFFF00, 0x00FF00, 0x00FFFF, 0x0000FF, 0xFF00FF};
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 设置字体并计算居中位置
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        // 转换颜色数组
        Color[] colorArr = new Color[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colorArr[i] = new Color(colors[i]);
        }

        // 创建颜色分布比例
        float[] fractions = new float[colors.length];
        for (int i = 0; i < fractions.length; i++) {
            fractions[i] = (float) i / (fractions.length - 1);
        }

        // 根据方向创建渐变
        Point2D start, end;
        switch (direction) {
            case LEFT_TO_RIGHT:
                start = new Point2D.Float(x + offset * getWidth(), y);
                end = new Point2D.Float(x + offset * getWidth() + getWidth(), y);
                break;
            case RIGHT_TO_LEFT:
                start = new Point2D.Float(x + offset * getWidth(), y);
                end = new Point2D.Float(x + offset * getWidth() - getWidth(), y);
                break;
            case TOP_TO_BOTTOM:
                start = new Point2D.Float(x, y + offset * getHeight());
                end = new Point2D.Float(x, y + offset * getHeight() + getHeight());
                break;
            case BOTTOM_TO_TOP:
                start = new Point2D.Float(x, y + offset * getHeight());
                end = new Point2D.Float(x, y + offset * getHeight() - getHeight());
                break;
            default:
                throw new IllegalArgumentException("Invalid direction");
        }

        // 应用渐变
        LinearGradientPaint paint = new LinearGradientPaint(
                start, end, fractions, colorArr,
                java.awt.MultipleGradientPaint.CycleMethod.REPEAT
        );
        g2d.setPaint(paint);
        g2d.drawString(text, x, y);
    }
    public enum GradientDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP
    }
}
