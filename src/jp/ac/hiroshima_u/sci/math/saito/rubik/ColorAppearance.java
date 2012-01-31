package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.Color;
import java.util.HashMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

/**
 * 色を表す文字列と、その色に対応するJava3D の Appearance オブジェクトとの
 * 対応を保持する。DiffuseColor 固定。
 * <pre>
 * "R" Red
 * "G" Green
 * "B" Blue
 * "Y" Yellow
 * "O" Orange
 * "W" White
 * </pre>
 * @author M. Saito
 */
public class ColorAppearance {
	private static final Color3f Red = new Color3f(1.0f, 0.0f, 0.0f);
	private static final Color3f Green = new Color3f(0.0f, 1.0f, 0.0f);
	private static final Color3f Blue = new Color3f(0.0f, 0.0f, 1.0f);
	private static final Color3f Yellow = new Color3f(1.0f, 1.0f, 0.0f);
	private static final Color3f Orange = new Color3f(1.0f, 0.4f, 0.0f);
	private static final Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
	private static final Color3f Grey = new Color3f(0.4f, 0.4f, 0.4f);
    private static final Color3f Black = new Color3f(0.1f, 0.1f, 0.1f);

	private static ColorAppearance me;
	private HashMap<String, Appearance> colors;
	private HashMap<String, Color3f> color3fs = makeColorMap();
	
	private Appearance getAppearance(Color3f c) {
        Material m = new Material();
        Appearance ap = new Appearance();
        m.setDiffuseColor(c);
        ap.setMaterial(m);
        return ap;
	}
	
	private ColorAppearance () {
	    colors = new HashMap<String, Appearance>();
		colors.put("R", getAppearance(Red));
        colors.put("G", getAppearance(Green));
        colors.put("B", getAppearance(Blue));
        colors.put("Y", getAppearance(Yellow));
        colors.put("O", getAppearance(Orange));
        colors.put("W", getAppearance(White));
        colors.put("", getAppearance(Grey));
		// 透明
        Material m = new Material();
        Appearance ap = new Appearance();
        m.setDiffuseColor(Black);
        ap.setMaterial(m);
        TransparencyAttributes attr=new TransparencyAttributes(TransparencyAttributes.NICEST, 0.8f);
        //attr.setTransparency(0.5f);
        ap.setTransparencyAttributes(attr);
        colors.put("T", ap);
//        ap = colors.get("");
//        ap.getMaterial().setSpecularColor(Grey);
//        ap.getMaterial().setAmbientColor(Grey);
//        ap.getMaterial().setShininess(100);
	}
	
	private static HashMap<String, Color3f> makeColorMap() {
	    HashMap<String, Color3f> map = new HashMap<String, Color3f>();
	    map.put("B", Blue);
        map.put("G", Green);
        map.put("O", Orange);
        map.put("Y", Yellow);
        map.put("R", Red);
        map.put("W", White);
	    return map;
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
	
	public Color3f getColor3f(String str) {
	    return color3fs.get(str);
	}
	
    public Color[] getAwtColor() {
        Color[] awtColors = new Color[6];
        awtColors[0] = color3fs.get("B").get();
        awtColors[1] = color3fs.get("G").get();
        awtColors[2] = color3fs.get("O").get();
        awtColors[3] = color3fs.get("Y").get();
        awtColors[4] = color3fs.get("R").get();
        awtColors[5] = color3fs.get("W").get();
        return awtColors;
    }
    public String[] getColorString() {
        String[] x = {"B","G","O","Y","R","W"};
        return x;
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
