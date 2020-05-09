import java.util.Scanner;
public class SudokuSolver {
	static Scanner stdIn = new Scanner(System.in);
	public static void main(String[] args) {
		String filename;
		if(args.length==0){
			System.out.println("Enter import file:");
			filename = stdIn.next();
		}else{
			filename = args[0];
		}
		SudokuBoard b = new SudokuBoard(filename);
		System.out.println("Imported number:");
		System.out.println(b.display());
		// System.out.println("Please wait...\n");
		// b.solve();
		// System.out.println("After solving:");
		// System.out.println(b.display());
		// if(b.finished())System.out.println("This is the answer!");

		System.out.println("Please wait...\n");
		b.solveByBacktrack();
		System.out.println("After solving:");
		System.out.println(b.display());
		if(b.finished())System.out.println("This is the answer!");
	}
}