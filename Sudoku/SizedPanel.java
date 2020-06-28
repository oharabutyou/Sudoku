package Sudoku;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

public class SizedPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    static enum SIZE {
        used(0), panel(1), mass(2), board(3);

        int tk;

        SIZE(int tk) {
            this.tk = tk;
        }

        int getBounds() {
            switch (this) {
                case board:
                    return solver.msize * mass.getBounds() + board.tk * 2;
                case mass:
                    return solver.msize * gridSize + mass.tk * 2;
                case panel:
                    return gridSize;
                case used:
                    return (gridSize - panel.tk * 2) / solver.msize;
                default:
                    return 0;
            }
        }
    }

    SIZE size;
    static final int gridSize = 60;
    static SudokuBoard solver;

    SizedPanel(SIZE size, SudokuBoard solver) {
        this.size = size;
        if (SizedPanel.solver == null && solver != null)
            setSolver(solver);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setPreferredSize(new Dimension(size.getBounds(), size.getBounds()));
        setBorder(new LineBorder(Color.black, size.tk));
    }

    static void setSolver(SudokuBoard solver) {
        SizedPanel.solver = solver;
    }

}

class NumPanel extends SizedPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int index;
    Color bg = Color.white;
    boolean answered;

    NumPanel(int index, SudokuBoard solver) {
        super(SIZE.panel, solver);
        this.index = index;
    }

    void rewrite() {
        removeAll();
        if (solver.panels.get(index).getAns() == 0)
            solver.numList().forEach(n -> showUsed(n));
        else {
            if (!answered) {
                bg = new Color(255, 200, 200);
                answered = true;
            }
            JLabel ans = new JLabel("" + solver.panels.get(index).getAns());
            ans.setFont(new Font("Arial", Font.PLAIN, gridSize - SIZE.panel.tk * 2));
            add(ans);
        }
        setBackground(bg);
        validate();
        bg = Color.white;
    }

    private void showUsed(int n) {
        JPanel used_panel = new SizedPanel(SIZE.used, solver);
        used_panel.add(new JLabel(solver.panels.get(index).ifUsed(n) ? "" : Integer.toString(n)));
        used_panel.setBackground(new Color(0, 0, 0, 0));
        add(used_panel);
    }
}