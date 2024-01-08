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
package com.frojasg1.desktop.liblens.graphics.lens.tx.accurate;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.desktop.liblens.graphics.lens.tx.RadiusTransformationInter;
import com.frojasg1.desktop.liblens.graphics.lens.util.LensUtils;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AccuratePixelTransformation
{
	protected List<AccuratePixelElement> _list = null;

	protected int _xx = -1;
	protected int _yy = -1;

	protected int _limitOfSummands = 8;

	public AccuratePixelTransformation( int xx, int yy )
	{
		_xx = xx;
		_yy = yy;
	}

	public void setLimitOfSummands( int limitOfSummands )
	{
		_limitOfSummands = limitOfSummands;
	}

	public void init( RadiusTransformationInter transformation,
						int radius, int samples1D )
	{
		Map<Coordinate2D, AccuratePixelElement> map = new HashMap<>();

		double increment = 1.0D / samples1D;

		double xxFraction = -0.5D + increment / 2;
		for( int ii=0; ii< samples1D; ii++, xxFraction+=increment );
		{
			double yyFraction = -0.5D + increment / 2;
			for( int jj=0; jj<samples1D; jj++, yyFraction += increment )
			{
				Coordinate2D transfCoor = transformation.calculateTransformation( _xx + xxFraction,
																					_yy + yyFraction,
																					radius );

				limitTransfCoor( transfCoor, radius );

				AccuratePixelElement elem = map.get( transfCoor );
				if( elem == null )
				{
					elem = createPixelElement( transfCoor );
					map.put(transfCoor, elem);
				}

				elem.incrementFactor( increment );
			}
		}

		_list = createAndSortList( map );

		recalculateFactors( );
	}

	protected void limitTransfCoor( Coordinate2D transfCoor, int radius )
	{
		transfCoor.M_setX( IntegerFunctions.max( -radius, IntegerFunctions.min( radius, transfCoor.M_getX() ) ) );
		transfCoor.M_setY( IntegerFunctions.max( -radius, IntegerFunctions.min( radius, transfCoor.M_getY() ) ) );
	}

	protected void recalculateFactors( )
	{
		recalculateFactors( _list );
	}

	protected void recalculateFactors( List<AccuratePixelElement> list )
	{
		double totalForLimit = 0.0D;

		Iterator< AccuratePixelElement > it = list.iterator();
		for( int ii=0; ii<_limitOfSummands && it.hasNext(); ii++ )
		{
			totalForLimit += it.next().getFactor();
		}

		Iterator< AccuratePixelElement > it2 = list.iterator();
		for( int ii=0; ii<_limitOfSummands && it2.hasNext(); ii++ )
		{
			AccuratePixelElement elem = it2.next();
			elem.setFactor( elem.getFactor() / totalForLimit );
		}
	}

	protected AccuratePixelElement createPixelElement( Coordinate2D transfCoor )
	{
		return( new AccuratePixelElement( transfCoor ) );
	}

	protected List<AccuratePixelElement> createAndSortList( Map< Coordinate2D, AccuratePixelElement > map )
	{
		List<AccuratePixelElement> result = new ArrayList<>();

		Iterator< AccuratePixelElement > it = map.values().iterator();
		while( it.hasNext() )
		{
			AccuratePixelElement elem = it.next();

			result.add(elem);
		}

		Collections.reverse(result);

		return( result );
	}

	public int getTransformedPixel( int lensX, int lensY,
									int radius,
									int[] pixelColors )
	{
		int result = 0;

		double[] components = ImageFunctions.instance().createEmptyComponents();

		Iterator< AccuratePixelElement > it = _list.iterator();
		for( int ii=0; ii<_limitOfSummands && it.hasNext(); ii++ )
		{
			AccuratePixelElement elem = it.next();

			Coordinate2D coor = elem.getCoordinate2D();

			int xx_original = lensX + coor.M_getX();
			int yy_original = lensY + coor.M_getY();

			int color = LensUtils.getTransformedPixel( xx_original, yy_original,
														lensX, lensY,
														radius,
														pixelColors );
			ImageFunctions.instance().addARGB( components, color, elem.getFactor() );
		}

		result = ImageFunctions.instance().getARGB( components );

		return( result );
	}

	public List<AccuratePixelElement> getListOfAccuratePixelElement()
	{
		return( _list );
	}
}
