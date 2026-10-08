package dsanexus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static dsanexus.Theme.*;
import static dsanexus.App.*;

public class Background extends JPanel implements Tickable {
    static final int N = 70;
    double[] x, y, bvx, bvy, px, py;
    boolean ready;
    Background() { setOpaque(true); tickables.add(this); }

    public void tick(double dt) {
        if (getWidth() < 20 || overlay == null) return;
        if (!ready) {
            x = new double[N]; y = new double[N]; bvx = new double[N]; bvy = new double[N]; px = new double[N]; py = new double[N];
            for (int i = 0; i < N; i++) { x[i] = rnd.nextDouble() * getWidth(); y[i] = rnd.nextDouble() * getHeight();
                bvx[i] = rnd.nextGaussian() * 10; bvy[i] = rnd.nextGaussian() * 10; }
            ready = true;
        }
        Point m = SwingUtilities.convertPoint(overlay, mouse, this);
        double decay = Math.exp(-2 * dt);
        for (int i = 0; i < N; i++) {
            double dx = x[i] - m.x, dy = y[i] - m.y, d = Math.hypot(dx, dy);
            if (d < 150 && d > 1) { double f = (150 - d) * 0.9 * dt; px[i] += dx / d * f * 8; py[i] += dy / d * f * 8; }
            px[i] *= decay; py[i] *= decay;
            x[i] += (bvx[i] + px[i]) * dt; y[i] += (bvy[i] + py[i]) * dt;
            if (x[i] < 0) x[i] += getWidth(); if (x[i] > getWidth()) x[i] -= getWidth();
            if (y[i] < 0) y[i] += getHeight(); if (y[i] > getHeight()) y[i] -= getHeight();
        }
    }
    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = aa(g0);
        int w = getWidth(), h = getHeight();
        g.setPaint(new GradientPaint(0, 0, BG1, w, h, BG2));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(90, 160, 255, 12));
        for (int gx = 0; gx < w; gx += 48) g.drawLine(gx, 0, gx, h);
        for (int gy = 0; gy < h; gy += 48) g.drawLine(0, gy, w, gy);
        if (ready) {
            g.setStroke(new BasicStroke(1f));
            for (int i = 0; i < N; i++) for (int j = i + 1; j < N; j++) {
                double d = Math.hypot(x[i] - x[j], y[i] - y[j]);
                if (d < 110) { g.setColor(new Color(80, 170, 255, (int) (40 * (1 - d / 110)))); g.draw(new Line2D.Double(x[i], y[i], x[j], y[j])); }
            }
            g.setColor(new Color(120, 210, 255, 90));
            for (int i = 0; i < N; i++) g.fill(new Ellipse2D.Double(x[i] - 1.5, y[i] - 1.5, 3, 3));
        }
        g.dispose();
    }
}
