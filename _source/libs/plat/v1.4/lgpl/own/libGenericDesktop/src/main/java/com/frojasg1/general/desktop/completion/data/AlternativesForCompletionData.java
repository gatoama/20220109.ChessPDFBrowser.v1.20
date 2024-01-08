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
package com.frojasg1.general.desktop.completion.data;

import com.frojasg1.general.completion.PrototypeForCompletionBase;
import java.awt.Rectangle;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AlternativesForCompletionData<LL>
{
	protected String _preText;
	protected PrototypeForCompletionBase[] _prototypes;
	protected LL _locationControl;

	public PrototypeForCompletionBase[] getPrototypes()
	{
		return( _prototypes );
	}

	public void setPrototypes(PrototypeForCompletionBase[] array)
	{
		_prototypes = array;
	}

	public String getPreText()
	{
		return( _preText );
	}

	public void setPreText( String value )
	{
		_preText = value;
	}

	public LL getLocationControl()
	{
		return( _locationControl );
	}

	public void setLocationControl(LL value)
	{
		_locationControl = value;
	}
}
