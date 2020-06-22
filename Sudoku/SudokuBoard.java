package Sudoku;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SudokuBoard {
    static enum RCM {
        ROW, COL, MASS
    }

    int msize;
    int psize;
    SudokuPanel[] sudoku_board;
    LinkedList<Integer> list = new LinkedList<>();
    LinkedList<Integer> numList;

    static LinkedList<SudokuBoard> solutions = new LinkedList<>();

    SudokuBoard(String filename) {
        load(filename);
    }

    SudokuBoard(SudokuBoard board) {
        msize = board.msize;
        psize = board.psize;
        sudoku_board = new SudokuPanel[psize * psize];
        for (int index = 0; index < sudoku_board.length; index++)
            sudoku_board[index] = board.sudoku_board[index].clone();
        setNumList();
    }

    public int getPsize() {
        return psize;
    }

    int getLine(int index, RCM rcm) {
        switch (rcm) {
            case ROW:
                return index % psize;
            case COL:
                return index / psize;
            case MASS:
                return (getLine(index, RCM.COL) / msize * msize) + (getLine(index, RCM.ROW) / msize);
        }
        return -1;
    }

    LinkedList<Integer> getGroup(int line, RCM rcm) {
        LinkedList<Integer> member = new LinkedList<>();
        switch (rcm) {
            case ROW:
                psizeIndex().forEach(group -> member.add(group * psize + line));
                break;
            case COL:
                psizeIndex().forEach(group -> member.add(line * psize + group));
                break;
            case MASS:
                int leftUp = line / msize * (psize * msize) + line % msize * msize;
                for (int gpcol = 0; gpcol < msize; gpcol++)
                    for (int gprow = 0; gprow < msize; gprow++)
                        member.add(leftUp + gpcol * psize + gprow);
                break;
        }
        return member;
    }

    private void load(String filename) {
        // Load from file
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            LinkedList<String> strings = new LinkedList<>(reader.lines().collect(Collectors.toList()));
            setSize((int) Math.round(Math.sqrt(strings.size())));
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

    private void setSize(int msize) {
        this.msize = msize;
        psize = msize * msize;
        sudoku_board = new SudokuPanel[psize * psize];
        setNumList();
    }

    private void setNumList() {
        numList = new LinkedList<>();
        for (int num = 1; num <= psize; num++)
            numList.add(num);
    }

    private LinkedList<Integer> numList() {
        return new LinkedList<>(numList);
    }

    private LinkedList<Integer> psizeIndex() {
        return new LinkedList<>(numList().stream().map(num -> num - 1).collect(Collectors.toList()));
    }

    String display() {
        return psizeIndex().stream()
                .map(col -> Arrays.asList(sudoku_board).subList(col * psize, (col + 1) * psize).stream()
                        .map(row -> row.getAns() == 0 ? "  " : String.format("%2d", row.getAns()))
                        .collect(Collectors.joining(",", "", "\n")))
                .collect(Collectors.joining()) + "count:" + countNotAns() + "\n";
    }

    private long countNotAns() {
        return Arrays.asList(sudoku_board).parallelStream().filter(p -> p.getAns() == 0).count();
    }

    boolean finished() {
        return psizeIndex().parallelStream()//
                .allMatch(line -> Arrays.asList(RCM.values()).stream()//
                        .allMatch(rcm -> numList().stream()//
                                .allMatch(num -> getGroup(line, rcm).stream()//
                                        .anyMatch(p -> sudoku_board[p].getAns() == num))));
    }

    private void setUsed(int index) {
        for (RCM rcm : RCM.values())
            for (Integer member : getGroup(getLine(index, rcm), rcm))
                setUsed(sudoku_board[index].getAns(), member);
    }

    private void setUsed(int num, int member) {
        if (!sudoku_board[member].getUsed(0) && sudoku_board[member].setUsed(num))
            // if count==1 ,store ans and add to list
            list.add(member);
    }

    private void checkOnly() {
        // Scanning number which can use only one panel at the group
        for (Integer scan : psizeIndex()) {
            for (RCM rcm : RCM.values()) {
                for (Integer num : numList()) {
                    List<Integer> finds = getGroup(scan, rcm).stream()
                            .filter(member -> sudoku_board[member].getUsed(num)).collect(Collectors.toList());
                    if (finds.size() == 1) {
                        sudoku_board[finds.get(0)].setAns(num);
                        list.add(finds.get(0));
                    }
                }
            }
        }
    }

    private boolean solve() {
        while (!list.isEmpty()) {
            while (!list.isEmpty())
                setUsed(list.pollFirst());
            checkOnly();
        }
        return finished();
    }

    void solveByBacktrack() {
        solve();
        addNum(new SudokuBoard(this), 0);
        sudoku_board = solutions.getFirst().sudoku_board;
        System.out.println(solutions.size());
    }

    private boolean addNum(SudokuBoard board, int index) {
        if (index >= psize * psize) {
            if (board.finished()) {
                solutions.add(board);
            }
            return finished();
        }
        if (board.sudoku_board[index].getAns() != 0)
            return addNum(board, index + 1);
        numList().parallelStream().forEach(num -> {
            if (!board.sudoku_board[index].ifUsed(num)) {
                SudokuBoard cpy = new SudokuBoard(board);
                cpy.sudoku_board[index].setAns(num);
                if (cpy.checkBoard(index)) {
                    cpy.list.add(index);
                    cpy.solve();
                    addNum(cpy, index + 1);
                }
            }
        });
        return false;
    }

    private boolean checkBoard(int index) {
        return !Arrays.asList(RCM.values()).parallelStream()//
                .anyMatch(rcm -> getGroup(getLine(index, rcm), rcm).stream()//
                        .anyMatch(member -> index != member //
                                && sudoku_board[member].getAns() == sudoku_board[index].getAns()));
    }

}
