package lyc.compiler.tercetos;

public class ConstantFolder {
    /**
     * Checks if a string argument represents a constant value (not an identifier or terceto reference)
     */
    public static boolean isConstant(String arg) {
        if (arg == null || arg.startsWith("[") || arg.equals("-")) {
            return false;
        }
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException e1) {
            try {
                Double.parseDouble(arg);
                return true;
            } catch (NumberFormatException e2) {
                return false; // It's an identifier
            }
        }
    }

    /**
     * Evaluates a binary operation between two constant values
     * @param op The operation (+, -, *, /, DIV, MOD)
     * @param arg1 First operand (constant)
     * @param arg2 Second operand (constant)
     * @return The computed result as a string
     * @throws RuntimeException if division by zero or invalid operation
     */
    public static String evaluate(String op, String arg1, String arg2) {
        if (!isConstant(arg1) || !isConstant(arg2)) {
            throw new IllegalArgumentException("Both arguments must be constants");
        }

        // Parse operands, handling type promotion
        Number num1 = parseNumber(arg1);
        Number num2 = parseNumber(arg2);

        // Determine result type: Float if either is Float, else Int
        boolean isFloat = num1 instanceof Double || num2 instanceof Double;

        switch (op) {
            case "+":
                if (isFloat) {
                    double addResult = num1.doubleValue() + num2.doubleValue();
                    return String.valueOf(addResult);
                } else {
                    int addResult = num1.intValue() + num2.intValue();
                    return String.valueOf(addResult);
                }

            case "-":
                if (isFloat) {
                    double subResult = num1.doubleValue() - num2.doubleValue();
                    return String.valueOf(subResult);
                } else {
                    int subResult = num1.intValue() - num2.intValue();
                    return String.valueOf(subResult);
                }

            case "*":
                if (isFloat) {
                    double mulResult = num1.doubleValue() * num2.doubleValue();
                    return String.valueOf(mulResult);
                } else {
                    int mulResult = num1.intValue() * num2.intValue();
                    return String.valueOf(mulResult);
                }

            case "/":
                if (num2.doubleValue() == 0.0) {
                    throw new RuntimeException("Division by zero");
                }
                // Division always produces Float
                double divResult = num1.doubleValue() / num2.doubleValue();
                return String.valueOf(divResult);

            case "DIV":
                if (num2.intValue() == 0) {
                    throw new RuntimeException("Integer division by zero");
                }
                // Integer division
                int divIntResult = num1.intValue() / num2.intValue();
                return String.valueOf(divIntResult);

            case "MOD":
                if (num2.intValue() == 0) {
                    throw new RuntimeException("Modulo by zero");
                }
                // Modulo operation
                int modResult = num1.intValue() % num2.intValue();
                return String.valueOf(modResult);

            default:
                throw new IllegalArgumentException("Unsupported operation: " + op);
        }
    }

    /**
     * Parses a string into a Number (Integer or Double)
     */
    private static Number parseNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return Double.parseDouble(s);
        }
    }
}