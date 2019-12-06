package put.nic;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.RandomUtils;
import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class CustomAgent implements Agent {
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
	public Brain brain;
	Action[] moves = {Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};

	public CustomAgent() {
		//The brain here should be read from the file that saved the best brain
		final int[] brainLayerSizes = {16, 16, 4};
		this.brain = new Brain(brainLayerSizes);
	}

	public CustomAgent(Brain brain) {
		this.brain = brain;
	}

	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
		Preconditions.checkArgument(0 < possibleActions.size());

		double[] startingActivations= new double[16];

		for (int row = 0; row < 4; row++) {
			for (int column = 0; column < 4; column++) {
				startingActivations[row * 4 + column] = board.getValue(row, column);
			}
		}

		//Chose what action to do
		double[] output = brain.propagateThought(startingActivations);
		double[] sortedOutput = output.clone();
		Arrays.sort(sortedOutput);

		for (int i = sortedOutput.length - 1; i >= 0; i--) {
			double searchFor = sortedOutput[i];
			int index = IntStream.
					range(0, output.length)
					.filter(j -> searchFor == output[j])
					.findFirst()
					.orElse(-1);

			if (index != -1) {
				Action action = moves[index];
				if (possibleActions.contains(action)) {
					return action;
				}
			}
		}

		return null;
	}
}