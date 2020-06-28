package Sudoku;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Sudoku.SizedPanel.SIZE;
import Sudoku.SudokuBoard.RCM;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class SudokuGUI {
    SudokuBoard solver;

    private HashMap<Integer, NumPanel> panel_map;
    private Integer selected;

    public static void main(String[] args) {
        (new SudokuGUI()).showLauncher();
    }

    void showLauncher() {
        JFrame frame = setFrame("Open File", 300, 200);

        JPanel panel = new JPanel(new FlowLayout());
        JTextField file_field = new JTextField("data/test6.txt", 20);
        JButton button = new JButton("OPEN");
        button.addActionListener(launchListener(frame, file_field));

        panel.add(file_field);
        panel.add(button);

        frame.add(panel);
        frame.setVisible(true);
    }

    private ActionListener launchListener(JFrame frame, JTextField file_field) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solver = new SudokuBoard(file_field.getText());
                if (!solver.loaded()) {
                    JOptionPane.showMessageDialog(frame, "Could not open:" + file_field.getText());
                    return;
                }
                frame.setVisible(false);
                showBoard();
            }
        };
    }

    private KeyListener ctrlKeyListener(JPanel frame_panel) {
        return new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!solver.list.isEmpty() || solver.checkOnly()) {
                    selected = solver.list.pollFirst();
                    solver.setUsed(selected);
                    RCM.List().parallelStream().forEach(rcm -> solver.getGroup(solver.getLine(selected, rcm), rcm)
                            .parallelStream().forEach(i -> panel_map.get(i).bg = new Color(200, 200, 255)));
                    panel_map.get(selected).bg = Color.blue;
                    panel_map.keySet().forEach(p -> panel_map.get(p).rewrite());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

        };
    }

    void showBoard() {
        int width = solver.psize * SizedPanel.gridSize * 3 / 2;
        int height = solver.psize * SizedPanel.gridSize + 100;
        JFrame frame = setFrame("Sudoku", width, height);
        panel_map = new HashMap<>();
        
        JPanel frame_panel = new JPanel(new FlowLayout());
        setBoardPanel(frame_panel);

        frame.add(frame_panel);
        frame.setVisible(true);
        frame.addKeyListener(ctrlKeyListener(frame_panel));
    }

    private void setBoardPanel(JPanel frame_panel) {
        JPanel board_panel = new SizedPanel(SIZE.board, solver);
        solver.psizeIndex().forEach(line -> setMassPanels(board_panel, line));
        frame_panel.add(board_panel);
    }

    private void setMassPanels(JPanel board_panel, int line) {
        JPanel mass_panel = new SizedPanel(SIZE.mass, solver);
        solver.getGroup(line, RCM.MASS).forEach(index -> setPanel(mass_panel, index));
        board_panel.add(mass_panel);
    }

    private void setPanel(JPanel mass_panel, Integer index) {
        NumPanel num_panel = new NumPanel(index, solver);
        panel_map.put(index, num_panel);
        num_panel.rewrite();
        mass_panel.add(num_panel);
    }

    private static JFrame setFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setBounds(10, 10, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }
}