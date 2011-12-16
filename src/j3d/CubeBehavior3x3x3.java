package j3d;

import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedFrames;

public class CubeBehavior3x3x3 extends CubeBehavior {
	private final Logger logger = Logger.getLogger(CubeBehavior3x3x3.class.getCanonicalName());
    private final EnumSet<CommandType> up = EnumSet.of(CommandType.U1, CommandType.U2, CommandType.U3);
    private final EnumSet<CommandType> down = EnumSet.of(CommandType.D1, CommandType.D2, CommandType.D3);
    private final EnumSet<CommandType> right = EnumSet.of(CommandType.R1, CommandType.R2, CommandType.R3);
    private final EnumSet<CommandType> left = EnumSet.of(CommandType.L1, CommandType.L2, CommandType.L3);
    private final EnumSet<CommandType> front = EnumSet.of(CommandType.F1, CommandType.F2, CommandType.F3);
    private final EnumSet<CommandType> back = EnumSet.of(CommandType.B1, CommandType.B2, CommandType.B3);
    private EnumSet<CommandType> two;
    private EnumSet<CommandType> inverse;
    private EnumSet<CommandType> counterSide;
    private EnumSet<CommandType> normalSetFrom;
    private EnumSet<CommandType> doubleSetFrom;
    private EnumSet<CommandType> inverseSetFrom;

    private HashMap<CommandType, CommandType> counterMap;
    private static final int[] upIndexes = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] downIndexes = {18, 19, 20, 21, 22, 23, 24, 25, 26};
    private static final int[] rightIndexes = {2, 5, 8, 11, 14, 17, 20, 23, 26};
    private static final int[] leftIndexes = {0, 3, 6, 9, 12, 15, 18, 21, 24};
    private static final int[] frontIndexes = {6, 7, 8, 15, 16, 17, 24, 25, 26};
    private static final int[] backIndexes = {0, 1, 2, 9, 10, 11, 18, 19, 20};
    private static final int[] normalIdxFrom = {6, 3, 0, 7, 4, 1, 8, 5, 2};
    private static final int[] doubleIdxFrom = {8, 7, 6, 5, 4, 3, 2, 1, 0};
    private static final int[] inverseIdxFrom = {2, 5, 8, 1, 4, 7, 0, 3, 6};

	private static final int[] colorToPos = 
	    {0, 1, 2, 3, 4, 5, 6, 7, 8,
	    6, 7, 8, 15, 16, 17, 24, 25, 26,
	    8, 5, 2, 17, 14, 11, 26, 23, 20,
	    2, 1, 0, 11, 10, 9, 20, 19, 18,
	    0, 3, 6, 9, 12, 15, 18, 21, 24,
	    24, 25, 26, 21, 22, 23, 18, 19, 20}; 

	private final WakeupOnElapsedFrames wakeUp;
	private static final Command NOP = new Command(CommandType.NOP, "");
	private long maxCounter = RubikProperties.getInt("cubebehavior3x3x3.maxCounter");
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
	public CubeBehavior3x3x3() {
		super();
		logger.fine("constructor called");
		this.cubies = makeRubik3x3();
		this.initialCubies = new Cubie[cubies.length];
		for (int i = 0; i < cubies.length; i++) {
			initialCubies[i] = cubies[i];
		}
		viewTransform = new ViewPoint(8.0);
		moving = new Cubie[9];
		wakeUp = new WakeupOnElapsedFrames(0);
		running = false;
		command = new LinkedList<Command>();
		counter = 0;
		currentCommand = NOP;
		viewCommand = EnumSet.of(CommandType.VIEW_UP, CommandType.VIEW_DOWN,
		        CommandType.VIEW_RIGHT, CommandType.VIEW_LEFT);
        viewCommand.add(CommandType.VIEW_RESET);
        two = EnumSet.of(CommandType.U2, CommandType.R2, CommandType.F2);
        two.addAll(EnumSet.of(CommandType.D2, CommandType.L2, CommandType.B2));
        inverse = EnumSet.of(CommandType.U3, CommandType.R3, CommandType.F3);
        inverse.addAll(EnumSet.of(CommandType.D3, CommandType.L3, CommandType.B3));
        counterSide = EnumSet.of(CommandType.D1, CommandType.D2, CommandType.D3);
        counterSide.addAll(EnumSet.of(CommandType.L1, CommandType.L2, CommandType.L3));
        counterSide.addAll(EnumSet.of(CommandType.B1, CommandType.B2, CommandType.B3));
        counterMap = new HashMap<CommandType, CommandType>();
        counterMap.put(CommandType.D1, CommandType.U3);
        counterMap.put(CommandType.D2, CommandType.U2);
        counterMap.put(CommandType.D3, CommandType.U1);
        counterMap.put(CommandType.L1, CommandType.R3);
        counterMap.put(CommandType.L2, CommandType.R2);
        counterMap.put(CommandType.L3, CommandType.R1);
        counterMap.put(CommandType.B1, CommandType.F3);
        counterMap.put(CommandType.B2, CommandType.F2);
        counterMap.put(CommandType.B3, CommandType.F1);
        normalSetFrom = EnumSet.of(CommandType.U1, CommandType.R3,CommandType.F1);
        normalSetFrom.addAll(EnumSet.of(CommandType.D3, CommandType.L1,CommandType.B3));
        doubleSetFrom = EnumSet.of(CommandType.U2, CommandType.R2,CommandType.F2);
        doubleSetFrom.addAll(EnumSet.of(CommandType.D2, CommandType.L2,CommandType.B2));
        inverseSetFrom = EnumSet.of(CommandType.U3, CommandType.R1,CommandType.F3);
        inverseSetFrom.addAll(EnumSet.of(CommandType.D1, CommandType.L3,CommandType.B1));
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
	
	private Cubie[] makeRubik3x3() {
        Cubie[] rubik = new Cubie[27];
        double size = 0.5;
        double alpha = size * 2.1;
        double x;
        double y;
        double z;
        double[] sign = {-1.0, 0, 1.0};
        for (int i = 0; i < rubik.length; i++) {
            x = sign[i%3] * alpha;
            z = sign[(i / 3) % 3] * alpha;
            y = - sign[(i / 9) % 3] * alpha;
            rubik[i] = new Cubie(size, x, y, z);
        }
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
		    if (counterSide.contains(type)) {
	            c.teardownRotation(counterMap.get(type));
		    } else {
		        c.teardownRotation(type);
		    }
		}
		int[] fromIdxs;		
        int[] toIdxs;
        if (normalSetFrom.contains(type)) {
            fromIdxs = normalIdxFrom;
        } else if (doubleSetFrom.contains(type)) {
            fromIdxs = doubleIdxFrom;
        } else if (inverseSetFrom.contains(type)) {
            fromIdxs = inverseIdxFrom;
        } else {
            logger.severe("Unexpected case");
            fromIdxs = new int[0];
        }
		if (up.contains(type)) {
		    toIdxs = upIndexes;
		} else if (down.contains(type)) {
		    toIdxs = downIndexes;
        } else if (right.contains(type)) {
            toIdxs = rightIndexes;
        } else if (left.contains(type)) {
            toIdxs = leftIndexes;
        } else if (front.contains(type)) {
            toIdxs = frontIndexes;
        } else if (back.contains(type)) {
            toIdxs = backIndexes;
		} else {
            logger.severe("Unexpected case");
		    toIdxs = new int[0];
		}
		for (int i = 0; i < toIdxs.length; i++) {
			cubies[toIdxs[i]] = moving[fromIdxs[i]];
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
        if (up.contains(type)) {
            indexes = upIndexes;
        } else if (down.contains(type)) {
            indexes = downIndexes;
        } else if (right.contains(type)) {
            indexes = rightIndexes;
        } else if (left.contains(type)) {
            indexes = leftIndexes;
        } else if (front.contains(type)) {
            indexes = frontIndexes;
        } else if (back.contains(type)) {
            indexes = backIndexes;
        } else {
            logger.severe("Unexpected case");
            indexes = new int[0];
        }
		for (int i = 0; i < moving.length; i++) {
			moving[i] = cubies[indexes[i]];
		}
        if (two.contains(type)) {
            counter = maxCounter * 2;
        } else {
            counter = maxCounter;
            if (inverse.contains(type)) {
                angle = - angle;
            }   
        }
		for (Cubie c : moving) {
	        if (counterSide.contains(type)) {
	            c.setupRotation(counterMap.get(type), - angle);
	        } else {
	            c.setupRotation(type, angle);
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
		String[][] result = new String[27][];
		for (int i = 0; i < result.length; i++) {
			result[i] = new String[6];
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = "";
			}
		}
		for (int i = 0; i < str.length(); i++) {
			String c = str.substring(i, i + 1);
			int pos = colorToPos[i];
			int face = i / 9;
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
