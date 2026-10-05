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

public class Dragon implements Tickable {
    static final int N = 16;
    final double[] x = new double[N], y = new double[N];
    double hx, hy, angle, time, boost;
    boolean init;

    Dragon() { tickables.add(this); }

    public void tick(double dt) {
        time += dt;
        if (mouse.x < -5000) return;
        double tx = mouse.x + 16, ty = mouse.y + 20;    // hovers beside the cursor, never covers it
        if (!init) { for (int i = 0; i < N; i++) { x[i] = tx; y[i] = ty; } hx = tx; hy = ty; init = true; }
        double k = 1 - Math.exp(-7 * dt);               // smoothing: head glides after cursor
        double nx = hx + (tx - hx) * k, ny = hy + (ty - hy) * k;
        double vx = (nx - hx) / dt, vy = (ny - hy) / dt, sp = Math.hypot(vx, vy);
        hx = nx; hy = ny;
        boost = damp(boost, Math.min(1, sp / 1400), 6, dt);   // rapid movement -> stronger motion
        if (sp > 30) angle += angDiff(Math.atan2(vy, vx), angle) * (1 - Math.exp(-10 * dt));
        double bob = sp < 30 ? Math.sin(time * 2.2) * 3 : 0;   // idle breathing / floating
        x[0] = hx; y[0] = hy + bob;
        for (int i = 1; i < N; i++) {                    // each segment follows the previous one
            double dx = x[i - 1] - x[i], dy = y[i - 1] - y[i], d = Math.hypot(dx, dy);
            double gap = 9 * (1 - i * 0.02);
            if (d > gap) { double f = (d - gap) / d; x[i] += dx * f; y[i] += dy * f; }
        }
        if (boost > 0.35 && rnd.nextInt(3) == 0)
            particles.emit(hx, hy, rnd.nextGaussian() * 25, rnd.nextGaussian() * 25, 0.5, 2.5, ACCENT2);
    }

    void draw(Graphics2D g) {
        if (!init) return;
        double amp = 1.5 + boost * 5;
        double[] px = new double[N], py = new double[N];
        for (int i = 0; i < N; i++) {
            int a = Math.max(0, i - 1), b = Math.min(N - 1, i + 1);
            double dx = x[a] - x[b], dy = y[a] - y[b], d = Math.hypot(dx, dy) + 1e-6;
            double sway = Math.sin(time * 6 - i * 0.6) * amp * (i / (double) N);
            px[i] = x[i] - dy / d * sway; py[i] = y[i] + dx / d * sway;   // perpendicular tail wave
        }
        Composite old = g.getComposite();
        for (int i = N - 1; i >= 1; i--) {
            double t = i / (double) N, r = 6.5 * (1 - t) + 1.4;
            Color c = new Color(clamp255(ACCENT.getRed() + (ACCENT2.getRed() - ACCENT.getRed()) * t),
                    clamp255(ACCENT.getGreen() + (ACCENT2.getGreen() - ACCENT.getGreen()) * t),
                    clamp255(ACCENT.getBlue() + (ACCENT2.getBlue() - ACCENT.getBlue()) * t));
            g.setColor(alpha(c, 45));
            g.fill(new Ellipse2D.Double(px[i] - r * 1.8, py[i] - r * 1.8, r * 3.6, r * 3.6));   // soft glow
            g.setColor(alpha(c, 210));
            g.fill(new Ellipse2D.Double(px[i] - r, py[i] - r, r * 2, r * 2));
            if (i % 3 == 0) {                                                                      // back spikes
                double dx = px[i - 1] - px[i], dy = py[i - 1] - py[i], d = Math.hypot(dx, dy) + 1e-6;
                double nx = -dy / d, ny = dx / d;
                Path2D sp = new Path2D.Double();
                sp.moveTo(px[i] + nx * r * 0.6 - dx / d * 2, py[i] + ny * r * 0.6 - dy / d * 2);
                sp.lineTo(px[i] + nx * (r + 4), py[i] + ny * (r + 4));
                sp.lineTo(px[i] + nx * r * 0.6 + dx / d * 2, py[i] + ny * r * 0.6 + dy / d * 2);
                g.setColor(alpha(c, 230)); g.fill(sp);
            }
        }
        // head
        AffineTransform at = g.getTransform();
        g.translate(px[0], py[0]);
        g.rotate(angle);
        double sc = 1 + boost * 0.25;
        g.scale(sc, sc);
        g.setColor(alpha(ACCENT, 50)); g.fill(new Ellipse2D.Double(-15, -13, 30, 26));
        Path2D horn = new Path2D.Double();
        horn.moveTo(-3, -5); horn.lineTo(-13, -13); horn.lineTo(1, -7); horn.closePath();
        Path2D horn2 = new Path2D.Double();
        horn2.moveTo(-3, 5); horn2.lineTo(-13, 13); horn2.lineTo(1, 7); horn2.closePath();
        g.setColor(alpha(ACCENT2, 230)); g.fill(horn); g.fill(horn2);
        Path2D head = new Path2D.Double();
        head.moveTo(-8, 0); head.curveTo(-8, -8, 4, -8, 13, -3); head.lineTo(15, 0); head.lineTo(13, 3);
        head.curveTo(4, 8, -8, 8, -8, 0); head.closePath();
        g.setPaint(new GradientPaint(-8, 0, ACCENT2, 14, 0, ACCENT));
        g.fill(head);
        g.setColor(Color.WHITE); g.fill(new Ellipse2D.Double(3, -4.5, 4, 4));
        g.setColor(BG1);        g.fill(new Ellipse2D.Double(4.5, -3.6, 2, 2.4));
        g.setTransform(at);
        g.setComposite(old);
    }
}
