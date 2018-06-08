package ru.mirea;

public class Token {
    private String text;
    private String type;

    public Token(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[type: " + type + ", text: " + text + "]";
    }
}
