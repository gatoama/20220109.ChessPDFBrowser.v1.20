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
package com.frojasg1.generic;

/**
 *
 * @author Usuario
 */
public class GenericFunctions
{
	protected static GenericInterface _instance = null;

	public static GenericInterface instance()
	{
		if( _instance == null )
			throw( new RuntimeException( "Before using this function, you must register the particular implementation of the genericInterface" ) );

		return( _instance );
	}

	public GenericFunctions( )
	{
	}

	public static void register( GenericInterface interfaceToRegister )
	{
		_instance = interfaceToRegister;
	}
}
