package com.github.lipinskipawel.game.tictactoe;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.neuristic.DeepNeuralNetwork;
import com.github.lipinskipawel.neuristic.Layer;
import com.github.lipinskipawel.neuristic.Matrix;
import com.github.lipinskipawel.neuristic.NeuralNetwork;
import com.github.lipinskipawel.neuristic.activation.Relu;
import com.github.lipinskipawel.neuristic.activation.Sigmoid;
import com.github.lipinskipawel.neuristic.lossfunction.MSE;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

final class Agent implements MoveStrategy, BoardEvaluator {

    private final NeuralNetwork agent;
    private final List<BoardInterface> historyOfMovePredictions;

    Agent(final int inputLength) {
        this.agent = new DeepNeuralNetwork.Builder()
                .addLayer(new Layer(inputLength, new Relu()))
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
        var bestMove = new Move(emptyList());
        var bestPrediction = Double.MIN_VALUE;
        final var moves = board.allLegalMoves();
        if (moves.size() == 0) {
            System.out.println("aha");
        }
        for (var move : moves) {
            final var afterMove = board.executeMove(move);
            final var predicted = evaluate(afterMove);
            if (predicted > bestPrediction) {
                bestPrediction = predicted;
                bestMove = move;
            }
        }
        this.historyOfMovePredictions.add(board.executeMove(bestMove));
        return bestMove;
    }

    void learn(final boolean didIWonTheGame) {
        if (didIWonTheGame) {
            this.historyOfMovePredictions
                    .forEach(board -> this.agent.train(Matrix.of(board.nonBinaryTransformation()), Matrix.of(1)));
        } else {
            this.historyOfMovePredictions
                    .forEach(board -> this.agent.train(Matrix.of(board.nonBinaryTransformation()), Matrix.of(-1)));
        }
        this.historyOfMovePredictions.clear();
    }

    void print() {
        System.out.println(this.agent.transform(NeuralNetwork.fromModelToString()));
    }

    @Override
    public double evaluate(final BoardInterface board) {
        return this.agent.predict(board.nonBinaryTransformation()).getBestValue().doubleValue();
    }

    public String savableForm() {
        return this.agent.transform(NeuralNetwork.fromModelToString());
    }
}
