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

public class Sidebar extends JPanel implements Tickable {
    static final int IH = 48, TOP = 84;
    final String[] items; final Consumer<String> cb;
    int sel = 0, hover = -1; double indY = 0; final double[] hov;
    Sidebar(String[] items, Consumer<String> cb) {
        this.items = items; this.cb = cb; hov = new double[items.length];
        setOpaque(false); setPreferredSize(new Dimension(210, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter ma = new MouseAdapter() {
            int idx(MouseEvent e) { int i = (e.getY() - TOP) / IH; return (e.getY() >= TOP && i < items.length) ? i : -1; }
            public void mouseMoved(MouseEvent e) { hover = idx(e); }
            public void mouseExited(MouseEvent e) { hover = -1; }
            public void mousePressed(MouseEvent e) { int i = idx(e); if (i >= 0 && i != sel) { sel = i; cb.accept(items[i]); } }
        };
        addMouseListener(ma); addMouseMotionListener(ma);
        tickables.add(this);
    }
    public void tick(double dt) {
        indY = damp(indY, sel * IH, 14, dt);          // indicator glides between items
        for (int i = 0; i < hov.length; i++) hov[i] = damp(hov[i], i == hover ? 1 : 0, 14, dt);
    }
    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = aa(g0);
        int w = getWidth(), h = getHeight();
        glass(g, 0, 0, w, h, 24);
        g.setFont(new Font("SansSerif", Font.BOLD, 20)); g.setColor(ACCENT);
        g.drawString("DSA NEXUS", 24, 44);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11)); g.setColor(new Color(150, 180, 220));
        g.drawString("visualization workstation", 24, 62);
        int iy = (int) (TOP + indY);
        g.setPaint(new GradientPaint(10, iy, alpha(ACCENT, 70), w - 10, iy, alpha(ACCENT2, 40)));
        g.fillRoundRect(10, iy + 3, w - 20, IH - 6, 16, 16);
        g.setColor(ACCENT); g.fillRoundRect(10, iy + 10, 4, IH - 20, 4, 4);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        for (int i = 0; i < items.length; i++) {
            double near = Math.max(0, 1 - Math.abs(indY / IH - i));
            int c = (int) (150 + 105 * Math.max(near, hov[i] * 0.7));
            g.setColor(new Color(c, Math.min(255, c + 10), 255));
            g.drawString((near > 0.5 ? "\u25B6 " : "   ") + items[i], 26 + (int) (4 * hov[i]), TOP + i * IH + IH / 2 + 5);
        }
        g.dispose();
    }
}
