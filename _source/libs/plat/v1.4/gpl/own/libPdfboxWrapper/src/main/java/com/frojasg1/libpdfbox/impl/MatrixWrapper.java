/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.libpdfbox.impl;

import org.apache.pdfbox.util.Matrix;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MatrixWrapper implements Comparable<MatrixWrapper> {
	protected Matrix _matrix;
	
	protected int _xx;
	protected int _yy;
	protected float _fx;
	protected float _fy;

	public MatrixWrapper( Matrix matrix )
	{
		_matrix = matrix;
	}

	public Matrix getMatrix()
	{
		return( _matrix );
	}

	protected int compareFloats( float f1, float f2 )
	{
		float diff = f1 - f2;
		return (int) ( Math.signum(diff) * Math.floor( Math.abs( diff ) ) );
	}

	protected <CC> int compareTo( Comparable<CC> c1, CC c2 )
	{
		return( c1.compareTo(c2) );
	}

	@Override
	public int compareTo( MatrixWrapper that )
	{
		int result = compareFloats( _matrix.getTranslateX(), that.getMatrix().getTranslateX() );
		if( result != 0 )
			return( result );

		result = compareFloats( _matrix.getTranslateY(), that.getMatrix().getTranslateY() );
		if( result != 0 )
			return( result );

		result = compareFloats( _matrix.getScaleX(), that.getMatrix().getScaleX() );
		if( result != 0 )
			return( result );

		result = compareFloats( _matrix.getScaleY(), that.getMatrix().getScaleY() );

		return( result );
	}

	@Override
	public int hashCode() {
		int hash = 7;
		if( _matrix != null )
		{
			hash = 79 * hash + Float.floatToIntBits(_matrix.getTranslateX());
			hash = 79 * hash + Float.floatToIntBits(_matrix.getTranslateX());
			hash = 79 * hash + Float.floatToIntBits(_matrix.getScaleX());
			hash = 79 * hash + Float.floatToIntBits(_matrix.getScaleY());
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MatrixWrapper other = (MatrixWrapper) obj;

		return compareTo(other) == 0;
	}

	
}
