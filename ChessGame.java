import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;

public class ChessGame extends JFrame {
    static final int SIZE = 8;
    static final int TILE = 68;

    // Thin, always-visible boundary drawn around every square so the grid
    // never disappears, even when a square's background changes for a
    // selection or move-hint highlight.
    static final Border SQUARE_BORDER = BorderFactory.createLineBorder(AppTheme.BOARD_GRID_LINE, 1);

    // Traditional green/cream board colors
    static final Color LIGHT = AppTheme.BOARD_LIGHT;
    static final Color DARK = AppTheme.BOARD_DARK;
    static final Color SELECT = AppTheme.GOLD_ACCENT;
    static final Color MOVE_HINT = AppTheme.MOVE_HINT;
    static final Color CAPTURE_HINT = AppTheme.CAPTURE_HINT;

    Board board = new Board();
    JLabel[][] squares = new JLabel[8][8];
    int selRow = -1;
    int selCol = -1;
    String turn = "White";
    JLabel statusBar;
    boolean gameOver = false;

    Player whitePlayer;
    Player blackPlayer;

    // Legal move highlighting
    List<int[]> legalHints = new ArrayList<>();

    // Clocks
    boolean timedGame;
    int initialSeconds;
    int whiteSeconds;
    int blackSeconds;
    Timer clockTimer;
    JLabel whiteClockLabel;
    JLabel blackClockLabel;
    JPanel whiteCard;
    JPanel blackCard;

    // Move history
    JTextArea historyArea;
    int moveCount = 0;

    // Captured piece trays
    JPanel whiteCapturedTray; // pieces White has captured (black pieces)
    JPanel blackCapturedTray; // pieces Black has captured (white pieces)

    // Floating "Game Over" overlay
    JPanel overlayPanel;
    JLabel overlayTitle;
    JLabel overlaySubtitle;

    ChessGame(String whiteName, String blackName, int minutesPerPlayer) {
        this.whitePlayer = new Player(whiteName, "White");
        this.blackPlayer = new Player(blackName, "Black");
        this.timedGame = minutesPerPlayer > 0;
        this.initialSeconds = minutesPerPlayer * 60;
        this.whiteSeconds = this.initialSeconds;
        this.blackSeconds = this.initialSeconds;

        this.buildWindow();
        this.buildTopBar();
        this.buildBoardArea();
        this.buildSidePanels();
        this.buildStatus();
        this.buildGameOverOverlay();
        this.drawBoard();
        this.startClock();
    }

    void buildWindow() {
        this.setTitle("Chess Game — " + whitePlayer.getName() + " vs " + blackPlayer.getName());
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout());
        this.setResizable(true);
        this.getContentPane().setBackground(AppTheme.DARK_BG);
    }

    void buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppTheme.NAVY);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("<html><font face='Serif'>\u265A</font> CHESS <font face='Serif'>\u265B</font></html>");
        title.setFont(AppTheme.decorativeFont(24));
        title.setForeground(AppTheme.GOLD_ACCENT);
        bar.add(title, BorderLayout.WEST);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(AppTheme.bodyBoldFont(13));
        newGameButton.setBackground(AppTheme.ACCENT_BLUE);
        newGameButton.setForeground(AppTheme.DARK_PANEL_LO);
        newGameButton.setFocusPainted(false);
        newGameButton.addActionListener(e -> confirmRestart());
        bar.add(newGameButton, BorderLayout.EAST);

        this.add(bar, BorderLayout.NORTH);
    }

    void buildBoardArea() {
        JPanel grid = new JPanel(new GridLayout(8, 8));
        grid.setPreferredSize(new Dimension(TILE * 8, TILE * 8));

        for(int row = 0; row < 8; ++row) {
            for(int col = 0; col < 8; ++col) {
                final int r = row;
                final int c = col;
                JLabel sq = new JLabel();
                sq.setPreferredSize(new Dimension(TILE, TILE));
                sq.setHorizontalAlignment(0);
                sq.setFont(AppTheme.pieceFont(38));
                sq.setOpaque(true);
                sq.setBorder(SQUARE_BORDER);
                if ((row + col) % 2 == 0) {
                    sq.setBackground(LIGHT);
                } else {
                    sq.setBackground(DARK);
                }

                sq.addMouseListener(new MouseAdapter() {
                    {
                        Objects.requireNonNull(ChessGame.this);
                    }

                    public void mouseClicked(MouseEvent e) {
                        ChessGame.this.handleClick(r, c);
                    }
                });
                this.squares[row][col] = sq;
                grid.add(sq);
            }
        }

        // Wrap the grid with rank (1-8) and file (a-h) labels for a traditional look
        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBackground(AppTheme.BOARD_FRAME);
        boardWrapper.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JPanel ranks = new JPanel(new GridLayout(8, 1));
        ranks.setBackground(AppTheme.BOARD_FRAME);
        for (int row = 0; row < 8; row++) {
            JLabel rankLabel = new JLabel(String.valueOf(8 - row), SwingConstants.CENTER);
            rankLabel.setFont(AppTheme.bodyBoldFont(13));
            rankLabel.setForeground(Color.WHITE);
            rankLabel.setPreferredSize(new Dimension(18, TILE));
            ranks.add(rankLabel);
        }

        JPanel files = new JPanel(new GridLayout(1, 8));
        files.setBackground(AppTheme.BOARD_FRAME);
        String[] fileLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        for (String letter : fileLetters) {
            JLabel fileLabel = new JLabel(letter, SwingConstants.CENTER);
            fileLabel.setFont(AppTheme.bodyBoldFont(13));
            fileLabel.setForeground(Color.WHITE);
            fileLabel.setPreferredSize(new Dimension(TILE, 18));
            files.add(fileLabel);
        }
        JPanel filesRow = new JPanel(new BorderLayout());
        filesRow.setBackground(AppTheme.BOARD_FRAME);
        filesRow.add(files, BorderLayout.CENTER);
        filesRow.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));

        boardWrapper.add(ranks, BorderLayout.WEST);
        boardWrapper.add(grid, BorderLayout.CENTER);
        boardWrapper.add(filesRow, BorderLayout.SOUTH);

        JPanel centerHolder = new JPanel();
        centerHolder.setBackground(AppTheme.DARK_BG);
        centerHolder.add(boardWrapper);

        // Wrapped in a scroll pane as a safety net: if a user's screen is too
        // small to show the whole board at once, they can scroll to see the
        // rest of it instead of the bottom rows being silently clipped off.
        JScrollPane boardScroll = new JScrollPane(centerHolder);
        boardScroll.setBorder(BorderFactory.createEmptyBorder());
        boardScroll.getViewport().setBackground(AppTheme.DARK_BG);
        boardScroll.getVerticalScrollBar().setUnitIncrement(16);

        this.add(boardScroll, BorderLayout.CENTER);
    }

    void buildSidePanels() {
        // WEST: player cards (name + clock + captured tray)
        JPanel west = new JPanel();
        west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
        west.setBackground(AppTheme.DARK_BG);
        west.setPreferredSize(new Dimension(200, TILE * 8));
        west.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        whiteCard = buildPlayerCard(whitePlayer, true);
        blackCard = buildPlayerCard(blackPlayer, false);

        west.add(whiteCard);
        west.add(javax.swing.Box.createVerticalStrut(30));
        west.add(blackCard);
        west.add(javax.swing.Box.createVerticalGlue());

        this.add(west, BorderLayout.WEST);

        // EAST: move history
        JPanel east = new JPanel(new BorderLayout());
        east.setPreferredSize(new Dimension(210, TILE * 8));
        east.setBackground(AppTheme.DARK_BG);
        east.setBorder(BorderFactory.createEmptyBorder(20, 8, 20, 12));

        JLabel historyTitle = new JLabel("Move History", SwingConstants.CENTER);
        historyTitle.setFont(AppTheme.headingFont(15));
        historyTitle.setForeground(AppTheme.ACCENT_BLUE);
        east.add(historyTitle, BorderLayout.NORTH);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        historyArea.setBackground(AppTheme.DARK_PANEL);
        historyArea.setForeground(AppTheme.TEXT_LIGHT);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.ROYAL_BLUE, 1));
        east.add(scrollPane, BorderLayout.CENTER);

        this.add(east, BorderLayout.EAST);
    }

    JPanel buildPlayerCard(Player player, boolean isWhite) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(isWhite ? AppTheme.DARK_PANEL_HI : AppTheme.DARK_PANEL_LO);
        Border line = BorderFactory.createLineBorder(AppTheme.ROYAL_BLUE, 2);
        card.setBorder(BorderFactory.createCompoundBorder(line, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setMaximumSize(new Dimension(180, 140));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("<html><font face='Serif'>" + (isWhite ? "\u2654" : "\u265A")
                + "</font> " + player.getName() + "</html>");
        nameLabel.setFont(AppTheme.headingFont(15));
        nameLabel.setForeground(AppTheme.TEXT_LIGHT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);

        JLabel clockLabel = new JLabel(formatTime(initialSeconds));
        clockLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        clockLabel.setForeground(AppTheme.TEXT_LIGHT);
        clockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(clockLabel);

        JPanel tray = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        tray.setBackground(card.getBackground());
        tray.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isWhite) {
            whiteClockLabel = clockLabel;
            whiteCapturedTray = tray;
        } else {
            blackClockLabel = clockLabel;
            blackCapturedTray = tray;
        }

        card.add(tray);
        return card;
    }

    void buildStatus() {
        this.statusBar = new JLabel(currentPlayer().getName() + " (WHITE) — click a piece to select it", 0);
        this.statusBar.setFont(AppTheme.bodyBoldFont(15));
        this.statusBar.setForeground(AppTheme.TEXT_LIGHT);
        this.statusBar.setBackground(AppTheme.DARK_PANEL);
        this.statusBar.setOpaque(true);
        this.statusBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(this.statusBar, "South");
    }

    /** Builds a floating, semi-transparent "Game Over" card shown via the frame's glass pane. */
    void buildGameOverOverlay() {
        JPanel backdrop = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 165));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        backdrop.setOpaque(false);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.DARK_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
                g2.setColor(AppTheme.ACCENT_BLUE);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 36, 36);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(36, 55, 32, 55));

        overlayTitle = new JLabel("GAME OVER");
        overlayTitle.setFont(AppTheme.posterFont(34));
        overlayTitle.setForeground(AppTheme.ACCENT_BLUE);
        overlayTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        overlaySubtitle = new JLabel("Play again?");
        overlaySubtitle.setFont(AppTheme.bodyBoldFont(15));
        overlaySubtitle.setForeground(AppTheme.TEXT_LIGHT);
        overlaySubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonRow.setOpaque(false);
        JButton yesButton = pillButton("Yes", AppTheme.ACCENT_BLUE, AppTheme.DARK_PANEL_LO);
        JButton noButton = pillButton("No", AppTheme.DARK_PANEL_HI, AppTheme.TEXT_LIGHT);
        yesButton.addActionListener(e -> {
            hideGameOverOverlay();
            restartGame();
        });
        noButton.addActionListener(e -> hideGameOverOverlay());
        buttonRow.add(yesButton);
        buttonRow.add(noButton);

        card.add(overlayTitle);
        card.add(javax.swing.Box.createVerticalStrut(12));
        card.add(overlaySubtitle);
        card.add(javax.swing.Box.createVerticalStrut(24));
        card.add(buttonRow);

        backdrop.add(card);

        this.overlayPanel = backdrop;
        this.setGlassPane(backdrop);
    }

    JButton pillButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 44, 44);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(fg);
        button.setFont(AppTheme.bodyBoldFont(15));
        button.setBorder(BorderFactory.createEmptyBorder(10, 34, 10, 34));
        return button;
    }

    void showGameOverOverlay(String title, String subtitle) {
        overlayTitle.setText(title);
        overlaySubtitle.setText(subtitle);
        overlayPanel.setVisible(true);
    }

    void hideGameOverOverlay() {
        overlayPanel.setVisible(false);
    }

    Player currentPlayer() {
        return turn.equals("White") ? whitePlayer : blackPlayer;
    }

    void startClock() {
        clockTimer = new Timer(1000, e -> tickClock());
        clockTimer.start();
        updateClockLabels();
    }

    void tickClock() {
        if (gameOver) {
            return;
        }
        if (timedGame) {
            if (turn.equals("White")) {
                whiteSeconds--;
            } else {
                blackSeconds--;
            }
            if (whiteSeconds <= 0) {
                whiteSeconds = 0;
                updateClockLabels();
                timeUp("White");
                return;
            }
            if (blackSeconds <= 0) {
                blackSeconds = 0;
                updateClockLabels();
                timeUp("Black");
                return;
            }
        } else {
            if (turn.equals("White")) {
                whiteSeconds++;
            } else {
                blackSeconds++;
            }
        }
        updateClockLabels();
    }

    void updateClockLabels() {
        whiteClockLabel.setText(formatTime(whiteSeconds));
        blackClockLabel.setText(formatTime(blackSeconds));
        whiteCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(turn.equals("White") ? AppTheme.GOLD_ACCENT : AppTheme.ROYAL_BLUE, turn.equals("White") ? 4 : 2),
                BorderFactory.createEmptyBorder(9, 9, 9, 9)));
        blackCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(turn.equals("Black") ? AppTheme.GOLD_ACCENT : AppTheme.ROYAL_BLUE, turn.equals("Black") ? 4 : 2),
                BorderFactory.createEmptyBorder(9, 9, 9, 9)));
    }

    String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    void timeUp(String colorOutOfTime) {
        gameOver = true;
        clockTimer.stop();
        SoundUtil.playTimeUp();
        String winner = colorOutOfTime.equals("White") ? blackPlayer.getName() : whitePlayer.getName();
        showGameOverOverlay("TIME'S UP", colorOutOfTime + "'s clock ran out — " + winner + " wins! Play again?");
    }

    void confirmRestart() {
        int choice = JOptionPane.showConfirmDialog(this, "Start a new game with the same players?",
                "New Game", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        }
    }

    String squareName(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    void logMove(String movingPieceName, int fromRow, int fromCol, int toRow, int toCol, String extraNote) {
        moveCount++;
        String entry = moveCount + ". " + currentPlayer().getName() + " (" + turn + "): "
                + movingPieceName + " " + squareName(fromRow, fromCol) + " \u2192 " + squareName(toRow, toCol)
                + extraNote;
        historyArea.append(entry + "\n");
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    void addCapturedPiece(Piece captured) {
        JLabel symbol = new JLabel(captured.getSymbol());
        symbol.setFont(AppTheme.pieceFont(22));
        symbol.setForeground(captured.isWhite() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        if (turn.equals("White")) {
            whiteCapturedTray.add(symbol);
            whiteCapturedTray.revalidate();
            whiteCapturedTray.repaint();
        } else {
            blackCapturedTray.add(symbol);
            blackCapturedTray.revalidate();
            blackCapturedTray.repaint();
        }
    }

    void computeLegalHints(Piece piece) {
        legalHints.clear();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (piece.isValidMove(r, c, board.grid)) {
                    legalHints.add(new int[]{r, c});
                }
            }
        }
    }

    void drawBoard() {
        for(int row = 0; row < 8; ++row) {
            for(int col = 0; col < 8; ++col) {
                JLabel sq = this.squares[row][col];
                sq.setBackground((row + col) % 2 == 0 ? LIGHT : DARK);
                Piece p = this.board.get(row, col);
                if (p != null) {
                    sq.setText(p.getSymbol());
                    sq.setForeground(p.color.equals("White") ? Color.WHITE : Color.BLACK);
                } else {
                    sq.setText("");
                }
            }
        }

        for (int[] hint : legalHints) {
            boolean isCapture = this.board.get(hint[0], hint[1]) != null;
            this.squares[hint[0]][hint[1]].setBackground(isCapture ? CAPTURE_HINT : MOVE_HINT);
        }

        if (this.selRow != -1) {
            this.squares[this.selRow][this.selCol].setBackground(SELECT);
        }

    }

    void handleClick(int row, int col) {
        if (gameOver) {
            return;
        }
        Piece clicked = this.board.get(row, col);
        if (this.selRow == -1) {
            if (clicked != null && clicked.color.equals(this.turn)) {
                this.selRow = row;
                this.selCol = col;
                computeLegalHints(clicked);
                this.statusBar.setText(currentPlayer().getName() + " — " + clicked.name + " selected. Click a highlighted square to move.");
                this.drawBoard();
            } else {
                this.statusBar.setText(currentPlayer().getName() + "'s turn — please click one of YOUR pieces!");
            }
        } else {
            if (row == this.selRow && col == this.selCol) {
                this.selRow = -1;
                this.selCol = -1;
                this.legalHints.clear();
                this.statusBar.setText(currentPlayer().getName() + "'s turn — selection cancelled");
                this.drawBoard();
                return;
            }

            if (clicked != null && clicked.color.equals(this.turn)) {
                this.selRow = row;
                this.selCol = col;
                computeLegalHints(clicked);
                this.statusBar.setText(currentPlayer().getName() + " — switched to " + clicked.name);
                this.drawBoard();
                return;
            }

            Piece moving = this.board.get(this.selRow, this.selCol);
            String extra = "";
            if (!moving.isValidMove(row, col, this.board.grid)) {
                this.statusBar.setText(currentPlayer().getName() + "'s turn — Invalid move! Try again.");
                return;
            }

            int fromRow = this.selRow;
            int fromCol = this.selCol;

            if (clicked != null) {
                extra = " — Captured enemy " + clicked.name + "!";
                addCapturedPiece(clicked);
                SoundUtil.playCapture();
                if (clicked.name.equals("King")) {
                    this.board.move(this.selRow, this.selCol, row, col);
                    this.selRow = -1;
                    this.selCol = -1;
                    this.legalHints.clear();
                    logMove(moving.name, fromRow, fromCol, row, col, extra);
                    this.drawBoard();
                    gameOver = true;
                    clockTimer.stop();
                    SoundUtil.playGameOver();
                    showGameOverOverlay("GAME OVER", currentPlayer().getName() + " wins by capturing the King! Play again?");
                    return;
                }
            } else {
                SoundUtil.playMove();
            }

            this.board.move(this.selRow, this.selCol, row, col);
            Piece moved = this.board.get(row, col);
            if (moved instanceof Pawn) {
                if ((moved.isWhite() && row == 0) || (!moved.isWhite() && row == 7)) {
                    this.board.grid[row][col] = new Queen(row, col, moved.color);
                    extra += " — Pawn promoted to Queen!";
                }
            }

            logMove(moving.name, fromRow, fromCol, row, col, extra);

            this.turn = this.turn.equals("White") ? "Black" : "White";
            this.selRow = -1;
            this.selCol = -1;
            this.legalHints.clear();
            this.statusBar.setText(currentPlayer().getName() + "'s turn" + extra);
            this.drawBoard();
        }

    }

    void restartGame() {
        this.overlayPanel.setVisible(false);
        this.board = new Board();
        this.turn = "White";
        this.selRow = -1;
        this.selCol = -1;
        this.legalHints.clear();
        this.gameOver = false;
        this.whiteSeconds = this.initialSeconds;
        this.blackSeconds = this.initialSeconds;
        this.historyArea.setText("");
        this.moveCount = 0;
        this.whiteCapturedTray.removeAll();
        this.blackCapturedTray.removeAll();
        this.whiteCapturedTray.revalidate();
        this.whiteCapturedTray.repaint();
        this.blackCapturedTray.revalidate();
        this.blackCapturedTray.repaint();
        this.statusBar.setText("NEW GAME — " + currentPlayer().getName() + " (WHITE) — click a piece");
        this.updateClockLabels();
        if (!clockTimer.isRunning()) {
            clockTimer.start();
        }
        this.drawBoard();
    }

    public static void main(String[] args) {
        StartScreen.main(args);
    }
}
