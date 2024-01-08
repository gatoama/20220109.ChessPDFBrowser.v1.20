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

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import java.lang.ref.WeakReference;
import javax.swing.AbstractButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ResizableImageButtonHelper implements ColorThemeInvertible
{
	protected static final String CLASS_NAME="ResizableImageJButton";
	
	protected WeakReference<AbstractButton> _parent;

	protected String _jarResourceName = null;
	protected Insets _insets = null;

	protected BufferedImage _image = null;

	public ResizableImageButtonHelper( AbstractButton parent,
										String jarResourceName, Insets insets )
	{
		_parent = new WeakReference<>(parent);
		_jarResourceName = jarResourceName;
		_insets = insets;
		ResizableImageButtonHelper.this.setImage();
	}

	public String getResourceName()
	{
		return( _jarResourceName );
	}

	public void setImageResource( String jarResourceNameForImage )
	{
		_jarResourceName = jarResourceNameForImage;

		BufferedImage image = ExecutionFunctions.instance().safeFunctionExecution( () -> ImageFunctions.instance().loadImageFromJar(_jarResourceName) );
		if( image != null )
		{
			if( hasToInvert() )
				image = invertImage( image );

			setImage( image );
		}
	}

	protected boolean hasToInvert()
	{
		boolean result = FrameworkComponentFunctions.instance().isDarkMode( getParent(), false );
//		ColorInversor ci = FrameworkComponentFunctions.instance().getColorInversor( getParent() );
//		if( ci != null )
//			result = ci.isDarkMode( getParent() );

		return( result );
	}

	public BufferedImage getImage()
	{
		return( _image );
	}

	public void setImage()
	{
		if( _image != null )
			setImage( _image );
		else
			setImageResource( _jarResourceName );
	}

	protected AbstractButton getParent()
	{
		return( _parent.get() );
	}

	public final void setImage( BufferedImage image )
	{
		_image = image;
		ViewFunctions.instance().addImageToButtonAccurate( getParent(), image, _insets );
	}

	public void resizeImage()
	{
		ResizableImageButtonHelper.this.setImage();
	}

	protected BufferedImage invertImage(BufferedImage image)
	{
		return( ImageFunctions.instance().invertImage(image) );
	}

	@Override
	public void invertColors( ColorInversor colorInversor )
	{
		setImage( invertImage( getImage() ) );

		colorInversor.invertColor( getParent().getBackground(), getParent()::setBackground );
		colorInversor.invertColor( getParent().getForeground(), getParent()::setForeground );
	}
}
