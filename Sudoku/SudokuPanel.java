package Sudoku;

public class SudokuPanel {
	int ans = 0;
	int psize;
	int count;
	// when used[0] is false scannable,the others indicate used number
	boolean[] used = new boolean[psize + 1];

	SudokuPanel(int ans,int psize) {
		this.psize = psize;
		count = psize;
		setAns(ans);
	}

	SudokuPanel(int ans,int psize,int count,boolean[] used){
		this.ans=ans;
		this.psize=psize;
		this.count=count;
		this.used=used;
	}

	@Override
	protected SudokuPanel clone() {
		return new SudokuPanel(ans, psize, count, used.clone());
	}

	boolean getUsed(int n) {
		return used[n] || used[0];
	}

	boolean ifUsed(int n){
		return used[n];
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
