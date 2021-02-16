package org.ittr.tasks.task3;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameScript {

    public static void main(String... args) {
        // args = new String[]{};
        // args = new String[]{"VW"};
        // args = new String[]{"VW", "BMW"};
        // args = new String[]{"VW", "BMW", "VW"};
        // args = new String[]{"VW", "BMW", "Mercedes", "Opel"};

        // args = new String[]{"VW", "BMW", "Mercedes"};
        // args = new String[]{"VW", "BMW", "Mercedes", "Opel", "Renault", "Toyota", "Honda"};

        try {
            GameScript game = new GameScript();
            game.play(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Нужно: <BR>
     * 1) сгенерировать ключ (безопасным генератором)
     * 2) сделать ход компа
     * 3) вычислить HMAC (стандартным алгоритмом) от хода компа и ключа, т.е. !!!зафиксировать значение компа
     * 4) показать HMAC
     * 5) получить ход пользователя
     * 6) показать ключ
     *
     * @param args
     * @throws Exception
     */
    public void play(String... args) throws Exception {
        if (validateParams(args)) {
            // сгенерировать ключ (безопасным генератором)
            byte[] key = generateRandomKey();

            // список значений
            List<GameMove> values = wrapValues(args);

            // сделать ход компа
            GameMove compGameMove = handleComp(values);

            // вычислить HMAC (стандартным алгоритмом) от хода компа и ключа, т.е. !!!зафиксировать значение компа
            String hmacComp = generateHmacAsHex(key, compGameMove.getName());
            System.out.println("HMAC: " + hmacComp);

            // получить ход пользователя
            GameMove exitGameMove = new GameMove(0, "Exit");
            values.add(exitGameMove);

            printValues(values);
            GameMove userGameMove = handleUserInput(values);
            if (userGameMove.getIndex() == 0) {
                return;
            }

            // разыграть и показать ключ
            String result = whoWin(userGameMove, compGameMove);
            System.out.println("Computer move: " + getMoveTitle(compGameMove));
            System.out.println(result);
            System.out.println("HMAC key: " + asHexValue(key));
        }
    }

    private String getMoveTitle(GameMove gameMove) {
        return gameMove.getIndex() + "-" + gameMove.getName();
    }

    private GameMove handleComp(List<GameMove> values) {
        Random random = new Random();
        int compValue = random.nextInt(values.size()) + 1;
        GameMove value = lookupValue(values, String.valueOf(compValue));
        return value;
    }

    private boolean validateParams(String[] args) {
        if (args.length <= 1) {
            System.out.println("Error: The list of arguments is empty or args.length <= 1");
            return false;
        }
        if (args.length % 2 == 0) {
            System.out.println("Error: Even number of arguments is not allowed");
            return false;
        }
        if (args.length != Arrays.stream(args).distinct().count()) {
            System.out.println("Error: The list of arguments contains duplicates");
            return false;
        }
        return true;
    }

    private List<GameMove> wrapValues(String[] args) {
        List<GameMove> values = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            values.add(new GameMove(i + 1, args[i]));
        }
//        GameMove exitGameMove = new GameMove(0, "Exit");
//        values.add(exitGameMove);
        return values;
    }

    private void printValues(List<GameMove> moves) {
        System.out.println("Available moves: ");
        for (GameMove gameMove : moves) {
            System.out.println(getMoveTitle(gameMove));
        }
    }

    private GameMove handleUserInput(List<GameMove> values) {
        boolean isValid = false;
        String userInput = null;
        GameMove gameMove = null;
        while (!isValid) {
            System.out.println("Select move: ");
            java.util.Scanner sc = new Scanner(System.in).useDelimiter("\\s");
            userInput = sc.next();
            gameMove = lookupValue(values, userInput);
            isValid = (gameMove != null);
            if (isValid) {
                System.out.println("Your move: " + getMoveTitle(gameMove));
            } else {
                System.err.println("Incorrect value selected: " + userInput);
                printValues(values);
            }
        }
        return gameMove;
    }

    private byte[] generateRandomKey() {
        byte[] key = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return key;
    }

    private byte[] generateHmac(byte[] key, String valueData) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        sha256HMAC.init(secretKey);
        return sha256HMAC.doFinal(valueData.getBytes("UTF-8"));
    }

    private String generateHmacAsHex(byte[] key, String valueData) throws Exception {
        return asHexValue(generateHmac(key, valueData));
    }

    private String asHexValue(byte[] bytes) {
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X", b));
        }
        return buf.toString();
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private GameMove lookupValue(List<GameMove> values, String userInput) {
        for (GameMove gameMove : values) {
            if (String.valueOf(gameMove.getIndex()).equals(userInput) ||
                    gameMove.getName().equalsIgnoreCase(userInput)) {
                return gameMove;
            }
        }
        return null;
    }

    private String whoWin(GameMove userValue, GameMove compValue) {
        if (userValue.getIndex() == compValue.getIndex()) {
            return "No any winner! The values are the same.";
        }
        return userValue.getIndex() > compValue.getIndex() ? "You win!" : "Computer win!";
    }
}