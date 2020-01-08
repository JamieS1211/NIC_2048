package put.game2048;

import java.time.Duration;
import java.util.List;

import nic.MultLayeredNN;

public interface Agent {
	public Action chooseAction(MultLayeredNN player, Board board, List<Action> possibleActions, Duration maxTime);
}