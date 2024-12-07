package com.chinese_checkers;

import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.networking.NetworkListener;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        NetworkConnector connector = new NetworkConnector(12345, "localhost");
        connector.connect();

        Scanner scanner = new Scanner(System.in);
        String input;

        while (true)
        {
            input = scanner.nextLine();

            if (input == null || input.equals("exit"))
                break;

            connector.send(input);
        }

        connector.disconnect();
    }
}