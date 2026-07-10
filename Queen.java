class Queen extends Piece {

    Queen(int row, int col, String color) {
        super("Queen", color, row, col);
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {

        if (row == newRow && col == newCol) {
            return false;
        }

        if (row == newRow || col == newCol) {

            int rowStep = Integer.compare(newRow, row);
            int colStep = Integer.compare(newCol, col);

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

        if (Math.abs(newRow - row) == Math.abs(newCol - col)) {

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

        return false;
    }
}