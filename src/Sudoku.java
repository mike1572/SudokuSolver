import java.util.*;
import java.io.*;

public class Sudoku {
    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.  For
     * a standard Sudoku puzzle, SIZE is 3 and N is 9.
     */
    public int SIZE;
    public int N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0.
     */
    public int grid[][];

    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<Sudoku> solutions = new HashSet<Sudoku>();

    public Sudoku( int size ) {
        SIZE = size;
        N = size*size;

        grid = new int[N][N];
        for( int i = 0; i < N; i++ )
            for( int j = 0; j < N; j++ )
                grid[i][j] = 0;
    }


    /* readInteger is a helper function for the reading of the input file.  It reads
     * words until it finds one that represents an integer. For convenience, it will also
     * recognize the string "x" as equivalent to "0". */
    static int readInteger( InputStream in ) throws Exception {
        int result = 0;
        boolean success = false;
        while( !success ) {
            String word = readWord( in );
            try {
                result = Integer.parseInt( word );
                success = true;
            } catch( Exception e ) {
                // Convert 'x' words into 0's
                if( word.compareTo("x") == 0 ) {
                    result = 0;
                    success = true;
                }
                // Ignore all other words that are not integers
            }
        }
        return result;
    }


    /* readWord is a helper function that reads a word separated by white space. */
    static String readWord( InputStream in ) throws Exception {
        StringBuffer result = new StringBuffer();
        int currentChar = in.read();
        String whiteSpace = " \t\r\n";
        // Ignore any leading white space
        while( whiteSpace.indexOf(currentChar) > -1 ) {
            currentChar = in.read();
        }
        // Read all characters until you reach white space
        while( whiteSpace.indexOf(currentChar) == -1 ) {
            result.append( (char) currentChar );
            currentChar = in.read();
        }
        return result.toString();
    }


    /* This function reads a Sudoku puzzle from the input stream in.  The Sudoku
     * grid is filled in one row at at time, from left to right.  All non-valid
     * characters are ignored by this function and may be used in the Sudoku file
     * to increase its legibility. */
    public void read( InputStream in ) throws Exception {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                grid[i][j] = readInteger( in );
            }
        }
    }


    /* Helper function for the printing of Sudoku puzzle.  This function will print
     * out text, preceded by enough ' ' characters to make sure that the printint out
     * takes at least width characters.  */
    void printFixedWidth( String text, int width ) {
        for( int i = 0; i < width - text.length(); i++ )
            System.out.print( " " );
        System.out.print( text );
    }


    /* The print() function outputs the Sudoku grid to the standard output, using
     * a bit of extra formatting to make the result clearly readable. */
    public void print() {
        // Compute the number of digits necessary to print out each number in the Sudoku puzzle
        int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

        // Create a dashed line to separate the boxes
        int lineLength = (digits + 1) * N + 2 * SIZE - 3;
        StringBuffer line = new StringBuffer();
        for( int lineInit = 0; lineInit < lineLength; lineInit++ )
            line.append('-');

        // Go through the grid, printing out its values separated by spaces
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                printFixedWidth( String.valueOf( grid[i][j] ), digits );
                // Print the vertical lines between boxes
                if( (j < N-1) && ((j+1) % SIZE == 0) )
                    System.out.print( " |" );
                System.out.print( " " );
            }
            System.out.println();

            // Print the horizontal line between boxes
            if( (i < N-1) && ((i+1) % SIZE == 0) )
                System.out.println( line.toString() );
        }
    }


     public static void main( String args[] ) throws Exception {
        InputStream in = new FileInputStream("hard3x3.txt");

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger( in );
        if( puzzleSize > 100 || puzzleSize < 1 ) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        Sudoku s = new Sudoku( puzzleSize );
        s.read( in );
        System.out.println("Before the solve:");
        s.print();
        System.out.println();

        // Solve the puzzle by finding one solution.
        s.solve(false);

        // Print out the puzzle
        System.out.println("After the solve:");
        s.print();

    }

    private boolean sameInColumn(int value, int colNumber){
        for (int i = 0; i < this.N; i++){
            if (this.grid[i][colNumber] == value){
                return true;
            }
        }
        return false;
    }

    private boolean sameInRow(int value, int rowNumber){
        for (int i = 0; i < this.N; i++){
            if (this.grid[rowNumber][i] == value){
                return true;
            }
        }
        return false;
    }

    private boolean sameInBox(int value, int rowNumber, int colNumber){
        int startRow = rowNumber - rowNumber % this.SIZE;
        int startCol = colNumber - colNumber % this.SIZE;

        for (int i = startRow; i < startRow + this.SIZE; i++){
            for (int j = startCol; j < startCol + this.SIZE; j++){
                if (this.grid[i][j] == value){
                    return true;
                }
            }
        }

        return false;
    }

    private boolean solveSudoku(){

        for (int gridRow = 0; gridRow < this.N; gridRow++){
            for(int gridCol = 0; gridCol < this.N; gridCol++){
                if(this.grid[gridRow][gridCol] == 0){
                    for (int sudokuNum = 1; sudokuNum <= this.N; sudokuNum++){
                        if (!this.sameInBox(sudokuNum, gridRow, gridCol) && !this.sameInRow(sudokuNum,gridRow) && !this.sameInColumn(sudokuNum, gridCol)){
                                this.grid[gridRow][gridCol] = sudokuNum;
                                if (this.solveSudoku()) {
                                    return true;
                                } else {
                                    this.grid[gridRow][gridCol] = 0;
                                }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public void solve(boolean allSolutions) {
        if (allSolutions){
            this.solveSudoku();
            this.solutions.add(this);
        } else {
            this.solveSudoku();
        }

    }

}
