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

public class HoverButton extends JButton implements Tickable {
    double hov, prs; boolean hover, press;
    HoverButton(String text, Runnable action) {
        super(text);
        setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
        setForeground(Color.WHITE); setFont(new Font("SansSerif", Font.BOLD, 13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(getFontMetrics(getFont()).stringWidth(text) + 44, 44));
        addActionListener(e -> action.run());
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hover = true; }
            public void mouseExited(MouseEvent e) { hover = false; press = false; }
            public void mousePressed(MouseEvent e) { press = true; }
            public void mouseReleased(MouseEvent e) { press = false; }
        });
        tickables.add(this);
    }
    public void tick(double dt) { hov = damp(hov, hover ? 1 : 0, 14, dt); prs = damp(prs, press ? 1 : 0, 30, dt); }
    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = aa(g0);
        int w = getWidth(), h = getHeight();
        double s = 1 + 0.07 * hov - 0.06 * prs;
        g.translate(w / 2.0, h / 2.0); g.scale(s, s); g.translate(-w / 2.0, -h / 2.0);
        int x = 7, y = 7, bw = w - 14, bh = h - 14;
        for (int k = 4; k >= 1; k--) {                                    // soft hover glow
            g.setColor(alpha(ACCENT, (int) (26 * hov / k)));
            g.setStroke(new BasicStroke(k * 2.2f));
            g.drawRoundRect(x, y, bw, bh, 16, 16);
        }
        g.setPaint(new GradientPaint(0, y, alpha(ACCENT, (int) (40 + 60 * hov + 30 * prs)), 0, y + bh, alpha(ACCENT2, (int) (40 + 40 * hov))));
        g.fillRoundRect(x, y, bw, bh, 16, 16);
        g.setColor(alpha(ACCENT, (int) (110 + 120 * hov)));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, bw, bh, 16, 16);
        g.setFont(getFont()); g.setColor(getForeground());
        centerText(g, getText(), w / 2.0, h / 2.0);
        g.dispose();
    }
}
