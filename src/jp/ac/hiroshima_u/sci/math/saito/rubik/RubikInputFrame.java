package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class RubikInputFrame extends JFrame implements ActionListener {
    Logger logger = Logger.getLogger(RubikInputFrame.class.getCanonicalName());
    private static final long serialVersionUID = 1L;
    private int rubikSize;
    private JButton[] buttons;
    private JButton[] colorButtons;
    private JButton currentColorButton;
    private JButton ok;
    private JButton cancel;
    private CubeBehavior cubeBehavior;
    
    public RubikInputFrame(String name, int size) {
        super(name);
        setLayout(new GridLayout(3, 4));
        this.rubikSize = size;
        this.buttons = new JButton[size * size * 6];
        this.colorButtons = new JButton[6];
        JPanel[] panels = new JPanel[6];
        for (int i = 0; i < panels.length; i++) {
            panels[i] = makeFacePanel(this.rubikSize, i * size * size);
        }
        JPanel colorPanel = makeColorPanel();
        JPanel currentColorPanel = makeCurrentColorPanel();
        add(new JPanel());
        add(panels[0]);
        add(currentColorPanel);
        add(colorPanel);
        add(panels[1]);
        add(panels[2]);
        add(panels[3]);
        add(panels[4]);
        add(new JPanel());
        add(panels[5]);
        add(new JPanel());
        add(makeCommandPanel());
        pack();
        if (size == 2) {
            setSize(RubikProperties.getInt("rubikinputframe.size2x2x2.width"),
                    RubikProperties.getInt("rubikinputframe.size2x2x2.height"));
        } else {
            setSize(RubikProperties.getInt("rubikinputframe.size3x3x3.width"),
                    RubikProperties.getInt("rubikinputframe.size3x3x3.height"));            
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private JPanel makeCurrentColorPanel() {
        JPanel panel = new JPanel();
        currentColorButton = new JButton("W");
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel("selected color", SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);
        panel.add(currentColorButton, BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeColorPanel() {
        Color[] bgColor = ColorAppearance.getColorAppearance().getAwtColor();
        String[] color = ColorAppearance.getColorAppearance().getColorString();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel("color select", SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);
        JPanel inner = new JPanel();
        panel.add(inner, BorderLayout.CENTER);
        inner.setLayout(new GridLayout(2, 3));
        for (int i = 0; i < colorButtons.length; i++) {
            logger.fine("bgColor:" + bgColor[i]);
            String c = color[i];
            JButton bt = new JButton(c);
            bt.addActionListener(this);
            bt.setOpaque(true);
            colorButtons[i] = bt;
            if (c.equals("W")) {
                bt.setForeground(Color.BLACK);
            } else if (c.equals("Y")) {
                bt.setForeground(Color.BLACK);
            } else {
                bt.setForeground(bgColor[i]);
            }
            inner.add(bt);
        }
        return panel;
    }
    
    private JPanel makeFacePanel(int size, int index) {
        JPanel panel = new JPanel();
        Dimension preferredSize = new Dimension(0, 0);
        panel.setBorder(new BevelBorder(BevelBorder.RAISED));
        panel.setLayout(new GridLayout(size, size));
        for (int i = 0; i < size * size; i++) {
            JButton bt = new JButton("");
            buttons[i + index] = bt;
            bt.addActionListener(this);
            bt.setPreferredSize(preferredSize);
            panel.add(bt);
        }
        return panel;
    }
    
    private JPanel makeCommandPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel inner = new JPanel();
        panel.add(inner, BorderLayout.SOUTH);
        ok = new JButton("OK");
        ok.addActionListener(this);
        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        inner.setLayout(new GridLayout(2,1));
        inner.add(ok);
        inner.add(cancel);
        return panel;
    }
    
    public void addCubeBehavior(CubeBehavior cubeBehavior) {
        this.cubeBehavior = cubeBehavior;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object src = actionEvent.getSource();
        for (JButton bt : colorButtons) {
            if (src == bt) {
                currentColorButton.setText(bt.getText());
                currentColorButton.setForeground(bt.getForeground());
                return;
            }
        }
        for (JButton bt : buttons) {
            if (src == bt) {
                bt.setText(currentColorButton.getText());
                bt.setForeground(currentColorButton.getForeground());
                return;
            }
        }
        if (src == ok) {
            if (cubeBehavior != null) {
                Command command = new Command(CommandType.COLOR, makeColorString());
                //cubeBehavior.stop();
                cubeBehavior.addCommand(command);
                //cubeBehavior.start();
            }
            setVisible(false);
        } else if (src == cancel) {
            setVisible(false);
        }        
    }
    
    private String makeColorString() {
        StringBuilder sb = new StringBuilder();
        int square = rubikSize * rubikSize;
        for (int i = 0; i < square; i++) {
            sb.append(buttons[i].getText());
        }
        for (int i = square * 2; i < square * 5; i++) {
            sb.append(buttons[i].getText());
        }
        for (int i = square; i < square * 2; i++) {
            sb.append(buttons[i].getText());
        }
        for (int i = square * 5; i < square * 6; i++) {
            sb.append(buttons[i].getText());
        }
        logger.info("colorString:" + sb.toString());
        return sb.toString();
    }

    public static void main(String args[]) {
        //RubikInputFrame sample = new RubikInputFrame("Color Input Frame", 3);
        RubikInputFrame sample = new RubikInputFrame("Color Input Frame", 2);
        sample.setBounds(10, 10, 400, 300);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);        
    }
}
