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

public class Overlay extends JComponent {
    Overlay() { setOpaque(false); }
    @Override public boolean contains(int x, int y) { return false; }     // clicks pass straight through
    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = aa(g0);
        particles.draw(g);
        dragon.draw(g);
        g.dispose();
    }
}
