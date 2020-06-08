package com.github.lipinskipawel.neuristic;

import com.github.lipinskipawel.neuristic.activation.Sigmoid;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("API -- LayerDTO")
class LayerDTOTest {

    @Nested
    @DisplayName("parseString")
    class StringParser {

        @Test
        @DisplayName("invalid input - empty string")
        void empty() {
            final var throwable = catchThrowable(
                    () -> LayerDTO.parseStringDoubleWeightDoubleBiasClassActivationFunction(""));

            Assertions.assertThat(throwable).isInstanceOf(InvalidInputFormatException.class);
        }

        @Test
        @DisplayName("invalid input - one :")
        void twoGroups() {
            final var throwable = catchThrowable(
                    () -> LayerDTO.parseStringDoubleWeightDoubleBiasClassActivationFunction("[[1, 1]]:[[4, 3]]"));

            Assertions.assertThat(throwable).isInstanceOf(InvalidInputFormatException.class);
        }

        @Test
        @DisplayName("invalid input - non existing activation function")
        void nonExistingActivationFunction() {
            final var throwable = catchThrowable(
                    () -> LayerDTO.parseStringDoubleWeightDoubleBiasClassActivationFunction("[[1, 1]]:[[4, 3]]:NonExistingActivationFunction"));

            Assertions.assertThat(throwable).isInstanceOf(InvalidInputFormatException.class);
        }

        @Test
        @DisplayName("parse 1d arrays")
        void shouldParseStringFrom1dArray() {
            final var preparedWeight = Matrix.of(new double[][]{{0.6775818874783545, 0.39148446642093626, 0.5835065076748652, 0.2719096671665818, -0.32859816820175425}});
            final var preparedBiases = Matrix.of(new double[][]{{0.08625705839435478}});
            final var layerDTO = LayerDTO.parseStringDoubleWeightDoubleBiasClassActivationFunction(
                    "[[0.6775818874783545, 0.39148446642093626, 0.5835065076748652, 0.2719096671665818, -0.32859816820175425]]:" +
                            "[[0.08625705839435478]]:" + Sigmoid.class.getCanonicalName());

            final var weight = layerDTO.getWeight();
            final var biases = layerDTO.getBiases();
            final var activationFunction = layerDTO.getActivationFunction();

            assertAll(
                    () -> Assertions.assertThat(weight).usingRecursiveComparison().isEqualTo(preparedWeight),
                    () -> Assertions.assertThat(biases).usingRecursiveComparison().isEqualTo(preparedBiases),
                    () -> Assertions.assertThat(activationFunction).isInstanceOf(Sigmoid.class)
            );
        }

        @Test
        @DisplayName("parse 2d arrays")
        void shouldParseStringFrom2dArray() {
            final var preparedWeight = Matrix.of(new double[][]{{0.8, -1.2}, {-4.2, 3}});
            final var preparedBiases = Matrix.of(new double[][]{{1, 1}, {2, -0.9}});
            final var layerDTO = LayerDTO
                    .parseStringDoubleWeightDoubleBiasClassActivationFunction("[[0.8, -1.2], [-4.2, 3]]:" +
                            "[[1, 1], [2, -0.9]]:" + Sigmoid.class.getCanonicalName());

            final var weight = layerDTO.getWeight();
            final var biases = layerDTO.getBiases();
            final var activationFunction = layerDTO.getActivationFunction();

            assertAll(
                    () -> Assertions.assertThat(weight).usingRecursiveComparison().isEqualTo(preparedWeight),
                    () -> Assertions.assertThat(biases).usingRecursiveComparison().isEqualTo(preparedBiases),
                    () -> Assertions.assertThat(activationFunction).isInstanceOf(Sigmoid.class)
            );
        }
    }
}
