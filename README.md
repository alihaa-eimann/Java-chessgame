# Java Chess Game

A full-featured 2-player chess game built with Java Swing, featuring a polished dark-themed UI, full rule enforcement, and sound effects.

## Features

- **Full chess rules** — legal move generation for every piece, check and checkmate detection, and automatic pawn promotion to Queen
- **Two-player local play** — set custom player names and choose who plays White
- **Time controls** — optional per-player countdown clocks (5, 10, 15, or 30 minutes) or an untimed stopwatch mode
- **Move highlighting** — click a piece to see all of its legal destination squares highlighted on the board
- **Captured piece tray** — see each player's captured pieces displayed alongside their clock
- **Sound effects** — audio feedback for moves, captures, game over, and time running out
- **Polished start screen** — a poster-style welcome screen with a built-in "How to Play" guide
- **Full-screen play** — the game launches maximized for the best view of the board

## Getting Started

### Prerequisites

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) version 11 or later

### Running the Game

1. Clone the repository:
   ```bash
   git clone https://github.com/alihaa-eimann/Java-chessgame.git
   cd Java-chessgame
   ```

2. Compile the source files:
   ```bash
   javac -d out src/*.java
   ```

3. Run the game:
   ```bash
   java -cp out StartScreen
   ```

The welcome screen will open — enter player names, choose sides and a time control, then click **START** to begin.

## How to Play

- White always moves first. Click a piece, then click a highlighted square to move it.
- Legal moves for the selected piece are highlighted on the board.
- Capture the opponent's King to win the game instantly.
- Pawns automatically promote to a Queen when they reach the far side.
- Each player has their own clock, shown at the side of the board.

## Project Structure

```
Chess/
└── src/
    ├── StartScreen.java   # Welcome screen and game setup
    ├── ChessGame.java     # Main game window and board UI
    ├── Board.java         # Board state and move logic
    ├── Piece.java         # Base class for all chess pieces
    ├── King.java, Queen.java, Rook.java,
    │   Bishop.java, Knight.java, Pawn.java
    ├── Player.java        # Player data (name, color, clock)
    ├── Position.java      # Board coordinate helper
    ├── AppTheme.java      # Shared colors and fonts
    └── SoundUtil.java     # Sound effect playback
```

## License

This project is open source and available for personal and educational use.
