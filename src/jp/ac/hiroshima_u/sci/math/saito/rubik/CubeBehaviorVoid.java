package jp.ac.hiroshima_u.sci.math.saito.rubik;


public class CubeBehaviorVoid extends CubeBehavior3x3x3 {
    private static int[] voidIndexes = {4, 10, 12, 13, 14, 16, 22};

    public CubeBehaviorVoid() {
        super(makeRubikVoid(), 8.0);
    }

    private static boolean isVoidIndex(int index) {
        for (int i = 0; i < voidIndexes.length; i++) {
            if (voidIndexes[i] == index) {
                return true;
            }
        }
        return false;
    }
    
    private static Cubie[] makeRubikVoid() {
        Cubie[] rubik = new Cubie[27];
        double size = 0.5;
        double alpha = size * 2.1;
        double[] sign = { -1.0, 0, 1.0 };
        for (int i = 0; i < rubik.length; i++) {
            double x = sign[i % 3] * alpha;
            double z = sign[(i / 3) % 3] * alpha;
            double y = -sign[(i / 9) % 3] * alpha;
            if (isVoidIndex(i)) {
                rubik[i] = new Cubie(0.001, x, y, z);
            } else {
                rubik[i] = new Cubie(size, x, y, z);
            }
        }
        return rubik;
    }
}
