package jp.ac.hiroshima_u.sci.math.saito.rubik;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 * ŒõŒ¹‚Ìİ’è
 * ŠÂ‹«ŒõŒ¹1ŒÂ, •½süŒõŒ¹2ŒÂ(•\‚Æ— ‚©‚çÆ‚ç‚·)
 */
public class LightBranchGroup extends BranchGroup {
    public LightBranchGroup() {
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        AmbientLight ambient = new AmbientLight(lightColor);
        Vector3f direction = new Vector3f(-1.8f, -1.3f, 2.0f);
        Vector3f direction2 = new Vector3f(1.8f, 1.3f, -2.0f);
        DirectionalLight light = new DirectionalLight(lightColor, direction);
        DirectionalLight light2 = new DirectionalLight(lightColor, direction2);
        BoundingSphere lightBounds = new BoundingSphere(new Point3d(),
                Double.POSITIVE_INFINITY);
        ambient.setInfluencingBounds(lightBounds);
        light.setInfluencingBounds(lightBounds);
        light2.setInfluencingBounds(lightBounds);
//        BranchGroup br = new BranchGroup();
        addChild(ambient);
        addChild(light);
        addChild(light2);
    }

}
