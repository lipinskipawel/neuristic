package com.github.lipinskipawel.neuristic;

import com.github.lipinskipawel.neuristic.activation.Linear;
import com.github.lipinskipawel.neuristic.activation.Relu;
import com.github.lipinskipawel.neuristic.activation.Sigmoid;
import com.github.lipinskipawel.neuristic.activation.Softmax;
import com.github.lipinskipawel.neuristic.activation.Tanh;
import com.github.lipinskipawel.neuristic.lossfunction.MSE;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.catchThrowable;
import static org.assertj.core.api.Java6Assertions.offset;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("API -- Neural network")
class NeuralNetworkTest {

    @Nested
    @DisplayName("Learning process")
    class Learning {

        /**
         * This test has high margin of error due to illness of its nature.
         */
        @Test
        @DisplayName("XOR -- train and predict")
        void sillyTest() {
            final var trainingDataset = new ArrayList<int[][]>();
            trainingDataset.add(new int[][]{new int[]{1, 1}, new int[]{0}});
            trainingDataset.add(new int[][]{new int[]{1, 0}, new int[]{1}});
            trainingDataset.add(new int[][]{new int[]{0, 0}, new int[]{0}});
            trainingDataset.add(new int[][]{new int[]{0, 1}, new int[]{1}});

            final var model = new DeepNeuralNetwork.Builder()
                    .addLayer(new Layer(2, new Tanh()))
                    .addLayer(new Layer(4, new Tanh()))
                    .addLayer(new Layer(1, new Tanh()))
                    .compile()
                    .lossFunction(new MSE())
                    .build();

            for (int i = 0; i < 30_000; i++) {
                var pick = new Random().nextInt(trainingDataset.size());
                model.train(Matrix.of(trainingDataset.get(pick)[0]), Matrix.of(trainingDataset.get(pick)[1][0]));
            }

            final var output1 = (double) model.predict(new int[]{1, 0}).getBestValue();
            final var output2 = (double) model.predict(new int[]{1, 1}).getBestValue();
            final var output3 = (double) model.predict(new int[]{0, 0}).getBestValue();
            final var output4 = (double) model.predict(new int[]{0, 1}).getBestValue();

            assertAll("Assert all OXR operations",
                    () -> Assertions.assertThat(output1).isCloseTo(1.0, withPercentage(30)),
                    () -> Assertions.assertThat(output2).isCloseTo(0.0, offset(0.30)),
                    () -> Assertions.assertThat(output3).isCloseTo(0.0, offset(0.30)),
                    () -> Assertions.assertThat(output4).isCloseTo(1.0, withPercentage(30))
            );
        }

        @Test
        @DisplayName("y=2x-1 -- train and predict")
        void sillyTest2() {
            final var trainingDataset = new ArrayList<int[][]>();
            trainingDataset.add(new int[][]{new int[]{-1}, new int[]{-3}});
            trainingDataset.add(new int[][]{new int[]{0}, new int[]{-1}});
            trainingDataset.add(new int[][]{new int[]{1}, new int[]{1}});
            trainingDataset.add(new int[][]{new int[]{2}, new int[]{3}});
            trainingDataset.add(new int[][]{new int[]{3}, new int[]{5}});
            trainingDataset.add(new int[][]{new int[]{4}, new int[]{7}});

            final var model = new DeepNeuralNetwork.Builder()
                    .addLayer(new Layer(1, new Linear()))
                    .compile()
                    .lossFunction(new MSE())
                    .build();

            for (int i = 0; i < 5_000; i++) {
                var pick = new Random().nextInt(trainingDataset.size());
                model.train(trainingDataset.get(pick)[0], trainingDataset.get(pick)[1][0]);
            }

            final var output1 = (double) model.predict(new int[]{10}).getBestValue();
            final var output2 = (double) model.predict(new int[]{-3}).getBestValue();

            assertAll("Assert all y=2x-1",
                    () -> Assertions.assertThat(output1).isCloseTo(19.0, withPercentage(5)),
                    () -> Assertions.assertThat(output2).isCloseTo(-7.0, offset(0.05))
            );
        }

        @Test
        @DisplayName("input 1, 3 -> 2")
        void sillyTest3() {
            final var model = new DeepNeuralNetwork.Builder()
                    .addLayer(new Layer(2, new Tanh()))
                    .addLayer(new Layer(1, new Relu()))
                    .compile()
                    .lossFunction(new MSE())
                    .build();

            IntStream.range(0, 20_00).forEach(number -> model.train(new int[]{1, 3}, 2));
            final var result = (double) model.predict(new int[]{1, 3}).getBestValue();
            Assertions.assertThat(result).isCloseTo(2, offset(0.001));
        }
    }

    @Nested
    @DisplayName("Building process")
    class Building {

        @Test
        @DisplayName("1 defined layer coause exception")
        void exceptionWhenOneLayer() {
            final var model = catchThrowable(() -> new DeepNeuralNetwork.Builder()
                    .addLayer(new Layer(2, new Tanh()))
                    .compile()
                    .lossFunction(new MSE())
                    .build());

            Assertions.assertThat(model)
                    .isNull();
        }
    }

    @Nested
    @DisplayName("Transform")
    class Transform {

        @Test
        @DisplayName("fromModelToString - one layer")
        void fromModelToStringThenFromStringToModel1Layer() {
            final NeuralNetwork model = new DeepNeuralNetwork.Builder()
                    .addLayer(new Layer(1, new Relu()))
                    .compile()
                    .lossFunction(new MSE())
                    .build();

            final var transformed = model.transform(NeuralNetwork.fromModelToString());
            final var lines = transformed.lines().collect(Collectors.toList());
            final var clazzName = lines.get(0);
            final var size = lines.size();
            final var row = lines.get(1).split(":");

            Assertions.assertThat(size).isEqualTo(2);
            Assertions.assertThat(clazzName).isEqualTo(SimpleNeuralNetwork.class.getCanonicalName());
            Assertions.assertThat(row[0]).contains("[[");
            Assertions.assertThat(row[1]).contains("]]");
            Assertions.assertThat(row[2]).isEqualTo(Relu.class.getCanonicalName());
        }

        @Test
        @DisplayName("fromModelToString -> fromStringToModel - two layers")
        void fromModelToStringThenFromStringToModel2Layers() {
            final NeuralNetwork model = new DeepNeuralNetwork.Builder()
                    .addLayer(new Layer(2, new Relu()))
                    .addLayer(new Layer(3, new Softmax()))
                    .compile()
                    .lossFunction(new MSE())
                    .build();

            final var transformed = model.transform(NeuralNetwork.fromModelToString());
            final var backToModel = NeuralNetwork.fromStringToModel(transformed);

            Assertions.assertThat(model)
                    .usingRecursiveComparison()
                    .usingDefaultComparator()
                    .withStrictTypeChecking()
                    .isEqualTo(backToModel);
        }

        @Test
        @DisplayName("fromModelToString -> fromStringToModel - three layers")
        void fromModelToStringThenFromStringToModel3Layers() {
            final NeuralNetwork model = new DeepNeuralNetwork.Builder()
                    .addLayer(new Layer(9, new Relu()))
                    .addLayer(new Layer(5, new Relu()))
                    .addLayer(new Layer(1, new Sigmoid()))
                    .compile()
                    .lossFunction(new MSE())
                    .build();

            final var transformed = model.transform(NeuralNetwork.fromModelToString());
            final var backToModel = NeuralNetwork.fromStringToModel(transformed);

            Assertions.assertThat(model)
                    .usingRecursiveComparison()
                    .usingDefaultComparator()
                    .withStrictTypeChecking()
                    .isEqualTo(backToModel);
        }
    }
}
