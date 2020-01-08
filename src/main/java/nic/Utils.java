package nic;

import java.util.Arrays;
import java.util.List;

import put.ci.cevo.games.game2048.State2048;
import put.game2048.Action;
import put.game2048.Board;

public class Utils {
	
	
	//Phoenix 04/01/2020
	
	public static float[] int2float(int[] data) {
		float[] c_data = new float[data.length];
		for (int i=0;i<data.length;i++) {
			c_data[i] = (float)data[i];
		}
		return c_data;
	}
	
	public static int minValue(int[] numbers){
		  Arrays.sort(numbers);
		  return numbers[0];
		}
	
	public static float maxValue(float[] numbers){
		  Arrays.sort(numbers);
		  return numbers[numbers.length-1];
	}
	
	public static Action guess2action(float[] guess, List<Action> possibleActions) {
		
		//0 -> [1,0,0,0] (UP)
		//1 ->[0,1,0,0] (RIGHT)
		//2 ->[0,0,1,0] (DOWN)
		//3 ->[0,0,0,1] (LEFT)
		float[] guesscopy = guess.clone();
		Arrays.sort(guesscopy);
		int max_index = guess.length-1;
		float max_value = guesscopy[max_index];
		int[] possActionsInt = new int[4];
		Action act=null;
		boolean action_found = false;
		
		//System.out.println("guess2actions");
		if( possibleActions.contains(Action.UP)) {
			possActionsInt[0] = 1;
		}
		if(possibleActions.contains(Action.RIGHT)) {
			possActionsInt[1] = 1;
		}
		if(possibleActions.contains(Action.DOWN)) {
			possActionsInt[2] = 1;
		}
		if(possibleActions.contains(Action.LEFT)){
			possActionsInt[3] = 1;
		}
		//System.out.println(Arrays.toString(guesscopy));
		//System.out.println(possibleActions);
		//System.out.println(Arrays.toString(possActionsInt));
		while(action_found==false) {
			//System.out.println(max_index + " " + max_value);
			//System.out.println("--------------------------");
			for (int i=0;i<guess.length;i++) {
				//System.out.println(guess[i] + " " + possActionsInt[i]);
				if(guess[i] == max_value && possActionsInt[i]==1) {
					//System.out.println("caught");
					act = Action.get_action(i);
					action_found = true;
					return act;
				}
			}
			//System.out.println("----------------------------");
			max_index -=1;
			max_value = guesscopy[max_index];
		}
		return act;
	}
	
	//Phoenix 04/01/2020
	public static int maxValue(int[] numbers){
		  Arrays.sort(numbers);
		  return numbers[numbers.length-1];
	}
	
	public static float randomInt(float a,float b) {
		float rand = (float)(a + Math.random() * (b - a));
		return rand;
		
	}
	
	//Phoenix 04/01/2020
	static public float[] normalize_data(int[] board) {
		
		float[] normed_board = new float[board.length];
		int max_val;
		int min_val;
		
		
	
		max_val = maxValue(board);
		min_val = minValue(board);
			
		for (int p=0;p<board.length;p++) {
			float nVal = (float)(board[p]-min_val)/(max_val - min_val);
			normed_board[p] = nVal;
		}
				
		
		return normed_board;
	}
	
	
	///Flatten board to be used in NN training
	static public int[] flatten_board(int[][] board){
		int[] flattened_board = new int[board.length*board[0].length];
		int index = 0;
		for (int i=0; i<board.length;i++) {
			//System.out.println(Arrays.toString(board[i]));
			for (int j=0; j<board[0].length;j++) {
				flattened_board[index] = board[i][j];
				index +=1;
			}
			
		}
		
		return flattened_board;
	}
	
	//One-hot encode actions for NN training
	static public float[] one_hot_action(int action){
		
		//0 -> [1,0,0,0] (UP)
		//1 ->[0,1,0,0] (RIGHT)
		//2 ->[0,0,1,0] (DOWN)
		//3 ->[0,0,0,1] (LEFT)
		
		
		switch (action) {
		case 0:
			return new float[]{1,0,0,0};
		case 1:
			return new float[]{0,1,0,0};
		case 2:
			return new float[]{0,0,1,0};
		case 3:
			return new float[]{0,0,0,1};
		}	
		throw new IllegalStateException("Cannot happen");
		
	}
	
	static public void printBoard(int[][] board) {
		State2048 s = new State2048(board);
		s.printHumanReadable();
	}
	static public void printBoard(Board board) { printBoard(board.get()); }
	
	static public int[][] simulateMove(int[][] oldBoard, Action action) {
		/* Adapted from State2048.java/moveUP */
		int[][] board = oldBoard.clone();
		switch(action) {
		case UP:
			for (int col = 0; col < 4; col++) {
				int firstFreeRow = 0;
				boolean alreadyAggregated = false;
				for (int row = 0; row < 4; row++) {
					if (board[row][col] == 0) {
						continue;
					}
					if (firstFreeRow > 0 && !alreadyAggregated && board[firstFreeRow - 1][col] == board[row][col]) {
						board[firstFreeRow - 1][col]++;
						board[row][col] = 0;
						alreadyAggregated = true;
					} else {
						int temp = board[row][col];
						board[row][col] = 0;
						board[firstFreeRow++][col] = temp;
						alreadyAggregated = false;
					}
				}
			}
			return board;
		case DOWN:
			for (int col = 0; col < 4; col++) {
				int firstFreeRow = 3;
				boolean alreadyAggregated = false;
				for (int row = 3; row >= 0; row--) {
					if (board[row][col] == 0) {
						continue;
					}
					if (firstFreeRow < 3 && !alreadyAggregated && board[firstFreeRow + 1][col] == board[row][col]) {
						board[firstFreeRow + 1][col]++;
						board[row][col] = 0;
						alreadyAggregated = true;
					} else {
						int temp = board[row][col];
						board[row][col] = 0;
						board[firstFreeRow--][col] = temp;
						alreadyAggregated = false;
					}
				}
			}
			return board;
		case LEFT:
			for (int row = 0; row < 4; row++) {
				int firstFreeCol = 0;
				boolean alreadyAggregated = false;
				for (int col = 0; col < 4; col++) {
					if (board[row][col] == 0) {
						continue;
					}
					if (firstFreeCol > 0 && !alreadyAggregated && board[row][firstFreeCol - 1] == board[row][col]) {
						board[row][firstFreeCol - 1]++;
						board[row][col] = 0;
						alreadyAggregated = true;
					} else {
						int temp = board[row][col];
						board[row][col] = 0;
						board[row][firstFreeCol++] = temp;
						alreadyAggregated = false;
					}
				}
			}
			return board;
		case RIGHT:
			for (int row = 0; row < 4; row++) {
				int firstFreeCol = 3;
				boolean alreadyAggregated = false;
				for (int col = 3; col >= 0; col--) {
					if (board[row][col] == 0) {
						continue;
					}
					if (firstFreeCol < 3 && !alreadyAggregated && board[row][firstFreeCol + 1] == board[row][col]) {
						board[row][firstFreeCol + 1]++;
						board[row][col] = 0;
						alreadyAggregated = true;
					} else {
						int temp = board[row][col];
						board[row][col] = 0;
						board[row][firstFreeCol--] = temp;
						alreadyAggregated = false;
					}
				}
			}
			return board;
		}
		return null;
	}

}

