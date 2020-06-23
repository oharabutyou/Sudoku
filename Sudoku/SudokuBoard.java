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
        static List<RCM> List(){
            return Arrays.asList(RCM.values());
        }
    }

    int msize;
    int psize;
    SudokuPanel[] panels;
    LinkedList<Integer> list = new LinkedList<>();
    LinkedList<Integer> numList;

    static LinkedList<SudokuBoard> solutions = new LinkedList<>();

    SudokuBoard(String filename) {
        load(filename);
    }

    SudokuBoard(SudokuBoard board) {
        msize = board.msize;
        psize = board.psize;
        panels = new SudokuPanel[psize * psize];
        for (int index = 0; index < panels.length; index++)
            panels[index] = board.panels[index].clone();
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
                    panels[col * psize + row] = new SudokuPanel(psize);
                    if (ans != 0)
                        setAns(col * psize + row, ans);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void setAns(int index, int ans) {
        panels[index].setAns(ans);
        list.add(index);
    }

    private void setSize(int msize) {
        this.msize = msize;
        psize = msize * msize;
        panels = new SudokuPanel[psize * psize];
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
                .map(col -> Arrays.asList(panels).subList(col * psize, (col + 1) * psize).stream()
                        .map(row -> row.getAns() == 0 ? "  " : String.format("%2d", row.getAns()))
                        .collect(Collectors.joining(",", "", "\n")))
                .collect(Collectors.joining()) + "count:" + countNotAns() + "\n";
    }

    private long countNotAns() {
        return Arrays.asList(panels).parallelStream().filter(p -> p.getAns() == 0).count();
    }

    boolean finished() {
        return psizeIndex().parallelStream()//
                .allMatch(line -> RCM.List().stream()//
                        .allMatch(rcm -> numList().stream()//
                                .allMatch(num -> getGroup(line, rcm).stream()//
                                        .anyMatch(p -> panels[p].getAns() == num))));
    }

    private void setUsed(int index) {
        for (RCM rcm : RCM.values())
            for (Integer member : getGroup(getLine(index, rcm), rcm))
                setUsed(panels[index].getAns(), member);
    }

    private void setUsed(int num, int member) {
        if (!panels[member].getUsed(0) && panels[member].setUsed(num))
            // if count==1 ,store ans and add to list
            list.add(member);
    }

    private boolean checkOnly() {
        // Scanning number which can use only one panel at the group
        for (Integer scan : psizeIndex()) {
            for (RCM rcm : RCM.values()) {
                List<Integer> used = getGroup(scan, rcm).stream().map(m -> panels[m].getAns())
                        .collect(Collectors.toList());
                LinkedList<Integer> unused = numList();
                unused.removeAll(used);
                for (Integer num : unused) {
                    List<Integer> finds = getGroup(scan, rcm).stream().filter(m -> !panels[m].getUsed(num))
                            .collect(Collectors.toList());
                    if (finds.size() == 1) {
                        setAns(finds.get(0), num);
                        return true;
                    }
                }
            }
        }
        return false;
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
        if (board.panels[index].getAns() != 0)
            return addNum(board, index + 1);
        numList().stream().forEach(num -> {
            if (!board.panels[index].ifUsed(num)) {
                SudokuBoard cpy = new SudokuBoard(board);
                cpy.panels[index].setAns(num);
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
        return !RCM.List().parallelStream()//
                .anyMatch(rcm -> getGroup(getLine(index, rcm), rcm).stream()//
                        .anyMatch(member -> index != member //
                                && panels[member].getAns() == panels[index].getAns()));
    }

}
