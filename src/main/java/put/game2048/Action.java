package put.game2048;

public enum Action {
	UP(0), RIGHT(1), DOWN(2), LEFT(3);

	private final int id;

	Action(int id) {
		this.id = id;
	}

	int getId() {
		return id;
	}
	
	public static Action get_action(int i) {
		switch(i) {
		case(0):
			return Action.UP;
		
		case(1):
			return Action.RIGHT;
		case(2):
			return Action.DOWN;
		case(3):
			return Action.LEFT;
		}
		return null;
		
	}
}
