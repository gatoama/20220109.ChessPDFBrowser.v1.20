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
package com.frojasg1.general.desktop.image;

import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ResizeImageAccurate implements ResizeImageInterface
{
	protected static ResizeImageAccurate _instance = null;
	
	public static ResizeImageAccurate instance()
	{
		if( _instance == null )
			_instance = new ResizeImageAccurate();

		return( _instance );
	}

	protected BufferedImage resizeImageJava( BufferedImage originalImage, int newWidth, int newHeight )
	{
		BufferedImage resizedImage = new BufferedImage(newWidth , newHeight, originalImage.getType() );
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newWidth , newHeight , null);
		g.dispose();

		return( resizedImage );
	}

	public BufferedImage resizeImage( BufferedImage originalImage, int newWidth,
										int newHeight,
										Function<Integer, Integer> colorTranslator )
		throws IllegalArgumentException
	{
		return( resizeImageManual( originalImage, newWidth, newHeight, colorTranslator ) );
	}

	@Override
	public BufferedImage resizeImage( BufferedImage originalImage, int newWidth, int newHeight ) throws IllegalArgumentException
	{
		return( resizeImage( originalImage, newWidth, newHeight, null ) );
	}

	protected BufferedImage resizeImageManual( BufferedImage originalImage, int newWidth,
											int newHeight, Function<Integer, Integer> colorTranslator )
		throws IllegalArgumentException
	{
		if( ( newWidth < 1 ) || ( newHeight < 1 ) )		throw( new IllegalArgumentException( "Bad size for image.   Width: " + newWidth + ". Height: " + newHeight ) );

		BufferedImage result = new BufferedImage( newWidth, newHeight, BufferedImage.TYPE_INT_ARGB );

		double xZoomFactor = ((double) newWidth ) / originalImage.getWidth() ;
		double yZoomFactor = ((double) newHeight ) / originalImage.getHeight() ;

		int[] pixels = ImageFunctions.instance().getRGB(0, 0, originalImage.getWidth(), originalImage.getHeight(), originalImage );

		CalculateOffsets offsetsXX = new CalculateOffsets(xZoomFactor);
		CalculateOffsets offsetsYY = new CalculateOffsets(yZoomFactor);
		DoublePixelComponents dpc = new DoublePixelComponents();

		for( int tY = 0; tY < newHeight; tY++,	offsetsYY.inc() )
		{
//			System.out.print( "\n" + Integer.toString( tY ) + "\n\t" );
			offsetsXX.reset();
			for( int tX = 0; tX < newWidth; tX++, offsetsXX.inc() )
			{
//				System.out.print( (tX==0?"":", ") + Integer.toString( tX ) );
				int pixelColor = calculatePixelColor(pixels, offsetsXX, offsetsYY,
														originalImage.getWidth(), dpc,
														colorTranslator );

				result.setRGB( tX, tY, pixelColor );
			}
		}

		return( result );
	}

	protected int calculatePixelColor( int[] pixels, CalculateOffsets offsetsXX,
												CalculateOffsets offsetsYY,
												int originalWidth,
												DoublePixelComponents dpc,
												Function<Integer, Integer> colorTranslator )
	{
		dpc.reset();

		// top-left
		addCornerPixel(offsetsXX.getCoordinateForInitialFraction(),
						offsetsYY.getCoordinateForInitialFraction(),
						originalWidth,
						pixels,
						offsetsXX._initialFraction * offsetsYY._initialFraction,
						dpc,
						colorTranslator );

		// top-right
		addCornerPixel(offsetsXX.getCoordinateForFinalFraction(),
						offsetsYY.getCoordinateForInitialFraction(),
						originalWidth,
						pixels,
						offsetsXX._finalFraction * offsetsYY._initialFraction,
						dpc,
						colorTranslator );

		// bottom-left
		addCornerPixel(offsetsXX.getCoordinateForInitialFraction(),
						offsetsYY.getCoordinateForFinalFraction(),
						originalWidth,
						pixels,
						offsetsXX._initialFraction * offsetsYY._finalFraction,
						dpc,
						colorTranslator );

		// bottom-right
		addCornerPixel(offsetsXX.getCoordinateForFinalFraction(),
						offsetsYY.getCoordinateForFinalFraction(),
						originalWidth,
						pixels,
						offsetsXX._finalFraction * offsetsYY._finalFraction,
						dpc,
						colorTranslator );

		int increment = 1;
		int countX = 0;
		countX = offsetsXX._finalWholeValue - offsetsXX._initialWholeValue + 1;
		if( countX > 0 )
		{
			// top
			addStraightBoundary( offsetsXX._initialWholeValue,
							offsetsYY.getCoordinateForInitialFraction(),
							originalWidth,
							increment, countX,
							offsetsXX.getFactorForPixel() * offsetsYY._initialFraction,
							pixels, dpc,
							colorTranslator );
			// bottom
			addStraightBoundary( offsetsXX._initialWholeValue,
							offsetsYY.getCoordinateForFinalFraction(),
							originalWidth,
							increment, countX,
							offsetsXX.getFactorForPixel() * offsetsYY._finalFraction,
							pixels, dpc,
							colorTranslator );
		}

		int countY = offsetsYY._finalWholeValue - offsetsYY._initialWholeValue + 1;
		if( countY > 0 )
		{
			increment = originalWidth;
			// left
			addStraightBoundary( offsetsXX.getCoordinateForInitialFraction(),
							offsetsYY._initialWholeValue,
							originalWidth,
							increment, countY,
							offsetsXX._initialFraction * offsetsYY.getFactorForPixel(),
							pixels, dpc,
							colorTranslator );
			// right
			addStraightBoundary( offsetsXX.getCoordinateForFinalFraction(),
							offsetsYY._initialWholeValue,
							originalWidth,
							increment, countY,
							offsetsXX._finalFraction * offsetsYY.getFactorForPixel(),
							pixels, dpc,
							colorTranslator );
		}

		// internal pixels
		if( ( countX > 0 ) && ( countY > 0 ) )
		{
			double factor = Math.min( 1.0D, offsetsXX.getFactorForPixel() ) *
							Math.min( 1.0D, offsetsYY.getFactorForPixel() );
			int indexForOriginalPixel = getOffset( offsetsXX._initialWholeValue,
													offsetsYY._initialWholeValue,
													originalWidth );
			for( int yy = offsetsYY._initialWholeValue;
				yy<=offsetsYY._finalWholeValue;
				yy++, indexForOriginalPixel += originalWidth - offsetsXX._finalWholeValue + offsetsXX._initialWholeValue - 1)
			{
				for( int xx = offsetsXX._initialWholeValue;
					xx<=offsetsXX._finalWholeValue;
					xx++, indexForOriginalPixel++ )
				{
					int pixelValue = getPixelValue( pixels, indexForOriginalPixel,
							colorTranslator );
					dpc.addPixelToComponents(pixelValue, factor );
				}
			}
		}

		return( dpc.getRGB() );
	}

	protected int getPixelValue( int[] pixels, int index,
									Function<Integer, Integer> colorTranslator )
	{
		int inputColor = pixels[index];
		int result = inputColor;
		if( colorTranslator != null )
			result = colorTranslator.apply(inputColor);

		return( result );
	}

	protected static int getOffset( int xx, int yy, int width )
	{
		int result = yy * width + xx;

		return( result );
	}

	protected void addStraightBoundary( int initialXX, int initialYY, int originalWidth,
							int incrementForOffset, int count,
							double factor,
							int [] pixels,
							DoublePixelComponents dpc,
							Function<Integer, Integer> colorTranslator )
	{
		if( ( factor > 0.1e-6D ) && ( initialXX > -1 ) && ( initialYY > -1 ) )
		{
			int offset = getOffset( initialXX, initialYY, originalWidth );

			for( int ii=0; ii<count; ii++, offset += incrementForOffset )
			{
				int rgb = getPixelValue( pixels, offset, colorTranslator );
				dpc.addPixelToComponents(rgb, factor);
			}
		}
	}

	protected void addCornerPixel( int xx, int yy, int width,
											int[] pixels, double factor,
											DoublePixelComponents dpc,
											Function<Integer, Integer> colorTranslator )
	{
		if( ( factor > 0.001D ) && ( yy>-1 ) && ( xx > -1 ) )
		{
			int offset = getOffset( xx, yy, width );
			int argb = getPixelValue( pixels, offset, colorTranslator );

			dpc.addPixelToComponents(argb, factor);
		}
	}

	protected static class CalculateOffsets
	{
		public double _zoomFactor = 1.0D;
		public double _zoomFactorInverse;

		double _initialOriginalCoordinateforTransformedPixel;
		double _finalOriginalCoordinateforTransformedPixel;

		public double _initialFraction;
		public double _finalFraction;
		public int _initialWholeValue;
		public int _finalWholeValue;

		public CalculateOffsets( double zoomFactor )
		{
			_zoomFactor = zoomFactor;
			_zoomFactorInverse = 1.0D / _zoomFactor;

			reset();
		}

		public void reset()
		{
			_initialOriginalCoordinateforTransformedPixel = 0;
			_finalOriginalCoordinateforTransformedPixel = _zoomFactorInverse;

			calculateOffsets();
		}

		protected void calculateOffsets( )
		{
			_initialWholeValue = IntegerFunctions.roundToInt(
								Math.ceil( _initialOriginalCoordinateforTransformedPixel )
												);
			_finalWholeValue = IntegerFunctions.roundToInt(
								Math.floor( _finalOriginalCoordinateforTransformedPixel ) - 1 );
			_initialFraction = Math.ceil( _initialOriginalCoordinateforTransformedPixel ) -
														_initialOriginalCoordinateforTransformedPixel;

			// every unit is a original pixel
			// every _zoomFactorInverse corresponds with a zoomed pixel
			if( _initialFraction > _zoomFactorInverse)
			{
				_initialFraction = 1.0D;
				_finalFraction = 0.0D;
			}
			else
			{
				_finalFraction = _finalOriginalCoordinateforTransformedPixel -
										Math.floor( _finalOriginalCoordinateforTransformedPixel );

				if( _finalFraction > _zoomFactorInverse)
				{
					_finalFraction = _zoomFactorInverse;
				}

				_initialFraction *= _zoomFactor;
				_finalFraction *= _zoomFactor;
			}
		}

		public void inc()
		{
			_initialOriginalCoordinateforTransformedPixel += _zoomFactorInverse;
			_finalOriginalCoordinateforTransformedPixel += _zoomFactorInverse;

			calculateOffsets();
		}

		public int getCoordinateForInitialFraction()
		{
			int result = -1;
			if( _initialFraction > 0 )
				result = _initialWholeValue - 1;

			return( result );
		}

		public int getCoordinateForFinalFraction()
		{
			int result = -1;
			if( _finalFraction > 0 )
				result = _finalWholeValue + 1;

			return( result );
		}

		public double getFactorForPixel()
		{
			return( _zoomFactor > 1.0D ? 1.0D : _zoomFactor );
		}
	}

	protected static class DoublePixelComponents
	{
		protected double[] _components = new double[4];
		protected double _factorAlpha0 = 0.0D;

		public DoublePixelComponents()
		{
			reset();
		}

		public void reset()
		{
			_factorAlpha0 = 0;
			for( int ii=0; ii< _components.length; ii++ )
				_components[ii] = 0;
		}

		public void addPixelToComponents( int argb, double factor )
		{
			double alpha = ( argb >>> 24 );
			_components[0] += alpha * factor;

			if( alpha == 0 )
				_factorAlpha0 += factor;
			else
			{
				double rr = ( ( argb & 0x00FF0000 ) >>> 16 );
				double gg = ( ( argb & 0x0000FF00 ) >>> 8 );
				double bb = ( argb & 0x000000FF );

				_components[1] += rr * factor;
				_components[2] += gg * factor;
				_components[3] += bb * factor;
			}
		}

		protected int normalize( double value )
		{
			return( IntegerFunctions.min( 255, IntegerFunctions.roundToInt(value) ) );
		}

		public int getRGB()
		{
			int result = ( normalize( _components[0] ) << 24 ) |
						( normalize( _components[1] / ( 1 - _factorAlpha0 ) ) << 16 ) |
						( normalize( _components[2] / ( 1 - _factorAlpha0 ) ) << 8 ) |
						normalize( _components[3] / ( 1 - _factorAlpha0 ) );

			return( result );
		}
	}
}
