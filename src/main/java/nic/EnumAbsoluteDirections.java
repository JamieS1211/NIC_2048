package nic;

public enum EnumAbsoluteDirections {
    UP       (0, 0, -1),
    LEFT     (1,-1, 0),
    DOWN     (2, 0, 1),
    RIGHT    (3, 1, 0);

    public final int directionID;
    public final int xDiff;
    public final int yDiff;

    EnumAbsoluteDirections(int directionID, int xDiff, int yDiff) {
        this.directionID = directionID;
        this.xDiff = xDiff;
        this.yDiff = yDiff;
    }

    public static EnumAbsoluteDirections getDirectionData(int directionID) {
        for (EnumAbsoluteDirections turnEnum : values()) {
            if (turnEnum.directionID == directionID) {
                return turnEnum;
            }
        }
        return null;
    }
}
