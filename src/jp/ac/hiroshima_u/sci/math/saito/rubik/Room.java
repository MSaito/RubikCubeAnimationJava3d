package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;

/**
 * 手抜き工事の部屋（の壁）
 * もしかして、でかい立方体一個でよかったのか。
 */
public class Room {
    private static final Logger logger = Logger.getLogger(Room.class.getCanonicalName());
    /*
     * TOP FRONT RIGHT BOTTOM BACK LEFT
     */
    private static final float[][] wallSize = { { 1.0f, 0.01f, 1.0f },
            { 1.0f, 1.0f, 0.01f }, { 0.01f, 1.0f, 1.0f },
            { 1.0f, 0.01f, 1.0f }, { 1.0f, 1.0f, 0.01f }, { 0.01f, 1.0f, 1.0f } };
    private static final float[][] wallPos = {
        {0.0f, 1.0f, 0.0f},
        {0.0f, 0.0f, 1.0f},
        {1.0f, 0.0f, 0.0f},
        {0.0f, -1.0f, 0.0f},
        {0.0f, 0.0f, -1.0f},
        {-1.0f, 0.0f, 0.0f}};
    private TransformGroup transGrp;

    /**
     * ルービックキューブのある部屋
     * 六方向に同じ模様の壁を置いているだけ
     * @param size 壁の一辺の大きさ
     */
    public Room(double size) {
        transGrp = new TransformGroup();
        BufferedImage image;
        Texture texture;
        try {
            image = ImageIO.read(RubikProperties.getURL("checkerWall2.png"));
            texture = new TextureLoader(image).getTexture();
        } catch (IOException e) {
            logger.severe("Can't get texture");
            throw new RuntimeException(e);
        }
        Appearance ap = new Appearance();
        ap.setTexture(texture);
        Transform3D texTrans = new Transform3D();
        TextureAttributes attribute = new TextureAttributes();
        texTrans.setScale(size);
        attribute.setTextureTransform(texTrans);
        ap.setTextureAttributes(attribute);
        float fsize = (float) size;
        for (int i = 0; i < 6; i++) {
            Box wall = new Box(wallSize[i][0] * fsize,
                    wallSize[i][1] * fsize,
                    wallSize[i][2] * fsize,
                    Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS,
                    ap);
            TransformGroup child = new TransformGroup();
            child.addChild(wall);
            Transform3D trans = new Transform3D();
            trans.setTranslation(new Vector3d(wallPos[i][0] * size,
                        wallPos[i][1] * size,
                        wallPos[i][2] * size));
            child.setTransform(trans);
            transGrp.addChild(child);
        }
    }
    
    /**
     * この部屋を表すTransformGroupを返す
     * @return この部屋を表すTransformGroup
     */
    public TransformGroup getTransformGroup() {
        return transGrp;
    }
}
