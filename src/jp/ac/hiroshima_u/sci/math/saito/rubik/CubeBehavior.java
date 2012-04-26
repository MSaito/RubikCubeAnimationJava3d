package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.util.EnumSet;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedFrames;

/**
 * 2x2x2 と 3x3x3 のルービックキューブをまとめたクラス
 * 視点移動、色の設定なども含む
 * よく考えられて作られたクラスではないので取り扱い注意
 * 
 * @author M. Saito
 *
 */
public abstract class CubeBehavior extends Behavior {
    private final Logger logger = Logger.getLogger(CubeBehavior.class
            .getCanonicalName());
    private static final Command NOP = new Command(CommandType.NOP, "");
    private boolean running;
    private Command currentCommand;
    private Queue<Command> command;
    private static final EnumSet<CommandType> viewCommand;
    static {
    viewCommand = EnumSet.of(CommandType.VIEW_UP, CommandType.VIEW_DOWN,
            CommandType.VIEW_RIGHT, CommandType.VIEW_LEFT);
    viewCommand.add(CommandType.VIEW_RESET);
    }
    private static final EnumSet<CommandType> internalCommand; 
    static {
        internalCommand = EnumSet.of(CommandType.COLOR, CommandType.SPEED);
        internalCommand.addAll(viewCommand);   
    }
 
    private static final EnumSet<CommandType> normalSetFrom;
    static {
        normalSetFrom = EnumSet.of(CommandType.U1, CommandType.R3,
                CommandType.F1);
        normalSetFrom.addAll(EnumSet.of(CommandType.D3, CommandType.L1,
                CommandType.B3));
    }
    private static final EnumSet<CommandType> inverseSetFrom;
    static {
        inverseSetFrom = EnumSet.of(CommandType.U3, CommandType.R1,
                CommandType.F3);
        inverseSetFrom.addAll(EnumSet.of(CommandType.D1, CommandType.L3,
                CommandType.B1));
    }

    private long defaultMax = RubikProperties.getInt("cubebehavior.maxCounter");
    private long maxCounter = defaultMax;
    private long minCounter = 10;
    private long counter;
    private double viewAngle;

    private ViewPoint viewTransform;
    protected Cubie[] cubies;
    private Cubie[] initialCubies;
    private TransformGroup rubikCube;

    private final WakeupOnElapsedFrames wakeUp;

    public CubeBehavior(Cubie[] cubies, double viewDistance) {
        this.cubies = cubies;
        this.initialCubies = new Cubie[this.cubies.length];
        this.rubikCube = new TransformGroup();
        for (int i = 0; i < this.cubies.length; i++) {
            initialCubies[i] = cubies[i];
            rubikCube.addChild(cubies[i]);
        }
        viewTransform = new ViewPoint(viewDistance);
        wakeUp = new WakeupOnElapsedFrames(0);
        running = false;
        command = new LinkedList<Command>();
        currentCommand = NOP;
        counter = 0;
    }

    protected boolean isNormalSetFrom(CommandType type) {
        return normalSetFrom.contains(type);
    }
    protected boolean isInverseSetFrom(CommandType type) {
        return inverseSetFrom.contains(type);
    }
    protected long getMaxCounter() {
        return maxCounter;
    }

    protected void setCounter(long counter) {
        this.counter = counter;
    }

    public void setParent(TransformGroup parent) {
        viewTransform.setParent(parent);
    }

    public Transform3D getViewTransform() {
        return viewTransform.getTransform3D();
    }

    public TransformGroup getTarget() {
        return rubikCube;
    }

    public void addCommand(Command com) {
        logger.fine("commandType is " + com);
        synchronized (command) {
            command.add(com);
        }
    }

    public boolean isIdle() {
        return command.isEmpty() && (running == false);
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

    protected CommandType getCommandType() {
        return currentCommand.getType();
    }

    protected String getCommandArgument() {
        return currentCommand.getArgument();
    }

    protected Command getCurrentCommand() {
        return currentCommand;
    }

    protected void resetCurrentCommand() {
        currentCommand = NOP;
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
                CommandType type = currentCommand.getType();
                if (internalCommand.contains(type)) {
                    if (counter > 0) {
                        counter--;
                        internalDoCommand(type);
                    } else {
                        internalTearDown(type);
                    }
                } else {
                    if (counter > 0) {
                        counter--;
                        doCommand(type);
                    } else {
                        tearDown(type);
                    }
                }
            } else if (!command.isEmpty()) {
                synchronized (command) {
                    currentCommand = command.remove();
                }
                CommandType type = currentCommand.getType();
                logger.fine("commandType:" + type);
                if (internalCommand.contains(type)) {
                    internalSetupCommand(type);
                    if (counter > 0) {
                        counter--;
                        internalDoCommand(type);
                    } else {
                        internalTearDown(type);
                    }
                } else {
                    setupCommand(type);
                    if (counter > 0) {
                        counter--;
                        doCommand(type);
                    } else {
                        tearDown(type);
                    }
                }
            } else {
                running = false;
            }
        }
        wakeupOn(wakeUp);
    }

    protected void internalSetupCommand(CommandType type) {
        counter = 0;
        if (type == CommandType.COLOR) {
            setupColor();
        } else if (type == CommandType.SPEED) {
            setupSpeed();
        } else {
            setupView(type);
        }
    }

    private void setupColor() {
        String colorString = getCommandArgument();
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

    private void setupSpeed() {
        String speedStr = getCommandArgument();
        if (speedStr.matches("^[0-9]+$")) {
            long speed = Long.parseLong(speedStr);
            if (speed <= 0) {
                speed = 1;
            }
            maxCounter = Math.round((1.0 * defaultMax) / speed * defaultMax);
            if (maxCounter < minCounter) {
                maxCounter = minCounter;
            }
        }
        logger.fine("maxCounter = " + maxCounter);
    }

    private void internalDoCommand(CommandType type) {
        viewTransform.doRotation(type);
    }

    private void internalTearDown(CommandType type) {
        if (type == CommandType.NOP || type == CommandType.COLOR
                || type == CommandType.SPEED) {
            resetCurrentCommand();
            return;
        }
        if (viewCommand.contains(type)) {
            viewTransform.teardownRotation(type);
            resetCurrentCommand();
            return;
        }
    }

    private void setupView(CommandType type) {
        logger.fine("type = " + type);
        if (type == CommandType.VIEW_RESET) {
            viewTransform.reset();
        } else {
            viewAngle = Math.PI / 2.0 / maxCounter;
            if (type == CommandType.VIEW_LEFT || type == CommandType.VIEW_UP) {
                viewAngle = -viewAngle;
            }
            counter = maxCounter;
            viewTransform.setupRotate(type, viewAngle);
        }
    }

    protected abstract void doCommand(CommandType type);

    protected abstract void setupCommand(CommandType type);

    protected abstract void tearDown(CommandType type);

    protected abstract String[][] makeRubikColors(String colors);
}
