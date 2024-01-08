/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.desktop.liblens.graphics.lens.tx;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.desktop.liblens.graphics.lens.LensException;
import static com.frojasg1.desktop.liblens.graphics.lens.Lens.SA_MODE_AMPLIFY;
import static com.frojasg1.desktop.liblens.graphics.lens.Lens.SA_MODE_REDUCE;
import com.frojasg1.general.number.IntegerFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RadiusTransformationImpl implements RadiusTransformationInter
{
	protected double a_range_Y = 1;
	protected double a_initialValue_Y = 0;
	protected double a_range_X = 1;
	protected double a_initialValue_X = 0;
	protected double a_base = 2;

	protected int _mode = -1;

	public RadiusTransformationImpl( int radius, double base,
									double min_x_transformation,
									double max_x_transformation,
									int mode ) throws LensException
	{
		if( (mode != SA_MODE_AMPLIFY) && (mode != SA_MODE_REDUCE) )
		{
			throw( new LensException( "Parameter mode must be either equal to SA_MODE_AMPLIFY or equal to SA_MODE_REDUCE." ) );
		}

		a_base = base;
		a_initialValue_X = min_x_transformation;
		a_range_X = max_x_transformation - min_x_transformation;
		a_initialValue_Y = Math.pow(base, a_initialValue_X );
		a_range_Y = Math.pow( base, max_x_transformation ) - a_initialValue_Y;
		_mode = mode;
	}

	@Override
	public double transform( double radiusRatio )
	{
		double result = ( Math.pow(a_base, a_initialValue_X + radiusRatio * a_range_X ) - a_initialValue_Y ) / a_range_Y;
		
		return( result );
	}

	@Override
	public Coordinate2D calculateTransformation( double xx, double yy,
													double radius )
	{
		double radius2 = Math.sqrt(xx*xx + yy*yy);

		int transformed_xx = IntegerFunctions.round( xx );
		int transformed_yy = IntegerFunctions.round( yy );

		if( radius2 <= radius )
		{
			double angle = Math.acos( xx / radius2 );

			double transformedRadius = 0;
			
			if( _mode == SA_MODE_AMPLIFY )
				transformedRadius = radius * transform( radius2 / radius );
			else if( _mode == SA_MODE_REDUCE )
				transformedRadius = radius * ( 1 - transform( 1 - radius2 / radius ) );
			else
				transformedRadius = radius2;

			transformed_xx = IntegerFunctions.round( transformedRadius * Math.cos( angle ) );
			transformed_yy = IntegerFunctions.round( transformedRadius * Math.sin( angle ) );

			if( yy < 0 )
				transformed_yy = -transformed_yy;
		}

		Coordinate2D result = new Coordinate2D( transformed_xx, transformed_yy );

		return( result );
	}
}
