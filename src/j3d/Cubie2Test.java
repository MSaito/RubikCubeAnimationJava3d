package j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Cubie2Test extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();
        Cubie2[] Rubik = new Cubie2[8];
        // â∫íi
        Rubik[0] = new Cubie2(0.5, -0.55, -0.55, -0.55);
        Rubik[1] = new Cubie2(0.5, +0.55, -0.55, -0.55);
        Rubik[2] = new Cubie2(0.5, -0.55, -0.55, +0.55);
        Rubik[3] = new Cubie2(0.5, +0.55, -0.55, +0.55);
        // è„íi
        Rubik[4] = new Cubie2(0.5, -0.55, +0.55, -0.55);
        Rubik[5] = new Cubie2(0.5, +0.55, +0.55, -0.55);
        Rubik[6] = new Cubie2(0.5, -0.55, +0.55, +0.55);
        Rubik[7] = new Cubie2(0.5, +0.55, +0.55, +0.55);
        Transform3D t3d = new Transform3D();
        //t3d.mul(t3dy, t3dx);
        TransformGroup objTrans1 = new TransformGroup(t3d);
        objRoot.addChild(objTrans1);
        for (Cubie2 c: Rubik) {
        	objTrans1.addChild(c);
        }
        Background background = new Background(new Color3f(0.8f, 0.8f, 0.9f));
        background.setApplicationBounds(new BoundingSphere());
        objTrans1.addChild(background);
        objRoot.compile();
        return objRoot;
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
	
    public Cubie2Test() {
        getContentPane().setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);

        BranchGroup scene = createSceneGraph();
        SimpleUniverse universe = new SimpleUniverse(canvas);
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
        Cubie2Test sample = new Cubie2Test();

        sample.setBounds( 10, 10, 240, 240);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }
}
