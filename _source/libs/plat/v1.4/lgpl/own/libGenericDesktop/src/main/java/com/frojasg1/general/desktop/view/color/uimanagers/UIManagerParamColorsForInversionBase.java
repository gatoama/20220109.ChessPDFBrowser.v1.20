/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color.uimanagers;

import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UIManagerParamColorsForInversionBase extends ColorThemeChangeableBase
{
	protected Map<String, Color> _originalMap;
	protected Map<String, Color> _currentMap;

	protected List<String> _keys;

	protected void init()
	{
		_keys = new ArrayList<>();
	}

	protected void addKey( String key )
	{
		_keys.add(key);
	}

	protected synchronized void setDarkModeInternal( boolean value, ColorInversor colorInversor )
	{
		Map<String, Color> originalMap = getOrCreateOriginalMap();

		if( value )
			_currentMap = invertColors( originalMap, colorInversor );
		else
			_currentMap = originalMap;

		updateUImanager( _currentMap );
	}

	protected void setOriginalMap( Map<String, Color> map )
	{
		_originalMap = map;
	}

	protected void setCurrentMap( Map<String, Color> map )
	{
		_currentMap = map;
	}

	protected synchronized Map<String, Color> getOrCreateOriginalMap()
	{
		if( _originalMap == null )
			_originalMap = createOriginalMap();

		return( _originalMap );
	}

	protected <KK, CC> Map<KK, CC> createMap()
	{
		return( new HashMap<>() );
	}

	protected synchronized Map<String, Color> createOriginalMap()
	{
		Map<String, Color> result = createMap();
		for( String key: _keys )
			result.put( key, UIManager.getColor(key) );

		return( result );
	}

	@Override
	public void setDarkMode( boolean value, ColorInversor colorInversor )
	{
		if( value != isDarkMode() )
		{
			super.setDarkMode(value, colorInversor);

			setDarkModeInternal(value, colorInversor);

			setLatestWasDark(value);
		}
	}

	protected Map<String, Color> invertColors( Map<String, Color> originalMap, ColorInversor colorInversor )
	{
		Map<String, Color> result = null;
		
		if( originalMap != null )
		{
			result = createMap();
			for( Map.Entry<String, Color> entry: originalMap.entrySet() )
				result.put( entry.getKey(), colorInversor.invertColor( entry.getValue()) );
		}

		return( result );
	}

	protected synchronized void updateUImanager( Map<String, Color> map )
	{
		for( Map.Entry<String, Color> entry: map.entrySet() )
			UIManager.put( entry.getKey(), entry.getValue() );
	}
}
