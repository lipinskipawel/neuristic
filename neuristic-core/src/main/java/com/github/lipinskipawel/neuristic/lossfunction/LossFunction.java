package com.github.lipinskipawel.neuristic.lossfunction;

import com.github.lipinskipawel.neuristic.Matrix;

abstract public class LossFunction {


    abstract public Matrix compute(Matrix target, Matrix output);
}
