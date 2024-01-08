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
package com.frojasg1.applications.common.configuration.application;

import com.frojasg1.general.number.DoubleReference;
import java.awt.Rectangle;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ConfigurationForFileChooserImp implements ConfigurationForFileChooserInterface
{
	protected double _zoomFactor = 1.0D;
	protected Rectangle _lastFileChooserBounds = null;
	protected String _lastDirectory = null;
	protected boolean _isFileDetailsActivated = false;

	public void init( BaseApplicationConfigurationInterface conf )
	{
		if( conf != null )
		{
			setLastDirectory( conf.getLastDirectory() );
			setLastFileChooserBounds( conf.getLastFileChooserBounds() );
			setZoomFactor( conf.getZoomFactor() );
		}
	}

	@Override
	public String getLastDirectory()
	{
		return( _lastDirectory );
	}

	@Override
	public void setLastDirectory(String lastDirectory)
	{
		_lastDirectory = lastDirectory;
	}

	@Override
	public Rectangle getLastFileChooserBounds()
	{
		return( _lastFileChooserBounds );
	}

	@Override
	public void setLastFileChooserBounds(Rectangle bounds)
	{
		_lastFileChooserBounds = bounds;
	}

	@Override
	public void setZoomFactorReference(DoubleReference zoomFactor)
	{
		_zoomFactor = zoomFactor._value;
	}

	@Override
	public void setZoomFactor(double zoomFactor)
	{
		_zoomFactor = zoomFactor;
	}

	@Override
	public double getZoomFactor()
	{
		return( _zoomFactor );
	}

	@Override
	public DoubleReference getZoomFactorReference()
	{
		return( null );
	}

	public void updateGlobalConfiguration( BaseApplicationConfigurationInterface conf )
	{
		if( conf != null )
		{
			if( _lastDirectory != null )
				conf.setLastDirectory(_lastDirectory);

			if( _lastFileChooserBounds != null )
				conf.setLastFileChooserBounds( _lastFileChooserBounds );

			conf.serverChangeZoomFactor(_zoomFactor);
		}
	}

	@Override
	public boolean isFileDetailsActivated() {
		return( _isFileDetailsActivated );
	}

	@Override
	public void setFileDetailsSelected(boolean value) {
		_isFileDetailsActivated = value;
	}
}
