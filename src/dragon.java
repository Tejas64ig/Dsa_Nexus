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
