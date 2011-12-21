package j3d;

import java.util.logging.Logger;

public final class CubeBehavior2x2x2 extends CubeBehavior {
	Logger logger = Logger.getLogger(CubeBehavior2x2x2.class.getCanonicalName());
	private static final int[] colorToPos = {0, 1, 2, 3, 2, 3, 6, 7, 3, 1, 7, 5,
		1, 0, 5, 4, 0, 2, 4, 6, 6, 7, 4, 5}; 
    private static final int[] upIndexes = { 0, 1, 2, 3};
    private static final int[] rightIndexes = { 1, 3, 5, 7};
    private static final int[] frontIndexes = { 2, 3, 6, 7 };
    private static final int[] normalIdxFrom = { 2, 0, 3, 1 };
    private static final int[] doubleIdxFrom = { 3, 2, 1, 0 };
    private static final int[] inverseIdxFrom = { 1, 3, 0, 2 };
    private static final double totalAngle = Math.PI / 2.0;

	private Cubie[] moving;
	private double angle;

	/**
	 * コンストラクタ
	 * @param cubies
	 */
	public CubeBehavior2x2x2() {
        super(makeRubik(), 6.0);
		logger.fine("constructor called");
		moving = new Cubie[4];
	}
	
	private static Cubie[] makeRubik() {
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

	@Override
	protected void tearDown(CommandType type) {
		logger.fine("tear down");

		for (Cubie c : moving) {
			c.teardownRotation(type);
		}
		int[] indexes;
        int[] toIdx;
		if (isNormalSetFrom(type)) {
            indexes = normalIdxFrom; 
		} else if (type.isDouble()) {
		    indexes = doubleIdxFrom;
		} else if (isInverseSetFrom(type)) {
		    indexes = inverseIdxFrom;
		} else {
		    indexes = new int[0];
		}
        if (type.isUp()) {
            toIdx = upIndexes;
        } else if (type.isRight()) {
            toIdx = rightIndexes;
        } else if (type.isFront()) {
            toIdx = frontIndexes;
        } else {
            toIdx = new int[0];
        }
		for (int i = 0; i < 4; i++) {
			cubies[toIdx[i]] = moving[indexes[i]];
		}
        resetCurrentCommand();
	}

	@Override
	protected void setupCommand(CommandType type) {
        angle = totalAngle / getMaxCounter();
        double toAngle = totalAngle;
        int[] indexes;
        if (type.isUp()) {
            indexes = upIndexes;
        } else if (type.isRight()) {
            indexes = rightIndexes;
        } else if (type.isFront()) {
            indexes = frontIndexes;
        } else {
            logger.severe("Unexpected case");
            indexes = new int[0];
        }
        for (int i = 0; i < 4; i++) {
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
            c.setupRotation(type, angle, toAngle);
        }
	}
	
	protected String[][] makeRubikColors(String str) {
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
	
	protected void doCommand(CommandType type) {
        for (int i = 0; i < moving.length; i++) {
            moving[i].rotate();
        }
	}	
}
