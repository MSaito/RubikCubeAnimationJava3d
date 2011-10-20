package j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

// Beh
public class Cubie2AnimationTestNew extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Cubie[] rubik;
	private JButton buttons[][];
    private BranchGroup root;
    private CubeAnimation animation;
	
	private void setUpButton() {
		buttons = new JButton[3][];
		String[] op = {"U", "R", "F"};
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,3));
		for (int i = 0; i < 3; i++) {
			buttons[i] = new JButton[3];
			for (int j = 0; j < 3; j++) {
				buttons[i][j] = new JButton(op[i] + (j + 1));
		        panel.add(buttons[i][j]);
		        buttons[i][j].addActionListener(this);
			}
		}
        getContentPane().add(panel, BorderLayout.EAST);
	}
		
	public BranchGroup createSceneGraph() {
		BoundingSphere bs = new BoundingSphere();
        rubik = new Cubie[8];
        // ã’i
        rubik[0] = new Cubie(0.5, -0.55, +0.55, -0.55);
        rubik[1] = new Cubie(0.5, +0.55, +0.55, -0.55);
        rubik[2] = new Cubie(0.5, -0.55, +0.55, +0.55);
        rubik[3] = new Cubie(0.5, +0.55, +0.55, +0.55);
        // ‰º’i
        rubik[4] = new Cubie(0.5, -0.55, -0.55, -0.55);
        rubik[5] = new Cubie(0.5, +0.55, -0.55, -0.55);
        rubik[6] = new Cubie(0.5, -0.55, -0.55, +0.55);
        rubik[7] = new Cubie(0.5, +0.55, -0.55, +0.55);
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
        Background background = new Background(new Color3f(0.8f, 0.8f, 0.9f));
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
        Canvas3D canvas = new Canvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);
        setUpButton();
        BranchGroup scene = createSceneGraph();
        SimpleUniverse universe = new SimpleUniverse(canvas);
        // View Platform
        ViewingPlatform vp = universe.getViewingPlatform();
        vp.setNominalViewingTransform();
        Transform3D trg = createViewTransform();
        vp.getViewPlatformTransform().setTransform(trg);
        universe.addBranchGraph(scene);
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
		if (src instanceof JButton) {
			JButton bt = (JButton)src;
			animation.addCommand(Command.valueOf(bt.getText()));
		}
	}
}
