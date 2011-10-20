package j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class CubeTest extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();

        CubieOld cube = new CubieOld(new Color3f[0]);
        cube.setColor(Box.RIGHT, new Color3f(0.0f, 0.0f, 1.0f));
        cube.setColor(Box.FRONT, new Color3f(1.0f, 0.0f, 0.0f));
        cube.setColor(Box.TOP, new Color3f(0.0f, 1.0f, 0.0f));
        
        Transform3D t3dy = new Transform3D();
        t3dy.rotY(Math.PI / 6);
        Transform3D t3dx = new Transform3D();
        t3dx.rotX(Math.PI / 4);
        Transform3D t3d = new Transform3D();
        t3d.mul(t3dy, t3dx);
        TransformGroup objTrans = new TransformGroup(t3d);
        objRoot.addChild(objTrans);
        objTrans.addChild(cube);
        
        // Light 1
        AmbientLight light = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
        BoundingSphere sphere = new BoundingSphere();
        light.setInfluencingBounds(sphere);
        objRoot.addChild(light);
        // Light 2
        DirectionalLight light2 = new DirectionalLight( true,
                new Color3f(1.0f, 1.0f, 1.0f),
                new Vector3f(0.0f, 0.0f, -1.0f));
        light2.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));
        objRoot.addChild(light2);
        objRoot.compile();

        return objRoot;
      }

    public CubeTest() {
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
        CubeTest sample = new CubeTest();

        sample.setBounds( 10, 10, 240, 240);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }
}
