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
package com.frojasg1.general.desktop.view.icons;

import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomIconBuilder
{
	protected static ZoomIconBuilder _instance;
	protected static final Dimension DIMENSION_FOR_HUNDRED_PERCENT_ZOOM_SQUARED_ICON_FOR_MENU_ITEM = new Dimension( 14, 14 );

	public static void changeInstance( ZoomIconBuilder inst )
	{
		_instance = inst;
	}

	public static ZoomIconBuilder instance()
	{
		if( _instance == null )
			_instance = new ZoomIconBuilder();
		return( _instance );
	}

	protected double calculateFactor( double size, double target )
	{
		double result = 1;

		if( size != 0 )
			result = target / size;

		return( result );
	}

	public Double calculateAdditionalFactor( Icon icon, Dimension dimen )
	{
		Double result = null;

		if( ( icon != null ) && ( dimen != null ) )
		{
			double xFactor = calculateFactor( icon.getIconWidth(), dimen.width );
			double yFactor = calculateFactor( icon.getIconHeight(), dimen.height );

			result = ( xFactor > yFactor ) ? yFactor : xFactor;
		}

		return( result );
	}

	public Icon createOriginalIcon( BufferedImage image )
	{
		Icon result = null;

		try
		{
			result = new ImageIcon( image );
		}
		catch( Exception ex )
		{}

		return( result );
	}

	public ZoomIconImp createSquaredHundredPercentZoomIconDefaultForMenuItem( String resourceName ) throws IOException
	{
		return( createZoomIconDefault( resourceName, DIMENSION_FOR_HUNDRED_PERCENT_ZOOM_SQUARED_ICON_FOR_MENU_ITEM ) );
	}

	public ZoomIconImp createZoomIconDefault( String resourceName, Dimension dimen ) throws IOException
	{
		BufferedImage image = ImageFunctions.instance().loadImageFromJar( resourceName );
		Integer transparentColor = image.getRGB(0, 0);
		image = ImageFunctions.instance().resizeImage( image, image.getWidth(), image.getHeight(), transparentColor, null, null );
		return( createZoomIcon(image, dimen) );
	}

	public ZoomIconImp createZoomIcon( BufferedImage image, Dimension dimen )
	{
		return( createZoomIcon( createOriginalIcon(image), dimen ) );
	}

	public ZoomIconImp createZoomIcon( BufferedImage image, double factor )
	{
		return( createZoomIcon( createOriginalIcon(image), factor ) );
	}

	public ZoomIconImp createZoomIcon( Icon originalIcon, double factor )
	{
		ZoomIconImp result = new ZoomIconImp( originalIcon );
		result.setAdditionalFactor(factor);

		return( result );
	}

	public ZoomIconImp createZoomIcon( Icon originalIcon, Dimension dimen )
	{
		ZoomIconImp result = null;
		Double factor = calculateAdditionalFactor( originalIcon, dimen );
		if( factor != null )
		{
			result = createZoomIcon( originalIcon, factor );
		}

		return( result );
	}
}
