package ru.mirea;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Parser {
    private Queue<Token> tokens;
    private Queue<Token> expr = new LinkedList<>();
    private Queue<Token> poliz = new LinkedList<>();
    private Token currentToken;
    private VariableTable table = new VariableTable();
    private boolean success = true;

    public VariableTable getTable() {
        return table;
    }

    public Queue<Token> getPoliz() {
        return poliz;
    }

    private void checkToken(String appropriateType) {
        match();
        String currentTokenType = currentToken.getType();
        if (!currentTokenType.equals(appropriateType)) {
            error(currentTokenType, appropriateType);
        }
    }

    private void error(String currentTokenType, String appropriateType) {
        System.out.printf("Ошибка: ожидается %s, найден %s\n", appropriateType, currentTokenType);
        success = false;
    }

    private void match() {
        currentToken = tokens.poll();
        expr.add(currentToken);
    }

    public boolean parse(Queue<Token> t) {
        tokens = new LinkedList<>(t);

        while (!tokens.isEmpty()) {
            lang();
        }

        return success;
    }

    private void lang() {
        expr();
    }

    private void expr() {
        assignExpr();
        //После проверки корректности выражение сразу переводится в ПОЛИЗ (если проверка была пройдена)
        if (success) {
            poliz.addAll(toPoliz(expr));
        }
        expr.clear();
    }

    private void assignExpr() {
        var();
        //Добавление переменной в таблицу переменных
        try {
            table.addVariable(currentToken.getText(), "number", "0");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assignOp();
        assignValue();
    }

    private void assignValue() {
        arithmExpr();
    }

    private void inBr() {
        openBracket();
        arithmExpr();
        closeBracket();
    }

    private void arithmExpr() {
        operand();
        while (tokens.peek() != null && tokens.peek().getType().equals("ARITHMETIC_OP")) {
            arOp();
            operand();
        }
    }

    private void operand() {
        Token nextToken = tokens.peek();
        if (nextToken.getType().equals("OPEN_BRACKET")) {
            inBr();
        } else {
            singleOperand();
        }
    }

    private void singleOperand() {
        Token token = tokens.peek();
        switch (token.getType()) {
            case "VAR":
                var();
                //Проверка наличия используемой переменной в таблице переменных
                if (!table.check(token.getText())) {
                    System.out.printf("Переменная %s не была инициализирована\n", token.getText());
                }
                break;
            case "CONST_INT":
                constInt();
                break;
            case "CONST_FLOAT":
                constFloat();
                break;
            default:
                error(token.getType(), "VAR | CONST_INT | CONST_FLOAT");
                tokens.remove();
        }
    }

    private void var() {
        checkToken("VAR");
    }

    private void assignOp() {
        checkToken("ASSIGN_OP");
    }

    private void openBracket() {
        checkToken("OPEN_BRACKET");
    }

    private void closeBracket() {
        checkToken("CLOSE_BRACKET");
    }

    private void arOp() {
        checkToken("ARITHMETIC_OP");
    }

    private void constInt() {
        checkToken("CONST_INT");
    }

    private void constFloat() {
        checkToken("CONST_FLOAT");
    }

    private boolean higherPriority(String op1, String op2) {
        return (op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"));
    }

    public Queue<Token> toPoliz(Queue<Token> tokens) {
        Queue<Token> result = new LinkedList<>();
        Stack<Token> stack = new Stack<>();
        Token upperInStack;

        while (!tokens.isEmpty()) {
            Token token = tokens.poll();
            String type = token.getType();
            switch (type) {
                //Если следующий токен - операнд, то он сразу добавляется в ПОЛИЗ
                case "VAR":
                case "CONST_INT":
                case "CONST_FLOAT":
                    result.add(token);
                    break;
                case "ARITHMETIC_OP":
                    //Вытолкнуть из стека все операции с более высоким приоритетом
                    while (!stack.isEmpty() && (upperInStack = stack.peek()).getType().equals("ARITHMETIC_OP") &&
                            higherPriority(upperInStack.getText(), token.getText())) {
                        result.add(stack.pop());
                    }
                    stack.push(token);
                    break;
                case "ASSIGN_OP":
                case "OPEN_BRACKET":
                    stack.push(token);
                    break;
                case "CLOSE_BRACKET":
                    //Выталкивать из стека все операции пока не встретится открывающаяся скобка
                    while (!(upperInStack = stack.pop()).getText().equals("(")) {
                        result.add(upperInStack);
                    }
                    break;
            }
        }

        //Выталкиваем оставшееся содержимое стека в ПОЛИЗ
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        return result;
    }
}
