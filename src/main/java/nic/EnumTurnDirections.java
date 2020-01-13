package nic;

public enum EnumTurnDirections {
    RIGHT       (0,-1),
    STRAIGHT    (1,0),
    LEFT        (2, 1);

    public final int turnID;
    public final int turnValue;

    EnumTurnDirections(int turnID, int turnValue) {
        this.turnID = turnID;
        this.turnValue = turnValue;
    }

    public static EnumTurnDirections getTurnData(int turnID) {
        for (EnumTurnDirections turnEnum : values()) {
            if (turnEnum.turnID == turnID) {
                return turnEnum;
            }
        }
        return null;
    }
}
