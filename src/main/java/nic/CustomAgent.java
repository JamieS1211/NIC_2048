package nic;

import java.time.Duration;
import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.RandomUtils;
import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;

public class CustomAgent implements Agent {
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
		System.out.println(possibleActions);
		Preconditions.checkArgument(0 < possibleActions.size());

		//return (Action)RandomUtils.
		return (Action)RandomUtils.pickRandom(possibleActions.toArray(), random);
	}
}
