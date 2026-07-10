import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Central place for the app's color palette and font choices, so the
 * front page and the game board always look consistent.
 */
class AppTheme {

    // ---- Professional navy-blue UI palette ----
    static final Color DARK_BG        = new Color(10, 20, 34);    // deep navy backdrop
    static final Color DARK_PANEL     = new Color(18, 34, 54);    // panels, cards
    static final Color DARK_PANEL_LO  = new Color(6, 13, 22);     // deepest panel shade (black side)
    static final Color DARK_PANEL_HI  = new Color(28, 52, 82);    // lighter panel shade (white side)
    static final Color NAVY           = new Color(15, 38, 64);    // chrome / top bar
    static final Color ROYAL_BLUE     = new Color(59, 108, 156);  // borders, secondary buttons (steel blue)
    static final Color ACCENT_BLUE    = new Color(66, 165, 245);  // bright accent, titles, glow (sky blue)
    static final Color ACCENT_BLUE_DIM = new Color(45, 120, 180);
    static final Color TEXT_LIGHT     = new Color(236, 244, 251); // crisp near-white
    static final Color TEXT_MUTED     = new Color(142, 170, 199); // muted steel blue
    static final Color GOLD_ACCENT    = new Color(240, 176, 60);  // warm amber, high-contrast highlight

    // ---- Steel-blue chessboard palette ----
    static final Color BOARD_LIGHT = new Color(224, 233, 242); // cool ice-blue/white
    static final Color BOARD_DARK  = new Color(41, 71, 107);   // deep steel blue
    static final Color BOARD_FRAME = new Color(15, 38, 64);    // navy frame
    static final Color BOARD_GRID_LINE = new Color(240, 176, 60); // amber square boundary, always visible

    // Move-hint shades — cool blue for a quiet move, warm amber/orange for danger
    static final Color MOVE_HINT    = new Color(110, 190, 235); // light cyan-blue for a quiet move
    static final Color CAPTURE_HINT = new Color(224, 122, 46);  // warm burnt-orange for a capture

    private static final Set<String> AVAILABLE = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

    /** Elegant serif heading font, e.g. for titles and section headers. */
    static Font headingFont(int size) {
        return pick(new String[]{"Palatino Linotype", "Book Antiqua", "Georgia", "Garamond"}, Font.BOLD, size);
    }

    /** A decorative, script-like face for the main banner title, with graceful fallback. */
    static Font decorativeFont(int size) {
        return pick(new String[]{"Segoe Script", "Lucida Handwriting", "Brush Script MT", "Papyrus"}, Font.PLAIN, size);
    }

    /** Bold blocky face for the poster-style start button, retro-game inspired. */
    static Font posterFont(int size) {
        return pick(new String[]{"Segoe UI Black", "Arial Black", "Impact", "Verdana"}, Font.BOLD, size);
    }

    /** Clean body font for labels and buttons. */
    static Font bodyFont(int size) {
        return pick(new String[]{"Trebuchet MS", "Verdana", "SansSerif"}, Font.PLAIN, size);
    }

    static Font bodyBoldFont(int size) {
        return pick(new String[]{"Trebuchet MS", "Verdana", "SansSerif"}, Font.BOLD, size);
    }

    /** Reliable font for rendering chess glyphs — must stay a face that supports them. */
    static Font pieceFont(int size) {
        return new Font("Serif", Font.PLAIN, size);
    }

    private static Font pick(String[] preferred, int style, int size) {
        for (String name : preferred) {
            if (AVAILABLE.contains(name)) {
                return new Font(name, style, size);
            }
        }
        return new Font("Serif", style, size);
    }
}
