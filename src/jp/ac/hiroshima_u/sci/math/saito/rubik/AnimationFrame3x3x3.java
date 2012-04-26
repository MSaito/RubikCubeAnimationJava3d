package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.util.HashMap;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class AnimationFrame3x3x3 extends JFrame {
    private static final long serialVersionUID = 1L;
    private HashMap<JButton, CommandType> commandMap;
    private CubeBehavior3x3x3 animation;
    private Canvas3D canvas;
    private CommandPanel commandPanel;
    private boolean isVoid = false;


    public AnimationFrame3x3x3() {
        this(false);
    }
    
    public AnimationFrame3x3x3(boolean isVoid) {
        super("Rubik Cube 3x3x3");
        this.isVoid = isVoid;
        getContentPane().setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse
                .getPreferredConfiguration();
        canvas = new Canvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);
        initialize(canvas);
        setUpButton();
    }

    private void setUpButton() {
        commandMap = new HashMap<JButton, CommandType>();
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        getContentPane().add(outer, BorderLayout.EAST);
        commandPanel = new CommandPanel(3, animation, commandMap);
        outer.add(commandPanel);
        outer.add(new OperationPanel(3, commandMap, commandPanel));
        outer.add(new ViewOperationPanel(commandMap, commandPanel));
        outer.add(Box.createVerticalGlue());
        outer.revalidate();
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();
        BoundingSphere bounds = new BoundingSphere(new Point3d(),
                Double.POSITIVE_INFINITY);
        if (isVoid) {
            animation = new CubeBehaviorVoid();
        } else {
            animation = new CubeBehavior3x3x3();
        }
        animation.setSchedulingBounds(bounds);
        TransformGroup target = animation.getTarget();
        root.addChild(target);
        root.addChild(animation);
        Background background = new Background(new Color3f(0.6f, 0.6f, 0.8f));
        background.setApplicationBounds(bounds);
        root.addChild(background);
        Room room = new Room(16);
        root.addChild(room.getTransformGroup());
        return root;
    }

    private void initialize(Canvas3D canvas) {
        SimpleUniverse universe = new SimpleUniverse(canvas);
        BranchGroup scene = createSceneGraph();

        ViewingPlatform vp = universe.getViewingPlatform();
        vp.setNominalViewingTransform();
        TransformGroup viewingTG = vp.getViewPlatformTransform();
        viewingTG.setTransform(animation.getViewTransform());
        animation.setParent(viewingTG);
        universe.addBranchGraph(scene);

        universe.addBranchGraph(new LightBranchGroup());
        setColor("BBBBBBBBBGGGGGGGGGOOOOOOOOOYYYYYYYYYRRRRRRRRRWWWWWWWWW");
    }

    public boolean isIdle() {
        return animation.isIdle();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        //AnimationFrame3x3x3 sample = new AnimationFrame3x3x3(true); // void cube
        AnimationFrame3x3x3 sample = new AnimationFrame3x3x3();
        sample.setBounds(10, 10, 1000, 1000);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }

    private void setColor(String text) {
        if (text.length() == 0) {
            return;
        }
        if (text.length() < 54) {
            commandPanel.setWarning("•¶Žš—ñ‚ª’Z‚©‚·‚¬‚Ü‚·");
            return;
        }
        animation.stop();
        animation.addCommand(new Command(CommandType.COLOR, text));
        animation.start();
        //color.setText("");
    }
}
