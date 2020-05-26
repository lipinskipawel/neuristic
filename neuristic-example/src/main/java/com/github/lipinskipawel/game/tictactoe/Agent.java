package com.github.lipinskipawel.game.tictactoe;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.neuristic.DeepNeuralNetwork;
import com.github.lipinskipawel.neuristic.Layer;
import com.github.lipinskipawel.neuristic.NeuralNetwork;
import com.github.lipinskipawel.neuristic.activation.Relu;
import com.github.lipinskipawel.neuristic.activation.Sigmoid;
import com.github.lipinskipawel.neuristic.lossfunction.MSE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Agent implements MoveStrategy, BoardEvaluator {

    private final NeuralNetwork agent;
    private final List<Double> historyOfMovePredictions;

    Agent(final BoardInterface game) {
        this.agent = new DeepNeuralNetwork.Builder()
                .addLayer(new Layer(game.nonBinaryTransformation().length, new Relu()))
                .addLayer(new Layer(5, new Relu()))
                .addLayer(new Layer(1, new Sigmoid()))
                .compile()
                .lossFunction(new MSE())
                .build();
        this.historyOfMovePredictions = new ArrayList<>();
    }

    @Override
    public Move execute(final BoardInterface board, final int depth) {
        return execute(board, depth, this);
    }

    @Override
    public Move execute(final BoardInterface board, final int depth, final BoardEvaluator evaluator) {
        var bestMove = new Move(Collections.emptyList());
        var bestPrediction = Double.MIN_VALUE;
        final var moves = board.allLegalMoves();
        for (var move : moves) {
            final var afterMove = board.executeMove(move);
            final var predicted = evaluate(afterMove);
            if (predicted > bestPrediction) {
                bestPrediction = predicted;
                bestMove = move;
            }
        }
        this.historyOfMovePredictions.add(bestPrediction);
        return bestMove;
    }

    void learn(final boolean didIWonTheGame) {

    }

    @Override
    public double evaluate(final BoardInterface board) {
        return this.agent.predict(board.nonBinaryTransformation()).getBestValue().doubleValue();
    }
}
