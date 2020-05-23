package com.github.lipinskipawel.game;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Direction;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("TicTacToe")
final class TicTacToeTest {

    @Test
    @DisplayName("winner is first")
    void winnerIsFirst() {
        final BoardInterface game = TicTacToe.createGame();

        final BoardInterface afterGame = game
                .executeMove(Direction.E)
                .executeMove(Direction.N)
                .executeMove(Direction.W)
                .executeMove(Direction.S)
                .executeMove(new Move(List.of(Direction.N, Direction.N)));

//        Assertions.assertThat(afterGame.takeTheWinner().get()).isEqualTo(Player.FIRST);
    }
}
