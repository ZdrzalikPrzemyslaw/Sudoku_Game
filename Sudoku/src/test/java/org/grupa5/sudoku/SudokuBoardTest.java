package org.grupa5.sudoku;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App. Testing methods: resetBoard.
 */
public class SudokuBoardTest {

    @Test
    void resetBoardTest() {
        List<List<SudokuField>> board; // = Arrays.asList(
//                Arrays.asList(new SudokuField[9]),
//                Arrays.asList(new SudokuField[9]),
//                Arrays.asList(new SudokuField[9]),
//                Arrays.asList(new SudokuField[9]),
//                Arrays.asList(new SudokuField[9]),
//                Arrays.asList(new SudokuField[9]),
//                Arrays.asList(new SudokuField[9]),
//                Arrays.asList(new SudokuField[9]),
//                Arrays.asList(new SudokuField[9]));
        SudokuBoard sudoku = new SudokuBoard();
        SudokuSolver Wypelniacz = new BacktrackingSudokuSolver();
        Wypelniacz.solve(sudoku);
        sudoku.resetBoard();
        board = sudoku.getBoard();
        boolean check = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.get(i).get(j).getFieldValue() != 0) {
                    check = true;
                }
            }
        }
        assertFalse(check);
    }

//    @Test
//    void getInfoSudokuTest() {
//        ArrayList<ArrayList<SudokuField>> board;
//        SudokuBoard sudoku = new SudokuBoard();
//        SudokuSolver Wypelniacz = new BacktrackingSudokuSolver();
//        Wypelniacz.solve(sudoku);
//        board = sudoku.getBoard();
//
//        StringBuilder output = new StringBuilder("X ");
//        for (int i = 0; i <= 8; i++) {
//            output.append((char) ('a' + i)).append(" ");
//        }
//        output.append("\n");
//        int counter = 0;
//        for (ArrayList<SudokuField> x : board) {
//            output.append((char) ('a' + counter)).append(" ");
//            for (SudokuField y : x) {
//                output.append(y.getFieldValue()).append(" ");
//            }
//            output.append("\n");
//            counter++;
//        }
//        assertEquals(sudoku.getInfoSudoku(), output.toString());
//    }

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
