package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.util.EnumSet;

public enum CommandType {
	U1, U2, U3, R1, R2, R3, F1, F2, F3,
	D1, D2, D3, L1, L2, L3, B1, B2, B3,
	VIEW_UP, VIEW_DOWN, VIEW_RIGHT, VIEW_LEFT, VIEW_RESET,
	COLOR, SPEED, NOP;
    private static final EnumSet<CommandType> up = EnumSet.of(CommandType.U1,
            CommandType.U2, CommandType.U3);
    private static final EnumSet<CommandType> down = EnumSet.of(CommandType.D1,
            CommandType.D2, CommandType.D3);
    private static final EnumSet<CommandType> right = EnumSet.of(CommandType.R1,
            CommandType.R2, CommandType.R3);
    private static final EnumSet<CommandType> left = EnumSet.of(CommandType.L1,
            CommandType.L2, CommandType.L3);
    private static final EnumSet<CommandType> front = EnumSet.of(CommandType.F1,
            CommandType.F2, CommandType.F3);
    private static final EnumSet<CommandType> back = EnumSet.of(CommandType.B1,
            CommandType.B2, CommandType.B3);
    private static final EnumSet<CommandType> two;
    static {
        two = EnumSet.of(CommandType.U2, CommandType.R2, CommandType.F2);
        two.addAll(EnumSet.of(CommandType.D2, CommandType.L2, CommandType.B2));
    }
    private static final EnumSet<CommandType> inverse;
    static {
        inverse = EnumSet.of(CommandType.U3, CommandType.R3, CommandType.F3);
        inverse.addAll(EnumSet.of(CommandType.D3, CommandType.L3,
                CommandType.B3));
    }
    private static final EnumSet<CommandType> counterSide;
    static {
        counterSide = EnumSet
                .of(CommandType.D1, CommandType.D2, CommandType.D3);
        counterSide.addAll(EnumSet.of(CommandType.L1, CommandType.L2,
                CommandType.L3));
        counterSide.addAll(EnumSet.of(CommandType.B1, CommandType.B2,
                CommandType.B3));
    }
	//private static final EnumSet<CommandType> viewCommand;
	static {
	}
	public boolean isUp() {
	    return up.contains(this);
	}
	public boolean isDown() {
	    return down.contains(this);	    
	}
	public boolean isRight() {
	    return right.contains(this);
	}
	public boolean isLeft() {
	    return left.contains(this);
	}
	public boolean isFront() {
	    return front.contains(this);
	}
	public boolean isBack() {
	    return back.contains(this);
	}
	public boolean isDouble() {
	    return two.contains(this);
	}
	public boolean isInverse() {
	    return inverse.contains(this);
	}
	public boolean isCounterSide() {
	    return counterSide.contains(this);
	}
}
