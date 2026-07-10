abstract class Piece {
    String name;
    String color;
    int row;
    int col;

    Piece(String name, String color, int row, int col) {
        this.name = name;
        this.color = color;
        this.row = row;
        this.col = col;
    }
    boolean isWhite() {
        return color.equals("White");
    }
    String getSymbol() {
        if (this.color.equals("White")) {
            if (this.name.equals("King")) {
                return "♔";
            }

            if (this.name.equals("Queen")) {
                return "♕";
            }

            if (this.name.equals("Rook")) {
                return "♖";
            }

            if (this.name.equals("Bishop")) {
                return "♗";
            }

            if (this.name.equals("Knight")) {
                return "♘";
            }

            if (this.name.equals("Pawn")) {
                return "♙";
            }
        }

        if (this.color.equals("Black")) {
            if (this.name.equals("King")) {
                return "♚";
            }

            if (this.name.equals("Queen")) {
                return "♛";
            }

            if (this.name.equals("Rook")) {
                return "♜";
            }

            if (this.name.equals("Bishop")) {
                return "♝";
            }

            if (this.name.equals("Knight")) {
                return "♞";
            }

            if (this.name.equals("Pawn")) {
                return "♟";
            }
        }
        return "?";
    }
    abstract boolean isValidMove(int newRow, int newCol, Piece[][] board);

}
