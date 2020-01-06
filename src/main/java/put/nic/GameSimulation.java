package put.nic;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.Game2048;
import put.ci.cevo.games.game2048.Player2048;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.environment.Transition;
import put.ci.cevo.util.Pair;
import put.game2048.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static nic.Utils.printBoard;

public class GameSimulation {

	private final Duration actionTimeLimitSoft;

	public GameSimulation(Duration actionTimeLimit) {
		this.actionTimeLimitSoft = actionTimeLimit;
	}

	// If max moves is given as -1 play the full game
	public GameResult playSingle(Supplier<Agent> agentSupplier, RandomDataGenerator random, int maxMoves) {
		Preconditions.checkNotNull(agentSupplier);
		Preconditions.checkNotNull(random);

		Agent agent = agentSupplier.get();

		GameResult.Builder gameResult = new GameResult.Builder();

		// Override the game so that it only lasts for a specific number of moves and the score given the fitness not the actual score
		Game2048 game = new Game2048() {
			@Override
			public Pair<Integer, Integer> playGame(Player2048 player, RandomDataGenerator random) {
				int sumRewards = 0;
				int moves = 0;
				int fitness = 0;

				State2048 state = sampleInitialStateDistribution(random);
				List<Action2048> actions = getPossibleActions(state);
				while (!actions.isEmpty() && (maxMoves < 0 || moves < maxMoves)) {

					if (maxMoves == -2) {
						printBoard(state.getBoard());
					}

					moves++;

					//Put state evaluation here
					fitness += GameStateEvaluation.getGameStateScore(new Board(state.getBoard()));

					Action2048 action = player.chooseAction(state, actions);
					Transition<State2048, Action2048> transition = computeTransition(state, action);
					sumRewards += transition.getReward();

					state = getNextState(transition.getAfterState(), random);
					actions = getPossibleActions(state);
				}

				if (maxMoves == -1) {
					return new Pair<>(sumRewards, state.getMaxTile());
				} else {
					return new Pair<>(fitness, state.getMaxTile());
				}
			}
		};

		// Override the player to be based on our agent
		Pair<Integer, Integer> result = game.playGame(new Player2048() {
			@Override
			public Action2048 chooseAction(State2048 state, List<Action2048> actions) {
				List<Action> possibleActions = actions.stream().map(GameSimulation::toAction).collect(Collectors.toList());
				Stopwatch stopwatch = new Stopwatch();
				stopwatch.start();
				Action action = agent.chooseAction(new Board(state.getBoard()), possibleActions, actionTimeLimitSoft);
				if (action == null || !possibleActions.contains(action))
					throw new IllegalStateException("The agent made an illegal action");
				long elapsedNanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
				gameResult.addActionDuration(elapsedNanos);

				return toAction2048(action);
			}
		}, random);



		return gameResult.build(result.first(), result.second());
	}

	public MultipleGamesResult playMultiple(Supplier<Agent> agentSupplier, int numGames,
			RandomDataGenerator random, int maxMoves) {
		Preconditions.checkNotNull(agentSupplier);
		Preconditions.checkArgument(numGames > 0);
		Preconditions.checkNotNull(random);

		MultipleGamesResult result = new MultipleGamesResult();
		for (int i = 0; i < numGames; ++i) {
			result.addGameResult(playSingle(agentSupplier, random, maxMoves));
		}
		return result;
	}

	static Action2048 toAction2048(Action action) {
		switch (action) {
		case UP:
			return Action2048.UP;
		case DOWN:
			return Action2048.DOWN;
		case LEFT:
			return Action2048.LEFT;
		case RIGHT:
			return Action2048.RIGHT;
		}
		throw new IllegalStateException("Cannot happen");
	}

	static Action toAction(Action2048 action) {
		switch (action) {
		case UP:
			return Action.UP;
		case DOWN:
			return Action.DOWN;
		case LEFT:
			return Action.LEFT;
		case RIGHT:
			return Action.RIGHT;
		}
		throw new IllegalStateException("Cannot happen");
	}
}
