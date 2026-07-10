// ===================== KING =====================
class King extends Piece {

    King(int row, int col, String color) {
        super("King", color, row, col);
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {

        int rowDiff = Math.abs(newRow - row);
        int colDiff = Math.abs(newCol - col);

        if (rowDiff == 0 && colDiff == 0) {
            return false;
        }

        if (rowDiff <= 1 && colDiff <= 1) {

            return board[newRow][newCol] == null ||
                    board[newRow][newCol].isWhite() != this.isWhite();
        }

        return false;
    }
}