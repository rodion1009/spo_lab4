package ru.mirea;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;

public enum Lexer {
    CONST_INT("CONST_INT", Pattern.compile("^0|[1-9][0-9]*$")),
    CONST_FLOAT("CONST_FLOAT", Pattern.compile("^(0|[1-9][0-9]*)\\.[0-9]*$")),
    VAR("VAR", Pattern.compile("^[a-z][a-zA-Z_]*$")),
    OPEN_BRACKET("OPEN_BRACKET", Pattern.compile("^\\($")),
    CLOSE_BRACKET("CLOSE_BRACKET", Pattern.compile("^\\)$")),
    ASSIGN_OP("ASSIGN_OP", Pattern.compile("^=$")),
    ARITHMETIC_OP("ARITHMETIC_OP", Pattern.compile("^[/*+-]$")),
    SPACE("SPACE", Pattern.compile("^\\s$"));

    private Pattern pattern;
    private String name;

    Lexer(String name, Pattern pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    private boolean matches(String string) {
        return pattern.matcher(string).matches();
    }

    private static boolean hasAnyMatch(String string) {
        for (Lexer lex : values()) {
            if (lex.matches(string)) {
                return true;
            }
        }
        return false;
    }

    private static Lexer getMatch(String s) throws Exception {
        for (Lexer lex: values()) {
            if (lex.matches(s)) {
                return lex;
            }
        }
        throw new Exception("Unknown character sequence: " + s);
    }

    public static Queue<Token> getTokenList(String source) throws Exception {
        String tmp;
        Queue<Token> tokens = new LinkedList<>();
        while (!source.isEmpty()) {
            tmp = "";
            char c;
            int pos = 0;
            do {
                if (pos < source.length()) {
                    if ((c = source.charAt(pos)) != ' ') {
                        tmp += c;
                    }
                } else {
                    if (hasAnyMatch(tmp)) {
                        tokens.add(new Token(tmp, getMatch(tmp).getName()));
                        return tokens;
                    }
                }
                pos++;
            } while (hasAnyMatch(tmp));
            tmp = tmp.substring(0, tmp.length() - 1); //Удалить последний символ из tmp
            pos--;
            String tokenType = getMatch(tmp).getName();
            if (!tokenType.equals("SPACE")) {
                tokens.add(new Token(tmp, getMatch(tmp).getName()));
            }
            source = source.substring(pos);
        }
        return tokens;
    }

    public static Queue<Token> fromFile(String filename) throws Exception {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        Queue<Token> tokens = new LinkedList<>();
        while (scanner.hasNextLine()) {
            tokens.addAll(getTokenList(scanner.nextLine()));
        }
        return tokens;
    }
}
