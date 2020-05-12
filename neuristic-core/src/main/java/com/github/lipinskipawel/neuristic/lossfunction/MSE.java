package com.github.lipinskipawel.neuristic.lossfunction;

import com.github.lipinskipawel.neuristic.Matrix;

final public class MSE extends LossFunction {

    @Override
    public Matrix compute(final Matrix target, final Matrix output) {
        return target.subtract(output);
    }
}
