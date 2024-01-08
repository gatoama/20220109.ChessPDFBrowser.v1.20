/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ColorThemeChangeableStatus extends ColorThemeChangeableStatusGetter
{
	public void setDarkMode( boolean value, ColorInversor colorInversor );

	public void setLatestWasDark( boolean value );

	default public boolean doNotInvertColors()
	{
		return( false );
	}

	default public void setDoNotInvertColors( boolean value )
	{
		
	}
}
