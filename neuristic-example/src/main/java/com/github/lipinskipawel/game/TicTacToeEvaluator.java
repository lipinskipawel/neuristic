package com.github.lipinskipawel.game;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.engine.BoardInterface;

import java.util.Random;

final class TicTacToeEvaluator implements BoardEvaluator {

    @Override
    public double evaluate(final BoardInterface board) {
        if (board instanceof TicTacToe && board.isGameOver()) {
            final TicTacToe game = (TicTacToe) board;
            return game.isFirstPlayerWon() ? new Random().nextDouble() : new Random().nextDouble() - 2.0;
        }
        return new Random().nextDouble();
    }
}
