package j3d;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class CubeAnimation extends Behavior {
	Logger logger = Logger.getAnonymousLogger();
	
	private final WakeupOnElapsedFrames wakeUp;
	private final long maxCounter = 1000;
	private boolean running;
	private List<Command> command;
	private long counter;
	private Command currentCommand;
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
		this.cubies = cubies;
		moving = new Cubie[4];
		wakeUp = new WakeupOnElapsedFrames(0);
		running = false;
		command = new LinkedList<Command>();
		counter = 0;
		currentCommand = Command.NOP;
	}
	
	public void addCommand(Command kind) {
		logger.info("addCommand called");
		synchronized (command) {
			command.add(kind);
		}
		running = true;
	}
	
	@Override
	public void initialize() {
		logger.info("initialize called");
		running = false;
		wakeupOn(wakeUp);
	}

	@Override
	public void processStimulus(Enumeration arg0) {
		if (running) {
			if (currentCommand != Command.NOP) {
				doCommand();
				tearDown();
			} else if (! command.isEmpty()) {
				synchronized (command) {
					currentCommand = command.remove(0);
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
		running = false;
		for (Cubie c : moving) {
			c.stop(currentCommand);
		}
		switch (currentCommand) {
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
		currentCommand = Command.NOP;
	}

	private void setupCommand() {
		logger.info("currentCommand:" + currentCommand);
		angle = Math.PI / 2.0 / maxCounter;
		switch (currentCommand) {
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
		switch (currentCommand) {
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
			c.start(currentCommand, angle);
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

	private void doCommand() {
		counter--;
		for (int i = 0; i < moving.length; i++) {
			moving[i].rotate();
		}
	}
}
