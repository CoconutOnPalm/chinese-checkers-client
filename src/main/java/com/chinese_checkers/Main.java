package com.chinese_checkers;

import com.chinese_checkers.game.Game;
import com.chinese_checkers.networking.NetworkConnector;
import com.chinese_checkers.networking.NetworkListener;

import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Game game = new Game();
        game.run();
    }
}