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

public class SortView extends View {
    static final int N = 22;
    static class Bar { int v; double x, lift, glow, bounce, bv; boolean done; }
    final Bar[] bars = new Bar[N];
    List<int[]> ops = new ArrayList<>();
    int opIdx, selA = -1, selB = -1; double opTimer; boolean running;

    SortView() {
        addTool(new HoverButton("Shuffle", this::shuffle));
        addTool(new HoverButton("Bubble Sort", () -> start("bubble")));
        addTool(new HoverButton("Selection Sort", () -> start("selection")));
        addTool(new HoverButton("Insertion Sort", () -> start("insertion")));
        shuffle();
        MouseAdapter ma = new MouseAdapter() {       // paint bar heights with the mouse
            public void mousePressed(MouseEvent e) { editBar(e); }
            public void mouseDragged(MouseEvent e) { editBar(e); }
        };
        canvas.addMouseListener(ma); canvas.addMouseMotionListener(ma);
    }
    void editBar(MouseEvent e) {
        if (running) return;
        int w = canvas.getWidth(), h = canvas.getHeight();
        double slotW = (w - 80.0) / N;
        int idx = (int) Math.floor((e.getX() - 40) / slotW);
        if (idx < 0 || idx >= N) return;
        int v = (int) Math.round((h - 60 - e.getY()) / (h - 170.0) * 100);
        bars[idx].v = Math.max(5, Math.min(100, v));
        for (Bar b : bars) b.done = false;
        bannerT = -1;
    }
    void shuffle() {
        steps.clear(); ops.clear(); running = false; bannerT = -1; selA = selB = -1;
        for (int i = 0; i < N; i++) { Bar b = bars[i] != null ? bars[i] : new Bar(); b.v = 8 + rnd.nextInt(93); b.done = false; b.x = i; bars[i] = b; }
    }
    void start(String algo) {
        if (running || steps.busy()) return;
        for (Bar b : bars) b.done = false;
        bannerT = -1;
        int[] a = new int[N];
        for (int i = 0; i < N; i++) a[i] = bars[i].v;
        ops = new ArrayList<>();
        
        if (algo.equals("bubble")) {
            // Bubble Sort - compare adjacent, swap if out of order
            for (int i = 0; i < N - 1; i++) {
                for (int j = 0; j < N - 1 - i; j++) {
                    // Compare a[j] and a[j+1]
                    ops.add(new int[]{0, j, j + 1});
                    if (a[j] > a[j+1]) {
                        // Swap them
                        int temp = a[j];
                        a[j] = a[j+1];
                        a[j+1] = temp;
                        ops.add(new int[]{1, j, j + 1});
                    }
                }
            }
        } else if (algo.equals("selection")) {
            // Selection Sort - find minimum, place it
            for (int i = 0; i < N - 1; i++) {
                int minIdx = i;
                for (int j = i + 1; j < N; j++) {
                    ops.add(new int[]{0, minIdx, j});
                    if (a[j] < a[minIdx]) minIdx = j;
                }
                // Swap minimum to correct position
                if (minIdx != i) {
                    int temp = a[i];
                    a[i] = a[minIdx];
                    a[minIdx] = temp;
                    ops.add(new int[]{1, i, minIdx});
                }
            }
        } else {
            // Insertion Sort - insert each element into sorted portion
            for (int i = 1; i < N; i++) {
                for (int j = i; j > 0; j--) {
                    ops.add(new int[]{0, j - 1, j});
                    if (a[j-1] > a[j]) {
                        int temp = a[j];
                        a[j] = a[j-1];
                        a[j-1] = temp;
                        ops.add(new int[]{1, j - 1, j});
                    } else break;
                }
            }
        }
        opIdx = 0; opTimer = 0; running = true;
    }
    void step() {
        if (opIdx >= ops.size()) { finish(); return; }
        int[] op = ops.get(opIdx++);
        selA = op[1]; selB = op[2];
        if (op[0] == 1) {
            Bar t = bars[op[1]]; bars[op[1]] = bars[op[2]]; bars[op[2]] = t;
            bars[op[1]].bv = -170; bars[op[2]].bv = -170;         // bounce when swapped
        }
    }
    void finish() {
        running = false; selA = selB = -1;
        for (int i = 0; i < N; i++) { final int j = i; steps.add(0.03, () -> bars[j].done = true); }
        steps.add(0, () -> complete("\u2713 SORTING COMPLETE"));
    }
    void update(double dt) {
        if (running) { opTimer += dt; while (opTimer >= 0.06 && running) { opTimer -= 0.06; step(); } }
        for (int i = 0; i < N; i++) {
            Bar b = bars[i]; boolean sel = i == selA || i == selB;
            b.x = damp(b.x, i, 16, dt);
            b.lift = damp(b.lift, sel ? 1 : 0, 14, dt);
            b.glow = damp(b.glow, sel ? 1 : (b.done ? 0.55 : 0), 8, dt);
            b.bv += (-b.bounce * 260 - b.bv * 9) * dt; b.bounce += b.bv * dt;
        }
    }
    void render(Graphics2D g, int w, int h) {
        double slotW = (w - 80.0) / N, bw = slotW * 0.6, depth = bw * 0.4, base = h - 60, maxH = h - 170;
        g.setColor(new Color(90, 160, 255, 40)); g.fillRoundRect(28, (int) base + 6, w - 56, 6, 6, 6);   // floor
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        for (int i = 0; i < N; i++) {
            Bar b = bars[i];
            double bh = b.v / 100.0 * maxH, cx = 40 + (b.x + 0.5) * slotW;
            Color c = b.done ? Color.getHSBColor(0.40f, 0.65f, 0.95f) : Color.getHSBColor((float) (0.52 + 0.22 * b.v / 100.0), 0.75f, 0.95f);
            drawBar(g, cx, base + b.bounce - b.lift * 14, bw, bh, depth, c, b.glow, 1 + 0.08 * b.lift);
            g.setColor(new Color(200, 225, 255, 200));
            centerText(g, String.valueOf(b.v), cx, base + 24);
        }
        g.setColor(new Color(150, 180, 220, 150)); g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Click / drag on the bars to edit their heights", 30, 22);
    }
    static void drawBar(Graphics2D g, double cx, double base, double w, double h, double d, Color c, double glow, double scale) {
        AffineTransform old = g.getTransform();
        g.translate(cx, base); g.scale(scale, scale);
        double x0 = -w / 2;
        g.setColor(new Color(0, 0, 0, 90));
        g.fill(new Ellipse2D.Double(x0 - 2, -d * 0.4, w + d + 4, d * 0.9));                            // shadow
        if (glow > 0.02) for (int k = 4; k >= 1; k--) {
            g.setColor(alpha(c, (int) (34 * glow / k * 2))); g.setStroke(new BasicStroke(k * 3f));
            g.drawRoundRect((int) x0 - 1, (int) -h - 1, (int) w + 2, (int) h + 2, 6, 6);
        }
        g.setPaint(new GradientPaint((float) x0, (float) -h, shade(c, 1.15), (float) x0, 0, shade(c, 0.65)));
        g.fill(new Rectangle2D.Double(x0, -h, w, h));                                                   // front
        Path2D top = new Path2D.Double();
        top.moveTo(x0, -h); top.lineTo(x0 + d, -h - d * 0.7); top.lineTo(x0 + w + d, -h - d * 0.7); top.lineTo(x0 + w, -h); top.closePath();
        g.setColor(shade(c, 1.4)); g.fill(top);                                                         // top
        Path2D side = new Path2D.Double();
        side.moveTo(x0 + w, -h); side.lineTo(x0 + w + d, -h - d * 0.7); side.lineTo(x0 + w + d, -d * 0.7); side.lineTo(x0 + w, 0); side.closePath();
        g.setColor(shade(c, 0.5)); g.fill(side);                                                        // side
        g.setColor(new Color(255, 255, 255, 60)); g.fill(new Rectangle2D.Double(x0 + 2, -h + 2, 3, Math.max(0, h - 4)));
        g.setTransform(old);
    }
}
