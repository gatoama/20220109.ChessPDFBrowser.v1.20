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
package com.frojasg1.general.desktop.view.zoom.ui;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ColorInversorBasicMenuItemUI extends BasicMenuItemUI
{
	protected ColorInversor _colorInversor = null;
	protected JMenuItem _comp;

	public ColorInversorBasicMenuItemUI(ColorInversor colorInversor)
	{
		super();
		_colorInversor = colorInversor;
	}

	protected static ColorInversor getColorInversor( JComponent jcomp )
	{
		return( FrameworkComponentFunctions.instance().getColorInversor(jcomp) );
	}

	public static ComponentUI createUI( JComponent x ) {
        return new ColorInversorBasicMenuItemUI(getColorInversor(x));
    }

    public void installUI(JComponent c) {
        _comp = (JMenuItem) c;

		super.installUI(c);
	}

	protected boolean isDarkModeActivated()
	{
		return( _colorInversor.isDarkMode(_comp) );
	}

	public void paint(Graphics g, JComponent c) {
		if( ! isDarkModeActivated() )
			super.paint(g, c);
		else
		{
			BufferedImage image = new BufferedImage( _comp.getWidth(), _comp.getHeight(),
													BufferedImage.TYPE_INT_ARGB );

			Graphics grp = image.createGraphics();
			grp.setColor( g.getColor() );
			grp.setClip( g.getClip() );

			super.paint( grp, c );

//			ExecutionFunctions.instance().safeMethodExecution(  () ->
//				ImageIO.write(image, "png", new File( "J:\\notInvertedImage.png" ) ) );

			g.drawImage(_colorInversor.invertImage(image), 0, 0, null);
		}
	}
}
