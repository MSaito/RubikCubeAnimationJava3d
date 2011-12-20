package j3d;

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
    private EnumSet<CommandType> viewCommand;
    private EnumSet<CommandType> internalCommand;
 
    /*
     * protected final EnumSet は安全ではない。むしろかなり危険。
     * ImmutableEnumSet を使うべきである。もし、あれば。
     * enum が便利なクラスならば、EnumSetもまた便利なクラスであるが、
     * enum はimmutable なのに EnumSet は mutable なので、危険。
     * サブクラスに見せる必要性がなければ、問題ないのだが。
     */
    /**
     * U1, U2, U3 をまとめた
     */
    protected final EnumSet<CommandType> up = EnumSet.of(CommandType.U1,
            CommandType.U2, CommandType.U3);
    protected final EnumSet<CommandType> down = EnumSet.of(CommandType.D1,
            CommandType.D2, CommandType.D3);
    protected final EnumSet<CommandType> right = EnumSet.of(CommandType.R1,
            CommandType.R2, CommandType.R3);
    protected final EnumSet<CommandType> left = EnumSet.of(CommandType.L1,
            CommandType.L2, CommandType.L3);
    protected final EnumSet<CommandType> front = EnumSet.of(CommandType.F1,
            CommandType.F2, CommandType.F3);
    protected final EnumSet<CommandType> back = EnumSet.of(CommandType.B1,
            CommandType.B2, CommandType.B3);
    protected final EnumSet<CommandType> two;
    protected final EnumSet<CommandType> inverse;
    protected final EnumSet<CommandType> counterSide;
    protected final EnumSet<CommandType> normalSetFrom;
    protected final EnumSet<CommandType> doubleSetFrom;
    protected final EnumSet<CommandType> inverseSetFrom;

    private long defaultMax = RubikProperties.getInt("cubebehavior.maxCounter");
    private long maxCounter = defaultMax;
    private long minCounter = 10;
    private long counter;
    private double viewAngle;

    private ViewPoint viewTransform;
    protected Cubie[] cubies;
    private Cubie[] initialCubies;

    private final WakeupOnElapsedFrames wakeUp;

    public CubeBehavior(Cubie[] cubies, double viewDistance) {
        two = EnumSet.of(CommandType.U2, CommandType.R2, CommandType.F2);
        two.addAll(EnumSet.of(CommandType.D2, CommandType.L2, CommandType.B2));
        inverse = EnumSet.of(CommandType.U3, CommandType.R3, CommandType.F3);
        inverse.addAll(EnumSet.of(CommandType.D3, CommandType.L3,
                CommandType.B3));
        counterSide = EnumSet
                .of(CommandType.D1, CommandType.D2, CommandType.D3);
        counterSide.addAll(EnumSet.of(CommandType.L1, CommandType.L2,
                CommandType.L3));
        counterSide.addAll(EnumSet.of(CommandType.B1, CommandType.B2,
                CommandType.B3));
        normalSetFrom = EnumSet.of(CommandType.U1, CommandType.R3,
                CommandType.F1);
        normalSetFrom.addAll(EnumSet.of(CommandType.D3, CommandType.L1,
                CommandType.B3));
        doubleSetFrom = EnumSet.of(CommandType.U2, CommandType.R2,
                CommandType.F2);
        doubleSetFrom.addAll(EnumSet.of(CommandType.D2, CommandType.L2,
                CommandType.B2));
        inverseSetFrom = EnumSet.of(CommandType.U3, CommandType.R1,
                CommandType.F3);
        inverseSetFrom.addAll(EnumSet.of(CommandType.D1, CommandType.L3,
                CommandType.B1));
        this.cubies = cubies;
        this.initialCubies = new Cubie[this.cubies.length];
        for (int i = 0; i < this.cubies.length; i++) {
            initialCubies[i] = cubies[i];
        }
        viewTransform = new ViewPoint(viewDistance);
        wakeUp = new WakeupOnElapsedFrames(0);
        running = false;
        command = new LinkedList<Command>();
        currentCommand = NOP;
        counter = 0;
        viewCommand = EnumSet.of(CommandType.VIEW_UP, CommandType.VIEW_DOWN,
                CommandType.VIEW_RIGHT, CommandType.VIEW_LEFT);
        viewCommand.add(CommandType.VIEW_RESET);
        internalCommand = EnumSet.of(CommandType.COLOR, CommandType.SPEED);
        internalCommand.addAll(viewCommand);
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

    public TransformGroup[] getTarget() {
        return cubies;
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
