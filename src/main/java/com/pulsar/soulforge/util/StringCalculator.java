package com.pulsar.soulforge.util;

import java.util.ArrayList;
import java.util.List;

public class StringCalculator {
    public static float getResult(String calc) throws Exception {
        CompoundEntry current = new CompoundEntry(null);
        char[] chars = calc.toCharArray();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                if (!current.entries.isEmpty()) {
                    Entry last = current.entries.get(current.entries.size() - 1);
                    if (last instanceof IntEntry intEntry) {
                        intEntry.value = Integer.parseInt(String.valueOf(intEntry.value) + c);
                    } else if (last instanceof OperatorEntry) {
                        current.entries.add(new IntEntry(current, Integer.parseInt(Character.toString(c))));
                    } else {
                        throw new UnsupportedOperationException("Invalid operation found. Attempted to add IntEntry to CompoundEntry.");
                    }
                } else {
                    current.entries.add(new IntEntry(current, Integer.parseInt(Character.toString(c))));
                }
            } else if (c == '(') {
                CompoundEntry next = new CompoundEntry(current);
                current.entries.add(next);
                current = next;
            } else if (c == ')') {
                current = current.parent;
            } else if (!Character.isWhitespace(c)) {
                switch (c) {
                    case '+':
                        current.entries.add(new OperatorEntry(current, OperatorEntry.Operator.ADD));
                        break;
                    case '-':
                        current.entries.add(new OperatorEntry(current, OperatorEntry.Operator.SUBTRACT));
                        break;
                    case '*':
                        current.entries.add(new OperatorEntry(current, OperatorEntry.Operator.MULTIPLY));
                        break;
                    case '/':
                        current.entries.add(new OperatorEntry(current, OperatorEntry.Operator.DIVIDE));
                        break;
                }
            }
        }
        if (current.parent != null) throw new Exception("Never returned to start CompoundEntry. Are you missing some brackets?");
        return current.calculate();
    }

    public static class Entry {
        public CompoundEntry parent;

        public Entry(CompoundEntry parent) {
            this.parent = parent;
        }
    }

    public static class IntEntry extends Entry {
        public int value;

        public IntEntry(CompoundEntry parent, int value) {
            super(parent);
            this.value = value;
        }
    }

    public static class OperatorEntry extends Entry {
        public Operator operator;

        public OperatorEntry(CompoundEntry parent, Operator operator) {
            super(parent);
            this.operator = operator;
        }

        public enum Operator {
            ADD,
            SUBTRACT,
            MULTIPLY,
            DIVIDE
        }
    }
    public static class CompoundEntry extends Entry {
        public List<Entry> entries = new ArrayList<>();

        public CompoundEntry(CompoundEntry parent) {
            super(parent);
        }

        public float calculate() {
            float value = Float.NaN;
            OperatorEntry operator = null;
            for (Entry entry : this.entries) {
                if (entry instanceof IntEntry intEntry) {
                    if (Float.isNaN(value)) value = intEntry.value;
                    else if (operator != null) {
                        switch (operator.operator) {
                            case ADD -> value = value + intEntry.value;
                            case SUBTRACT -> value = value - intEntry.value;
                            case MULTIPLY -> value = value * intEntry.value;
                            case DIVIDE -> value = value / intEntry.value;
                        }
                    }
                } else if (entry instanceof OperatorEntry operatorEntry) {
                    operator = operatorEntry;
                } else if (entry instanceof CompoundEntry compoundEntry) {
                    float entryVal = compoundEntry.calculate();
                    if (Float.isNaN(value)) value = entryVal;
                    else if (operator != null) {
                        switch (operator.operator) {
                            case ADD -> value = value + entryVal;
                            case SUBTRACT -> value = value - entryVal;
                            case MULTIPLY -> value = value * entryVal;
                            case DIVIDE -> value = value / entryVal;
                        }
                    }
                }
            }
            return value;
        }
    }
}