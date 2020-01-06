package put.nic;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static put.ci.cevo.games.game2048.State2048.REWARDS;

public class CustomAgent implements Agent {
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
	public MultLayeredNN brain;
	Action[] moves = {Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};

	public CustomAgent() {
		//The brain here should be read from the file that saved the best brain
		final int[] brainLayerSizes = {16, 16, 4};
		this.brain = new MultLayeredNN(brainLayerSizes, 0);
	}

	public CustomAgent(MultLayeredNN brain) {
		this.brain = brain;
	}

	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
		Preconditions.checkArgument(0 < possibleActions.size());

		float[] startingActivations= new float[16];

		for (int row = 0; row < 4; row++) {
			for (int column = 0; column < 4; column++) {
				startingActivations[row * 4 + column] = REWARDS[board.getValue(row, column)];
			}
		}

		//Chose what action to do
		float[] output = brain.feedforward(startingActivations);
		float largestPossibleMove = 0;
		int largestPossibleMoveLocation = 0;

		for (int moveIndex = 0; moveIndex < moves.length; moveIndex++) {
			Action action = moves[moveIndex];
			if (possibleActions.contains(action)) {
				if (output[moveIndex] > largestPossibleMove || moveIndex == 0) {
					largestPossibleMove = output[moveIndex];
					largestPossibleMoveLocation = moveIndex;
				}
			}
		}

		return moves[largestPossibleMoveLocation];
	}
}