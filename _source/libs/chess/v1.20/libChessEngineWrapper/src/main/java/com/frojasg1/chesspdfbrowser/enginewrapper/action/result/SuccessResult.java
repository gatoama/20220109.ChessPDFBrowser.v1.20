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
package com.frojasg1.chesspdfbrowser.enginewrapper.action.result;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SuccessResult
{
	public static final SuccessResult ERROR_SUCCESS_RESULT = new SuccessResult( false, true );
	public static final SuccessResult SUCCESS_LAST_RESULT = new SuccessResult( true, true );
	public static final SuccessResult SUCCESS_NON_LAST_RESULT = new SuccessResult( true, false );

	public static final int SUCCESSFUL = 0;
	public static final int ERROR = 1;

	protected int _result = 0;
	protected Exception _exception = null;
	protected String _errorString = null;

	protected boolean _isLast = false;

	public SuccessResult()
	{}

	public SuccessResult( boolean success, boolean isLast )
	{
		init( success, isLast );
	}

	public void init( boolean success, boolean isLast )
	{
		if( success )
			_result = SUCCESSFUL;
		else
			_result = ERROR;

		_isLast = isLast;
	}

	public void setIsLast( boolean isLast )
	{
		_isLast = isLast;
	}

	public boolean isLast()
	{
		return( _isLast );
	}

	public void setErrorString( String errorString )
	{
		_errorString = errorString;
	}

	public String getErrorString()
	{
		return( _errorString );
	}

	public void setException( Exception ex )
	{
		_exception = ex;
	}

	public Exception getException()
	{
		return( _exception );
	}

	public int getResultCode()
	{
		return( _result );
	}

	public boolean isAsuccess()
	{
		return( getResultCode() == SUCCESSFUL );
	}
}
