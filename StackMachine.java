package ru.mirea;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class StackMachine {
    VariableTable table;
    Queue<Token> tokens;
    Stack<Token> stack = new Stack<>();

    public StackMachine(VariableTable table, Queue<Token> tokens) {
        this.table = table;
        this.tokens = new LinkedList<>(tokens);
    }

    private Token calcArithmOp(String operation) throws Exception {
        Token tokenOp2 = stack.pop();
        Token tokenOp1 = stack.pop();

        double numOp1 = tokenOp1.getType().equals("VAR") ? Double.valueOf(table.getVariableValue(tokenOp1.getText())) :
                Double.valueOf(tokenOp1.getText());
        double numOp2 = tokenOp2.getType().equals("VAR") ? Double.valueOf(table.getVariableValue(tokenOp2.getText())) :
                Double.valueOf(tokenOp2.getText());
        double result = 0.0;

        switch (operation) {
            case "+":
                result = numOp1 + numOp2;
                break;
            case "-":
                result = numOp1 - numOp2;
                break;
            case "*":
                result = numOp1 * numOp2;
                break;
            case "/":
                result = numOp1 / numOp2;
                break;
        }

        //Если дробная часть равна 0, то отбросить её
        return (result - (int)result) == 0 ? new Token(String.valueOf((int)result), "CONST_INT") :
                new Token(String.valueOf(result), "CONST_FLOAT");
    }

    private void assign() throws Exception {
        String value = stack.pop().getText();
        String name = stack.pop().getText();
        table.addVariable(name, "number", value);
    }

    public void calculate() throws Exception {
        while (!tokens.isEmpty()) {
            Token currentToken = tokens.poll();
            switch (currentToken.getType()) {
                case "VAR":
                case "CONST_INT":
                case "CONST_FLOAT":
                    stack.push(currentToken);
                    break;
                case "ARITHMETIC_OP":
                    stack.push(calcArithmOp(currentToken.getText()));
                    break;
                case "ASSIGN_OP":
                    assign();
                    break;
            }
        }
        System.out.println(table);
    }
}
