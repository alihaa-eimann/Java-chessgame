class Bishop extends Piece {

    Bishop(int row, int col, String color) {
        super("Bishop", color, row, col);
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {

        if (row == newRow && col == newCol) {
            return false;
        }

        // Bishop only moves diagonally
        if (Math.abs(newRow - row) != Math.abs(newCol - col)) {
            return false;
        }

        int rowStep = (newRow > row) ? 1 : -1;
        int colStep = (newCol > col) ? 1 : -1;

        int r = row + rowStep;
        int c = col + colStep;

        while (r != newRow || c != newCol) {
            if (board[r][c] != null) {
                return false;
            }
            r += rowStep;
            c += colStep;
        }

        return board[newRow][newCol] == null ||
                board[newRow][newCol].isWhite() != this.isWhite();
    }
}