/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.buttons;

import com.frojasg1.applications.common.components.name.ComponentNameComponents;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JToggleButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ResizableImageJButtonBuilder
{
	protected static class LazyHolder
	{
		public static final ResizableImageJButtonBuilder INSTANCE = new ResizableImageJButtonBuilder();
	}

	public static ResizableImageJButtonBuilder instance()
	{
		return( LazyHolder.INSTANCE );
	}

	public String getImageResourceFromName( String name )
	{
		ComponentNameComponents cnc = new ComponentNameComponents( name );
		String result = cnc.getComponent( ComponentNameComponents.ICON_COMPONENT );

		return( result );
	}

	public AbstractButton createResizableImageJButton( AbstractButton jbutton )
	{
		Insets insets = new Insets( 1, 1, 1, 1 );
		AbstractButton result = null;
		String imageResource = getImageResourceFromName( jbutton.getName() );

		if( imageResource != null )
		{
			if( jbutton instanceof JToggleButton )
				result = new ResizableImageJToggleButton( imageResource, insets );
			else if( jbutton instanceof JButton )
				result = new ResizableImageJButton( imageResource, insets );
		}

		return( result );
	}
}
