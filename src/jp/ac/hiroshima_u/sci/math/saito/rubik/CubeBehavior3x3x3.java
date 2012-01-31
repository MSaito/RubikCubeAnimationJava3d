package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.util.HashMap;
import java.util.logging.Logger;

public class CubeBehavior3x3x3 extends CubeBehavior {
    private final Logger logger = Logger.getLogger(CubeBehavior3x3x3.class
            .getCanonicalName());

    private HashMap<CommandType, CommandType> counterMap;
    private static final int[] upIndexes = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
    private static final int[] downIndexes = { 18, 19, 20, 21, 22, 23, 24, 25,
            26 };
    private static final int[] rightIndexes = { 2, 5, 8, 11, 14, 17, 20, 23, 26 };
    private static final int[] leftIndexes = { 0, 3, 6, 9, 12, 15, 18, 21, 24 };
    private static final int[] frontIndexes = { 6, 7, 8, 15, 16, 17, 24, 25, 26 };
    private static final int[] backIndexes = { 0, 1, 2, 9, 10, 11, 18, 19, 20 };
    private static final int[] normalIdxFrom = { 6, 3, 0, 7, 4, 1, 8, 5, 2 };
    private static final int[] doubleIdxFrom = { 8, 7, 6, 5, 4, 3, 2, 1, 0 };
    private static final int[] inverseIdxFrom = { 2, 5, 8, 1, 4, 7, 0, 3, 6 };

    private static final int[] colorToPos = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 6, 7,
            8, 15, 16, 17, 24, 25, 26, 8, 5, 2, 17, 14, 11, 26, 23, 20, 2, 1,
            0, 11, 10, 9, 20, 19, 18, 0, 3, 6, 9, 12, 15, 18, 21, 24, 24, 25,
            26, 21, 22, 23, 18, 19, 20 };
    private static final double totalAngle = Math.PI / 2.0;

    private Cubie[] moving;
    private double angle;

    /**
     * コンストラクタ
     * 
     * @param cubies
     */
    public CubeBehavior3x3x3() {
        super(makeRubik3x3x3(), 8.0);
        constructerHelper();
    }
    
    private void constructerHelper() {
        logger.fine("constructor called");
        moving = new Cubie[9];
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
    }
    public CubeBehavior3x3x3(Cubie[] cubies, double distance) {
        super(cubies, distance);
        constructerHelper();
    }
    
    private static Cubie[] makeRubik3x3x3() {
        Cubie[] rubik = new Cubie[27];
        double size = 0.5;
        double alpha = size * 2.1;
        double[] sign = { -1.0, 0, 1.0 };
        for (int i = 0; i < rubik.length; i++) {
            double x = sign[i % 3] * alpha;
            double z = sign[(i / 3) % 3] * alpha;
            double y = -sign[(i / 9) % 3] * alpha;
            rubik[i] = new Cubie(size, x, y, z);
        }
        return rubik;
    }

    @Override
    protected void tearDown(CommandType type) {
        logger.fine("tear down");
        for (Cubie c : moving) {
            if (type.isCounterSide()) {
                c.teardownRotation(counterMap.get(type));
            } else {
                c.teardownRotation(type);
            }
        }
        int[] fromIdxs;
        int[] toIdxs;
        if (isNormalSetFrom(type)) {
            fromIdxs = normalIdxFrom;
        } else if (type.isDouble()) {
            fromIdxs = doubleIdxFrom;
        } else if (isInverseSetFrom(type)) {
            fromIdxs = inverseIdxFrom;
        } else {
            logger.severe("Unexpected case");
            fromIdxs = new int[0];
        }
        if (type.isUp()) {
            toIdxs = upIndexes;
        } else if (type.isDown()) {
            toIdxs = downIndexes;
        } else if (type.isRight()) {
            toIdxs = rightIndexes;
        } else if (type.isLeft()) {
            toIdxs = leftIndexes;
        } else if (type.isFront()) {
            toIdxs = frontIndexes;
        } else if (type.isBack()) {
            toIdxs = backIndexes;
        } else {
            logger.severe("Unexpected case");
            toIdxs = new int[0];
        }
        for (int i = 0; i < toIdxs.length; i++) {
            cubies[toIdxs[i]] = moving[fromIdxs[i]];
        }
        resetCurrentCommand();
    }

    @Override
    protected void setupCommand(CommandType type) {
        logger.fine("setupCommand type:" + type);
        angle = totalAngle / getMaxCounter();
        double toAngle = totalAngle;
        int[] indexes;
        if (type.isUp()) {
            indexes = upIndexes;
        } else if (type.isDown()) {
            indexes = downIndexes;
        } else if (type.isRight()) {
            indexes = rightIndexes;
        } else if (type.isLeft()) {
            indexes = leftIndexes;
        } else if (type.isFront()) {
            indexes = frontIndexes;
        } else if (type.isBack()) {
            indexes = backIndexes;
        } else {
            logger.severe("Unexpected case");
            indexes = new int[0];
        }
        for (int i = 0; i < moving.length; i++) {
            moving[i] = cubies[indexes[i]];
        }
        if (type.isDouble()) {
            setCounter(getMaxCounter() * 2);
        } else {
            setCounter(getMaxCounter());
            if (type.isInverse()) {
                angle = -angle;
                toAngle = -toAngle;
            }
        }
        for (Cubie c : moving) {
            if (type.isCounterSide()) {
                c.setupRotation(counterMap.get(type), -angle, -toAngle);
            } else {
                c.setupRotation(type, angle, toAngle);
            }
        }
    }

    @Override
    protected String[][] makeRubikColors(String str) {
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

    @Override
    protected void doCommand(CommandType type) {
        for (int i = 0; i < moving.length; i++) {
            moving[i].rotate();
        }
    }
}
