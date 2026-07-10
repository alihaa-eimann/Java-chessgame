class Rook extends Piece {

    Rook(int row, int col, String color) {
        super("Rook", color, row, col);
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {

        if (row == newRow && col == newCol) {
            return false;
        }

        // Rook only moves straight
        if (row != newRow && col != newCol) {
            return false;
        }

        // Horizontal movement
        if (row == newRow) {
            int step = (newCol > col) ? 1 : -1;
            for (int c = col + step; c != newCol; c += step) {
                if (board[row][c] != null) {
                    return false;
                }
            }
        }

        // Vertical movement
        if (col == newCol) {
            int step = (newRow > row) ? 1 : -1;
            for (int r = row + step; r != newRow; r += step) {
                if (board[r][col] != null) {
                    return false;
                }
            }
        }

        return board[newRow][newCol] == null ||
                board[newRow][newCol].isWhite() != this.isWhite();
    }
}