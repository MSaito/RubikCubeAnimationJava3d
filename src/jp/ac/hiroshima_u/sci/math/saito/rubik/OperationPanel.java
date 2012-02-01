package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * ルービックキューブの基本操作用ボタン設定
 */
public class OperationPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String PRE_STRING = "<html>";
    private static final String[] OPERATION_3X3X3 = { "U", "R", "F", "D", "L", "B" };
    private static final String[] OPERATION_2X2X2 = { "U", "R", "F" };
    private static final String[] OPERATION_SUB = { "", "<sup>2</sup>", "<sup>-1</sup>" };

    public OperationPanel(int count, HashMap<JButton, CommandType> commandMap,
            ActionListener listener) {
        if (count == 3) {
            initialize(6, 3, commandMap, OPERATION_3X3X3, listener);
        } else if (count == 2) {
            initialize(3, 3, commandMap, OPERATION_2X2X2, listener);
        }
    }

    private void initialize(int row, int col,
            HashMap<JButton, CommandType> commandMap, String[] operator,
            ActionListener listener) {
        JButton[][] operationButtons = new JButton[row][];
        this.setLayout(new GridLayout(row, col, 2, 2));
        for (int i = 0; i < operationButtons.length; i++) {
            operationButtons[i] = new JButton[col];
            for (int j = 0; j < operationButtons[i].length; j++) {
                operationButtons[i][j] 
                        = new JButton(PRE_STRING 
                                + OPERATION_3X3X3[i] 
                                        + OPERATION_SUB[j]);
                this.add(operationButtons[i][j]);
                operationButtons[i][j].addActionListener(listener);
                commandMap.put(operationButtons[i][j],
                        Command.getCommandType(OPERATION_3X3X3[i] + (j + 1)));
            }
        }
        setBorder(new TitledBorder("基本操作"));
    }
}
