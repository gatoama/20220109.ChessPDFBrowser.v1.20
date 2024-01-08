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
package com.frojasg1.chesspdfbrowser.view.chess.images;

import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedObserved;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.image.ImageUtilFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.factory.impl.ColorInversorFactoryImpl;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Usuario
 */
public class ChessBoardImagesCache extends ChessBoardImages
{
	protected ChessBoardImages _originalChessBoardImages;
	protected boolean _isDarkMode = false;
	protected ColorInversor _colorInversor = null;

	public ChessBoardImagesCache( ChessBoardImages originalChessBoardImages,
									ColorInversor ci ) throws IOException
	{
		_originalChessBoardImages = originalChessBoardImages;
		_colorInversor = ci;
		// default constructor
		initResizedMaps();
	}

	protected synchronized void initialize(String resourcePath) throws IOException
	{
		
	}

	public ChessBoardImagesCache() throws IOException
	{
		this( ChessBoardImages.instance(), ColorInversorFactoryImpl.instance().createColorInversor() );
	}

	public void setDarkMode( boolean isDarkMode )
	{
		_isDarkMode = isDarkMode;
	}

	public boolean isDarkMode()
	{
		return( _isDarkMode );
	}

	protected BufferedImage[] resizeSquares( int newSquareWidth )
	{
		BufferedImage[] result = new BufferedImage[2];

		BufferedImage[] originals = _originalChessBoardImages.getSquareImages(newSquareWidth);
		for( int ii=0; ii<result.length; ii++ )
			result[ii] = invertImageIfNecessary( originals[ii] );

		return( result );
	}

	protected ColorInversor getColorInversor()
	{
		return( _colorInversor );
	}

	protected BufferedImage putOutImageColor( BufferedImage original )
	{
		return( _colorInversor.putOutImageColor(original, 0.5d) );
	}

	protected BufferedImage invertImageIfNecessary( BufferedImage originalImage )
	{
		BufferedImage result = originalImage;
		if( isDarkMode() )
			result = putOutImageColor(originalImage);
		
		return( result );
	}

	protected Map< String, BufferedImage > createInvertedIfNecessaryImagesMap( Map< String, BufferedImage > originalMap )
	{
		Map< String, BufferedImage > result = null;

		if( originalMap != null )
		{
			result = createMap();

			for( Map.Entry<String, BufferedImage> entry: originalMap.entrySet() )
				result.put( entry.getKey(), invertImageIfNecessary( entry.getValue() ) );
		}

		return( result );
	}

	@Override
	protected synchronized Map< String, BufferedImage > getSizeNormalPiecesMap( int pieceWidth )
	{
		Map< String, BufferedImage > result = _resizedPiecesMap.get( pieceWidth );

		if( result == null )
		{
			result = createInvertedIfNecessaryImagesMap( _originalChessBoardImages.getSizeNormalPiecesMap(pieceWidth) );
			_resizedPiecesMap.put( pieceWidth, result );
		}

		return( result );
	}

	@Override
	protected synchronized Map< String, BufferedImage > getSizeSemiTransparentPiecesMap( int pieceWidth )
	{
		Map< String, BufferedImage > result = _resizedSemiTransparentPiecesMap.get( pieceWidth );

		if( result == null )
		{
			result = createInvertedIfNecessaryImagesMap( _originalChessBoardImages.getSizeSemiTransparentPiecesMap(pieceWidth) );
			_resizedSemiTransparentPiecesMap.put( pieceWidth, result );
		}

		return( result );
	}
}
