package com.restaurant.utils;

import javax.swing.*;
import java.awt.*;

public class DesignSystem {

    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color SECONDARY = new Color(71, 85, 105);
    public static final Color BACKGROUND = new Color(248, 250, 252);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color CSV_GREEN = new Color(33, 115, 70);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color CHART_PLOT = new Color(245, 245, 250);

    public static final Font FONT_HUGE = new Font("SansSerif", Font.BOLD, 32);
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BADGE = new Font("SansSerif", Font.BOLD, 11);

    public static final int BORDER_RADIUS = 1;
    public static final Dimension SIDEBAR_BTN_SIZE = new Dimension(230, 45);
    public static final Dimension SIDEBAR_WIDTH_DIM = new Dimension(250, Integer.MAX_VALUE);

    public static void styleButton(JButton btn) {
        styleButton(btn, PRIMARY);
    }

    public static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), BORDER_RADIUS, true),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
    }

    public static void styleTextField(JTextField tf) {
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    public static void styleLabel(JLabel lbl, boolean isTitle) {
        lbl.setFont(isTitle ? FONT_TITLE : FONT_BODY);
        lbl.setForeground(isTitle ? TEXT : SECONDARY);
    }

    public static JLabel createBadge(String text) {
        JLabel lbl = new JLabel(" " + text + " ");
        lbl.setFont(FONT_BADGE);
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(DANGER);
        lbl.setOpaque(true);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        return card;
    }
}
