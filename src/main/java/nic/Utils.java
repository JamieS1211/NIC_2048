package nic;

import put.ci.cevo.games.game2048.State2048;
import put.game2048.Action;
import put.game2048.Board;

public class Utils {
	/**
	 * Print the 22048 board from state
	 * @param board - Board to print
	 */
	static private void printBoard(int[][] board) {
		State2048 s = new State2048(board);
		s.printHumanReadable();
	}

	/**
	 * Print the 2048 board from board object
	 * @param board - Board object to print
	 */
	static public void printBoard(Board board) {
		printBoard(board.get());
	}

	/**
	 * Simulates a movement and returns the board state that would follow that move
	 * @param board - Board state to simulate the action on
	 * @param action - Action to simulate
	 * @return - Board state that would result from action
	 */
	static public int[][] simulateMove(int[][] board, Action action) {
		/* Adapted from State2048.java/moveUP */
		int[][] simulatedBoard = board.clone();

		switch(action) {
			case UP:
				for (int col = 0;  col >= 0 && col < 4; col++) {
					int firstFreeRow = 0;
					boolean alreadyAggregated = false;

					for (int row = firstFreeRow; row >= 0 && row < 4; row++) {
						if (simulatedBoard[row][col] == 0) {
							continue;
						}

						if (firstFreeRow > 0 && !alreadyAggregated && simulatedBoard[firstFreeRow - 1][col] == simulatedBoard[row][col]) {
							simulatedBoard[firstFreeRow - 1][col]++;
							simulatedBoard[row][col] = 0;
							alreadyAggregated = true;
						} else {
							simulatedBoard[row][col] = 0;
							simulatedBoard[firstFreeRow++][col] = simulatedBoard[row][col];
							alreadyAggregated = false;
						}
					}
				}
				return simulatedBoard;
			case DOWN:
				for (int col = 0; col < 4; col++) {
					int firstFreeRow = 3;
					boolean alreadyAggregated = false;

					for (int row = 3; row >= 0; row--) {
						if (simulatedBoard[row][col] == 0) {
							continue;
						}

						if (firstFreeRow < 3 && !alreadyAggregated && simulatedBoard[firstFreeRow + 1][col] == simulatedBoard[row][col]) {
							simulatedBoard[firstFreeRow + 1][col]++;
							simulatedBoard[row][col] = 0;
							alreadyAggregated = true;
						} else {
							int temp = simulatedBoard[row][col];
							simulatedBoard[row][col] = 0;
							simulatedBoard[firstFreeRow--][col] = temp;
							alreadyAggregated = false;
						}
					}
				}
				return simulatedBoard;
			case LEFT:
				for (int row = 0; row < 4; row++) {
					int firstFreeCol = 0;
					boolean alreadyAggregated = false;

					for (int col = 0; col < 4; col++) {
						if (simulatedBoard[row][col] == 0) {
							continue;
						}

						if (firstFreeCol > 0 && !alreadyAggregated && simulatedBoard[row][firstFreeCol - 1] == simulatedBoard[row][col]) {
							simulatedBoard[row][firstFreeCol - 1]++;
							simulatedBoard[row][col] = 0;
							alreadyAggregated = true;
						} else {
							int temp = simulatedBoard[row][col];
							simulatedBoard[row][col] = 0;
							simulatedBoard[row][firstFreeCol++] = temp;
							alreadyAggregated = false;
						}
					}
				}
				return simulatedBoard;
			case RIGHT:
				for (int row = 0; row < 4; row++) {
					int firstFreeCol = 3;
					boolean alreadyAggregated = false;

					for (int col = 3; col >= 0; col--) {
						if (simulatedBoard[row][col] == 0) {
							continue;
						}

						if (firstFreeCol < 3 && !alreadyAggregated && simulatedBoard[row][firstFreeCol + 1] == simulatedBoard[row][col]) {
							simulatedBoard[row][firstFreeCol + 1]++;
							simulatedBoard[row][col] = 0;
							alreadyAggregated = true;
						} else {
							int temp = simulatedBoard[row][col];
							simulatedBoard[row][col] = 0;
							simulatedBoard[row][firstFreeCol--] = temp;
							alreadyAggregated = false;
						}
					}
				}
				return simulatedBoard;
			default:
				throw new IllegalStateException("Unexpected value: " + action);
		}
	}
}
