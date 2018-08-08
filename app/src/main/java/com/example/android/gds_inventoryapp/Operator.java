package com.example.android.gds_inventoryapp;

public enum Operator {
    PLUS("+") {
        @Override
        public int apply(int a, int b) {
            return a + b;
        }
    },
    MINUS("-") {
        @Override
        public int apply(int a, int b) {
            return a - b;
        }
    };

    private final String text;

    Operator(String text) {
        this.text = text;
    }

    public abstract int apply(int a, int b);

    @Override
    public String toString() {
        return text;
    }
}
