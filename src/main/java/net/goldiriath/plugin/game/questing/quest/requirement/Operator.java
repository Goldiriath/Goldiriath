package net.goldiriath.plugin.game.questing.quest.requirement;

public enum Operator implements Operatable {

    LESSER("<") {
        @Override
        public boolean operate(int a, int b) {
            return a < b;
        }
    },
    GREATER(">") {
        @Override
        public boolean operate(int a, int b) {
            return a > b;
        }
    },
    LESSER_OR_EQUAL("<=") {
        @Override
        public boolean operate(int a, int b) {
            return a <= b;
        }
    },
    GREATER_OR_EQUAL(">=") {
        @Override
        public boolean operate(int a, int b) {
            return a >= b;
        }
    },
    EQUAL("==") {
        @Override
        public boolean operate(int a, int b) {
            return a == b;
        }
    },
    NOT_EQUAL("!=") {
        @Override
        public boolean operate(int a, int b) {
            return a != b;
        }
    };

    private final String operator;

    private Operator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public static Operator fromOperator(String operator) {
        for (Operator op : values()) {
            if (op.getOperator().equals(operator)) {
                return op;
            }
        }
        return null;
    }

}
