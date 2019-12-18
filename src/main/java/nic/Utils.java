package nic;

import put.ci.cevo.games.game2048.State2048;
import put.game2048.Action;
import put.game2048.Board;

public class Utils {
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
