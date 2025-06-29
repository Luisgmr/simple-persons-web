package com.luisgmr.senai.api.testutil;

import java.util.Random;

public final class Generator {

    private Generator() {}

    public static String randomCpf() {
        int[] digits = new int[11];
        Random random = new Random();

        for (int i = 0; i < 9; i++) {
            digits[i] = random.nextInt(10);
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int mod = sum % 11;
        digits[9] = (mod < 2) ? 0 : 11 - mod;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        mod = sum % 11;
        digits[10] = (mod < 2) ? 0 : 11 - mod;

        StringBuilder sb = new StringBuilder(11);
        for (int d : digits) {
            sb.append(d);
        }
        return sb.toString();
    }
}