package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.util.logging.Logger;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;

/**
 * ルービックキューブを構成する小さな立方体 ポイントは ax, ay, az が親オブジェクト（設定していないが、全体としてのルービックキューブ）
 * におけるx座標、y座標、z座標を表すということ
 * 
 * @author M. Saito
 */
public class Cubie extends TransformGroup {
    private final Logger logger = Logger.getLogger(Cubie.class
            .getCanonicalName());
    private static final String[] defaultColor = { "B", "G", "O", "Y", "R", "W" };
    private static final int[] faces = { Box.TOP, Box.FRONT, Box.RIGHT,
            Box.BACK, Box.LEFT, Box.BOTTOM };
    private ColorAppearance appearance;
    private Vector3d currentAxis;
    private Vector3d ax;
    private Vector3d ay;
    private Vector3d az;
    private Matrix3d matPos;
    private Matrix3d matRot;
    private Matrix3d inverseRot;
    private Vector3d initPos;
    private Box cube;

    /**
     * コンストラクタ。一辺のサイズと中心部の位置から立方体を作成する。
     * 
     * @param size
     *            一辺の長さ
     * @param x
     *            中心のx座標
     * @param y
     *            中心のy座標
     * @param z
     *            中心のz座標
     */
    public Cubie(double size, double x, double y, double z) {
        this(size, x, y, z, defaultColor);
    }

    /**
     * コンストラクタ。一辺のサイズ、中心部の位置、各面の色から立方体を作成する。
     * 
     * @param size
     *            一辺の長さ
     * @param x
     *            中心のx座標
     * @param y
     *            中心のy座標
     * @param z
     *            中心のz座標
     * @param colors
     */
    public Cubie(double size, double x, double y, double z, String[] colors) {
        cube = new Box((float) size, (float) size, (float) size,
                Box.GENERATE_NORMALS, null);
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
     * 
     * @param face
     *            面の指定 Box.TOP など
     * @param color
     *            色を表す文字列
     */
    private void setAppearance(int face, String color) {
        Shape3D shape = cube.getShape(face);
        shape.setAppearance(appearance.get(color));
    }

    /**
     * 立方体の各面の色を設定する
     * 
     * <pre>
     * colors[0] TOP の色
     * colors[1] FRONT の色
     * colors[2] RIGHT の色
     * colors[3] BACK の色
     * colors[4] LEFT の色
     * colors[5] BOTTOM の色
     * </pre>
     * 
     * @param colors
     *            色を表す文字列の配列
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
     * 親オブジェクトからみた座標軸を初期化する
     */
    private void initAxis() {
        ax = new Vector3d(1, 0, 0);
        ay = new Vector3d(0, 1, 0);
        az = new Vector3d(0, 0, 1);
    }

    /**
     * 一連の回転アニメーションの最初に行う設定
     * 
     * @param type
     *            コマンドタイプ、回転方向を表す
     * @param angle
     *            アニメーション一コマで回転する角度
     */
    public void setupRotation(CommandType type, double angle, double totalAngle) {
        logger.fine("type:" + type);
        logger.fine("angle:" + angle);
        logger.fine("totalAngle:" + totalAngle);
        // cubie の中心位置の移動行列 matPos および回転軸を設定する
        if (type.isUp()) {
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            matPos = new Matrix3d(cos, 0, -sin, 0, 1, 0, sin, 0, cos);
            currentAxis = ay;
        } else if (type.isRight()) {
            double sin = Math.sin(-angle);
            double cos = Math.cos(-angle);
            matPos = new Matrix3d(1, 0, 0, 0, cos, -sin, 0, sin, cos);
            currentAxis = ax;
        } else if (type.isFront()) {
            double sin = Math.sin(-angle);
            double cos = Math.cos(-angle);
            matPos = new Matrix3d(cos, -sin, 0, sin, cos, 0, 0, 0, 1);
            currentAxis = az;
        }
        // cubie の回転行列 matRot を設定する
        AxisAngle4d axis = new AxisAngle4d(currentAxis.x, currentAxis.y,
                currentAxis.z, -angle);
        Transform3D rot = new Transform3D();
        rot.setRotation(axis);
        matRot = new Matrix3d();
        rot.get(matRot);
        //matRot.set(axis);
        // 座標軸の回転行列 inverseMat を設定する
        axis = new AxisAngle4d(currentAxis.x, currentAxis.y,
                currentAxis.z, totalAngle);
        inverseRot = new Matrix3d();
        inverseRot.set(axis);
    }

    /**
     * アニメーション終了時に、座標軸を変更する。 途中で変更する必要はない。
     * 
     * @param type
     */
    public void teardownRotation(CommandType type) {
        inverseRot.transform(ax);
        inverseRot.transform(ay);
        inverseRot.transform(az);
    }

    /**
     * アニメーションの1コマ分の回転操作
     */
    public void rotate() {
        Matrix3d oldRot = new Matrix3d();
        Vector3d oldPos = new Vector3d();
        Transform3D tr = new Transform3D();
        getTransform(tr);
        tr.get(oldRot, oldPos);
        matPos.transform(oldPos);
        oldRot.mul(matRot);
        setTransform(new Transform3D(oldRot, oldPos, 1));
    }
}
