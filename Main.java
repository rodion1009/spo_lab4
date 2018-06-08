package ru.mirea;

import java.util.Queue;

public class Main {
    public static void main(String[] args) throws Exception {
        Queue<Token> tokens = Lexer.getTokenList("b = 12" +
                "a = 10 /(b-80)");
        Parser parser = new Parser();
        if (!parser.parse(tokens)) {
            return;
        }
        Queue<Token> poliz = parser.getPoliz();
        StackMachine sm = new StackMachine(parser.getTable(), poliz);
        sm.calculate();
    }
}
