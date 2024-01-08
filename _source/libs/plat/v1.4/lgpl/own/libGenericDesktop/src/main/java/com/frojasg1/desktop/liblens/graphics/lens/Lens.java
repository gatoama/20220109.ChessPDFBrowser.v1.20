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
package com.frojasg1.desktop.liblens.graphics.lens;

import com.frojasg1.desktop.liblens.graphics.lens.tx.RadiusTransformationImpl;
import com.frojasg1.desktop.liblens.graphics.lens.tx.RadiusTransformationInter;
import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.desktop.liblens.graphics.filters.blur.BlurFilter;
import com.frojasg1.desktop.liblens.graphics.lens.tx.LensTransformation;
import com.frojasg1.desktop.liblens.graphics.lens.tx.accurate.AccurateTransformation;
import com.frojasg1.desktop.liblens.graphics.lens.tx.fast.FastTransformation;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Usuario
 */
public class Lens
{
	// values for mode in the constructor
	public static final int SA_MODE_AMPLIFY = RadiusTransformationInter.SA_MODE_AMPLIFY;
	public static final int SA_MODE_REDUCE = RadiusTransformationInter.SA_MODE_REDUCE;

	// values for accuracy in the constructor
	public static final int SA_ACCURATE = 0;
	public static final int SA_FAST = 1;

//	protected Coordinate2D[][] a_fastTransformationArray = null;
	protected int a_radius = 0;

	protected RadiusTransformationInter _transformation = null;

	protected BufferedImage _borderImage = null;

	protected int _accuracy = -1;

	protected LensTransformation _lensTransformation = null;

	protected boolean _applyBlur = false;

	public Lens( int radius, double base, double min_x_transformation, double max_x_transformation,
					int mode, int accuracy, boolean applyBlur ) throws LensException
	{
		if( radius <= 1 )
		{
			throw( new LensException( "Parameter radius must be greater than 1." ) );
		}
		
		if( base <= 1 )
		{
			throw( new LensException( "Parameter base must be greater than 1." ) );
		}
		
		if( max_x_transformation <= min_x_transformation )
		{
			throw( new LensException( "Parameter max_x_transformation must be greater than min_x_transformation." ) );
		}

		a_radius = radius;
		_transformation = new RadiusTransformationImpl( radius, base, min_x_transformation,
														max_x_transformation, mode );

		_accuracy = accuracy;
		_applyBlur = applyBlur;

		_lensTransformation = createLensTransformation();
		_lensTransformation.init(_transformation, radius);
	}

	public Lens( int radius, int mode, int accuracy ) throws LensException
	{
		this( radius, 2, -2, 1, mode, accuracy, true );
	}

	public Lens( int radius, int mode ) throws LensException
	{
		this( radius, mode, SA_FAST );
	}

	protected LensTransformation createLensTransformation()
	{
		LensTransformation result = null;
		if( _accuracy == SA_ACCURATE )
			result = new AccurateTransformation();
		else
			result = new FastTransformation();

		return( result );
	}

	public void fillImageWithARGB( BufferedImage image, int argb )
	{
		for( int xx=0; xx<image.getWidth(); xx++ )
			for( int yy=0; yy<image.getHeight(); yy++ )
				image.setRGB(xx, yy, argb);
	}

	public void setGradatedBorder( int externalColor, int internalColor, int thickOfBorder )
	{
		int width = a_radius * 2 + 1;
		_borderImage = new BufferedImage( width, width, BufferedImage.TYPE_INT_ARGB );

		int internalRadius = a_radius - thickOfBorder;
		
		if( internalRadius < 0 )
			internalRadius = 0;

		ARGBcomponentGradation colorGradation = new ARGBcomponentGradation( externalColor, internalColor );

		fillImageWithARGB( _borderImage, 0x00000000 );

		int yyLimit = (int) ( a_radius * Math.sin( Math.PI / 4 ) );

		for( int yy=0; yy<=yyLimit; yy++ )
		{
			int finalXX = (int) ( a_radius * Math.cos( Math.asin( yy/ (double) a_radius ) ) );
			int initialXX = 0;
			if( yy <= internalRadius )
				initialXX = IntegerFunctions.max( yy,
										IntegerFunctions.round( internalRadius * Math.cos( Math.asin( yy / (double) internalRadius ) ) )
												);
			else
				initialXX = yy;

			for( int xx=initialXX; xx<=finalXX; xx++ )
			{
				double factor = getFactorForGradation( xx, yy, a_radius, thickOfBorder );
				int gradatedARGB = colorGradation.getARGBGradation(factor);

				setSimmetricalPixelColor( _borderImage, xx, yy, gradatedARGB );
			}
		}
	}

	protected void setSimmetricalPixelColor( BufferedImage image,
											int xx, int yy, int gradatedARGB )
	{
		image.setRGB( a_radius + xx, a_radius + yy, gradatedARGB );
		image.setRGB( a_radius + xx, a_radius - yy, gradatedARGB );
		image.setRGB( a_radius - xx, a_radius + yy, gradatedARGB );
		image.setRGB( a_radius - xx, a_radius - yy, gradatedARGB );
		image.setRGB( a_radius + yy, a_radius + xx, gradatedARGB );
		image.setRGB( a_radius + yy, a_radius - xx, gradatedARGB );
		image.setRGB( a_radius - yy, a_radius + xx, gradatedARGB );
		image.setRGB( a_radius - yy, a_radius - xx, gradatedARGB );
	}

	protected double getFactorForGradation( int xx, int yy, int radius, int thickOfBorder )
	{
		double dist = Math.sqrt( xx*xx + yy*yy );

		double result = 1.0D + ( dist - radius ) / thickOfBorder;

		return( result );
	}

	public int M_getRadius()	{ return( a_radius );	}
	
/*
	public int getTransformedPixel( int xx_original, int yy_original,
									int lensX, int lensY,
									int radiusX2,
									int[] pixelColors )
	{
		int offsetPixelColors = ( xx_original + a_radius - lensX ) +
								( yy_original + a_radius - lensY ) * (radiusX2+1);

		return( pixelColors[ offsetPixelColors ] );
	}
*/
	public LensTransformationResult M_getTransformedImage( BufferedImage bi,
															Coordinate2D lensPosition )
	{
		LensTransformationResult result = null;

		int radiusX2 = 2*a_radius;

		BufferedImage bi_result = new BufferedImage( radiusX2+1, radiusX2+1, BufferedImage.TYPE_INT_ARGB );


		int lensX =  lensPosition.M_getX();
		int lensY =  lensPosition.M_getY();

		int fromX = ( lensX-a_radius >= 0 ? 0 : a_radius - lensX );
		int fromY = ( lensY-a_radius >= 0 ? 0 : a_radius - lensY );

		int toX = ( lensX+a_radius < bi.getWidth() ? radiusX2 : radiusX2 - (lensX+a_radius - bi.getWidth() )  );
		int toY = ( lensY+a_radius < bi.getHeight() ? radiusX2 : radiusX2 - (lensY+a_radius -bi.getHeight() ) );

		int[] pixelColors = ImageFunctions.instance().getRGB( lensPosition.M_getX()-a_radius, lensPosition.M_getY()-a_radius, radiusX2+1, radiusX2+1, bi );

		_lensTransformation.transform(fromX, toX, fromY, toY, lensX, lensY, pixelColors, bi_result);

		if( _applyBlur )
			bi_result = applyBlur( bi_result, fromX, toX, fromY, toY );

		Coordinate2D upleft = new Coordinate2D( fromX, fromY );
		Coordinate2D downright = new Coordinate2D( toX, toY );
		result = new LensTransformationResult( bi_result, upleft, downright );

		if( _borderImage != null )
		{
			Graphics grp = bi_result.getGraphics();

			grp.drawImage(_borderImage, fromX, fromY, toX, toY, fromX, fromY, toX, toY, null );
		}

		return( result );
	}

	protected BufferedImage applyBlur( BufferedImage inputImage,
										int fromX, int toX, int fromY, int toY )
	{
		BufferedImage result = null;

		BlurFilter bf = new BlurFilter();
		bf.setInputData( a_radius, fromX, toX, fromY, toY );
		result = bf.applyFilter(inputImage);

		return( result );
	}

	protected static class ARGBcomponentGradation
	{
		protected int[] _components1 = new int[4];
		protected int[] _components2 = new int[4];

		public ARGBcomponentGradation( int argb1, int argb2 )
		{
			setARGB( _components1, argb1 );
			setARGB( _components2, argb2 );
		}

		protected void setARGB( int[] argbArray, int argb )
		{
			argbArray[3] = ( argb >> 24 ) & 0xFF;
			argbArray[2] = ( argb >> 16 ) & 0xFF;
			argbArray[1] = ( argb >> 8 ) & 0xFF;
			argbArray[0] = argb & 0xFF;
		}

		protected double limitFactor( double factor )
		{
			if( factor > 1.0D )
				factor = 1.0D;
			else if( factor < 0.0D )
				factor = 0.0D;

			return( factor );
		}

		protected int gradate( int value1, int value2, double factor )
		{
			double factor2 = 1.0D - factor;

			int result = (int) ( value1 * factor + value2 * factor2 );
			result = IntegerFunctions.max( 0, result );
			result = IntegerFunctions.min( 255, result );

			return( result );
		}

		// factor must be between 0.0D and 1.0D
		public int getARGBGradation( double factor )
		{
			factor = limitFactor( factor );

			int alpha = gradate( _components1[3], _components2[3], factor );
			int rr = gradate( _components1[2], _components2[2], factor );
			int gg = gradate( _components1[1], _components2[1], factor );
			int bb = gradate( _components1[0], _components2[0], factor );

			return( getARGB( alpha, rr, gg, bb ) );
		}

		public int getARGB( int alpha, int rr, int gg, int bb )
		{
			int result = ( alpha << 24 ) |
						( rr << 16 ) |
						( gg << 8 ) |
						bb;

			return( result );
		}
	}
}
