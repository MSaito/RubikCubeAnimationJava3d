package j3d;

import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Box;

public class MiniCube extends Group {
	private static final float cubieSize = 0.5f;
	private static final float halfSize = cubieSize / 2.0f;
	private static final Color3f Red = new Color3f(1.0f, 0.0f, 0.0f);
	private static final Color3f Green = new Color3f(0.0f, 1.0f, 0.0f);
	private static final Color3f Blue = new Color3f(0.0f, 0.0f, 1.0f);
	private static final Color3f Yellow = new Color3f(1.0f, 1.0f, 0.0f);
	private static final Color3f Orange = new Color3f(1.0f, 0.5f, 0.2f);
	private static final Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
	private static final int frontIndex[] = {0, 1, 2, 3};
	private static final int rightIndex[] = {1, 3, 5, 7};
	private static final int topIndex[] = {0, 1, 4, 5};
	private static final int backIndex[] = {4, 5, 6, 7};
	private static final int leftIndex[] = {0, 2, 4, 6};
	private static final int bottomIndex[] = {2, 3, 6, 7};
	private static MiniCube adam = null;

	private Cubie[] cubies;
	private TransformGroup[] trGrp;

	
	public MiniCube () {
		trGrp = new TransformGroup[8];
		cubies = new Cubie[8];
		for (int i = 0; i < 8; i++) {
			adam.cubies[i] = new Cubie(cubieSize);
		}
		moveInitial(this);
	}
	
	public void moveInitial(Group root) {
		Vector3f[] vec = new Vector3f[8];
		for (int i : rightIndex) {
			vec[i] = new Vector3f(halfSize, 0, 0);
		}
		for (int i : leftIndex) {
			vec[i] = new Vector3f(- halfSize, 0, 0);
		}
		// ‡¬
		for (int i : topIndex) {
			vec[i] = add(vec[i], 0, halfSize, 0);
		}
		for (int i : bottomIndex) {
			vec[i] = add(vec[i], 0, - halfSize, 0);
		}
		for (int i : frontIndex) {
			vec[i] = add(vec[i], 0, 0, halfSize);
			Transform3D trans = new Transform3D();
			trans.setTranslation(vec[i]);
			trGrp[i].setTransform(trans);
			trGrp[i].addChild(cubies[i]);
		}
		for (int i : backIndex) {
			vec[i] = add(vec[i], 0, 0, - halfSize);
			Transform3D trans = new Transform3D();
			trans.setTranslation(vec[i]);
			trGrp[i].setTransform(trans);
			trGrp[i].addChild(cubies[i]);
		}
		for (TransformGroup trg : trGrp) {
			root.addChild(trg);
		}
	}

	public static MiniCube getAdam() {
		if (adam == null) {
			adam = new MiniCube();
			for (int i : frontIndex) {
				adam.cubies[i].setColor(Box.FRONT, Red);
			}
			for (int i : rightIndex) {
				adam.cubies[i].setColor(Box.RIGHT, Green);
			}
			for (int i : topIndex) {
				adam.cubies[i].setColor(Box.TOP, Blue);
			}
			for (int i : backIndex) {
				adam.cubies[i].setColor(Box.BACK, Yellow);
			}
			for (int i : leftIndex) {
				adam.cubies[i].setColor(Box.LEFT, Orange);
			}
			for (int i : bottomIndex) {
				adam.cubies[i].setColor(Box.BOTTOM, White);
			}			
		}
		return adam;
	}
	
	private static Vector3f add(Vector3f dest, float x, float y, float z) {
		float xx = dest.x + x;
		float yy = dest.y + y;
		float zz = dest.z + z;
		return new Vector3f(xx, yy, zz);
	}
}
