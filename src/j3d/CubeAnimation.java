package j3d;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class CubeAnimation extends Behavior {
	Logger logger = Logger.getAnonymousLogger();
	private static final int[] colorToPos = {0, 1, 2, 3, 2, 3, 6, 7, 3, 1, 7, 5,
		1, 0, 5, 4, 0, 2, 4, 6, 6, 7, 4, 5}; 

	private final WakeupOnElapsedFrames wakeUp;
	private static final Command NOP = new Command(CommandType.NOP, "");
	private long maxCounter = 500;
	private boolean running;
	private Queue<Command> command;
	//private Queue<String> arguments;
	private long counter;
	private Command currentCommand;
	private Cubie[] initialCubies;
	private Cubie[] cubies;
	private Cubie[] moving;
	private double angle;

	/**
	 * コンストラクタ
	 * @param cubies
	 */
	public CubeAnimation(Cubie[] cubies) {
		super();
		logger.info("constructor called");
		initialCubies = new Cubie[cubies.length];
		this.cubies = new Cubie[cubies.length];
		for (int i = 0; i < cubies.length; i++) {
			initialCubies[i] = cubies[i];
			this.cubies[i] = cubies[i];
		}
		moving = new Cubie[4];
		wakeUp = new WakeupOnElapsedFrames(0);
		running = false;
		command = new LinkedList<Command>();
		//arguments = new LinkedList<String>();
		counter = 0;
		currentCommand = NOP;
	}
	
	public void addCommand(Command com) {
		logger.info("commandType is " + com);
		synchronized (command) {
			command.add(com);
		}
	}
	
//	public void addArguments(String arg) {
//		logger.info("arguments are  " + arg);
//		synchronized (arguments) {
//			arguments.add(arg);
//		}
//	}
	
	public void start() {
		logger.info("start");
		synchronized (this) {
			running = true;
		}
	}

	public void stop() {
		logger.info("stop");
		synchronized (this) {
			running = false;
		}
	}

	@Override
	public void initialize() {
		logger.info("initialize called");
		running = true;
		wakeupOn(wakeUp);
	}

	@Override
	public void processStimulus(Enumeration arg0) {
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
		logger.info("tear down");
		if (currentCommand == NOP || 
				currentCommand.getType() == CommandType.COLOR || 
				currentCommand.getType() == CommandType.SPEED) {
			currentCommand = NOP;
			return;
		}
		for (Cubie c : moving) {
			c.teardownRotation(currentCommand.getType());
		}
		switch (currentCommand.getType()) {
		case U1:
			cubies[0] = moving[2];
			cubies[1] = moving[0];
			cubies[2] = moving[3];
			cubies[3] = moving[1];
			break;
		case U2:
			cubies[0] = moving[3];
			cubies[1] = moving[2];
			cubies[2] = moving[1];
			cubies[3] = moving[0];
			break;
		case U3:
			cubies[0] = moving[1];
			cubies[1] = moving[3];
			cubies[2] = moving[0];
			cubies[3] = moving[2];
			break;
		case R1:
			cubies[1] = moving[1];
			cubies[3] = moving[3];
			cubies[5] = moving[0];
			cubies[7] = moving[2];
			break;
		case R2:
			cubies[1] = moving[3];
			cubies[3] = moving[2];
			cubies[5] = moving[1];
			cubies[7] = moving[0];
			break;
		case R3:
			cubies[1] = moving[2];
			cubies[3] = moving[0];
			cubies[5] = moving[3];
			cubies[7] = moving[1];
			break;
		case F1:
			cubies[2] = moving[2];
			cubies[3] = moving[0];
			cubies[6] = moving[3];
			cubies[7] = moving[1];
			break;
		case F2:
			cubies[2] = moving[3];
			cubies[3] = moving[2];
			cubies[6] = moving[1];
			cubies[7] = moving[0];
			break;
		case F3:
			cubies[2] = moving[1];
			cubies[3] = moving[3];
			cubies[6] = moving[0];
			cubies[7] = moving[2];
			break;
		default:
		}
		currentCommand = NOP;
	}

	private void setupCommand() {
		logger.info("currentCommand:" + currentCommand);
		if (currentCommand.getType() == CommandType.COLOR) {
			setupColor();
			counter = 0;
			return;
		} else if (currentCommand.getType() == CommandType.SPEED) {
			setupSpeed();
			counter = 0;
			return;
		}
		angle = Math.PI / 2.0 / maxCounter;
		switch (currentCommand.getType()) {
		case U1:
		case U2:
		case U3:
			moving[0] = cubies[0];
			moving[1] = cubies[1];
			moving[2] = cubies[2];
			moving[3] = cubies[3];
			break;
		case R1:
		case R2:
		case R3:
			moving[0] = cubies[1];
			moving[1] = cubies[3];
			moving[2] = cubies[5];
			moving[3] = cubies[7];
			break;
		case F1:
		case F2:
		case F3:
			moving[0] = cubies[2];
			moving[1] = cubies[3];
			moving[2] = cubies[6];
			moving[3] = cubies[7];
			break;
		default:
		}
		switch (currentCommand.getType()) {
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
			c.setupRotation(currentCommand.getType(), angle);
		}
		if (logger.isLoggable(Level.INFO)) {
			Transform3D tr = new Transform3D();
			Matrix3d mat = new Matrix3d();
			Vector3d vec = new Vector3d();
			for (Cubie c : moving) {
				c.getTransform(tr);
				tr.get(mat, vec);
				logger.info("mat:" + mat);
				logger.info("vec:" + vec);
			}
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
		logger.info("maxCounter = " + maxCounter);
	}

	private void setupColor() {
		String colorString = currentCommand.getArgument();
		logger.info("colorString = " + colorString);
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
		counter--;
		for (int i = 0; i < moving.length; i++) {
			moving[i].rotate();
		}
	}

}
