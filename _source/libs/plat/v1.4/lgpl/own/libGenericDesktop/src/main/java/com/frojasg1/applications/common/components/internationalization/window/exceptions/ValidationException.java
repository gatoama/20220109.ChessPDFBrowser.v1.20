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
package com.frojasg1.applications.common.components.internationalization.window.exceptions;

import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ValidationException extends Exception
{
	protected Component _componentWithException = null;
	protected ViewComponent _vc = null;
	protected boolean _doNotShowWarning = false;

	public ValidationException( String message )
	{
		this( message, null );
	}

	public ValidationException( String message, Component comp )
	{
		this( message, comp, null );
	}

	public ValidationException( String message, Component comp, Exception ex )
	{
		this( message, comp, ex, false );
	}

	public ValidationException( String message, Component comp, Exception ex,
								boolean doNotShowWarning )
	{
		super( message, ex );
		_componentWithException = comp;
		_doNotShowWarning = doNotShowWarning;
	}

	public Component getComponentWithException()
	{
		return( _componentWithException );
	}

	public ViewComponent getViewComponentWithException()
	{
		if( _vc == null )
			_vc = GenericFunctions.instance().getViewFacilities().createViewComponent(_componentWithException);

		return( _vc );
	}

	public boolean getDoNotShowWarning()
	{
		return( _doNotShowWarning );
	}
}
