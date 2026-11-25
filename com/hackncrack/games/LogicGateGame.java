package com.hackncrack.games;

import com.hackncrack.ui.AnsiColors;
import com.hackncrack.ui.ConsoleUI;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LogicGateGame extends AbstractGame {
    private static final String[] BINARY_GATES = {"AND", "OR", "XOR", "NAND", "NOR"};

    private final ConsoleUI ui;
    private final Random random;
    private final Set<String> recentExpressions;

    public LogicGateGame(ConsoleUI ui) {
        this(ui, 2);
    }

    public LogicGateGame(ConsoleUI ui, int difficulty) {
        super("Logic Gate", "Evaluate digital logic circuits", difficulty, 60);
        this.ui = ui;
        this.random = new Random();
        this.recentExpressions = new HashSet<>();
    }

    @Override
    protected ConsoleUI getUI() {
        return ui;
    }

    @Override
    protected void onSessionStart() {
        recentExpressions.clear();
    }

    @Override
    protected RoundChallenge nextChallenge() {
        if (recentExpressions.size() >= 18) {
            recentExpressions.clear();
        }

        String[] circuit;
        do {
            circuit = generateCircuit(getDifficulty());
        } while (!recentExpressions.add(circuit[0]));

        String expression = circuit[0];
        String expected = circuit[1];

        return new RoundChallenge(
                AnsiColors.BRIGHT_CYAN + "Circuit: " + expression + AnsiColors.RESET,
                "Output [0/1]: ",
                expected,
                "",
                "Correct output was: " + expected,
                false
        );
    }

    @Override
    protected String validateFormat(String rawInput) {
        if (!rawInput.equals("0") && !rawInput.equals("1")) {
            return "Invalid bit. Enter only 0 or 1.";
        }
        return null;
    }

    private String[] generateCircuit(int difficulty) {
        if (difficulty == 1) {
            if (random.nextInt(4) == 0) {
                int a = random.nextInt(2);
                return new String[]{"NOT " + a, String.valueOf(1 - a)};
            }
            int a = random.nextInt(2);
            int b = random.nextInt(2);
            String gate = BINARY_GATES[random.nextInt(BINARY_GATES.length)];
            return new String[]{a + " " + gate + " " + b, String.valueOf(evalGate(gate, a, b))};
        } else if (difficulty == 3) {
            int a = random.nextInt(2);
            int b = random.nextInt(2);
            int c = random.nextInt(2);
            int d = random.nextInt(2);
            String g1 = BINARY_GATES[random.nextInt(BINARY_GATES.length)];
            String g2 = BINARY_GATES[random.nextInt(BINARY_GATES.length)];
            String g3 = BINARY_GATES[random.nextInt(BINARY_GATES.length)];

            int out = evalGate(g3, evalGate(g1, a, b), evalGate(g2, c, d));
            String expr = "(" + a + " " + g1 + " " + b + ") " + g3 + " (" + c + " " + g2 + " " + d + ")";
            return new String[]{expr, String.valueOf(out)};
        } else {
            int a = random.nextInt(2);
            int b = random.nextInt(2);
            int c = random.nextInt(2);
            String g1 = BINARY_GATES[random.nextInt(BINARY_GATES.length)];
            String g2 = BINARY_GATES[random.nextInt(BINARY_GATES.length)];

            int inner = evalGate(g1, a, b);
            boolean invertRight = random.nextBoolean();
            int rightVal = invertRight ? (1 - c) : c;
            String rightText = invertRight ? "(NOT " + c + ")" : String.valueOf(c);
            String expr = "(" + a + " " + g1 + " " + b + ") " + g2 + " " + rightText;
            return new String[]{expr, String.valueOf(evalGate(g2, inner, rightVal))};
        }
    }

    private int evalGate(String gate, int a, int b) {
        return switch (gate) {
            case "AND" -> (a & b);
            case "OR" -> (a | b);
            case "XOR" -> (a ^ b);
            case "NAND" -> (a & b) == 1 ? 0 : 1;
            case "NOR" -> (a | b) == 1 ? 0 : 1;
            default -> 0;
        };
    }
}