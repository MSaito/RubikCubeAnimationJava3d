package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class ViewOperationPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ViewOperationPanel(
            HashMap<JButton, CommandType> commandMap, ActionListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        JButton vup = new JButton("Å™");
        commandMap.put(vup, Command.getCommandType("VIEW_UP"));
        top.add(Box.createHorizontalGlue());
        top.add(vup);
        top.add(Box.createHorizontalGlue());
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        JButton vleft = new JButton("Å©");
        JButton vright = new JButton("Å®");
        JButton vreset = new JButton("Reset");
        center.add(vleft);
        center.add(vreset);
        center.add(vright);
        commandMap.put(vright, Command.getCommandType("VIEW_RIGHT"));
        commandMap.put(vleft, Command.getCommandType("VIEW_LEFT"));
        commandMap.put(vreset, Command.getCommandType("VIEW_RESET"));
        JButton vdown = new JButton("Å´");
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        bottom.add(Box.createHorizontalGlue());
        bottom.add(vdown);
        commandMap.put(vdown, Command.getCommandType("VIEW_DOWN"));
        bottom.add(Box.createHorizontalGlue());
        add(top);
        add(center);
        add(bottom);
        vup.addActionListener(listener);
        vdown.addActionListener(listener);
        vleft.addActionListener(listener);
        vright.addActionListener(listener);
        vreset.addActionListener(listener);
        setBorder(new TitledBorder("éãì_à⁄ìÆ"));
    }
}
