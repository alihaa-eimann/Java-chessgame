class Pawn extends Piece {

    Pawn(int row, int col, String color) {
        super("Pawn", color, row, col);
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] board) {


        if (newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7) {
            return false;
        }

        int direction = isWhite() ? -1 : 1;


        if (newCol == col && board[newRow][newCol] == null) {
            if (newRow == row + direction) {
                return true;
            }


            if (newRow == row + (direction * 2)) {
                boolean isStartingRow = (isWhite() && row == 6) || (!isWhite() && row == 1);
                boolean middleEmpty = board[row + direction][col] == null;
                if (isStartingRow && middleEmpty) {  // Bug 1 fix — removed redundant check
                    return true;
                }
            }
        }

        if (Math.abs(newCol - col) == 1 && newRow == row + direction) {
            if (board[newRow][newCol] != null &&
                    board[newRow][newCol].isWhite() != this.isWhite()) {
                return true;
            }
        }

        return false;
    }
}