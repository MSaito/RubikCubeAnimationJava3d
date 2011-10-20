package j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
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

// Beh
public class Cubie2AnimationTestNew extends JFrame implements ActionListener {
	private static final Color3f Red = new Color3f(1.0f, 0.0f, 0.0f);
	private static final Color3f Green = new Color3f(0.0f, 1.0f, 0.0f);
	private static final Color3f Blue = new Color3f(0.0f, 0.0f, 1.0f);
	private static final Color3f Yellow = new Color3f(1.0f, 1.0f, 0.0f);
	private static final Color3f Orange = new Color3f(1.0f, 0.4f, 0.0f);
	private static final Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
	private static final Color3f Black = new Color3f(0.0f, 0.0f, 0.0f);

	private static final long serialVersionUID = 1L;
	private static final int[] colorToPos = {0, 1, 2, 3, 2, 3, 6, 7, 3, 1, 7, 5,
			1, 0, 5, 4, 0, 2, 4, 6, 6, 7, 4, 5}; 
	private Color3f[][] rubikColors;
	private Cubie[] rubik;
	private JButton buttons[][];
    private BranchGroup root;
    private CubeAnimation animation;
    private JTextField color;
    private JTextField command;
    private JButton colorButton;
    private JButton operationButton;
    private JLabel message;
    private Canvas3D canvas;
	
	private void setUpButton() {
		JPanel outer = new JPanel();
		outer.setLayout(new BorderLayout());
        getContentPane().add(outer, BorderLayout.EAST);
		JPanel north = new JPanel();
		north.setLayout(new GridLayout(7, 1));
		color = new JTextField(15);
		colorButton = new JButton("設定");
		command = new JTextField(15);
		operationButton = new JButton("設定");
		message = new JLabel();
		north.add(new JLabel("初期色の設定"));
		north.add(color);
		north.add(colorButton);
		north.add(new JLabel("操作の設定"));
		north.add(command);
		north.add(operationButton);
		north.add(message);
		outer.add(north, BorderLayout.NORTH);
		colorButton.addActionListener(this);
		operationButton.addActionListener(this);
		buttons = new JButton[3][];
		String[] op = {"U", "R", "F"};
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 2, 2));
		for (int i = 0; i < 3; i++) {
			buttons[i] = new JButton[3];
			for (int j = 0; j < 3; j++) {
				buttons[i][j] = new JButton(op[i] + (j + 1));
		        panel.add(buttons[i][j]);
		        buttons[i][j].addActionListener(this);
			}
		}
        outer.add(panel, BorderLayout.CENTER);
	}

	private Cubie[] makeRubik() {
        Cubie[] rubik = new Cubie[8];
        // 上段
        rubik[0] = new Cubie(0.5, -0.55, +0.55, -0.55, rubikColors[0]);
        rubik[1] = new Cubie(0.5, +0.55, +0.55, -0.55, rubikColors[1]);
        rubik[2] = new Cubie(0.5, -0.55, +0.55, +0.55, rubikColors[2]);
        rubik[3] = new Cubie(0.5, +0.55, +0.55, +0.55, rubikColors[3]);
        // 下段
        rubik[4] = new Cubie(0.5, -0.55, -0.55, -0.55, rubikColors[4]);
        rubik[5] = new Cubie(0.5, +0.55, -0.55, -0.55, rubikColors[5]);
        rubik[6] = new Cubie(0.5, -0.55, -0.55, +0.55, rubikColors[6]);
        rubik[7] = new Cubie(0.5, +0.55, -0.55, +0.55, rubikColors[7]);
        return rubik;
	}
	
	private BranchGroup createSceneGraph() {
		BoundingSphere bs = new BoundingSphere();
        rubik = makeRubik();
        // start animation
	    Transform3D axis = new Transform3D();
	    axis.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        // end animation
        //Transform3D t3d = new Transform3D();
        //t3d.mul(t3dy, t3dx);
        //rootTrg = new TransformGroup(t3d);
        root = new BranchGroup();
        for (Cubie c: rubik) {
        	root.addChild(c);
        }
        BoundingSphere bounds=new BoundingSphere(
        		new Point3d(),Double.POSITIVE_INFINITY);
        animation = new CubeAnimation(rubik);
        animation.setSchedulingBounds(bounds);
        root.addChild(animation);
        Background background = new Background(new Color3f(0.6f, 0.6f, 0.8f));
        background.setApplicationBounds(bs);
        root.addChild(background);
        root.compile();
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
	
    public Cubie2AnimationTestNew() {
        getContentPane().setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);
        rubikColors = makeRubikColors("BBBBGGGGOOOOYYYYRRRRWWWW");
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
        Transform3D trg = createViewTransform();
        vp.getViewPlatformTransform().setTransform(trg);
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
        Cubie2AnimationTestNew sample = new Cubie2AnimationTestNew();
        sample.setBounds( 10, 10, 1000, 1000);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }
    
	@Override
	public void actionPerformed(ActionEvent event) {
		Object src = event.getSource();
		if (src == colorButton) {
			setColor(color.getText());
		} else if (src == operationButton) {
			setOperation(command.getText());
		} else if (src instanceof JButton) {
			JButton bt = (JButton)src;
			animation.addCommand(Command.valueOf(bt.getText()));
		}
	}

	private void setOperation(String text) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	private Color3f[][] makeRubikColors(String str) {
		Color3f[][] result = new Color3f[8][];
		for (int i = 0; i < result.length; i++) {
			result[i] = new Color3f[6];
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = Black;
			}
		}
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			Color3f color = charToColor(c);
			int pos = colorToPos[i];
			int face = i / 4;
			result[pos][face] = color;
		}
		return result;
	}
	
	private	Color3f charToColor(char c) {
		switch (c) {
		case 'B':
			return Blue;
		case 'G':
			return Green;
		case 'O':
			return Orange;
		case 'Y':
			return Yellow;
		case 'R':
			return Red;
		case 'W':
			return White;
		default:
			return Black;
		}
	}
	
	private void setColor(String text) {
		if (text.length() < 24) {
			message.setText("文字列が短かすぎます");
		}
		rubikColors = makeRubikColors(text);
		initialize(canvas);
	}
}
