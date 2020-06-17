package Sudoku;

import java.util.Scanner;

public class SudokuSolver {
	static Scanner stdIn = new Scanner(System.in);

	public static void main(String[] args) {
		SudokuBoard b;
		if (args.length == 0)
			b = new SudokuBoard();
		else
			b = new SudokuBoard(args[0]);
		System.out.println("Imported number:");
		System.out.println(b.display());
		System.out.println("Please wait...\n");
		b.solveByBacktrack();
		System.out.println("After solving:");
		System.out.println(b.display());
		if (b.finished())
			System.out.println("This is the answer!");
	}
}