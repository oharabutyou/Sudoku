package Sudoku;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SudokuBoard {
    static enum RCM {
        ROW, COL, MASS
    }

    static Scanner stdIn = new Scanner(System.in);

    int msize;
    int psize;
    SudokuPanel[] sudoku_board;
    LinkedList<Integer> list = new LinkedList<>();

    SudokuBoard(String filename) {
        load(filename);
    }

    SudokuBoard() {
        load(getFilename());
    }

    String getFilename() {
        System.out.print("Enter Import File:");
        return stdIn.next();
    }

    public int getPsize() {
        return psize;
    }

    int getLine(int no, RCM rcm) {
        switch (rcm) {
            case ROW:
                return no % psize;
            case COL:
                return no / psize;
            case MASS:
                return (getLine(no, RCM.COL) / msize * msize) + (getLine(no, RCM.ROW) / msize);
        }
        return -1;
    }

    int[] getGroup(int line, RCM rcm) {
        int[] member = new int[psize];
        switch (rcm) {
            case ROW:
                for (int group = 0; group < psize; group++)
                    member[group] = group * psize + line;
                break;
            case COL:
                for (int group = 0; group < psize; group++)
                    member[group] = line * psize + group;
                break;
            case MASS:
                int leftUp = line / msize * (psize * msize) + line % msize * msize;
                for (int gpcol = 0; gpcol < msize; gpcol++)
                    for (int gprow = 0; gprow < msize; gprow++)
                        member[gpcol * msize + gprow] = leftUp + gpcol * psize + gprow;
                break;
        }
        return member;
    }

    void load(String filename) {
        // Load from file
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            LinkedList<String> strings = new LinkedList<>(reader.lines().collect(Collectors.toList()));
            msize = (int) Math.round(Math.sqrt(strings.size()));
            psize = msize * msize;
            sudoku_board = new SudokuPanel[psize * psize];
            for (int col = 0; col < psize; col++) {
                String s = strings.get(col);
                String[] sp = s.split(",");
                for (int row = 0; row < psize; row++) {
                    int ans = Integer.parseInt(sp[row]);
                    sudoku_board[col * psize + row] = new SudokuPanel(ans, psize);
                    if (ans != 0)
                        list.add(col * psize + row);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    String display(SudokuPanel[] panels) {
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

    String display() {
        return display(sudoku_board);
    }

    boolean finished(SudokuPanel[] panels) {
        for (int scan = 0; scan < psize; scan++) {
            for (RCM rcm : RCM.values()) {
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

    boolean finished() {
        return finished(sudoku_board);
    }

    void setUsed(int no, SudokuPanel[] panels) {
        int num = panels[no].getAns();
        for (RCM rcm : RCM.values()) {
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

    boolean checkOnly(SudokuPanel[] panels) {
        // Scanning number which can use only one panel at the group
        for (int scan = 0; scan < psize; scan++) {
            for (RCM rcm : RCM.values()) {
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
                        setUsed(find, panels);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void setUsed(int no, int ino, RCM rcm, SudokuPanel[] panels) {
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

    boolean solve(SudokuPanel[] panels) {
        while (!list.isEmpty()) {
            do {
                while (!list.isEmpty()) {
                    int no = list.pollFirst();
                    setUsed(no, panels);
                }
            } while (checkOnly(panels));
        }
        return finished(panels);
    }

    void solve() {
        solve(sudoku_board);
    }

    void solveByBacktrack() {
        solve(sudoku_board);
        addNum(sudoku_board, 0);
    }

    boolean addNum(SudokuPanel[] board, int index) {
        if (index >= psize * psize) {
            sudoku_board = board;
            return finished(board);
        }
        if (board[index].getAns() != 0)
            return addNum(board, index + 1);
        for (int num = 1; num <= psize; num++) {
            if (!board[index].ifUsed(num)) {
                SudokuPanel[] cpy = new SudokuPanel[board.length];
                for (int i = 0; i < cpy.length; i++) {
                    cpy[i] = board[i].clone();
                }
                cpy[index].setAns(num);
                if (checkBoard(cpy, index)) {
                    list.add(index);
                    solve(cpy);
                    if (addNum(cpy, index + 1))
                        return true;
                }
            }
        }
        return false;
    }

    boolean checkBoard(SudokuPanel[] board, int index) {
        for (RCM rcm : RCM.values()) {
            int[] member = getGroup(getLine(index, rcm), rcm);
            for (int i = 0; i < member.length; i++) {
                if (index != member[i] && board[member[i]].getAns() == board[index].getAns())
                    return false;
            }
        }
        return true;
    }

}
