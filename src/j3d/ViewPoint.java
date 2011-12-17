package j3d;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

/**
 * 視点変更のクラス
 * 視点方向はz軸のマイナス方向
 * baseTransform 複数回の回転を超えて累積していく
 * fixedTransform 正面ではなくちょっとずれてみるためのもの。
 * viewTransform baseTransform のあと、fixedTransform でずれた視点。
 * 
 * @author M. Saito
 */
public class ViewPoint {
    private static final Logger logger = Logger.getLogger(ViewPoint.class.getCanonicalName());
    private final double viewAngleX = -Math.PI / 8;
    private final double viewAngleY = Math.PI / 8;
    private double viewDistance;
    private Transform3D baseTransform;
    private Transform3D viewTransform;
    private Transform3D fixedTransform;
    private Transform3D saveBase;
    private TransformGroup parent;
    private Transform3D rotateTransform = new Transform3D();
    private Vector3d ax;
    private Vector3d ay;
    private Vector3d az;

    /**
     * コンストラクタ
     * @param distance 原点からの距離
     */
    public ViewPoint(double distance) {
        this.viewDistance = distance;
        initAxis();
        fixedTransform = makeFixedTransform();
        baseTransform = new Transform3D();
        viewTransform = new Transform3D(baseTransform);
        viewTransform.mul(fixedTransform);
        saveBase = new Transform3D(baseTransform);
    }

    /**
     * 座標軸の初期化
     */
    private void initAxis() {
        ax = new Vector3d(1, 0, 0);
        ay = new Vector3d(0, 1, 0);
        az = new Vector3d(0, 0, 1);
    }

    /**
     * 視点の親ノードであるTransformGroupの情報が必要
     * なのであとからもらう
     * @param parent
     */
    public void setParent(TransformGroup parent) {
        this.parent = parent;
    }

    /**
     * 視点のTransform3D情報を返す
     * @return
     */
    public Transform3D getTransform3D() {
        return viewTransform;
    }

    /**
     * 視点を初期位置にリセットする
     */
    public void reset() {
        initAxis();
        fixedTransform = makeFixedTransform();
        baseTransform = new Transform3D(saveBase);
        viewTransform = new Transform3D(baseTransform);
        viewTransform.mul(fixedTransform);
        parent.setTransform(viewTransform);
    }

    /**
     * 一連の視点移動処理の開始時点で必要な設定をする
     * @param type 視点の移動方向
     * @param angle アニメーションの一コマで動く角度
     */
    public void setupRotate(CommandType type, double angle) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("ax:" + ax);
            logger.fine("ay:" + ay);
            logger.fine("az:" + az);
        }
        if (type == CommandType.VIEW_LEFT || type == CommandType.VIEW_RIGHT) {
            rotateTransform = new Transform3D();
            AxisAngle4d axisY = new AxisAngle4d(ay.x, ay.y, ay.z, angle);
            rotateTransform.setRotation(axisY);
        } else if (type == CommandType.VIEW_DOWN || type == CommandType.VIEW_UP) {
            rotateTransform = new Transform3D();
            AxisAngle4d axisX = new AxisAngle4d(-ax.x, -ax.y, -ax.z, -angle);
            rotateTransform.setRotation(axisX);
        }
    }

    /**
     * 逆行列を作って　ax, ay を変換する
     * @return
     */
    private Transform3D makeFixedTransform() {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("ax:" + ax);
            logger.fine("ay:" + ay);
            logger.fine("az:" + az);
        }
        AxisAngle4d axisX = new AxisAngle4d(1.0, 0.0, 0.0, viewAngleX);
        AxisAngle4d axisY = new AxisAngle4d(0.0, 1.0, 0.0, viewAngleY);
        Transform3D t3dx = new Transform3D();
        t3dx.setRotation(axisX);
        Transform3D t3dy = new Transform3D();
        t3dy.setRotation(axisY);
        Transform3D fixedTransform = new Transform3D();
        fixedTransform.mul(t3dy, t3dx);

        Transform3D viewMove3d = new Transform3D();
//        Vector3d move = new Vector3d(az);
        Vector3d move = new Vector3d(0.0, 0.0, 1.0);
        logger.fine("viewDistance:" + viewDistance);
        move.scale(viewDistance);
        logger.fine("move:" + move);
        viewMove3d.set(move);
        fixedTransform.mul(viewMove3d);

        logger.fine("fixedTransform:" + fixedTransform);
        return fixedTransform;
    }

    /**
     * アニメーションの一コマで行う視点移動操作
     * @param type
     */
    public void doRotation(CommandType type) {
        baseTransform.mul(rotateTransform);
        viewTransform = new Transform3D(baseTransform);
        viewTransform.mul(fixedTransform);
        parent.setTransform(viewTransform);
     }

    /**
     * 一連のアニメーションの終了時点で行う操作
     * パラメータは必要かと思ったが、今は使っていない。
     * @param com 視点の移動方向を示す
     */
    public void teardownRotation(CommandType com) {
        fixedTransform = makeFixedTransform();
    }

}
