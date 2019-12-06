package put.nic;

import put.game2048.Board;

import static java.lang.Math.abs;

public class GameStateEvaluation {
    /**
     * Function that evaluates how good a board state is. High is good.
     *
     * @param board
     * @return
     */
    public static int getGameStateScore(Board board) {
        int currentCellValue;
        int testTileValue;
        int highestPossibleScore = 0;
        int gameStateScore = 0;

        for (int row = 0; row < 4; row++) {
            for (int column = 0; column< 4; column++) {
                currentCellValue = board.getValue(row, column);

                if (currentCellValue != 0) {
                    //Multiplied by 2 to simulate checking both sides
                    highestPossibleScore += 2 * (2 ^ currentCellValue);
                }

                //Check down
                if (row + 1 < 4) {
                    testTileValue = board.getValue( row + 1, column);

                    if (testTileValue != 0) {
                        if (currentCellValue != 0) {
                            gameStateScore += abs((2 ^ currentCellValue) - (2 ^ testTileValue));
                        } else {
                            gameStateScore += 2 ^ testTileValue;
                        }
                    }
                }

                //Check right
                if (column + 1 < 4) {
                    testTileValue = board.getValue( row, column + 1);

                    if (testTileValue != 0) {
                        if (currentCellValue != 0) {
                            gameStateScore += abs((2 ^ currentCellValue) - (2 ^ testTileValue));
                        } else {
                            gameStateScore += 2 ^ testTileValue;
                        }
                    }
                }
            }
        }

        return highestPossibleScore - gameStateScore;
    }
}
