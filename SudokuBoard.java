import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

class Pair {
    int a;
    int b;

    Pair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    int getA() {
        return a;
    }

    int getB() {
        return b;
    }

    boolean equalTo(Pair p) {
        if ((a == p.getA() && b == p.getB()) || (b == p.getA() && a == p.getB())) {
            return true;
        }
        return false;
    }
}

public class SudokuBoard {
    static int msize = 4;
    static int psize = msize * msize;
    SudokuPanel[] panels = new SudokuPanel[psize * psize];
    SudokuPanel[] sudoku_board = new SudokuPanel[psize * psize];
    LinkedList<Integer> list = new LinkedList<>();
    LinkedList<Pair> pairList = new LinkedList<>();
    final int ROW = 0;
    final int COL = 1;
    final int MASS = 2;
    final int RCM = 3;

    SudokuBoard(String filename) {
        load(filename);
    }

    public int getRCM() {
        return RCM;
    }

    public int getPsize() {
        return psize;
    }

    int getLine(int no, int rcm) {
        int line = -1;
        switch (rcm) {
            case ROW:
                line = no % psize;
                break;
            case COL:
                line = no / psize;
                break;
            case MASS:
                line = (getLine(no, COL) / msize * msize) + (getLine(no, ROW) / msize);
                break;
        }
        return line;
    }

    int[] getGroup(int line, int rcm) {
        int[] member = new int[psize];
        switch (rcm) {
            case ROW:
                for (int group = 0; group < psize; group++) {
                    member[group] = group * psize + line;
                }
                break;
            case COL:
                for (int group = 0; group < psize; group++) {
                    member[group] = line * psize + group;
                }
                break;
            case MASS:
                int leftUp = line / msize * (psize * msize) + line % msize * msize;
                for (int gpcol = 0; gpcol < msize; gpcol++) {
                    for (int gprow = 0; gprow < msize; gprow++) {
                        member[gpcol * msize + gprow] = leftUp + gpcol * psize + gprow;
                    }
                }
                break;
        }
        return member;
    }

    void load(String filename) {
        // Load from file
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            for (int col = 0; col < psize; col++) {
                String s = reader.readLine();
                String[] sp = s.split(",");
                for (int row = 0; row < psize; row++) {
                    int ans = Integer.parseInt(sp[row]);
                    panels[col * psize + row] = new SudokuPanel(ans);
                    if (ans != 0)
                        list.add(col * psize + row);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    String display() {
        String disp = "";
        int count = 0;
        for (int col = 0; col < psize; col++) {
            for (int row = 0; row < psize; row++) {
                disp += panels[col * psize + row].getAns();
                if (0 == panels[col * psize + row].getAns())
                    count++;
                if (row != psize - 1)
                    disp += ",";
            }
            disp += "\n";
        }
        disp += "count:" + count + "\n";
        return disp;
    }

    boolean finished() {
        for (int scan = 0; scan < psize; scan++) {
            for (int rcm = 0; rcm < RCM; rcm++) {
                int[] checker = getGroup(scan, rcm);
                for (int num = 1; num <= psize; num++) {
                    for (int group = 0; group < psize; group++) {
                        int ans = panels[checker[group]].getAns();
                        if (ans == num)
                            break;
                        else if (ans == 0)
                            return false;
                        if (group == psize - 1)
                            return false;
                    }
                }

            }
        }
        return true;
    }

    void setUsed(int no) {
        int num = panels[no].getAns();
        for (int rcm = 0; rcm < RCM; rcm++) {
            // row,col,mass Check by this order
            int[] checker = getGroup(getLine(no, rcm), rcm);
            for (int group = 0; group < psize; group++) {
                int gpno = checker[group];
                if (!panels[gpno].getUsed(0)) {
                    if (panels[gpno].setUsed(num)) {
                        // if count==1 ,store ans and add to list
                        list.add(gpno);
                    }
                }
            }
        }
    }

    boolean checkOnly() {
        // Scanning number which can use only one panel at the group
        for (int scan = 0; scan < psize; scan++) {
            for (int rcm = 0; rcm < RCM; rcm++) {
                int[] checker = getGroup(scan, rcm);
                for (int num = 1; num <= psize; num++) {
                    int find = -1;
                    for (int group = 0; group < psize; group++) {
                        int no = checker[group];
                        boolean used = panels[no].getUsed(num);
                        if (num == panels[no].getAns())
                            break;
                        else if (find == -1 && !used)
                            find = no;
                        else if (find != -1 && !used) {
                            find = -1;
                            break;
                        }
                    }
                    if (find != -1) {
                        panels[find].setAns(num);
                        setUsed(find);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean pairFinder() {
        // Find the pair
        for (int scan = 0; scan < psize; scan++) {
            for (int rcm = 0; rcm < RCM; rcm++) {
                int[] checker = getGroup(scan, rcm);
                for (int group = 0; group < psize - 1; group++) {
                    int no = checker[group];
                    if (panels[no].getCount() == 2) {
                        for (int i = group + 1; i < psize; i++) {
                            int ino = checker[i];
                            if (panels[ino].getCount() == 2) {
                                boolean[] compare = panels[no].getUsed();
                                if (equalUsed(compare, panels[ino].getUsed())) {
                                    if (!pairExist(new Pair(no, ino))) {
                                        pairList.add(new Pair(no, ino));
                                        convert(no, ino, rcm);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean pairExist(Pair p) {
        boolean exist = false;
        for (int i = 0; i < pairList.size(); i++) {
            if (pairList.get(i).equalTo(p)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    boolean equalUsed(boolean[] no, boolean[] ino) {
        boolean equal = true;
        for (int i = 0; i < psize; i++) {
            if (no[i] != ino[i]) {
                equal = false;
                break;
            }
        }
        return equal;
    }

    void setUsed(int no, int ino, int rcm) {
        // SetUsed pair number after finding
        int[] checker = getGroup(getLine(no, rcm), rcm);
        boolean[] used = panels[no].getUsed();
        for (int num = 1; num <= psize; num++) {
            if (!used[num]) {
                for (int group = 0; group < psize; group++) {
                    int gpno = checker[group];
                    if (gpno != no && gpno != ino) {
                        if (!panels[gpno].getUsed(0)) {
                            if (panels[gpno].setUsed(num)) {
                                // if count==1 ,store ans and add to list
                                list.add(gpno);
                            }
                        }
                    }
                }
            }
        }
    }

    void convert(int no, int ino, int rcm) {
        setUsed(no, ino, rcm);
        switch (rcm) {
            case ROW:
            case COL:
                if (getLine(no, MASS) == getLine(ino, MASS))
                    setUsed(no, ino, MASS);
                break;
            case MASS:
                if (getLine(no, ROW) == getLine(ino, ROW))
                    setUsed(no, ino, ROW);
                else if (getLine(no, COL) == getLine(ino, COL))
                    setUsed(no, ino, COL);
                break;
        }
    }

    boolean solve() {
        while (!list.isEmpty()) {
            do {
                while (!list.isEmpty()) {
                    int no = list.pollFirst();
                    setUsed(no);
                }
            } while (checkOnly() || pairFinder());
        }
        return finished();
    }

    void solveByBacktrack() {
        addNum(0);
    }

    boolean addNum(int index) {
        if (index >= psize * psize) 
            return true;
        if (panels[index].getAns() != 0)
            return addNum(index + 1);
        for (int num = 1; num <= psize; num++) {
            panels[index].setAns(num);
            // if (!panels[index].ifUsed(num) && checkBoard(panels, index) && addNum(panels, index + 1))
            if (checkBoard(index) && addNum(index + 1))
                return true;
        }
        panels[index].setAns(0);
        return false;
    }

    boolean checkBoard(int index) {
        for (int rcm = 0; rcm < RCM; rcm++) {
            int[] member = getGroup(getLine(index, rcm), rcm);
            for (int i = 0; i < member.length; i++) {
                if (index != member[i] && panels[member[i]].getAns() == panels[index].getAns())
                    return false;
            }
        }
        return true;
    }

}
