/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color.impl;

import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ColorThemeChangeableForCustomComponent extends ColorThemeChangeableBase
{
	protected Component _comp;
	protected Consumer<Graphics> _paintFunction;

	protected boolean _originallyWasDark;

	public ColorThemeChangeableForCustomComponent( Component comp,
												Consumer<Graphics> paintFunction,
												boolean originallyWasDark )
	{
		_comp = comp;
		_paintFunction = paintFunction;
		_originallyWasDark = originallyWasDark;
	}

	public boolean wasOriginallyDark()
	{
		return( _originallyWasDark );
	}

	public void paint( Graphics grp )
	{
		synchronized( this )
		{
			if( wasOriginallyDark() == isDarkMode() )
				_paintFunction.accept( grp );
			else
			{
				BufferedImage image = new BufferedImage( _comp.getWidth(),
														_comp.getHeight(),
														BufferedImage.TYPE_INT_ARGB );
				Graphics grp2 = image.createGraphics();
				grp2.setClip( grp.getClip() );
				_paintFunction.accept( grp2 );

				BufferedImage invertedImage = invertImage( image );
				grp.drawImage(invertedImage,
								0, 0,
								image.getWidth(), image.getHeight(),
								null );
				grp2.dispose();
			}
		}
	}

	public Consumer<Graphics> getPaintFunction() {
		return _paintFunction;
	}

	public void setPaintFunction(Consumer<Graphics> _paintFunction) {
		this._paintFunction = _paintFunction;
	}

	protected BufferedImage invertImage( BufferedImage image )
	{
		return( ImageFunctions.instance().invertImage( image ) );
	}

	protected Component getComponent()
	{
		return( _comp );
	}
}
