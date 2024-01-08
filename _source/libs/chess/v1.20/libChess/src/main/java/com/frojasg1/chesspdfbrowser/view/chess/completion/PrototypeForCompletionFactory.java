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
package com.frojasg1.chesspdfbrowser.view.chess.completion;

import com.frojasg1.general.completion.PrototypeForCompletionBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PrototypeForCompletionFactory
{
	protected static PrototypeForCompletionFactory _instance = null;

	public static final String BLOCK_REGEX = "block_regex";

/*
	public static final String FUNCTION = "function";
	public static final String USER_FUNCTION = "user_function";
	public static final String VARIABLE = "variable";
	public static final String CONSTANT = "constant";
	public static final String TOKEN = "token";
	public static final String HELP = "help";
	public static final String OPERATOR = "operator";
*/
	public static void changeInstance( PrototypeForCompletionFactory instance )
	{
		_instance = instance;
	}

	public static PrototypeForCompletionFactory instance()
	{
		if( _instance == null )
			_instance = new PrototypeForCompletionFactory();

		return( _instance );
	}

	public PrototypeForCompletionBase createObject( String name, String type )
	{
		PrototypeForCompletionBase result = null;
		
		switch( type )
		{
/*			case FUNCTION:
			case USER_FUNCTION:
			case VARIABLE:
			case CONSTANT:
			case TOKEN:
			case HELP:
			case OPERATOR:
*/
			case BLOCK_REGEX:

				result = new PrototypeForCompletionBase( name, type );
			break;
		}

		return( result );
	}
}
