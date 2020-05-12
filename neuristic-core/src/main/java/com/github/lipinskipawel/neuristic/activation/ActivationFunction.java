package com.github.lipinskipawel.neuristic.activation;

import com.github.lipinskipawel.neuristic.Matrix;

abstract public class ActivationFunction {


    abstract public Matrix compute(final Matrix matrix);

    abstract public Matrix derivative(final Matrix matrix);

}
