package put.nic;

import put.game2048.Board;

import static java.lang.Math.abs;
import static nic.Utils.printBoard;
import static put.ci.cevo.games.game2048.State2048.REWARDS;

public class GameStateEvaluation {
    /**
     * Function that evaluates how good a board state is. High is good, between 0 and 1.
     *
     * @param board
     * @return
     */
    public static int getGameStateScore(Board board) {
        double currentCellValue, rightCellValue, belowCellValue;
        double verticalDifference, horizontalDifference;
        int highestPossibleScore = 0;
        int gameStateScore = 0; // This should be minimized for a good score

        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                currentCellValue = REWARDS[board.getValue(row, column)];

                // Simulate checking above, below, left and right
                highestPossibleScore += currentCellValue * 2; // above / below
                highestPossibleScore += currentCellValue * 2 * 2; // left / right - double to encourage lining up left to right

                // Check below connection if present
                if (row + 1 < 4) {
                    belowCellValue = REWARDS[board.getValue(row + 1, column)];
                    verticalDifference = abs(currentCellValue - belowCellValue);

                    if (belowCellValue == 0) {
                        // Decrease gameStateScore increase to encourage 0s (aka encourage a merge)
                        gameStateScore += verticalDifference / 2;
                    } else {
                        gameStateScore += verticalDifference;
                    }
                }

                // Check right connection if present
                if (column + 1 < 4) {
                    rightCellValue = REWARDS[board.getValue(row, column + 1)];
                    horizontalDifference = abs(currentCellValue - rightCellValue) * 2; // double to encourage lining up left to right

                    if (rightCellValue == 0) {
                        // Decrease gameStateScore increase to encourage 0s (aka encourage a merge)
                        gameStateScore += horizontalDifference / 2;
                    } else {
                        gameStateScore += horizontalDifference;
                    }
                }
            }
        }

        if (highestPossibleScore == 0) {
            return 1;
        } else {
            //printBoard(board);
            return highestPossibleScore - gameStateScore;
        }
    }
}
