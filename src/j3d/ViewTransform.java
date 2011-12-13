package j3d;

import java.util.logging.Logger;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * baseTransform 累積していく
 * fixedTransform 正面ではなくちょっとずれてみるためのもの。毎回違うはず。
 * viewTransform baseTransform のあと、fixedTransform でずれた視点。
 * @author saito
 *
 */
public class ViewTransform {
    Logger logger = Logger.getLogger(ViewTransform.class.getCanonicalName());
    private final double viewAngleX = -Math.PI / 6;
    private final double viewAngleY = Math.PI / 6;
    private Transform3D baseTransform;
    private Transform3D viewTransform;
    private Transform3D fixedTransform;
    private Transform3D saveBase;
    private TransformGroup parent;
    private double angleAccumX;
    private double angleAccumY;
    private double angle;
    private double distance;
    private int countX;
    private int countY;
//    private Vector3d currentAxis;
//    private Vector3d ax;
//    private Vector3d ay;
//    private Vector3d az;

    public ViewTransform(double distance) {
        this.distance = distance;
        angleAccumX = 0;
        angleAccumY = 0;
        countX = 0;
        countY = 0;
        angle = 0;
//        initAxis();
        fixedTransform = makeFixedTransform();
        baseTransform = new Transform3D();
        viewTransform = new Transform3D(baseTransform);
        viewTransform.mul(fixedTransform);
        saveBase = new Transform3D(baseTransform);
    }

//    private void initAxis() {
//        ax = new Vector3d(1, 0, 0);
//        ay = new Vector3d(0, 1, 0);
//        az = new Vector3d(0, 0, 1);
//    }

    public void setParent(TransformGroup parent) {
        this.parent = parent;
    }

    public Transform3D getTransform3D() {
        return viewTransform;
    }

    public void reset() {
        angleAccumX = 0;
        angleAccumY = 0;
        countX = 0;
        countY = 0;
        angle = 0;
//        initAxis();
        fixedTransform = makeFixedTransform();
        baseTransform = new Transform3D(saveBase);
        viewTransform = new Transform3D(baseTransform);
        viewTransform.mul(fixedTransform);
        parent.setTransform(viewTransform);
    }

    public void setupRotate(CommandType type, double angle) {
        this.angle = angle;
        // if (type == CommandType.VIEW_DOWN || type == CommandType.VIEW_UP) {
        // currentAxis = ax;
        // } else {
        // currentAxis = ay;
        // }
    }

    private Transform3D makeFixedTransform() {
        Vector3d ax = new Vector3d(1, 0, 0);
        Vector3d ay = new Vector3d(0, 1, 0);

        AxisAngle4d axisX = new AxisAngle4d(ax.x, ax.y, ax.z, viewAngleX);
        AxisAngle4d axisY = new AxisAngle4d(ay.x, ay.y, ay.z, viewAngleY);
        Transform3D t3dx = new Transform3D();
        t3dx.setRotation(axisX);
        Transform3D t3dy = new Transform3D();
        t3dy.setRotation(axisY);
        Transform3D fixedTransform = new Transform3D();
        fixedTransform.mul(t3dy, t3dx);
        Transform3D viewMove3d = new Transform3D();

        Vector3d move = new Vector3d(0.0, 0.0, distance);
        viewMove3d.set(move);
        fixedTransform.mul(viewMove3d);
        logger.fine("fixedTransform:" + fixedTransform);
        return fixedTransform;
    }

    // public void doRotation(CommandType type) {
    // angleSum = angleSum + angle;
    // AxisAngle4d axis = new AxisAngle4d(currentAxis.x, currentAxis.y,
    // currentAxis.z, angleSum);
    // Transform3D rot = new Transform3D();
    // Matrix3d m = new Matrix3d();
    // Matrix3d n = new Matrix3d();
    // Vector3d v = new Vector3d();
    // rot.setRotation(axis);
    // rot.get(n);
    // viewTransform.get(m, v);
    // viewTransform = new Transform3D(n, new Vector3d(), 1);
    // //
    // // Transform3D t3dy = new Transform3D();
    // // t3dy.rotY(angleY);
    // // Transform3D t3dx = new Transform3D();
    // // t3dx.rotX(angleX);
    // // viewTransform = new Transform3D();
    // // viewTransform.mul(t3dy, t3dx);
    // viewTransform.mul(fixedTransform);
    // parent.setTransform(viewTransform);
    // }

    public void doRotation(CommandType type) {
        if (type == CommandType.VIEW_DOWN || type == CommandType.VIEW_UP) {
            angleAccumX = angleAccumX + angle;
        } else {
            angleAccumY = angleAccumY + angle;
        }
        Transform3D t3dy = new Transform3D();
        t3dy.rotY(angleAccumY);
        Transform3D t3dx = new Transform3D();
        t3dx.rotX(angleAccumX);
        viewTransform = new Transform3D();
        viewTransform.mul(t3dy, t3dx);
        viewTransform.mul(fixedTransform);
        parent.setTransform(viewTransform);
    }

    public void teardownRotation(CommandType com) {
        switch (com) {
        case VIEW_DOWN:
            countX = (countX + 3) % 4;
            break;
        case VIEW_UP:
            countX = (countX + 1) % 4;
            break;
        case VIEW_LEFT:
            countY = (countY + 3) % 4;
            break;
        case VIEW_RIGHT:
            countY = (countY + 1) % 4;
            break;
        default:
        }
        // Vector3d tmp;
        // if (com == CommandType.VIEW_UP) {
        // tmp = ay;
        // ay = az;
        // az = new Vector3d(-tmp.x, -tmp.y, -tmp.z);
        // // az = new Vector3d(tmp.x, tmp.y, tmp.z);
        // } else if (com == CommandType.VIEW_DOWN) {
        // tmp = ay;
        // ay = new Vector3d(-az.x, -az.y, -az.z);
        // az = tmp;
        // // } else if (com == CommandType.VIEW_RIGHT) {
        // // tmp = az;
        // // az = new Vector3d(-ax.x, -ax.y, -ax.z);
        // // ax = tmp;
        // // } else if (com == CommandType.VIEW_LEFT) {
        // // tmp = az;
        // // az = ax;
        // // ax = new Vector3d(-tmp.x, -tmp.y, -tmp.z);
        // }
        // //fixedTransform = makeFixedTransform();
    }

}
