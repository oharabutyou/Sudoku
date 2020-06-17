package Sudoku;

public class SudokuCUI {
	public static void main(String[] args) {
		SudokuBoard b = new SudokuBoard("data/test6.txt");
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