/*
 * (C) Copyright 2005 Arnaud Bailly (arnaud.oqube@gmail.com),
 *     Yves Roos (yroos@lifl.fr) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rationals.algebra;

import java.util.Arrays;

/**
 * Matrix representation of an automaton.
 * <p>
 * The elements of a the matrix are {@see SemiRing}objects.
 * 
 * @version $Id: Matrix.java 6 2006-08-30 08:56:44Z oqube $
 */
public final class Matrix implements SemiRing {

    /* matrices for transitions, initial and terminal states */
    protected final SemiRing[][] matrix;

    private int line;
    
    private int col;
    

    public Matrix(int ns) {
        this.line = this.col = ns;
        this.matrix = new SemiRing[ns][ns];
    }

    /**
     * @param matrix
     */
    public Matrix(Matrix matrix) {
        this(matrix.line);
        for (int i = 0; i < line; i++) 
            for (int j = 0; j < col; j++) 
                this.matrix[i][j] = matrix.matrix[i][j];
        
    }

    /**
     * @param l
     * @param c
     */
    public Matrix(int l, int c) {
        this.line = l;
        this.col = c;
        matrix = new SemiRing[l][c];
    }

    /**
     * Returns the n <sup>th </sup> power of this matrix.
     * 
     * @param n
     *            the power. Must be positive or null.
     * @param res
     *            matrix where the result should be stored. Must be same size as
     *            this matrix with all elements initialized with null.
     * @return the result Matrix object with transition matrix equals the n
     *         <sup>th </sup> power of this matrix's transition.
     */
    public Matrix power(int n, Matrix res) {
        int l = line;
        if(line != col)
            throw new IllegalStateException("Cannot compute power of a non square matrix");
        SemiRing[][] tmp = new SemiRing[l][l];
        for (int i = 0; i < l; i++)
            Arrays.fill(tmp[i], matrix[0][0].zero());
        for (int k = 0; k <n; k++) {
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < l; j++) {
                    for (int m = 0; m < l; m++) {
                        if (k==0)
                            tmp[i][j] = tmp[i][j].plus(matrix[i][m]
                                    .mult(matrix[m][j]));
                        else
                            tmp[i][j] = tmp[i][j].plus(res.matrix[i][m]
                                    .mult(matrix[m][j]));
                    }
                }
            }
            /* copy to res */
            for (int i = 0; i < l; i++)
                System.arraycopy(tmp[i],0,res.matrix[i],0,l);
        }
        return res;
    }


    /**
     * Returns the star of this matrix.
     * 
     * @return
     */
    public Matrix star() {
        return null;
    }

    public int getLine() {
        return line;
    }
    
    public String toString() {
        final String ln = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < line; i++) {
            sb.append("[ ");
            for (int j = 0; j < col; j++) {
                String s = matrix[i][j].toString();
                sb.append(s).append(' ');
            }
            sb.append("]").append(ln);
        }
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see rationals.algebra.SemiRing#plus(rationals.algebra.SemiRing)
     */
    public SemiRing plus(SemiRing s2) {
        if(s2 == null)
            throw new IllegalArgumentException("Null argument");
        Matrix o = (Matrix)s2; // maybe ClassCastException
        if(col != o.col || line != o.line)
            throw new IllegalArgumentException("Incompatible matrices dimensions : cannot add non square matrices");
        int l = line;
        int c = col;
        Matrix res = Matrix.zero(l,c,matrix[0][0]);
        for(int i=0;i<l;i++) 
            for(int j=0;j<c;j++)
                res.matrix[i][j] = matrix[i][j].plus(o.matrix[i][j]);
        return res;               
    }

    /* (non-Javadoc)
     * @see rationals.algebra.SemiRing#mult(rationals.algebra.SemiRing)
     */
    public SemiRing mult(SemiRing s2) {
        if(s2 == null)
            throw new IllegalArgumentException("Null argument");
        Matrix o = (Matrix)s2; // maybe ClassCastException
        if(col != o.line)
            throw new IllegalArgumentException("Incompatible matrices dimensions");
        int l = line; // lines
        int c = o.col;  // cols
        int m = col;
        Matrix res = Matrix.zero(l,c,matrix[0][0]);
        for(int i=0;i<l;i++) {
            for(int j=0;j<c;j++)
                for(int k=0;k<m;k++){
                   if(k ==0)
                       res.matrix[i][j] = matrix[i][k].mult(o.matrix[k][j]);
                   else
                       res.matrix[i][j] = res.matrix[i][j].plus(matrix[i][k].mult(o.matrix[k][j]));
                }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see rationals.algebra.SemiRing#one()
     */
    public SemiRing one() {
        if(line != col)
            throw new IllegalStateException("Cannot get unit matrix on non-square matrices");
        return one(line,matrix[0][0]);
    }

    /* (non-Javadoc)
     * @see rationals.algebra.SemiRing#zero()
     */
    public SemiRing zero() {
        return zero(line,col,matrix[0][0]);
    }
    
    public int getCol() {
        return col;
    }
    
    /**
     * Factory method for creating Matrix instances with coefficients
     * in a certain SemiRing.
     * 
     * @param sr a SemiRing instance. Used to get one and zero.
     * @return a new zero matrix.
     */
    public static Matrix zero(int line,int col,SemiRing sr) {
        Matrix m = new Matrix(line,col);
        for(int i=0;i<line;i++)
            for(int j=0;j<col;j++)
                m.matrix[i][j] = sr.zero();
        return m;
    }
    
    /**
     * Factory method for creating unit Matrix instances with coefficients
     * in a certain SemiRing.
     * 
     * @param sr a SemiRing instance. Used to get one and zero.
     * @return a new unit square matrix.
     */
    public static Matrix one(int dim,SemiRing sr) {
        Matrix m = new Matrix(dim);
        for(int i=0;i<dim;i++)
            for(int j=0;j<dim;j++)
                m.matrix[i][j] = (i == j) ? sr.one() : sr.zero();
        return m;
    }
    
    
}
