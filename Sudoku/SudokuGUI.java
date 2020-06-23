package Sudoku;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import Sudoku.SudokuBoard.RCM;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuGUI {
    SudokuBoard board;
    static final int gridSize = 50;
    static final int margin = 5;
    static final int board_tk = 3;
    static final int mass_tk = 2;
    static final int panel_tk = 1;
    int board_bounds;
    int mass_bounds;

    JPanel[] num_panels;

    public static void main(String[] args) {
        (new SudokuGUI()).makeGUI();
    }

    void makeGUI() {
        JFrame frame = setFrame("Open File", 300, 200);

        JPanel panel = new JPanel(new FlowLayout());
        JTextField file_field = new JTextField("data/test6.txt", 20);
        JButton button = new JButton("OPEN");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board = new SudokuBoard(file_field.getText());
                board.solveByBacktrack();
                if (board.panels == null) {
                    JOptionPane.showMessageDialog(frame, "Could not open:" + file_field.getText());
                    return;
                }
                frame.setVisible(false);
                showBoard();
            }
        });

        panel.add(file_field);
        panel.add(button);

        frame.add(panel);
        frame.setVisible(true);
    }

    void showBoard() {
        JFrame frame = setFrame("Sudoku", board.psize * gridSize * 3 / 2, board.psize * gridSize + 100);
        JPanel frame_panel = new JPanel(new FlowLayout());
        num_panels = new JPanel[board.psize * board.psize];
        mass_bounds = board.msize * gridSize + mass_tk * 2;
        board_bounds = board.msize * mass_bounds + board_tk * 2;

        JPanel board_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        board_panel.setPreferredSize(new Dimension(board_bounds, board_bounds));
        board_panel.setBorder(new LineBorder(Color.black, board_tk));

        for (int line = 0; line < board.psize; line++)
            setMassPanels(board_panel, line);

        frame_panel.add(board_panel);
        frame.add(frame_panel);
        frame.setVisible(true);
    }

    private void setMassPanels(JPanel board_panel, int line) {
        JPanel mass_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mass_panel.setPreferredSize(new Dimension(mass_bounds, mass_bounds));
        mass_panel.setBorder(new LineBorder(Color.black, mass_tk));

        for (Integer index : board.getGroup(line, RCM.MASS)) {
            JPanel num_panel = new JPanel();
            JLabel num_label = new JLabel("" + board.panels[index].ans);
            num_panel.add(num_label);
            num_panel.setPreferredSize(new Dimension(gridSize, gridSize));
            num_panel.setBorder(new LineBorder(Color.black, panel_tk));

            num_panels[index] = num_panel;
            mass_panel.add(num_panel);
        }
        board_panel.add(mass_panel);
    }

    private static JFrame setFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setBounds(10, 10, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }
}