/*
 * Copyright (c) 2009-2010, Peter Abeles. All Rights Reserved.
 *
 * This file is part of JMatrixBenchmark.
 *
 * JMatrixBenchmark is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * JMatrixBenchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JMatrixBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 */

package jmbench.tools.runtime.generator;

import jmbench.tools.OutputError;
import jmbench.tools.runtime.InputOutputGenerator;
import jmbench.tools.stability.StabilityBenchmark;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.SimpleMatrix;
import org.ejml.ops.RandomMatrices;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class EigSymmGenerator implements InputOutputGenerator {

    SimpleMatrix A;

    @Override
    public DenseMatrix64F[] createRandomInputs(Random rand, int matrixSize, boolean checkResults) {
        DenseMatrix64F A = RandomMatrices.createSymmetric(matrixSize,-1,1,rand);

        if( checkResults )
            this.A = SimpleMatrix.wrap(A);

        return new DenseMatrix64F[]{A};
    }

    @Override
    public OutputError checkResults(DenseMatrix64F[] output, double tol) {
        if( output[0] == null || output[1] == null ) {
            return OutputError.MISC;
        }

        SimpleMatrix D = SimpleMatrix.wrap(output[0]);
        SimpleMatrix V = SimpleMatrix.wrap(output[1]);

        SimpleMatrix L = A.mult(V);
        SimpleMatrix R = V.mult(D);

        if( L.hasUncountable() || R.hasUncountable() )
            return OutputError.UNCOUNTABLE;

        double error = StabilityBenchmark.residualError(L.getMatrix(),R.getMatrix());
        if( error > tol ) {
            return OutputError.LARGE_ERROR;
        }

        return OutputError.NO_ERROR;
    }

    @Override
    public int numOutputs() {
        return 2;
    }

    @Override
    public long getRequiredMemory( int matrixSize ) {
        return 8L*matrixSize*matrixSize*8L;
    }
}