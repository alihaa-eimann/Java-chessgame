class Knight extends Piece {
    Knight(int row, int col, String color) {
        super("Knight", color, row, col);
    }
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {

        int rowDiff = Math.abs(newRow - row);
        int colDiff = Math.abs(newCol - col);

        if ((rowDiff == 2 && colDiff == 1) ||
                (rowDiff == 1 && colDiff == 2)) {

            return board[newRow][newCol] == null ||
                    board[newRow][newCol].isWhite() != this.isWhite();
        }
        return false;
    }
}
