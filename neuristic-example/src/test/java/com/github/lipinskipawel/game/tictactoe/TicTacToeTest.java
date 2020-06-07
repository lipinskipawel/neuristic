package com.github.lipinskipawel.game.tictactoe;

import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.lipinskipawel.board.engine.Direction.E;
import static com.github.lipinskipawel.board.engine.Direction.N;
import static com.github.lipinskipawel.board.engine.Direction.S;
import static com.github.lipinskipawel.board.engine.Direction.SW;
import static com.github.lipinskipawel.board.engine.Direction.W;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TicTacToe")
final class TicTacToeTest {

    @Nested
    @DisplayName("takeTheWinner")
    class TakeWinner {

        @Test
        @DisplayName("after 0 moves")
        void zeroMoves() {
            final var player = TicTacToe
                    .createGame()
                    .takeTheWinner();

            assertThat(player.isPresent()).isFalse();
        }

        @Test
        @DisplayName("fastest first win")
        void fastestFirstWin() {
            final var player = TicTacToe
                    .createGame()
                    .executeMove(E)
                    .executeMove(N)
                    .executeMove(W)
                    .executeMove(SW)
                    .executeMove(new Move(List.of(N, N)))
                    .takeTheWinner();

            assertThat(player.isPresent()).isTrue();
            assertThat(player.get()).isEqualTo(Player.FIRST);
        }

        @Test
        @DisplayName("fastest second win")
        void fastestSecondWin() {
            final var player = TicTacToe
                    .createGame()
                    .executeMove(E)
                    .executeMove(N)
                    .executeMove(W)
                    .executeMove(new Move(List.of(N, N)))
                    .executeMove(SW)
                    .executeMove(S)
                    .takeTheWinner();

            assertThat(player.isPresent()).isTrue();
            assertThat(player.get()).isEqualTo(Player.SECOND);
        }
    }

    @Nested
    @DisplayName("getPlayer")
    class GetPlayer {

        @Test
        @DisplayName("after 0 moves")
        void zeroMoves() {
            final var player = TicTacToe.createGame()
                    .getPlayer();

            assertThat(player).isEqualTo(Player.FIRST);
        }

        @Test
        @DisplayName("after 1 move")
        void oneMove() {
            final var afterMoves = TicTacToe
                    .createGame()
                    .executeMove(N);

            assertThat(afterMoves.getPlayer()).isEqualTo(Player.SECOND);
        }

        @Test
        @DisplayName("after 2 move")
        void twoMoves() {
            final var afterMoves = TicTacToe
                    .createGame()
                    .executeMove(N)
                    .executeMove(E);

            assertThat(afterMoves.getPlayer()).isEqualTo(Player.FIRST);
        }
    }
}
