package put.game2048;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.Game2048;
import put.ci.cevo.games.game2048.Player2048;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.util.Pair;

import nic.Utils;

public class Game {

	private final Duration actionTimeLimitSoft;
	
	///Phoenix 04/01/2020
	private int[][][] states=null;
	private int[] actions=null;
	///////

	public Game(Duration actionTimeLimit) {
		this.actionTimeLimitSoft = actionTimeLimit;
	}
	
	public void update_actions(int act) {
		
		if(this.actions == null) {
			this.actions = new int[1];
			this.actions[0] = act;
		}
		else {
		
			
			int[] n_actions = new int[this.actions.length+1];
		
			for (int i=0; i<this.actions.length; i++) {
				n_actions[i] = this.actions[i];
			}
			n_actions[n_actions.length-1] = act;
			this.actions = n_actions;
		}
		
	}
	
	public int[][][] get_states() {
		return this.states;
		
	}
	///Phoenix 04/01/2020
	public void update_states(State2048 state) {
		int[][] c_state = state.getBoard();		
		if(this.states == null) {
			this.states = new int[1][c_state.length][c_state[0].length];
			this.states[0] = c_state;
		}
		else {
		
			int[][][] temp_state = this.states;
			int[][][] n_states = new int[this.states.length+1][c_state.length][c_state[0].length];
		
			for (int i=0; i<this.states.length; i++) {
				n_states[i] = temp_state[i];
			}
			n_states[n_states.length-1] = c_state;
			this.states = n_states;
		}
	}
	//////////////////

	public GameResult playSingle(Supplier<Agent> agentSupplier, RandomDataGenerator random) {
		Preconditions.checkNotNull(agentSupplier);
		Preconditions.checkNotNull(random);

		Agent agent = agentSupplier.get();

		GameResult.Builder gameResult = new GameResult.Builder();

		// I am wrapping up our original implementation
		Game2048 game = new Game2048();
		Pair<Integer, Integer> result = game.playGame(new Player2048() {
			@Override
			public Action2048 chooseAction(State2048 state, List<Action2048> actions) {
				//Utils.printBoard(state.getBoard());
				update_states(state);
				List<Action> possibleActions = actions.stream().map(Game::toAction).collect(Collectors.toList());
				Stopwatch stopwatch = new Stopwatch();
				stopwatch.start();
				Action action = agent.chooseAction(new Board(state.getBoard()), possibleActions, actionTimeLimitSoft);
				update_actions(action.getId());
				if (action == null || !possibleActions.contains(action))
					throw new IllegalStateException("The agent made an illegal action");
				long elapsedNanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
				gameResult.addActionDuration(elapsedNanos);

				return toAction2048(action);
			}
		}, random);
		
		
		//Phoenix 04/01/2020
		//Allows to get board states throughout a game, once it has completed
		GameResult g_result = gameResult.build(result.first(), result.second());
		g_result.set_states(this.states);
		g_result.set_actions(this.actions);
		
		return g_result;
	}

	public MultipleGamesResult playMultiple(Supplier<Agent> agentSupplier, int numGames,
			RandomDataGenerator random) {
		Preconditions.checkNotNull(agentSupplier);
		Preconditions.checkArgument(numGames > 0);
		Preconditions.checkNotNull(random);

		MultipleGamesResult result = new MultipleGamesResult();
		for (int i = 0; i < numGames; ++i) {
			result.addGameResult(playSingle(agentSupplier, random));
		}
		return result;
	}

	static Action2048 toAction2048(Action action) {
		//System.out.println(action);
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
		//System.out.println(action);
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
