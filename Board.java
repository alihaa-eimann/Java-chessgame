class Board {
    Piece[][] grid = new Piece[8][8];

    Board() {
        this.placePieces();
    }

    void placePieces() {
        this.grid[0][0] = new Rook(0, 0, "Black");
        this.grid[0][1] = new Knight(0, 1, "Black");
        this.grid[0][2] = new Bishop(0, 2, "Black");
        this.grid[0][3] = new Queen(0, 3, "Black");
        this.grid[0][4] = new King(0, 4, "Black");
        this.grid[0][5] = new Bishop(0, 5, "Black");
        this.grid[0][6] = new Knight(0, 6, "Black");
        this.grid[0][7] = new Rook(0, 7, "Black");

        for(int col = 0; col < 8; col++)
            this.grid[1][col] = new Pawn(1, col, "Black");

        for(int col = 0; col < 8; col++)
            this.grid[6][col] = new Pawn(6, col, "White");

        this.grid[7][0] = new Rook(7, 0, "White");
        this.grid[7][1] = new Knight(7, 1, "White");
        this.grid[7][2] = new Bishop(7, 2, "White");
        this.grid[7][3] = new Queen(7, 3, "White");
        this.grid[7][4] = new King(7, 4, "White");
        this.grid[7][5] = new Bishop(7, 5, "White");
        this.grid[7][6] = new Knight(7, 6, "White");
        this.grid[7][7] = new Rook(7, 7, "White");
    }

    void move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece p = grid[fromRow][fromCol];
        p.row = toRow;
        p.col = toCol;
        grid[toRow][toCol] = p;
        grid[fromRow][fromCol] = null;
    }

    Piece get(int row, int col) {
        return this.grid[row][col];
    }
}
