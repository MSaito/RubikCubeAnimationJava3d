package j3d;

import java.util.EnumSet;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class CubeBehavior2x2x2 extends CubeBehavior {
	Logger logger = Logger.getLogger(CubeBehavior2x2x2.class.getCanonicalName());
	private static final int[] colorToPos = {0, 1, 2, 3, 2, 3, 6, 7, 3, 1, 7, 5,
		1, 0, 5, 4, 0, 2, 4, 6, 6, 7, 4, 5}; 

	private final WakeupOnElapsedFrames wakeUp;
	private static final Command NOP = new Command(CommandType.NOP, "");
    private long maxCounter = RubikProperties.getInt("cubebehavior2x2x2.maxCounter");
	private boolean running;
	private Queue<Command> command;
	private long counter;
	private Command currentCommand;
	private Cubie[] initialCubies;
	private Cubie[] cubies;
	private Cubie[] moving;
	private double angle;
	private ViewPoint viewTransform;
	private EnumSet<CommandType> viewCommand;

	/**
	 * コンストラクタ
	 * @param cubies
	 */
	public CubeBehavior2x2x2() {
		super();
		logger.fine("constructor called");
		this.cubies = makeRubik();
		this.initialCubies = new Cubie[cubies.length];
		for (int i = 0; i < cubies.length; i++) {
			initialCubies[i] = cubies[i];
		}
		viewTransform = new ViewPoint(6.0);
		moving = new Cubie[4];
		wakeUp = new WakeupOnElapsedFrames(0);
		running = false;
		command = new LinkedList<Command>();
		counter = 0;
		currentCommand = NOP;
		viewCommand = EnumSet.of(CommandType.VIEW_UP, CommandType.VIEW_DOWN,
		        CommandType.VIEW_RIGHT, CommandType.VIEW_LEFT);
        viewCommand.add(CommandType.VIEW_RESET);
	}

	public void setParent(TransformGroup parent) {
	    viewTransform.setParent(parent);
	}
	public Transform3D getViewTransform() {
		return viewTransform.getTransform3D();
	}

	public TransformGroup[] getTarget() {
		return cubies;
	}
	
	private Cubie[] makeRubik() {
        Cubie[] rubik = new Cubie[8];
        // 上段
        rubik[0] = new Cubie(0.5, -0.55, +0.55, -0.55);
        rubik[1] = new Cubie(0.5, +0.55, +0.55, -0.55);
        rubik[2] = new Cubie(0.5, -0.55, +0.55, +0.55);
        rubik[3] = new Cubie(0.5, +0.55, +0.55, +0.55);
        // 下段
        rubik[4] = new Cubie(0.5, -0.55, -0.55, -0.55);
        rubik[5] = new Cubie(0.5, +0.55, -0.55, -0.55);
        rubik[6] = new Cubie(0.5, -0.55, -0.55, +0.55);
        rubik[7] = new Cubie(0.5, +0.55, -0.55, +0.55);
        return rubik;
	}
	

	public void addCommand(Command com) {
		logger.fine("commandType is " + com);
		synchronized (command) {
			command.add(com);
		}
	}
	
	public void start() {
		logger.fine("start");
		synchronized (this) {
			running = true;
		}
	}

	public void stop() {
		logger.fine("stop");
		synchronized (this) {
			running = false;
		}
	}

	@Override
	public void initialize() {
		logger.fine("initialize called");
		running = true;
		wakeupOn(wakeUp);
	}

	@Override
	public void processStimulus(@SuppressWarnings("rawtypes") Enumeration arg0) {
		if (running) {
			if (currentCommand != NOP) {
				doCommand();
				tearDown();
			} else if (! command.isEmpty()) {
				synchronized (command) {
					currentCommand = command.remove();
					setupCommand();
					doCommand();
					tearDown();
				}
			}			
		}
		wakeupOn(wakeUp);
	}

	private void tearDown() {
		if (counter > 0) {
			return;
		}
		logger.fine("tear down");
		CommandType type = currentCommand.getType();
		if (currentCommand == NOP || 
		        type == CommandType.COLOR || 
		        type == CommandType.SPEED) {
			currentCommand = NOP;
			return;
		}
		if (viewCommand.contains(type)) {
		    viewTransform.teardownRotation(type);
		    currentCommand = NOP;
		    return;
	    }

		for (Cubie c : moving) {
			c.teardownRotation(type);
		}
		int[] indexes;
		switch (type) {
		case U1:
		case R3:
		case F1:
			indexes = new int[]{2, 0, 3, 1};
			break;
		case U2:
		case R2:
		case F2:
			indexes = new int[]{3, 2, 1, 0};
			break;
		case U3:
		case R1:
		case F3:
		default:
			indexes = new int[]{1, 3, 0, 2};
		}
		int[] toIdx;
		switch (type) {
		case U1:
		case U2:
		case U3:
			toIdx = new int[]{0, 1, 2, 3};
			break;
		case R1:
		case R2:
		case R3:
			toIdx = new int[]{1, 3, 5, 7};
			break;
		case F1:
		case F2:
		case F3:
		default:	
			toIdx = new int[]{2, 3, 6, 7};
		}
		for (int i = 0; i < 4; i++) {
			cubies[toIdx[i]] = moving[indexes[i]];
		}
		currentCommand = NOP;
	}

	private void setupCommand() {
		logger.fine("currentCommand:" + currentCommand);
		CommandType type = currentCommand.getType();
		if (type == CommandType.COLOR) {
			setupColor();
			counter = 0;
			return;
		} else if (type == CommandType.SPEED) {
			setupSpeed();
			counter = 0;
			return;
		} else if (viewCommand.contains(type)) {
			setupView(type);
			return;
		} else {
			setupRotation(type);
		}
	}

	private void setupRotation(CommandType type) {
		angle = Math.PI / 2.0 / maxCounter;
		int[] indexes;
		switch (type) {
		case U1:
		case U2:
		case U3:
			indexes = new int[]{0, 1, 2, 3};
			break;
		case R1:
		case R2:
		case R3:
			indexes = new int[]{1, 3, 5, 7};
			break;
		case F1:
		case F2:
		case F3:
			indexes = new int[]{2, 3, 6, 7};
			break;
		default:
			indexes = new int[]{0, 1, 2, 3};
		}
		for (int i = 0; i < 4; i++) {
			moving[i] = cubies[indexes[i]];
		}
		switch (type) {
		case U2:
		case R2:
		case F2:
			counter = maxCounter * 2;
			break;
		case U3:
		case R3:
		case F3:
			angle = -angle;
		default:
			counter = maxCounter;
		}
		for (Cubie c : moving) {
			c.setupRotation(type, angle);
		}
		if (logger.isLoggable(Level.INFO)) {
			Transform3D tr = new Transform3D();
			Matrix3d mat = new Matrix3d();
			Vector3d vec = new Vector3d();
			for (Cubie c : moving) {
				c.getTransform(tr);
				tr.get(mat, vec);
				logger.fine("mat:" + mat);
				logger.fine("vec:" + vec);
			}
		}
	}
	
	private void setupView(CommandType type) {
	    logger.fine("type = " + type);
		if (type == CommandType.VIEW_RESET) {
		    viewTransform.reset();
		} else {
			angle = Math.PI / 2.0 / maxCounter;
			if (type == CommandType.VIEW_LEFT || type == CommandType.VIEW_UP) {
			    angle = - angle;
			}
			counter = maxCounter;
			viewTransform.setupRotate(type, angle);
		}
	}
	
	private void setupSpeed() {
		String speedStr = currentCommand.getArgument();
		if (speedStr.matches("^[0-9]+$")) {
			long speed = Long.parseLong(speedStr);
			if (speed <= 0) {
				speed = 1;
			}
			maxCounter = Math.round(1000.0 / speed * 1000);
		}
		logger.fine("maxCounter = " + maxCounter);
	}

	private void setupColor() {
		String colorString = currentCommand.getArgument();
		logger.fine("colorString = " + colorString);
		String[][] colors = makeRubikColors(colorString);
		for (int i = 0; i < initialCubies.length; i++) {
			cubies[i] = initialCubies[i];
		}
		for (int i = 0; i < cubies.length; i++) {
			cubies[i].reset();
			cubies[i].setColor(colors[i]);
		}
	}
	
	private String[][] makeRubikColors(String str) {
		String[][] result = new String[8][];
		for (int i = 0; i < result.length; i++) {
			result[i] = new String[6];
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = "";
			}
		}
		for (int i = 0; i < str.length(); i++) {
			String c = str.substring(i, i + 1);
			int pos = colorToPos[i];
			int face = i / 4;
			result[pos][face] = c;
		}
		return result;
	}
	
	private void doCommand() {
		if (counter <= 0) {
			return;
		}
		CommandType type = currentCommand.getType();
		if (viewCommand.contains(type)) {
			doViewRotation(type);
		} else {
			doRotation();
		}
	}
	
    private void doViewRotation(CommandType type) {
        counter--;
        viewTransform.doRotation(type);
    }

	private void doRotation() {
		counter--;
		for (int i = 0; i < moving.length; i++) {
			moving[i].rotate();
		}
	}
}
