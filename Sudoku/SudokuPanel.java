package Sudoku;

public class SudokuPanel {
	private int ans = 0;
	int psize;
	int count;
	boolean[] used;

	SudokuPanel(int psize) {
		this.psize = psize;
		used = new boolean[psize + 1];
		count = psize;
	}

	SudokuPanel(int ans, int psize, int count, boolean[] used) {
		this.ans = ans;
		this.psize = psize;
		this.count = count;
		this.used = used;
	}

	@Override
	protected SudokuPanel clone() {
		return new SudokuPanel(ans, psize, count, used.clone());
	}

	boolean getUsed(int n) {
		return used[n] || ans != 0;
	}

	boolean ifUsed(int n) {
		return used[n];
	}

	boolean[] getUsed() {
		return used.clone();
	}

	boolean setUsed(int n) {
		if (!used[n]) {
			count--;
			used[n] = true;
		}
		if (count == 1)
			setAns(lonelyFinder());
		return count == 1;
	}

	private int lonelyFinder() {
		for (int i = 1; i <= psize; i++)
			if (!used[i])
				return i;
		return -1;
	}

	void setAns(int ans) {
		this.ans = ans;
	}

	int getAns() {
		return ans;
	}

	int getCount() {
		return count;
	}

}
