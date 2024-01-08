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
package com.frojasg1.general.desktop.view;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.ExecutionFunctions.UnsafeFunction;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.icons.ZoomIconBuilder;
import com.frojasg1.general.desktop.view.zoom.ZoomIcon;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import javax.swing.Icon;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class IconFunctions {
	
	protected static IconFunctions _instance;

	public static void changeInstance( IconFunctions inst )
	{
		_instance = inst;
	}

	public static IconFunctions instance()
	{
		if( _instance == null )
			_instance = new IconFunctions();
		return( _instance );
	}

	public ZoomIcon createZoomIcon( Icon icon )
	{
		ZoomIcon result = new ZoomIconImp( icon );

		return( result );
	}

	protected boolean canInvertColors( Icon icon )
	{
		boolean result = true;
		if( icon instanceof ZoomIcon )
		{
			ZoomIcon zi = (ZoomIcon) icon;
			result = zi.canInvertColors();
		}
		return( result );
	}

	public Icon invertIconColors( Icon icon )
	{
		Icon result = icon;
		
		if( canInvertColors(icon) )
		{
			Icon curr = icon;
			ZoomIconImp zii = null;
			while( curr instanceof ZoomIconImp )
			{
				zii = (ZoomIconImp) curr;

				curr = zii.getOriginalIcon();
			}

			Icon invertedSingleIcon = invertSingleIcon( curr );

			if( zii != null )
			{
				result = icon;
				zii.setIconWithInvertedColors( invertedSingleIcon );
			}
			else
			{
				result = invertedSingleIcon;
			}
		}

		return( result );
	}

	protected Icon invertSingleIcon( Icon originalIcon )
	{
		Icon result = null;
		if( originalIcon != null )
		{
			BufferedImage bi = toImage( originalIcon );

			BufferedImage invertedImage = ImageFunctions.instance().invertImage(bi);

			result = new ImageIcon(invertedImage);
		}
		return( result );
	}

	public BufferedImage toImage( Icon icon )
	{
		BufferedImage result = null;
		if( icon != null )
		{
			result = new BufferedImage( icon.getIconWidth(),
										icon.getIconHeight(),
										BufferedImage.TYPE_INT_ARGB );

			Graphics g2 = result.getGraphics();
			icon.paintIcon(null, g2, 0, 0);
			g2.dispose();
		}
		return( result );
	}

	public ZoomIconImp getIconForMenuItem( String iconResourceName )
	{
		return( ExecutionFunctions.instance().safeFunctionExecution( () -> 
			ZoomIconBuilder.instance().createSquaredHundredPercentZoomIconDefaultForMenuItem( iconResourceName ) ) );
	}

	public void setIconForMenuItem( AbstractButton menuItem, String iconResourceName )
	{
		Icon icon = getIconForMenuItem( iconResourceName );
		if( icon != null )
			menuItem.setIcon(icon);
	}
}
