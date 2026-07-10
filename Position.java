public class Position {
    int row;
    int col;

    Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}