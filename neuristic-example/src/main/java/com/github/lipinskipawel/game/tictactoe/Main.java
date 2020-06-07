package com.github.lipinskipawel.game.tictactoe;

import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.ai.bruteforce.MiniMaxAlphaBeta;
import com.github.lipinskipawel.board.engine.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int NUMBER_OF_GAMES = 1_000;
    private static final int NUMBER_AFTER_SAVE = NUMBER_OF_GAMES / 10;
    private static int TIMES = 1;

    private static final ExecutorService pool = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        final WinnerKeeper state = new WinnerKeeper();
        final var agentStrategy = new Agent(9);
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            var game = TicTacToe.createGame();

            final var ticTacToeBruteForce = new MiniMaxAlphaBeta(new Evaluator());

            MoveStrategy currentStrategy = ticTacToeBruteForce;

            do {
                final var move = currentStrategy.execute(game, 9);
                game = game.executeMove(move);
                currentStrategy = game.getPlayer() == Player.FIRST ? ticTacToeBruteForce : agentStrategy;
            } while (!game.isGameOver());

            game
                    .takeTheWinner()
                    .ifPresentOrElse(player -> {
                                state.takeWinner(player);
                                if (player == Player.FIRST) {
                                    state.didSecondWinTheGame = false;
                                    agentStrategy.learn(true);
                                } else {
                                    agentStrategy.learn(false);
                                }
                            },
                            () -> {
                                state.tie++;
                                state.didSecondWinTheGame = false;
                            });
            if (state.didSecondWinTheGame) {
                System.out.println(new Evaluator().evaluate(game));
                System.out.println(game);
            }
            if (TIMES * NUMBER_AFTER_SAVE == i) {
                final var copy = TIMES * NUMBER_AFTER_SAVE;
                final var stringModel = agentStrategy.savableForm();
                TIMES++;
                pool.submit(() -> {
                    try {
                        final var filename = "after-" + copy + "-game.txt";
                        Files.writeString(Paths.get(filename), stringModel);
                        System.out.println("File " + filename + " has been saved.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        System.out.println("First: " + state.firstWon);
        System.out.println("Second: " + state.secondWon);
        System.out.println("Tie: " + state.tie);
        pool.shutdown();
    }
}
