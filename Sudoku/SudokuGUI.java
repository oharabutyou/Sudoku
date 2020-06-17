package Sudoku;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuGUI {
    SudokuBoard board;
    static final int gridSize = 50;
    static final int margin = 5;

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
                frame.setVisible(false);
                board = new SudokuBoard(file_field.getText());
                showBoard();
            }
        });

        panel.add(file_field);
        panel.add(button);

        frame.add(panel);
        frame.setVisible(true);
    }

    void showBoard() {
        JFrame frame = setFrame("Sudoku", board.psize * gridSize*3/2, board.psize * gridSize+100);
        JPanel frame_panel = new JPanel(new FlowLayout());

        JPanel board_panel = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        board_panel.setBorder(new LineBorder(Color.black, 3));
        board_panel.setPreferredSize(new Dimension(board.psize * (gridSize+1)+6, board.psize * (gridSize+1)+6));
        for (int line = 0; line < board.psize; line++)
            setMassPanels(board_panel);

        frame_panel.add(board_panel);
        frame.add(frame_panel);
        frame.setVisible(true);
    }

    private void setMassPanels(JPanel board_panel) {
        JPanel mass_panel = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        mass_panel.setPreferredSize(new Dimension(board.msize * (gridSize+1), board.msize * (gridSize+1)));
        for (int index = 0; index < board.psize; index++) {
            JPanel num_panel = new JPanel();
            JLabel num_label = new JLabel("0");
            num_panel.add(num_label);
            num_panel.setPreferredSize(new Dimension(gridSize, gridSize));
            num_panel.setBorder(new LineBorder(Color.black, 1));
            mass_panel.add(num_panel);
        }
        mass_panel.setBorder(new LineBorder(Color.black, 2));
        board_panel.add(mass_panel);
    }

    private static JFrame setFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setBounds(10, 10, width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }
}