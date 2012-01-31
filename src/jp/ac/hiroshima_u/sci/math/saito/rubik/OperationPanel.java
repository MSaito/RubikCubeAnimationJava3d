package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

public class OperationPanel extends JPanel implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public OperationPanel(HashMap<JButton, CommandType> commandMap) {
        JButton[][] operationButtons = new JButton[6][];
        String pre = "<html>";
        String[] op = { "U", "R", "F", "D", "L", "B" };
        String[] num = { "", "<sup>2</sup>", "<sup>-1</sup>" };
        this.setLayout(new GridLayout(6, 3, 2, 2));
        for (int i = 0; i < operationButtons.length; i++) {
            operationButtons[i] = new JButton[3];
            for (int j = 0; j < operationButtons[i].length; j++) {
                operationButtons[i][j] = new JButton(pre + op[i] + num[j]);
                this.add(operationButtons[i][j]);
                operationButtons[i][j].addActionListener(this);
                commandMap.put(operationButtons[i][j],
                        Command.getCommandType(op[i] + (j + 1)));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO 自動生成されたメソッド・スタブ
        
    }

}
