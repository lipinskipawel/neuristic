package com.github.lipinskipawel.game.tictactoe;

import com.github.lipinskipawel.board.engine.Player;

final class WinnerKeeper {
    boolean didSecondWinTheGame = true;
    int firstWon = 0;
    int secondWon = 0;
    int tie = 0;

    void takeWinner(final Player player) {
        if (player == Player.FIRST) {
            firstWon++;
        } else {
            secondWon++;
        }
    }
}
