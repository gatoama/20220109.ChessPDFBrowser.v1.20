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
package com.frojasg1.general;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fjavier.rojas
 */
public class CallStackFunctions
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CallStackFunctions.class);

	protected static CallStackFunctions _instance;

	public static void changeInstance( CallStackFunctions inst )
	{
		_instance = inst;
	}

	public static CallStackFunctions instance()
	{
		if( _instance == null )
			_instance = new CallStackFunctions();
		return( _instance );
	}

	public void dumpCallStack( String message )
	{
		getCallStackException(message).printStackTrace();
	}

	public Exception getCallStackException( String message )
	{
		Exception result = null;
		try
		{
			throw new RuntimeException( String.format( "dumpCallStack: %s", message ) );
		}
		catch( Exception ex )
		{
			result = ex;
		}
		return( result );
	}

	public void addCallStack( String message, List<Exception> callStacks )
	{
		if( callStacks != null )
			callStacks.add( getCallStackException( message ) );
	}

	public void logCallStacks( String message, List<Exception> callStacks )
	{
		if( callStacks != null )
		{
			LOGGER.info( "logging all call stacks: {}({})", message,
						( callStacks == null ? null : callStacks.size() ) );

			int index = 0;
			for( Exception ex: CollectionFunctions.instance().reverseList( callStacks ) )
				LOGGER.info( "{} ({})", ex.getMessage(), ++index, ex );
		}
	}
}
