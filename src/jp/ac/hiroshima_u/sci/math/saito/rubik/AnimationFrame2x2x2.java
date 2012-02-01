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

/**
 * 小さな立方体8個からなるルービックキューブ。
 * @author M. Saito
 */
public class AnimationFrame2x2x2 extends JFrame {
	private static final long serialVersionUID = 1L;
	private HashMap<JButton, CommandType> commandMap;
    private CubeBehavior2x2x2 animation;
    private CommandPanel commandPanel;
    private Canvas3D canvas;
	
	/**
	 * コンストラクタ
	 */
    public AnimationFrame2x2x2() {
    	super("Rubik Cube 2x2x2");
        getContentPane().setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);
        initialize(canvas);   
        setUpPanel();        
      }

    /**
     * パネルの設定
     */
    private void setUpPanel() {
        commandMap = new HashMap<JButton, CommandType>();
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        getContentPane().add(outer, BorderLayout.EAST);
        commandPanel = new CommandPanel(2, animation, commandMap);
        outer.add(commandPanel);
        outer.add(new OperationPanel(2, commandMap, commandPanel));
        outer.add(new ViewOperationPanel(commandMap, commandPanel));
        outer.add(Box.createVerticalGlue());
        outer.revalidate();
    }
    
    /**
     * シーングラフの作成
     * @return シーングラフ
     */
	private BranchGroup createSceneGraph() {
	    BranchGroup root = new BranchGroup();
        BoundingSphere bounds=new BoundingSphere(
        		new Point3d(),Double.POSITIVE_INFINITY);
        animation = new CubeBehavior2x2x2();
        animation.setSchedulingBounds(bounds);
        TransformGroup target = animation.getTarget();
        root.addChild(target);
        root.addChild(animation);
        Background background = new Background(new Color3f(0.6f, 0.6f, 0.8f));
        background.setApplicationBounds(bounds);
        root.addChild(background);
        Room room = new Room(8);
        root.addChild(room.getTransformGroup());
        return root;
      }
	
    
    /**
     * 初期化
     * @param canvas
     */
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
        setColor("BBBBGGGGOOOOYYYYRRRRWWWW");
	}

	public void setCommand(String cmd) {
	    commandPanel.setOperation(cmd);
	}
	
    /**
     * 起動用メイン処理
     * @param args
     */
    public static void main(String[] args) {
        AnimationFrame2x2x2 sample = new AnimationFrame2x2x2();
        sample.setBounds(10, 10, 1000, 1000);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }
    
	/**
	 * 色を変更するコマンド。
	 * 実際には、これから解くべきルービックキューブの色を指定する。
	 * @param text 色を表す文字列
	 */
	public void setColor(String text) {
		if (text.length() == 0) {
			return;
		}
		if (text.length() < 24) {
		    commandPanel.setWarning("文字列が短かすぎます");
			return;
		}
		//animation.stop();
		animation.addCommand(new Command(CommandType.COLOR, text));
		//animation.start();
		//color.setText("");
	}
	
}
