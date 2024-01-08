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
package com.frojasg1.general.streams;

import java.io.PrintStream;

/**
 *
 * @author fjavier.rojas
 */
public class InOutErrStreamFunctions
{
	protected static InOutErrStreamFunctions _instance;

	public static void changeInstance( InOutErrStreamFunctions inst )
	{
		_instance = inst;
	}

	public static InOutErrStreamFunctions instance()
	{
		if( _instance == null )
			_instance = new InOutErrStreamFunctions();
		return( _instance );
	}

	public PrintStream getOriginalStream(PrintStream stream)
	{
		PrintStream result = stream;
		if( result instanceof DeactivablePrintStream )
			result = ( (DeactivablePrintStream) result ).getOriginal();
		return( result );
	}

	public PrintStream getOriginalOutStream()
	{
		return( getOriginalStream( System.out ) );
	}

	public PrintStream getOriginalErrStream()
	{
		return( getOriginalStream( System.err ) );
	}
}
