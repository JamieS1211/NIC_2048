package nic;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;

public class CustomAgent implements Agent {
	public Random random = new Random(123);

	// A nonparametric constructor is required

    /** timeLimit will always be 1 ms */
	public Action chooseAction(Board board, List<Action> possibleActions, Duration timeLimit) {
	    // Prefer right direction
	    if (possibleActions.contains(Action.RIGHT)) {
	        return Action.RIGHT;
        } else {
            // If cannot go right, then make a random move
            return possibleActions.get(random.nextInt(possibleActions.size()));
        }
	}
}
