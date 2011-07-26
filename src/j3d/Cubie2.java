package j3d;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.ColorCube;

public class Cubie2 extends TransformGroup {
	//private double size;
	//private double x;
	//private double y;
	//private double z;
	private ColorCube cube;
	
	public Cubie2(double size, double x, double y, double z) {
		super();
		this.cube = new ColorCube(size);
        Transform3D t3dMove = new Transform3D();
        t3dMove.setTranslation(new Vector3d(x, y, z));
        setTransform(t3dMove);
        addChild(cube);
	}

}
