package j3d;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;

/**
 * ルービックキューブを構成する小さな立方体
 * @author M. Saito
 */
public class Cubie extends TransformGroup {
	private static final String[] defaultColor = {"B", "G", "O", "Y", "R", "W"};
	private static final int[] faces = {Box.TOP, Box.FRONT, Box.RIGHT, Box.BACK, Box.LEFT, Box.BOTTOM};
	private ColorAppearance appearance;
	private Vector3d currentAxis;
	private Vector3d ax;
	private Vector3d ay;
	private Vector3d az;
	private Matrix3d mat;
	private Vector3d initPos;
	private double angle;
	private Box cube;
	
	/**
	 * コンストラクタ。一辺のサイズと中心部の位置から立方体を作成する。
	 * @param size 一辺の長さ
	 * @param x 中心のx座標
	 * @param y 中心のy座標
	 * @param z 中心のz座標
	 */
	public Cubie(double size, double x, double y, double z) {
		this(size, x, y, z, defaultColor);
	}
	
	/**
	 * コンストラクタ。一辺のサイズ、中心部の位置、各面の色から立方体を作成する。
     * @param size 一辺の長さ
     * @param x 中心のx座標
     * @param y 中心のy座標
     * @param z 中心のz座標
	 * @param colors
	 */
	public Cubie(double size, double x, double y, double z, String[] colors) {
		cube = new Box((float)size, (float)size, (float)size, Box.GENERATE_NORMALS, null);
		appearance = ColorAppearance.getColorAppearance();
		for (int i = 0; i < faces.length; i++) {
			Shape3D shape = cube.getShape(faces[i]);
			shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
			shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			shape.setAppearance(appearance.get(colors[i]));
		}
		initPos = new Vector3d(x, y, z);
        Transform3D t3dMove = new Transform3D();
        t3dMove.setTranslation(initPos);
        setTransform(t3dMove);
        addChild(cube);
        setCapability(ALLOW_TRANSFORM_READ);
        setCapability(ALLOW_TRANSFORM_WRITE);
        initAxis();
	}
	
	/**
	 * 初期位置に戻す。各面の色は変更しない。
	 */
	public void reset() {
        Transform3D t3dMove = new Transform3D();
        t3dMove.setTranslation(initPos);
        setTransform(t3dMove);
        initAxis();
	}
	
	/**
	 * 面の色を設定する
	 * @param face 面の指定 Box.TOP など
	 * @param color 色を表す文字列
	 */
	private void setAppearance(int face, String color) {
		Shape3D shape = cube.getShape(face);
		shape.setAppearance(appearance.get(color));
	}
	
	/**
	 * 立方体の各面の色を設定する
	 * <pre>
	 * colors[0] TOP の色
     * colors[1] FRONT の色
     * colors[2] RIGHT の色
     * colors[3] BACK の色
     * colors[4] LEFT の色
     * colors[5] BOTTOM の色
     * </pre>
	 * @param colors 色を表す文字列の配列
	 */
	public void setColor(String[] colors) {		
		setAppearance(Box.TOP, colors[0]);
		setAppearance(Box.FRONT, colors[1]);
		setAppearance(Box.RIGHT, colors[2]);
		setAppearance(Box.BACK, colors[3]);
		setAppearance(Box.LEFT, colors[4]);
		setAppearance(Box.BOTTOM, colors[5]);
	}
	
	/**
	 * グローバル座標を初期化する
	 */
	private void initAxis() {
		ax = new Vector3d(1, 0, 0);
		ay = new Vector3d(0, 1, 0);
		az = new Vector3d(0, 0, 1);
	}

	/**
	 * 一連の回転アニメーションの最初に行う設定
	 * @param com コマンドタイプ、回転方向を表す
	 * @param angle アニメーション一コマで回転する角度
	 */
	public void setupRotation(CommandType com, double angle) {
		double sin;
		double cos;
		switch (com) {
		case U1:
		case U2:
		case U3:
			this.angle = angle;
			sin = Math.sin(this.angle);
			cos = Math.cos(this.angle);
			mat = new Matrix3d(cos, 0, -sin,
					           0,   1,    0,
					           sin, 0, cos);
			currentAxis = ay;
			break;
		case R1:
		case R2:
		case R3:
			this.angle = angle;
			sin = Math.sin(-this.angle);
			cos = Math.cos(-this.angle);
			mat = new Matrix3d(1,   0,    0,
					           0, cos, -sin,
					           0, sin,  cos);
			currentAxis = ax;
			break;
		case F1:
		case F2:
		case F3:
		default:
			this.angle = angle;
			sin = Math.sin(-this.angle);
			cos = Math.cos(-this.angle);
			currentAxis = ay;
			mat = new Matrix3d(cos, -sin, 0,
					           sin,  cos, 0,
					             0,    0, 1);
			currentAxis = az;
		}
	}
	
	/**
	 * アニメーション終了時に、グローバル座標を変更する。
	 * 途中で変更する必要はない。
	 * @param com
	 */
	public void teardownRotation(CommandType com) {
		Vector3d tmp;
		switch (com) {
		case U1:
			tmp = az;
			az = ax;
			ax = new Vector3d(-tmp.x, -tmp.y, -tmp.z);
			break;
		case U2:
			az = new Vector3d(-az.x, -az.y, -az.z);
			ax = new Vector3d(-ax.x, -ax.y, -ax.z);
			break;
		case U3:
			tmp = az;
			az = new Vector3d(-ax.x, -ax.y, -ax.z);
			ax = tmp;
			break;
		case R1:
			tmp = ay;
			ay = az;
			az = new Vector3d(-tmp.x, -tmp.y, -tmp.z);
			break;
		case R2:
			ay = new Vector3d(-ay.x, -ay.y, -ay.z);
			az = new Vector3d(-az.x, -az.y, -az.z);
			break;
		case R3:
			tmp = ay;
			ay = new Vector3d(-az.x, -az.y, -az.z);
			az = tmp;
			break;
		case F1:
			tmp = ax;
			ax = ay;
			ay = new Vector3d(-tmp.x, -tmp.y, -tmp.z);
			break;
		case F2:
			ax = new Vector3d(-ax.x, -ax.y, -ax.z);
			ay = new Vector3d(-ay.x, -ay.y, -ay.z);
			break;
		case F3:
		default:
			tmp = ax;
			ax = new Vector3d(-ay.x, -ay.y, -ay.z);
			ay = tmp;
		}
	}
	
	/**
	 * アニメーションの一コマ分の回転操作
	 */
	public void rotate() {
		AxisAngle4d axis = new AxisAngle4d(currentAxis.x, currentAxis.y, currentAxis.z, -angle);
		Transform3D rot = new Transform3D();
		Matrix3d m = new Matrix3d();
		Matrix3d n = new Matrix3d();
		Vector3d v = new Vector3d();
		rot.setRotation(axis);
		rot.get(n);
		Transform3D tr = new Transform3D();
		getTransform(tr);
		tr.get(m, v);
		mat.transform(v);
		m.mul(n);
		setTransform(new Transform3D(m, v, 1));
	}
}
