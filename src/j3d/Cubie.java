package j3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Box;

public class Cubie extends Box {
	public static final Color3f defaultColor = new Color3f(1.0f, 1.0f, 1.0f);
	
	public Cubie () {
		super();
	}
	
	public Cubie(float size) {
		super(size, size, size, Box.GENERATE_NORMALS, null);
	}
	
	public void setColor(int face, Color3f color) {
		Shape3D shape = this.getShape(face);
		Appearance ap = (Appearance)shape.getAppearance().cloneNodeComponent(true);
		Material mat = new Material();
		mat.setDiffuseColor(color);
		ap.setMaterial(mat);
		shape.setAppearance(ap);
	}
	
	public Cubie (Color3f[] faceColor) {
		super(0.5f, 0.5f, 0.5f, Box.GENERATE_NORMALS, null);
		final int[] face = {Box.FRONT, Box.RIGHT, Box.TOP, Box.LEFT, Box.BOTTOM, Box.BACK};
		for (int i = 0; i < faceColor.length; i++) {
			setColor(face[i], faceColor[i]);
		}
		for (int i = faceColor.length; i < 6; i++) {
			setColor(face[i], defaultColor);
		}
	}

}
