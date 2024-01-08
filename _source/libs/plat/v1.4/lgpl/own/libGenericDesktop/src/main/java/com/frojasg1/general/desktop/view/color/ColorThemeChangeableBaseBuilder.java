/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color;

import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ColorThemeChangeableBaseBuilder
{
	protected static ColorThemeChangeableBaseBuilder _instance = null;
	
	public static ColorThemeChangeableBaseBuilder instance()
	{
		if( _instance == null )
			_instance = new ColorThemeChangeableBaseBuilder();

		return( _instance );
	}

	public ColorThemeChangeableBase createColorThemeChangeableBase()
	{
		return( new ColorThemeChangeableBase() );
	}
}
