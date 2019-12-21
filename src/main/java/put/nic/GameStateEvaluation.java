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
        int gameStateScore = 0;

        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                currentCellValue = REWARDS[board.getValue(row, column)];

                // Simulate checking above, below, left and right
                highestPossibleScore += currentCellValue * 4;

                // Check below connection if present
                if (row + 1 < 4) {
                    belowCellValue = REWARDS[board.getValue(row + 1, column)];
                    verticalDifference = abs(currentCellValue - belowCellValue);

                    gameStateScore += verticalDifference;
                }

                // Check right connection if present
                if (column + 1 < 4) {
                    rightCellValue = REWARDS[board.getValue(row, column + 1)];
                    horizontalDifference = abs(currentCellValue - rightCellValue);

                    gameStateScore += horizontalDifference;
                }
            }
        }

        if (highestPossibleScore == 0) {
            return 1;
        } else {
            return highestPossibleScore - gameStateScore;
        }
    }
}
