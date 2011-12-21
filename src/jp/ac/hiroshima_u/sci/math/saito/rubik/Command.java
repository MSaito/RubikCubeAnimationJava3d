package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.util.HashMap;

public class Command {
	private static final HashMap<String, CommandType> map = makeHashMap();
	private CommandType type;
	private String argument;

	public Command(CommandType type, String argument) {
		this.type = type;
		this.argument = argument;
	}
	
	public String getArgument() {
		return argument;
	}
	
	public CommandType getType() {
		return type;
	}
	
	private static HashMap<String, CommandType> makeHashMap() {
		HashMap<String, CommandType> map = new HashMap<String, CommandType>();
		map.put("U1", CommandType.U1);
		map.put("U2", CommandType.U2);
		map.put("U3", CommandType.U3);
		map.put("R1", CommandType.R1);
		map.put("R2", CommandType.R2);
		map.put("R3", CommandType.R3);
		map.put("F1", CommandType.F1);
		map.put("F2", CommandType.F2);
		map.put("F3", CommandType.F3);
        map.put("D1", CommandType.D1);
        map.put("D2", CommandType.D2);
        map.put("D3", CommandType.D3);
        map.put("L1", CommandType.L1);
        map.put("L2", CommandType.L2);
        map.put("L3", CommandType.L3);
        map.put("B1", CommandType.B1);
        map.put("B2", CommandType.B2);
        map.put("B3", CommandType.B3);
		map.put("VIEW_UP", CommandType.VIEW_UP);
		map.put("VIEW_DOWN", CommandType.VIEW_DOWN);
		map.put("VIEW_RIGHT", CommandType.VIEW_RIGHT);
		map.put("VIEW_LEFT", CommandType.VIEW_LEFT);
		map.put("VIEW_RESET", CommandType.VIEW_RESET);
		map.put("COLOR", CommandType.COLOR);
		map.put("SPEED", CommandType.SPEED);
		map.put("NOP", CommandType.NOP);
		return map;
	}

	public static CommandType getCommandType(String str) {
		return map.get(str);
	}
	
	@Override
	public String toString() {
		return type.toString() + "(" + argument + ")";
	}
}
