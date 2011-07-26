package j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class ColorCubeTest extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();

        ColorCube cube = new ColorCube(0.5d);
        Transform3D t3dy = new Transform3D();
        t3dy.rotY(Math.PI / 6);
        Transform3D t3dx = new Transform3D();
        t3dx.rotX(Math.PI / 4);
        Transform3D t3d = new Transform3D();
        t3d.mul(t3dy, t3dx);
        TransformGroup objTrans = new TransformGroup(t3d);
        objRoot.addChild(objTrans);
        objTrans.addChild(cube);
        objRoot.compile();

        return objRoot;
      }

    public ColorCubeTest() {
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
        ColorCubeTest sample = new ColorCubeTest();

        sample.setBounds( 10, 10, 240, 240);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }
}
