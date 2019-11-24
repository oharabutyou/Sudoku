public class SudokuPanel {
	int ans = 0;
	int psize = SudokuBoard.psize;
	int count = psize;
	// when used[0] is false scannable,the others indicate used number
	boolean[] used = new boolean[psize + 1];

	SudokuPanel() {
	}

	SudokuPanel(int ans) {
		setAns(ans);
	}

	boolean getUsed(int n) {
		return used[n] || used[0];
	}

	boolean[] getUsed(){
		return used.clone();
	}

	boolean setUsed(int n) {
		if (!used[n]) {
			count--;
			used[n] = true;
		}
		if (count == 1) {
			int num = 0;
			for (int i = 1; i <= psize; i++) {
				if (!used[i]) {
					num = i;
					break;
				}
			}
			setAns(num);
		}
		return count == 1;
	}

	void setAns(int ans) {
		// Store ans and disable scan
		this.ans = ans;
		if (ans != 0) {
			used[0] = true;
		}
	}

	int getAns() {
		return ans;
	}

	int getCount(){
		return count;
	}

}
