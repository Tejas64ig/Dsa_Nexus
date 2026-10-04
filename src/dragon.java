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
