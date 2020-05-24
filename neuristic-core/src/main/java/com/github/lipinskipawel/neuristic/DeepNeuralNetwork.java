package com.github.lipinskipawel.neuristic;

import com.github.lipinskipawel.neuristic.lossfunction.LossFunction;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class DeepNeuralNetwork {

    private final List<Layer> layers;
    double learningRate;
    LossFunction lossFunction;
    NetworkDetails networkDetails;


    private DeepNeuralNetwork(final Builder builder) {
        this.layers = builder.layers;
        this.learningRate = 0.1;
    }

    DeepNeuralNetwork learningRate(final double lr) {
        this.learningRate = lr;
        return this;
    }

    public DeepNeuralNetwork lossFunction(final LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }

    public NeuralNetwork build() {
        if (this.lossFunction == null)
            throw new RuntimeException("Loss function is not defined");
        this.networkDetails = new NetworkDetails(layers);
        return SimpleNeuralNetwork.factory(this);
    }


    public static final class Builder {
        private List<Layer> layers;

        public Builder() {
        }

        public Builder addLayer(final Layer layer) {
            if (this.layers == null)
                this.layers = new ArrayList<>();
            this.layers.add(layer);
            return this;
        }

        public DeepNeuralNetwork compile() {
            requireNonNull(this.layers, "You have to add layer");
            requireNonNull(this.layers, "You have to configure output");
            return new DeepNeuralNetwork(this);
        }
    }
}
