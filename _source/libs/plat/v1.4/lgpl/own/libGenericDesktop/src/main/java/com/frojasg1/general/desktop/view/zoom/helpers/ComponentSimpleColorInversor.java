/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.zoom.helpers;

import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import java.awt.Color;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentSimpleColorInversor {
	
	protected static class LazyHolder
	{
		protected static final ComponentSimpleColorInversor INSTANCE = new ComponentSimpleColorInversor();
	}

	public static ComponentSimpleColorInversor instance()
	{
		return( LazyHolder.INSTANCE );
	}

	protected ColorInversor getColorInversor(JComponent comp)
	{
		return( FrameworkComponentFunctions.instance().getColorInversor(comp) );
	}

    public Color invertColorIfNecessary( JComponent comp, Color color ) {
		Color result = color;
		ColorInversor ci = getColorInversor(comp);
		if( ( ci != null ) && ( ci.isDarkMode(comp) ) )
			result = ci.invertColor(result);

		return result;
    }
}
