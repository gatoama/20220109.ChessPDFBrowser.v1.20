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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Usuario
 */
public class ImageJPanel_ extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
{

	protected BufferedImage a_image = null;

	public ImageJPanel_( String imageFileName )
	{
		super.init();

		File file = new File( imageFileName );
		try
		{
			a_image = ImageIO.read(file);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			a_image = null;
		}
	}
	
	@Override
	public void paint(Graphics gc)
	{
		super.paint(gc);

		if(a_image!=null)
		{
			int width = a_image.getWidth();
			int height = a_image.getHeight();

			if( width > getWidth() )	width = getWidth();
			if( height > getHeight() )	height = getHeight();

			gc.drawImage(a_image, 0, 0, width, height, 0, 0, width, height, null);
		}
	}

	public BufferedImage M_getImage()
	{
		return( a_image );
	}
	
}
