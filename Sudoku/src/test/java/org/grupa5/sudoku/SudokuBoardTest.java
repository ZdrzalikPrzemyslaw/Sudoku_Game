package org.grupa5.sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
* Unit test for simple App.
* Testing methods: getInfoSudoku, resetBoard.
*/
public class SudokuBoardTest {

   @Test
   void resetBoardTest() {
       int[][] board;
       SudokuBoard sudoku = new SudokuBoard();
       SudokuSolver Wypelniacz = new BacktrackingSudokuSolver();
       Wypelniacz.solve(sudoku);
       sudoku.resetBoard();
       board = sudoku.getBoard();
       boolean check = false;
       for (int i = 0; i < 9; i++) {
           for (int j = 0; j < 9; j++) {
               if (board[i][j] != 0) {
                   check = true;
               }
           }
       }
       assertFalse(check);
   }

   @Test
   void getInfoSudokuTest() {
       int[][] board;
       SudokuBoard sudoku = new SudokuBoard();
       SudokuSolver Wypelniacz = new BacktrackingSudokuSolver();
       Wypelniacz.solve(sudoku);
       board = sudoku.getBoard();

       StringBuilder output = new StringBuilder("X ");
       for (int i = 0; i <= 8; i++) {
           output.append((char) ('a' + i)).append(" ");
       }
       output.append("\n");
       int counter = 0;
       for (int[] x : board) {
           output.append((char) ('a' + counter)).append(" ");
           for (int y : x) {
               output.append(y).append(" ");
           }
           output.append("\n");
           counter++;
       }
       assertEquals(sudoku.getInfoSudoku(), output.toString());
   }

   @Test
   void getterAndSetterTest() {
        SudokuBoard sudoku = new SudokuBoard();
        assertEquals(sudoku.get(2, 2), 0);
        sudoku.set(2, 2, 2137);
        assertEquals(sudoku.get(2, 2), 2137);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            sudoku.get(9, 9);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            sudoku.set(9, 9, 1337);
        });
   }

}