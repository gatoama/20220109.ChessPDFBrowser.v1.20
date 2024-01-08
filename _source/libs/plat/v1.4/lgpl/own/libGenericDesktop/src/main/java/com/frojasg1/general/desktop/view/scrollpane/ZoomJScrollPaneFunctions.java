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
package com.frojasg1.general.desktop.view.scrollpane;

import com.frojasg1.general.desktop.view.zoom.ui.ZoomMetalScrollBarUI;
import java.awt.Color;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ScrollBarUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJScrollPaneFunctions
{
	protected static ZoomJScrollPaneFunctions _instance;

	public static void changeInstance( ZoomJScrollPaneFunctions instance )
	{
		_instance = instance;
	}

	public static ZoomJScrollPaneFunctions instance()
	{
		if( _instance == null )
			_instance = new ZoomJScrollPaneFunctions();
		
		return( _instance );
	}

	// for non ZoomJScrollPanes
	public void updateHorizontalScrollBarUi( JScrollPane sp, boolean isDarkMode,
												boolean wasLatestModeDark )
	{
		JScrollBar originalSb = sp.getHorizontalScrollBar();
		JScrollBar newSb = sp.createHorizontalScrollBar();
		sp.setHorizontalScrollBar(newSb);

		updateScrollBarUi( originalSb, newSb, isDarkMode, wasLatestModeDark );
	}

	// for non ZoomJScrollPanes
	public void updateVerticalScrollBarUi( JScrollPane sp, boolean isDarkMode,
												boolean wasLatestModeDark )
	{
		JScrollBar originalSb = sp.getVerticalScrollBar();
		JScrollBar newSb = sp.createVerticalScrollBar();
		sp.setVerticalScrollBar(newSb);

		updateScrollBarUi( originalSb, newSb, isDarkMode, wasLatestModeDark );
	}

	public void updateScrollBarUi( JScrollBar originalSb, JScrollBar newSb,
									boolean isDarkMode, boolean wasLatestModeDark )
	{
		if( originalSb != null )
		{
			newSb.setBackground( noResourceColor( originalSb.getBackground() ) );
			newSb.setForeground( noResourceColor( originalSb.getForeground() ) );
		}

//		forceChangeUI(newSb, isDarkMode, wasLatestModeDark);
		forceChangeUI(newSb, isDarkMode, wasLatestModeDark);

//		if( isDarkMode() )
//			FrameworkComponentFunctions.instance().getColorInversor(this).invertSingleColorsGen(result);
	}

	protected Color noResourceColor( Color color )
	{
		Color result = color;
		if( result instanceof ColorUIResource )
			result = new Color( color.getRGB(), true );

		return( result );
	}

	public void forceChangeUI(JScrollBar sb, boolean isDarkMode,
											boolean wasLatestModeDark) {
		if (sb != null) {
			sb.setUI(createScrollBarUI(sb, isDarkMode, wasLatestModeDark));
		}
	}

	protected ScrollBarUI createScrollBarUI(JScrollBar sb, boolean isDarkMode,
											boolean wasLatestModeDark )
	{
		ZoomMetalScrollBarUI result = new ZoomMetalScrollBarUI(isDarkMode,
																wasLatestModeDark,
																sb);
//		result.setScrollPane(this);
//		if (wasLatestModeDark()) //			result.invertColors( FrameworkComponentFunctions.instance().getColorInversor(this) );
//			getColorInversor().invertSingleColorsGen(sb);

		return (result);
	}
}
