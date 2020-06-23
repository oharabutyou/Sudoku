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
        ROW, COL, MASS;

        static List<RCM> List() {
            return Arrays.asList(RCM.values());
        }
    }

    int msize;
    int psize;
    LinkedList<SudokuPanel> panels = new LinkedList<>();
    LinkedList<Integer> list = new LinkedList<>();
    LinkedList<Integer> psizeIndex;

    static LinkedList<SudokuBoard> solutions = new LinkedList<>();

    SudokuBoard(String filename) {
        load(filename);
    }

    SudokuBoard(SudokuBoard board) {
        msize = board.msize;
        psize = board.psize;
        board.panels.stream().forEachOrdered(p -> panels.add(p.clone()));
        setPsizeIndex();
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
                psizeIndex().subList(0, msize).forEach(gpcol -> psizeIndex().subList(0, msize)
                        .forEach(gprow -> member.add(leftUp + gpcol * psize + gprow)));
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
            strings.subList(0, psize).stream()//
                    .forEach(s -> Arrays.asList(s.split(",")).subList(0, psize).stream()//
                            .forEach(sp -> setPanel(panels.size(), Integer.parseInt(sp))));
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    boolean loaded() {
        return panels != null && panels.size() == psize * psize;
    }

    private boolean setPanel(int index, int ans) {
        panels.add(new SudokuPanel(psize));
        return ans != 0 && setAns(index, ans);
    }

    private boolean setAns(int index, int ans) {
        panels.get(index).setAns(ans);
        return list.add(index);
    }

    private void setSize(int msize) {
        this.msize = msize;
        psize = msize * msize;
        setPsizeIndex();
    }

    private void setPsizeIndex() {
        psizeIndex = new LinkedList<>();
        for (int num = 0; num < psize; num++)
            psizeIndex.add(num);
    }

    private LinkedList<Integer> psizeIndex() {
        return new LinkedList<>(psizeIndex);
    }

    private LinkedList<Integer> numList() {
        return new LinkedList<>(psizeIndex().stream().map(num -> num + 1).collect(Collectors.toList()));
    }

    String display() {
        return psizeIndex().stream()
                .map(col -> panels.subList(col * psize, (col + 1) * psize).stream()
                        .map(row -> row.getAns() == 0 ? "  " : String.format("%2d", row.getAns()))
                        .collect(Collectors.joining(",", "", "\n")))
                .collect(Collectors.joining()) + "count:" + countNotAns() + "\n";
    }

    private long countNotAns() {
        return panels.parallelStream().filter(p -> p.getAns() == 0).count();
    }

    boolean finished() {
        return psizeIndex().parallelStream()//
                .allMatch(line -> RCM.List().stream()//
                        .allMatch(rcm -> numList().stream()//
                                .allMatch(num -> getGroup(line, rcm).stream()//
                                        .anyMatch(p -> panels.get(p).getAns() == num))));
    }

    private void setUsed(int index) {
        RCM.List().forEach(rcm -> getGroup(getLine(index, rcm), rcm)
                .forEach(member -> setUsed(panels.get(index).getAns(), member)));
    }

    private boolean setUsed(int num, int member) {
        // if count==1 ,store ans and add to list
        return (!panels.get(member).getUsed(0) && panels.get(member).setUsed(num) && list.add(member));
    }

    private boolean checkOnly() {
        // Scanning number which can use only one panel at the group
        return psizeIndex().stream().anyMatch(scan -> RCM.List().stream()
                .anyMatch(rcm -> removedNum(
                        getGroup(scan, rcm).stream().map(n -> panels.get(n).getAns()).collect(Collectors.toList()))
                                .stream().anyMatch(num -> lonelyFinder(scan, rcm, num))));
    }

    private boolean lonelyFinder(int line, RCM rcm, int num) {
        List<Integer> finds = getGroup(line, rcm).stream().filter(m -> !panels.get(m).getUsed(num))
                .collect(Collectors.toList());
        return finds.size() == 1 && setAns(finds.get(0), num);
    }

    private LinkedList<Integer> removedNum(List<Integer> remove) {
        LinkedList<Integer> numList = numList();
        numList.removeAll(remove);
        return numList;
    }

    private boolean solve() {
        while (!list.isEmpty() || checkOnly())
            setUsed(list.pollFirst());
        return finished();
    }

    void solveByBacktrack() {
        solve();
        addNum(new SudokuBoard(this), 0);
        panels = solutions.getFirst().panels;
        System.out.println(solutions.size());
    }

    private boolean addNum(SudokuBoard board, int index) {
        if (index >= psize * psize)
            return board.finished() && solutions.add(board);
        if (board.panels.get(index).getAns() != 0)
            return addNum(board, index + 1);
        numList().parallelStream().forEach(num -> tryToPut(board, index, num));
        return false;
    }

    private void tryToPut(SudokuBoard board, int index, Integer num) {
        if (!board.panels.get(index).ifUsed(num)) {
            SudokuBoard cpy = new SudokuBoard(board);
            cpy.panels.get(index).setAns(num);
            if (cpy.checkBoard(index)) {
                cpy.list.add(index);
                cpy.solve();
                addNum(cpy, index + 1);
            }
        }
    }

    private boolean checkBoard(int index) {
        return !RCM.List().parallelStream()//
                .anyMatch(rcm -> getGroup(getLine(index, rcm), rcm).stream()//
                        .anyMatch(member -> index != member //
                                && panels.get(member).getAns() == panels.get(index).getAns()));
    }

}
