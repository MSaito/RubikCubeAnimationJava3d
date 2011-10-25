package j3d;

import java.util.HashMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.vecmath.Color3f;

public class ColorAppearance {
	private static final Color3f Red = new Color3f(1.0f, 0.0f, 0.0f);
	private static final Color3f Green = new Color3f(0.0f, 1.0f, 0.0f);
	private static final Color3f Blue = new Color3f(0.0f, 0.0f, 1.0f);
	private static final Color3f Yellow = new Color3f(1.0f, 1.0f, 0.0f);
	private static final Color3f Orange = new Color3f(1.0f, 0.4f, 0.0f);
	private static final Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
	private static final Color3f Grey = new Color3f(0.1f, 0.1f, 0.1f);

	private static ColorAppearance me;
	private HashMap<String, Appearance> colors;
	
	private ColorAppearance () {
		colors = new HashMap<String, Appearance>();
		Appearance ap;
		Material m;
		m = new Material();
		ap = new Appearance();
		m.setDiffuseColor(Red);
		ap.setMaterial(m);
		colors.put("R", ap);
		m = new Material();
		ap = new Appearance();
		m.setDiffuseColor(Green);
		ap.setMaterial(m);
		colors.put("G", ap);
		m = new Material();
		ap = new Appearance();
		m.setDiffuseColor(Blue);
		ap.setMaterial(m);
		colors.put("B", ap);
		m = new Material();
		ap = new Appearance();
		m.setDiffuseColor(Yellow);
		ap.setMaterial(m);
		colors.put("Y", ap);
		m = new Material();
		ap = new Appearance();
		m.setDiffuseColor(Orange);
		ap.setMaterial(m);
		colors.put("O", ap);
		m = new Material();
		ap = new Appearance();
		m.setDiffuseColor(White);
		ap.setMaterial(m);
		colors.put("W", ap);
		m = new Material();
		ap = new Appearance();
		m.setDiffuseColor(Grey);
		ap.setMaterial(m);
		colors.put("", ap);
	}
	
	public static synchronized ColorAppearance getColorAppearance() {
		if (me == null) {
			me = new ColorAppearance();
		}
		return me;
	}

	public Appearance get(String str) {
		return colors.get(str);
	}
	
	public Appearance[] getDefault() {
		Appearance[] result = new Appearance[6];
		result[0] = get("B");
		result[1] = get("G");
		result[2] = get("O");
		result[3] = get("Y");
		result[4] = get("R");
		result[5] = get("W");
		return result;
	}
}
