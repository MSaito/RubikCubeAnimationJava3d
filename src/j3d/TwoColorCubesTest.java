package j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class TwoColorCubesTest extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TransformGroup createCube(double size, double xpos, double ypos, double zpos) {
		ColorCube cube = new ColorCube(size);
        Transform3D t3dMove = new Transform3D();
        t3dMove.setTranslation(new Vector3d(xpos, ypos, zpos));
        TransformGroup moveTG = new TransformGroup(t3dMove);
        moveTG.addChild(cube);
        return moveTG;
	}

	public BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();

        TransformGroup cube1 = createCube(0.5, -0.55, 0, 0);
        TransformGroup cube2 = createCube(0.5, +0.55, 0, 0);
        Transform3D t3dy = new Transform3D();
        t3dy.rotY(Math.PI / 6);
        Transform3D t3dx = new Transform3D();
        t3dx.rotX(Math.PI / 4);
        Transform3D t3d = new Transform3D();
        t3d.mul(t3dy, t3dx);
        TransformGroup objTrans1 = new TransformGroup(t3d);
        objRoot.addChild(objTrans1);
        objTrans1.addChild(cube1);
        objTrans1.addChild(cube2);
        objRoot.compile();

        return objRoot;
      }

    public TwoColorCubesTest() {
        getContentPane().setLayout(new BorderLayout());

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas = new Canvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);

        BranchGroup scene = createSceneGraph();
        SimpleUniverse universe = new SimpleUniverse(canvas);

        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);
      }

    /**
     * @param args
     */
    public static void main(String[] args) {
        TwoColorCubesTest sample = new TwoColorCubesTest();

        sample.setBounds( 10, 10, 240, 240);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }
}
