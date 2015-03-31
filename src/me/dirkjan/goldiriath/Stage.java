package me.dirkjan.goldiriath;

public enum Stage {

    STAGE_A,
    STAGE_B,
    STAGE_C;

    @Override
    public String toString() {
        if (this == STAGE_A) {
            return "Quest01a";
        } else {
            if (this == STAGE_B) {
                return "Quest01b";
            } else {
                return "Quest01c";
            }

        }
    }

    public static Stage fromString(String stagename) {
        for (Stage stage : Stage.values()) {
            if (stage.toString().equals(stagename)) {
                return stage;
            }
        }
        return null;
    }
}
