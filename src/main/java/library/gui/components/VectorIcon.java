package library.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Ellipse2D;

/**
 * A custom Icon implementation that draws vector shapes using Graphics2D.
 * This completely avoids missing .png file errors and scales perfectly.
 */
public class VectorIcon implements Icon {

    public enum IconType {
        HOME, PLUS_BOOK, PERSON, ARROW_RIGHT, ARROW_LEFT, 
        LIBRARY, USERS, HISTORY, SEARCH, TRASH, LOGOUT,
        MAIL, PHONE, LINK, MOON, SUN
    }

    private final IconType type;
    private final int size;
    private final Color color;

    public VectorIcon(IconType type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    public IconType getType() {
        return type;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int pad = 2;
        int s = size - (pad * 2);

        switch (type) {
            case HOME -> drawHome(g2, pad, pad, s);
            case PLUS_BOOK -> drawPlusBook(g2, pad, pad, s);
            case PERSON -> drawPerson(g2, pad, pad, s);
            case ARROW_RIGHT -> drawArrowRight(g2, pad, pad, s);
            case ARROW_LEFT -> drawArrowLeft(g2, pad, pad, s);
            case LIBRARY -> drawLibrary(g2, pad, pad, s);
            case USERS -> drawUsers(g2, pad, pad, s);
            case HISTORY -> drawHistory(g2, pad, pad, s);
            case SEARCH -> drawSearch(g2, pad, pad, s);
            case TRASH -> drawTrash(g2, pad, pad, s);
            case LOGOUT -> drawLogout(g2, pad, pad, s);
            case MAIL -> drawMail(g2, pad, pad, s);
            case PHONE -> drawPhone(g2, pad, pad, s);
            case LINK -> drawLink(g2, pad, pad, s);
            case MOON -> drawMoon(g2, pad, pad, s);
            case SUN -> drawSun(g2, pad, pad, s);
        }
        g2.dispose();
    }

    private void drawHome(Graphics2D g2, int x, int y, int s) {
        Path2D p = new Path2D.Double();
        p.moveTo(x + s/2.0, y);
        p.lineTo(x + s, y + s/2.0);
        p.lineTo(x + s - 2, y + s/2.0);
        p.lineTo(x + s - 2, y + s);
        p.lineTo(x + 2, y + s);
        p.lineTo(x + 2, y + s/2.0);
        p.lineTo(x, y + s/2.0);
        p.closePath();
        g2.draw(p);
    }

    private void drawPlusBook(Graphics2D g2, int x, int y, int s) {
        g2.drawRect(x + 2, y + 2, s - 4, s - 4);
        g2.drawLine(x + s/2, y + 6, x + s/2, y + s - 6);
        g2.drawLine(x + 6, y + s/2, x + s - 6, y + s/2);
    }

    private void drawPerson(Graphics2D g2, int x, int y, int s) {
        g2.drawOval(x + s/4, y, s/2, s/2);
        Path2D p = new Path2D.Double();
        p.moveTo(x, y + s);
        p.curveTo(x, y + s/2.0 + 2, x + s, y + s/2.0 + 2, x + s, y + s);
        g2.draw(p);
    }

    private void drawUsers(Graphics2D g2, int x, int y, int s) {
        drawPerson(g2, x - 2, y + 2, s - 4);
        g2.drawOval(x + s/2, y, (s-4)/2, (s-4)/2);
    }

    private void drawArrowRight(Graphics2D g2, int x, int y, int s) {
        g2.drawLine(x, y + s/2, x + s, y + s/2);
        g2.drawLine(x + s/2, y, x + s, y + s/2);
        g2.drawLine(x + s/2, y + s, x + s, y + s/2);
    }

    private void drawArrowLeft(Graphics2D g2, int x, int y, int s) {
        g2.drawLine(x, y + s/2, x + s, y + s/2);
        g2.drawLine(x + s/2, y, x, y + s/2);
        g2.drawLine(x + s/2, y + s, x, y + s/2);
    }

    private void drawLibrary(Graphics2D g2, int x, int y, int s) {
        g2.drawRect(x, y + 2, s/3, s - 2);
        g2.drawRect(x + s/3 + 2, y, s/3, s);
        g2.drawRect(x + 2*s/3 + 4, y + 2, s/3 - 4, s - 2);
    }

    private void drawHistory(Graphics2D g2, int x, int y, int s) {
        g2.drawOval(x, y, s, s);
        g2.drawLine(x + s/2, y + 2, x + s/2, y + s/2);
        g2.drawLine(x + s/2, y + s/2, x + s - 4, y + s/2 + 2);
    }

    private void drawSearch(Graphics2D g2, int x, int y, int s) {
        g2.drawOval(x, y, s - 4, s - 4);
        g2.drawLine(x + s - 5, y + s - 5, x + s, y + s);
    }

    private void drawTrash(Graphics2D g2, int x, int y, int s) {
        g2.drawLine(x, y + 4, x + s, y + 4);
        g2.drawRect(x + 2, y + 4, s - 4, s - 4);
        g2.drawLine(x + 4, y + 6, x + 4, y + s - 2);
        g2.drawLine(x + s - 4, y + 6, x + s - 4, y + s - 2);
        g2.drawRect(x + s/2 - 2, y, 4, 4);
    }

    private void drawLogout(Graphics2D g2, int x, int y, int s) {
        g2.drawRect(x, y, s - 4, s);
        drawArrowRight(g2, x + 4, y + 2, s - 4);
    }

    private void drawMail(Graphics2D g2, int x, int y, int s) {
        g2.drawRoundRect(x, y + 3, s, s - 6, 3, 3);
        g2.drawLine(x + 1, y + 4, x + s / 2, y + s / 2 + 1);
        g2.drawLine(x + s - 1, y + 4, x + s / 2, y + s / 2 + 1);
    }

    private void drawPhone(Graphics2D g2, int x, int y, int s) {
        Path2D p = new Path2D.Double();
        p.moveTo(x + 4, y + 2);
        p.curveTo(x + 1, y + 5, x + 3, y + 12, x + 8, y + s - 7);
        p.curveTo(x + 13, y + s - 2, x + s - 5, y + s - 1, x + s - 2, y + s - 5);
        p.lineTo(x + s - 6, y + s - 9);
        p.lineTo(x + s - 10, y + s - 6);
        p.lineTo(x + 8, y + 10);
        p.lineTo(x + 11, y + 6);
        p.closePath();
        g2.draw(p);
    }

    private void drawLink(Graphics2D g2, int x, int y, int s) {
        g2.drawRoundRect(x, y + s / 3, s / 2 + 1, s / 3, 6, 6);
        g2.drawRoundRect(x + s / 2 - 1, y + s / 3, s / 2 + 1, s / 3, 6, 6);
        g2.drawLine(x + s / 3, y + s / 2, x + 2 * s / 3, y + s / 2);
    }

    private void drawMoon(Graphics2D g2, int x, int y, int s) {
        Area moon = new Area(new Ellipse2D.Double(x + 1, y + 1, s - 2, s - 2));
        moon.subtract(new Area(new Ellipse2D.Double(x + s / 3.0, y - 1, s - 2, s - 2)));
        g2.fill(moon);
    }

    private void drawSun(Graphics2D g2, int x, int y, int s) {
        g2.drawOval(x + s / 4, y + s / 4, s / 2, s / 2);
        for (int i = 0; i < 8; i++) {
            double a = i * Math.PI / 4.0;
            int x1 = x + s / 2 + (int) (Math.cos(a) * s * 0.38);
            int y1 = y + s / 2 + (int) (Math.sin(a) * s * 0.38);
            int x2 = x + s / 2 + (int) (Math.cos(a) * s * 0.50);
            int y2 = y + s / 2 + (int) (Math.sin(a) * s * 0.50);
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    @Override
    public int getIconWidth() { return size; }

    @Override
    public int getIconHeight() { return size; }
}
