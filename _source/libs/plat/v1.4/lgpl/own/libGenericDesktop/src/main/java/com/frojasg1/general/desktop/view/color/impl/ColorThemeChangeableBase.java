/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color.impl;

import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatus;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ColorThemeChangeableBase implements ColorThemeChangeableStatus
{
	protected boolean _isDarkMode = false;
	protected boolean _wasLatestModeDark = false;
	protected boolean _doNotInvertColors = false;

	@Override
	public boolean isDarkMode()
	{
		return( _isDarkMode );
	}

	@Override
	public void setDarkMode(boolean value, ColorInversor colorInversor)
	{
		_isDarkMode = value;
	}

	@Override
	public boolean wasLatestModeDark()
	{
		return( _wasLatestModeDark );
	}

	@Override
	public void setLatestWasDark(boolean value) {
		_wasLatestModeDark = value;
	}

	public boolean hasToChangeColor()
	{
		return( _wasLatestModeDark != _isDarkMode );
	}

	@Override
	public boolean doNotInvertColors() {
		return _doNotInvertColors;
	}

	@Override
	public void setDoNotInvertColors(boolean _doNotInvertColors) {
		this._doNotInvertColors = _doNotInvertColors;
	}
}
