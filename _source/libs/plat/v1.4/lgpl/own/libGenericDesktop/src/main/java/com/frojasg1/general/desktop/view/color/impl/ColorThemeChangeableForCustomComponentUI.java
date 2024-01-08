/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color.impl;

import com.frojasg1.general.desktop.image.ImageFunctions;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ColorThemeChangeableForCustomComponentUI extends ColorThemeChangeableBase
{
	protected Component _comp;
	protected BiConsumer<Graphics, JComponent> _paintFunction;

	protected boolean _originallyWasDark;

	public ColorThemeChangeableForCustomComponentUI( Component comp,
												BiConsumer<Graphics, JComponent> paintFunction,
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

	public void paint( Graphics grp, JComponent jcomp )
	{
		synchronized( this )
		{
			if( wasOriginallyDark() == isDarkMode() )
				_paintFunction.accept( grp, jcomp );
			else
			{
				BufferedImage image = new BufferedImage( _comp.getWidth(),
														_comp.getHeight(),
														BufferedImage.TYPE_INT_ARGB );
				Graphics grp2 = image.createGraphics();
				grp2.setClip( grp.getClip() );
				_paintFunction.accept( grp2, jcomp );

				grp.drawImage(ImageFunctions.instance().invertImage( image ),
								0, 0,
								image.getWidth(), image.getHeight(),
								null );
				grp2.dispose();
			}
		}
	}
}
