import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;

/**
 * The front / welcome page of the game — a dark, poster-style cover screen.
 * Collects both player names, who plays White, and an optional
 * time control, then launches the ChessGame board.
 */
public class StartScreen extends JFrame {

    JTextField player1Field;
    JTextField player2Field;
    JRadioButton player1White;
    JRadioButton player1Black;
    JComboBox<String> timeControlBox;

    StartScreen() {
        buildWindow();
        buildContent();
    }

    void buildWindow() {
        setTitle("Java Chess — Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppTheme.DARK_BG);
    }

    void buildContent() {
        JPanel poster = new JPanel(new BorderLayout());
        poster.setBackground(AppTheme.DARK_BG);
        poster.setBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_BLUE_DIM, 3));

        poster.add(buildBanner(), BorderLayout.NORTH);
        poster.add(buildForm(), BorderLayout.CENTER);
        poster.add(buildStartButtonPanel(), BorderLayout.SOUTH);

        JPanel margin = new JPanel(new BorderLayout());
        margin.setBackground(AppTheme.DARK_BG);
        margin.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        margin.add(poster, BorderLayout.CENTER);

        // Center the fixed-size poster card as a floating panel on the full-screen dark backdrop
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setBackground(AppTheme.DARK_BG);
        centerWrap.add(margin);

        add(centerWrap, BorderLayout.CENTER);
    }

    JPanel buildBanner() {
        ChessBanner banner = new ChessBanner();
        banner.setPreferredSize(new Dimension(720, 180));
        banner.setLayout(new GridBagLayout());

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new javax.swing.BoxLayout(textStack, javax.swing.BoxLayout.Y_AXIS));

        JLabel crown = new JLabel("\u265A", SwingConstants.CENTER);
        crown.setFont(AppTheme.pieceFont(30));
        crown.setForeground(AppTheme.GOLD_ACCENT);
        crown.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel eyebrow = new JLabel("TWO PLAYER MODE");
        eyebrow.setFont(AppTheme.bodyBoldFont(13));
        eyebrow.setForeground(AppTheme.TEXT_MUTED);
        eyebrow.setAlignmentX(Component.CENTER_ALIGNMENT);

        ShimmerLabel title = new ShimmerLabel("JAVA CHESS", AppTheme.decorativeFont(48));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("PRESS START TO BEGIN");
        subtitle.setFont(AppTheme.posterFont(15));
        subtitle.setForeground(AppTheme.TEXT_LIGHT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        textStack.add(crown);
        textStack.add(javax.swing.Box.createVerticalStrut(4));
        textStack.add(eyebrow);
        textStack.add(javax.swing.Box.createVerticalStrut(4));
        textStack.add(title);
        textStack.add(javax.swing.Box.createVerticalStrut(6));
        textStack.add(subtitle);

        banner.add(textStack);
        return banner;
    }

    /** A title label that paints its text with an animated amber-to-blue shimmer,
     *  echoing the shimmering name effect used on the site's HTML pages. */
    static class ShimmerLabel extends JLabel {
        private float phase = 0f;
        private final Timer timer;

        ShimmerLabel(String text, Font font) {
            super(text);
            setFont(font);
            setForeground(AppTheme.ACCENT_BLUE);
            timer = new Timer(45, e -> {
                phase += 0.015f;
                if (phase > 1f) phase -= 1f;
                repaint();
            });
        }

        @Override
        public void addNotify() {
            super.addNotify();
            timer.start();
        }

        @Override
        public void removeNotify() {
            timer.stop();
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            java.awt.FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int textWidth = fm.stringWidth(text);
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            float offset = phase * textWidth * 2f - textWidth * 0.5f;
            GradientPaint gp = new GradientPaint(
                    x + offset, 0, AppTheme.GOLD_ACCENT,
                    x + offset + textWidth * 0.6f, 0, AppTheme.ROYAL_BLUE);
            g2.setPaint(gp);
            g2.drawString(text, x, y);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            Font f = getFont();
            java.awt.FontMetrics fm = getFontMetrics(f);
            return new Dimension(fm.stringWidth(getText()) + 10, fm.getHeight() + 6);
        }
    }

    /** Paints a dark backdrop with large, faded chess-glyph silhouettes for a poster feel. */
    static class ChessBanner extends JPanel {
        ChessBanner() {
            setBackground(AppTheme.NAVY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppTheme.ROYAL_BLUE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            g2.setFont(AppTheme.pieceFont(150));
            g2.drawString("\u265A", -25, getHeight() + 15);
            g2.setFont(AppTheme.pieceFont(110));
            g2.drawString("\u265B", getWidth() - 120, 95);
            g2.dispose();
        }
    }

    JPanel buildForm() {
        JPanel outer = new JPanel(new BorderLayout(24, 0));
        outer.setBackground(AppTheme.DARK_BG);
        outer.setBorder(BorderFactory.createEmptyBorder(22, 36, 14, 36));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.DARK_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = AppTheme.bodyBoldFont(14);
        Font fieldFont = AppTheme.bodyFont(14);

        // Player 1 name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(darkLabel("Player 1 name:", labelFont), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        player1Field = new JTextField("Player 1");
        styleField(player1Field, fieldFont);
        form.add(player1Field, gbc);

        // Player 2 name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(darkLabel("Player 2 name:", labelFont), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        player2Field = new JTextField("Player 2");
        styleField(player2Field, fieldFont);
        form.add(player2Field, gbc);

        // Who plays White
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(darkLabel("Player 1 plays:", labelFont), gbc);

        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(AppTheme.DARK_BG);
        player1White = new JRadioButton("<html>White <font face='Serif'>\u2654</font></html>", true);
        player1Black = new JRadioButton("<html>Black <font face='Serif'>\u265A</font></html>");
        for (JRadioButton rb : new JRadioButton[]{player1White, player1Black}) {
            rb.setBackground(AppTheme.DARK_BG);
            rb.setForeground(AppTheme.TEXT_LIGHT);
            rb.setFont(fieldFont);
        }
        ButtonGroup group = new ButtonGroup();
        group.add(player1White);
        group.add(player1Black);
        sidePanel.add(player1White);
        sidePanel.add(player1Black);

        gbc.gridx = 1; gbc.weightx = 1;
        form.add(sidePanel, gbc);

        // Time control
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(darkLabel("Time control:", labelFont), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        timeControlBox = new JComboBox<>(new String[]{
                "No limit (stopwatch only)", "5 minutes", "10 minutes", "15 minutes", "30 minutes"
        });
        timeControlBox.setFont(fieldFont);
        timeControlBox.setBackground(AppTheme.DARK_PANEL);
        timeControlBox.setForeground(AppTheme.TEXT_LIGHT);
        form.add(timeControlBox, gbc);

        outer.add(form, BorderLayout.CENTER);
        outer.add(buildHowToPlay(), BorderLayout.EAST);
        return outer;
    }

    JLabel darkLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(AppTheme.TEXT_LIGHT);
        return label;
    }

    void styleField(JTextField field, Font font) {
        field.setFont(font);
        field.setBackground(AppTheme.DARK_PANEL);
        field.setForeground(AppTheme.TEXT_LIGHT);
        field.setCaretColor(AppTheme.TEXT_LIGHT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.ROYAL_BLUE, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
    }

    JPanel buildHowToPlay() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.DARK_PANEL);
        panel.setPreferredSize(new Dimension(250, 230));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.ROYAL_BLUE, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel header = new JLabel("How to Play");
        header.setFont(AppTheme.headingFont(15));
        header.setForeground(AppTheme.ACCENT_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        panel.add(header, BorderLayout.NORTH);

        JTextArea info = new JTextArea(
                "\u2022 White always moves first. Click a piece, then click a highlighted square to move it.\n\n" +
                "\u2022 Legal moves for the selected piece are highlighted on the board.\n\n" +
                "\u2022 Capture the opponent's King to win the game instantly.\n\n" +
                "\u2022 Pawns automatically promote to a Queen when they reach the far side.\n\n" +
                "\u2022 Each player has their own clock, shown at the side of the board."
        );
        info.setEditable(false);
        info.setFocusable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(AppTheme.bodyFont(12));
        info.setForeground(AppTheme.TEXT_LIGHT);
        info.setBackground(AppTheme.DARK_PANEL);
        info.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JScrollPane infoScroll = new JScrollPane(info,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        infoScroll.setBorder(BorderFactory.createEmptyBorder());
        infoScroll.getViewport().setBackground(AppTheme.DARK_PANEL);
        infoScroll.getVerticalScrollBar().setUnitIncrement(12);

        panel.add(infoScroll, BorderLayout.CENTER);
        return panel;
    }

    JPanel buildStartButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AppTheme.DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 22, 10));

        JButton startButton = new JButton("START");
        startButton.setIcon(new TriangleIcon(AppTheme.DARK_PANEL_LO, 12, 14));
        startButton.setIconTextGap(10);
        startButton.setFont(AppTheme.posterFont(20));
        startButton.setBackground(AppTheme.ACCENT_BLUE);
        startButton.setForeground(AppTheme.DARK_PANEL_LO);
        startButton.setFocusPainted(false);
        Border outer = BorderFactory.createLineBorder(AppTheme.TEXT_LIGHT, 2);
        Border inner = BorderFactory.createEmptyBorder(8, 26, 8, 26);
        startButton.setBorder(BorderFactory.createCompoundBorder(outer, inner));
        startButton.addActionListener(e -> startGame());

        panel.add(startButton);
        return panel;
    }

    /** A small filled triangle "play" icon, drawn directly rather than relying
     *  on a unicode glyph that may not exist in every installed font. */
    static class TriangleIcon implements Icon {
        private final Color color;
        private final int w, h;

        TriangleIcon(Color color, int w, int h) {
            this.color = color;
            this.w = w;
            this.h = h;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            Polygon tri = new Polygon();
            tri.addPoint(x, y);
            tri.addPoint(x, y + h);
            tri.addPoint(x + w, y + h / 2);
            g2.fillPolygon(tri);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return w; }

        @Override
        public int getIconHeight() { return h; }
    }

    void startGame() {
        String name1 = player1Field.getText().trim();
        String name2 = player2Field.getText().trim();
        if (name1.isEmpty()) name1 = "Player 1";
        if (name2.isEmpty()) name2 = "Player 2";

        String whiteName = player1White.isSelected() ? name1 : name2;
        String blackName = player1White.isSelected() ? name2 : name1;

        int minutes = parseMinutes((String) timeControlBox.getSelectedItem());

        ChessGame game = new ChessGame(whiteName, blackName, minutes);
        game.pack();
        fitToScreen(game);
        game.setLocationRelativeTo(null);
        game.setVisible(true);

        this.dispose();
    }

    /** Sizes a frame to its natural preferred size, but never larger than the
     *  available screen — so content is always fully visible instead of being
     *  silently clipped when a forced maximize leaves less room than the
     *  window actually needs. */
    static void fitToScreen(JFrame frame) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension pref = frame.getSize();
        int width = Math.min(pref.width, screen.width - 60);
        int height = Math.min(pref.height, screen.height - 100);
        frame.setSize(width, height);
    }

    int parseMinutes(String label) {
        if (label == null || label.startsWith("No limit")) {
            return 0;
        }
        return Integer.parseInt(label.split(" ")[0]);
    }

    public static void main(String[] args) {
        StartScreen screen = new StartScreen();
        screen.pack();
        fitToScreen(screen);
        screen.setLocationRelativeTo(null);
        screen.setVisible(true);
    }
}
