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
package com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class SummarizedPage
{
	public static final int ALLOWED_GAP_FOR_DIMENSION = 3;

//	protected static final double PERCENTAGE_OF_POINTS_TO_MATCH_A_BACKGROUND_PIXEL = 75D;
	protected static final double PERCENTAGE_OF_POINTS_TO_MATCH_A_BACKGROUND_PIXEL = 60D;
//	protected static final double PERCENTAGE_OF_POINTS_TO_MATCH_A_VERTICAL_GAP = 85D;
	protected static final int MINIMUM_NUMBER_OF_PAGES_TO_VALIDATE = 3;

	protected static final int THRESHOLD_FOR_INK_COMPONENT = 64;

	protected Dimension _size = null;
	protected int _minimumNumberOfPagesToSummarize = 3;
	protected int _maximumNumberOfPagesToSummarize = 3;

//	protected Map< Integer, SummarizedHomogeneousPoint[] > _summarizedGapPoints_map = null;
	protected PixelComponents[][] _summarizedBackgroundPoints = null;

	protected List< int[] > _listOfPixelsOfPages = null;

	public SummarizedPage( )
	{
		_listOfPixelsOfPages = new ArrayList< int[] >();
	}

	public SummarizedPage( BufferedImage image )
	{
		_listOfPixelsOfPages = new ArrayList< int[] >();
		initWithSingleImage( image );
	}

	protected void initWithSingleImage( BufferedImage image )
	{
		_size = new Dimension( image.getWidth(), image.getHeight() );
		int[] pixel = ImageFunctions.instance().getRGB( 0, 0, 1, 1, image );

		initWithColor( pixel[0] );
	}

	protected short getComponent( int color, int bitsRight )
	{
		return( ( short ) ( ( color >>> bitsRight ) & 0xFF ) );
	}

	protected void initWithColor( int color )
	{
		boolean signedComponents = false;
		PixelComponents pixel = new PixelComponents( (short) 0xFF,
				getComponent( color, 16 ), getComponent( color, 8 ), getComponent( color, 0 ),
				signedComponents );

		_summarizedBackgroundPoints = new PixelComponents[ (int) _size.getWidth() ][ (int) _size.getHeight() ];
		for( int xx=0; xx<_size.getWidth(); xx++ )
			for( int yy=0; yy<_size.getHeight(); yy++ )
				_summarizedBackgroundPoints[xx][yy] = pixel;
	}

	public PixelComponents[][] getSummarizedPixels()
	{
		return( _summarizedBackgroundPoints );
	}

	public Dimension getSize()
	{
		return( _size );
	}

	public void setNumberOfPagesToSummarize( int minimumNumberOfPagesToSummarize,
												int maximumNumberOfPagesToSummarize )
	{
		_minimumNumberOfPagesToSummarize = minimumNumberOfPagesToSummarize;
		_maximumNumberOfPagesToSummarize = maximumNumberOfPagesToSummarize;
	}

	public boolean isComplete()
	{
		return( _summarizedBackgroundPoints != null );
	}

	public void addPage( BufferedImage image )
	{
		if( _listOfPixelsOfPages != null )
		{
			if( _size == null )
			{
				_size = new Dimension( image.getWidth(), image.getHeight() );
			}

			if( !imageDimensionsMatch( image, _size) )
			{
				throw( new RuntimeException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_SIZE_NOT_EXPECTED ) +
												" : [" + image.getWidth() + "," + image.getHeight() + "] <> [" +
												+ _size.getWidth() + "," + _size.getHeight() + "]" ) );
			}

			_listOfPixelsOfPages.add( getPixels( image ) );

			if( _listOfPixelsOfPages.size() >= _maximumNumberOfPagesToSummarize )
			{
				summarize();
			}
		}
	}

	protected boolean dimenMatches( int dimen1, int dimen2 )
	{
		return( abs( dimen1 - dimen2 ) <= ALLOWED_GAP_FOR_DIMENSION );
	}

	protected boolean imageDimensionsMatch( BufferedImage image, Dimension dimen )
	{
		boolean result = false;

		if( ( image != null ) && ( dimen != null ) )
		{
			result = ( dimenMatches( image.getWidth(), dimen.width ) &&
						dimenMatches( image.getHeight(), dimen.height ) );
		}

		return( result );
	}

	protected int abs( int value )
	{
		return( IntegerFunctions.abs( value ) );
	}

	protected int[] getPixels( BufferedImage image )
	{
		ImageSegmentator is = new ImageSegmentator( null, image, null );
		return( is.getPixels() );
	}

	public void summarize()
	{
		if( ( _listOfPixelsOfPages != null ) && ( _listOfPixelsOfPages.size() >= _minimumNumberOfPagesToSummarize ) )
		{
			Object[] arrayOfPages = _listOfPixelsOfPages.toArray();

			_summarizedBackgroundPoints = new PixelComponents[ (int) _size.getWidth() ][ (int) _size.getHeight() ];

			int offset = 0;
			for( int xx=0; xx<(int) _size.getWidth(); xx++ )
			{
				offset = xx;

				for( int yy=0; yy<(int) _size.getHeight(); yy++ )
				{
					short[][] arrayOfValuesOfPixels = getArrayOfValuesOfPixels( arrayOfPages, offset );

					PixelComponents shp = ImageSegmentator.getSummararizedHomogeneousPoint(arrayOfValuesOfPixels,
																		PERCENTAGE_OF_POINTS_TO_MATCH_A_BACKGROUND_PIXEL,
																		ImageSegmentator.TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_BACKGROUND_GAP );
					
					_summarizedBackgroundPoints[xx][yy] = shp;
					offset += (int) _size.getWidth();
				}
			}

			_listOfPixelsOfPages = null;
		}
	}

	public short[][] getArrayOfValuesOfPixels( Object[] arrayOfPages, int offset )
	{
		short[][] result = new short[arrayOfPages.length][];

		for( int ii=0; ii<arrayOfPages.length; ii++ )
		{
			int[] arrayOfPixels = (int[]) arrayOfPages[ii];

			int pixel = arrayOfPixels[offset];
			result[ii] = ImageSegmentator.getPixelComponents(pixel);
		}

		return( result );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
