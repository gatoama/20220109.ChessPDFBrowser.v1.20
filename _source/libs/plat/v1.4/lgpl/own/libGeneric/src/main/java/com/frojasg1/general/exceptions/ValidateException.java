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
package com.frojasg1.general.exceptions;

import java.awt.Component;

/**
 *
 * @author Usuario
 */
public class ValidateException extends Exception
{
	protected Component _componentNotValidated = null;
	protected boolean _doNotShowWarning = false;

	public ValidateException( String message )
	{
		this( message, null );
	}

	public ValidateException( String message, Component componentNotValidated )
	{
		this( message, componentNotValidated, null );
	}

	public ValidateException( String message, Component componentNotValidated, Exception ex )
	{
		this( message, componentNotValidated, ex, false );
	}

	public ValidateException( String message, Component componentNotValidated, Exception ex,
								boolean doNotShowWarning )
	{
		super( message, ex );
		_componentNotValidated = componentNotValidated;
		_doNotShowWarning = doNotShowWarning;
	}

	public Component getComponentNotValidated()
	{
		return( _componentNotValidated );
	}

	public void setComponentNotValidated( Component componentNotValidated )
	{
		_componentNotValidated = componentNotValidated;
	}

	public boolean getDoNotShowWarning()
	{
		return( _doNotShowWarning );
	}
}
