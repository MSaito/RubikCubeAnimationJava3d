package j3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Animation2x2 extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Cubie[] rubik;
	private Transform3D viewTransform;
	private HashMap<JButton, CommandType> commandMap;
    private BranchGroup root;
    private CubeAnimation animation;
    private JTextField color;
    private JTextField command;
    private JTextField speed;
    private JButton setupButton;
    private JLabel message;
	private JButton operationButtons[][];
	private JButton vup;
	private JButton vright;
	private JButton vdown;
	private JButton vleft;
	private JButton vreset;
    private Canvas3D canvas;
	
	private void setUpButton() {
		commandMap = new HashMap<JButton, CommandType>();
		JPanel outer = new JPanel();
		//outer.setLayout(new BorderLayout());
		outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        getContentPane().add(outer, BorderLayout.EAST);
		JPanel north = new JPanel();
		north.setLayout(new GridLayout(8, 1));
		//north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
		color = new JTextField(15);
		command = new JTextField(15);
		speed = new JTextField(15);
		setupButton = new JButton("設定");
		message = new JLabel("    ");
		north.add(new JLabel("初期色の設定"));
		north.add(color);
		north.add(new JLabel("操作の設定"));
		north.add(command);
		north.add(new JLabel("速度"));
		north.add(speed);
		speed.setText("1000");
		north.add(setupButton);
		north.add(message);
		outer.add(north);
		setupButton.addActionListener(this);
        outer.add(makeOperation());
        outer.add(makeViewOperation());
        outer.add(Box.createVerticalGlue());
        outer.revalidate();
	}

	private JPanel makeOperation() {
		operationButtons = new JButton[3][];
		String pre = "<html>";
		String[] op = {"U", "R", "F"};
		String[] num = {"", "<sup>2</sup>", "<sup>-1</sup>"};
        JPanel operationPanel = new JPanel();
        operationPanel.setLayout(new GridLayout(3, 3, 2, 2));
		for (int i = 0; i < 3; i++) {
			operationButtons[i] = new JButton[3];
			for (int j = 0; j < 3; j++) {
				operationButtons[i][j] = new JButton(pre + op[i] + num[j]);
		        operationPanel.add(operationButtons[i][j]);
		        operationButtons[i][j].addActionListener(this);
		        commandMap.put(operationButtons[i][j], Command.getCommandType(op[i] + (j+1)));
			}
		}
		return operationPanel;
	}
	
	private JPanel makeViewOperation() {
        JPanel viewOperation = new JPanel();
        viewOperation.setLayout(new BoxLayout(viewOperation, BoxLayout.Y_AXIS));
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        vup = new JButton("↑");
        commandMap.put(vup, Command.getCommandType("VIEW_UP"));
        top.add(Box.createHorizontalGlue());
        top.add(vup);
        top.add(Box.createHorizontalGlue());
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        vleft = new JButton("←");
        vright = new JButton("→");
        vreset = new JButton("・");
        center.add(vleft);
        center.add(vreset);
        center.add(vright);
        commandMap.put(vright, Command.getCommandType("VIEW_RIGHT"));
        commandMap.put(vleft, Command.getCommandType("VIEW_LEFT"));
        commandMap.put(vreset, Command.getCommandType("VIEW_RESET"));
        vdown = new JButton("↓");
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        bottom.add(Box.createHorizontalGlue());
        bottom.add(vdown);
        commandMap.put(vdown, Command.getCommandType("VIEW_DOWN"));
        bottom.add(Box.createHorizontalGlue());
        viewOperation.add(top);
        viewOperation.add(center);
        viewOperation.add(bottom);
		return viewOperation;
	}

	private Cubie[] makeRubik() {
        Cubie[] rubik = new Cubie[8];
        // 上段
        rubik[0] = new Cubie(0.5, -0.55, +0.55, -0.55);
        rubik[1] = new Cubie(0.5, +0.55, +0.55, -0.55);
        rubik[2] = new Cubie(0.5, -0.55, +0.55, +0.55);
        rubik[3] = new Cubie(0.5, +0.55, +0.55, +0.55);
        // 下段
        rubik[4] = new Cubie(0.5, -0.55, -0.55, -0.55);
        rubik[5] = new Cubie(0.5, +0.55, -0.55, -0.55);
        rubik[6] = new Cubie(0.5, -0.55, -0.55, +0.55);
        rubik[7] = new Cubie(0.5, +0.55, -0.55, +0.55);
        return rubik;
	}
	
	private BranchGroup createSceneGraph() {
		BoundingSphere bs = new BoundingSphere();
        rubik = makeRubik();
	    Transform3D axis = new Transform3D();
	    axis.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        root = new BranchGroup();
        for (Cubie c: rubik) {
        	root.addChild(c);
        }
        BoundingSphere bounds=new BoundingSphere(
        		new Point3d(),Double.POSITIVE_INFINITY);
        animation = new CubeAnimation(rubik);
        animation.setSchedulingBounds(bounds);
        root.addChild(animation);
        setColor("BBBBGGGGOOOOYYYYRRRRWWWW");
        Background background = new Background(new Color3f(0.6f, 0.6f, 0.8f));
        background.setApplicationBounds(bs);
        root.addChild(background);
        //root.compile();
        return root;
      }

	public Transform3D createViewTransform() {
        Transform3D t3dy = new Transform3D();
        t3dy.rotY(Math.PI / 6);
        Transform3D t3dx = new Transform3D();
        t3dx.rotX(-Math.PI / 6);
        Transform3D t3d = new Transform3D();
        t3d.mul(t3dy, t3dx);
        Transform3D viewMove3d = new Transform3D();
        viewMove3d.set(new Vector3d(0.0, 0.0, 6.0));
        t3d.mul(viewMove3d);
        return t3d;
	}
	
    public Animation2x2() {
        getContentPane().setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);
        setUpButton();        
        initialize(canvas);
        
      }

	private void initialize(Canvas3D canvas) {
		BranchGroup scene = createSceneGraph();
        SimpleUniverse universe = new SimpleUniverse(canvas);
        universe.addBranchGraph(scene);
        // View Platform
        ViewingPlatform vp = universe.getViewingPlatform();
        vp.setNominalViewingTransform();
        viewTransform = createViewTransform();
        vp.getViewPlatformTransform().setTransform(viewTransform);
        //vp.setCapability(ViewingPlatform.ALLOW_CHILDREN_READ);
        //vp.setCapability(ViewingPlatform.ALLOW_CHILDREN_WRITE);
        // Light
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        AmbientLight ambient = new AmbientLight(lightColor);
        Vector3f direction = new Vector3f(-2.0f, -2.0f, -2.0f);
        DirectionalLight light = new DirectionalLight(lightColor, direction);
        BoundingSphere bounds = new BoundingSphere(
        		new Point3d(),Double.POSITIVE_INFINITY);
        ambient.setInfluencingBounds(bounds);
        light.setInfluencingBounds(bounds);
        BranchGroup br = new BranchGroup();
        br.addChild(ambient);
        br.addChild(light);
        universe.addBranchGraph(br);
	}

    /**
     * @param args
     */
    public static void main(String[] args) {
        Animation2x2 sample = new Animation2x2();
        sample.setBounds( 10, 10, 1000, 1000);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }
    
	@Override
	public void actionPerformed(ActionEvent event) {
		Object src = event.getSource();
		if (src == setupButton) {
			setColor(color.getText());
			setOperation(command.getText());
			setSpeed(speed.getText());
		} else if (src instanceof JButton) {
			JButton bt = (JButton)src;
			CommandType t = commandMap.get(bt);
			if (t != null) {
				animation.addCommand(new Command(t, ""));
				animation.start();
			}
		}
	}

	private void setOperation(String text) {
		if (text.length() == 0) {
			return;
		}
		text = convertText(text);
		animation.stop();
		for (int i = 0; i < text.length(); i += 2) {
			String s = text.substring(i, i+2);
			CommandType t = commandMap.get(s);
			if (t != null) {
				animation.addCommand(new Command(t, ""));
			}
		}
		command.setText("");
		animation.start();
	}

	private void setColor(String text) {
		if (text.length() == 0) {
			return;
		}
		if (text.length() < 24) {
			message.setForeground(Color.RED);
			message.setText("文字列が短かすぎます");
			return;
		}
		animation.stop();
		animation.addCommand(new Command(CommandType.COLOR, text));
		animation.start();
		color.setText("");
	}
	
	private void setSpeed(String speed) {
		if (speed.length() == 0) {
			return;
		}
		animation.stop();
		animation.addCommand(new Command(CommandType.SPEED, speed));
		animation.start();
	}

	private String convertText(String text) {
		StringBuilder sb = new StringBuilder();
		char pre = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == 'U' || c == 'R' || c == 'F') {
				if (pre == 0) {
					pre = c;
				} else {
					sb.append(pre);
					sb.append('1');
					pre = c;
				}
			} else if (c == '1' || c == '2' || c == '3') {
				sb.append(pre);
				sb.append(c);
				pre = 0;
			}
		}
		if (pre != 0) {
			sb.append(pre);
			sb.append('1');
			pre = 0;
		}
		return sb.toString();
	}

}
