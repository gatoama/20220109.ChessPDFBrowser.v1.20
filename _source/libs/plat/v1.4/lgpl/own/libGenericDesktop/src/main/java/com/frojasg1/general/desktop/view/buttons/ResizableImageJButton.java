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
package com.frojasg1.general.desktop.view.buttons;

import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableBaseBuilder;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;
import java.awt.Insets;
import javax.swing.JButton;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ResizableImageJButton extends JButton
	implements ColorThemeInvertible, ResizableImage
{
	protected static final String CLASS_NAME="ResizableImageJButton";
	
	protected ResizableImageButtonHelper _helper;

	public ResizableImageJButton( String jarResourceName, Insets insets )
	{
		_helper = new ResizableImageButtonHelper( this, jarResourceName, insets );
		setImage_final();
	}

	protected ColorThemeChangeableBase createColorThemeChangeableBase()
	{
		return( ColorThemeChangeableBaseBuilder.instance().createColorThemeChangeableBase() );
	}

	public String getResourceName()
	{
		return( _helper.getResourceName() );
	}

	@Override
	public void setImage( BufferedImage image )
	{
		_helper.setImage(image);
	}

	@Override
	public void setImageResource( String imageResource )
	{
		_helper.setImageResource(imageResource);
	}

	protected final void setImage_final()
	{
		_helper.setImage();
	}

	@Override
	public void resizeImage()
	{
		setImage_final();
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
		super.setBounds( xx, yy, width, height );
		resizeImage();
	}

	@Override
	public void setSize( int width, int height )
	{
		super.setSize( width, height );
		resizeImage();
	}

	@Override
	public void setIcon( Icon icon )
	{
		super.setIcon( icon );
	}

	@Override
	public void invertColors( ColorInversor colorInversor )
	{
		_helper.invertColors(colorInversor);
	}
}
