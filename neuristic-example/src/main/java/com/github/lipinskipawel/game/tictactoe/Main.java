package com.github.lipinskipawel.game.tictactoe;

import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.ai.bruteforce.MiniMaxAlphaBeta;
import com.github.lipinskipawel.board.engine.Player;

public class Main {

    private static final int NUMBER_OF_GAMES = 100;

    public static void main(String[] args) {
        final WinnerKeeper state = new WinnerKeeper();
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            var game = TicTacToe.createGame();

            final var ticTacToeBruteForce = new MiniMaxAlphaBeta(new Evaluator());
            final var agentStrategy = new Agent(game);

            MoveStrategy currentStrategy = ticTacToeBruteForce;

            do {
                final var move = currentStrategy.execute(game, 9);
//                System.out.println("Best move for player " + game.getPlayer() + " is: " + move.getMove());
                game = game.executeMove(move);
                currentStrategy = game.getPlayer() == Player.FIRST ? ticTacToeBruteForce : agentStrategy;
            } while (!game.isGameOver());

            game
                    .takeTheWinner()
                    .ifPresentOrElse(player -> {
//                                System.out.println("The winner is: " + player);
                                state.takeWinner(player);
                                if (player == Player.FIRST) {
                                    state.didSecondWinTheGame = false;
                                    agentStrategy.learn(true);
                                } else {
                                    agentStrategy.learn(false);
                                }
                            },
                            () -> {
                                System.out.println("No winner");
                                state.tie++;
                                state.didSecondWinTheGame = false;
                            });
            if (state.didSecondWinTheGame) {
                System.out.println(new Evaluator().evaluate(game));
                System.out.println(game);
//                Assertions.fail("SECOND won the game");
            }
        }
        System.out.println("First: " + state.firstWon);
        System.out.println("Second: " + state.secondWon);
        System.out.println("Tie: " + state.tie);
    }
}
