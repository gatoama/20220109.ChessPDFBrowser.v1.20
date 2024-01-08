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
package com.frojasg1.general.completion;


/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class PrototypeManagerBase
{
	protected PrototypeManagerInitBase _init = null;
	protected MapOfPrototypesBase _mapOfPrototypes = null;


	public void init()
	{
		_mapOfPrototypes = createMapOfPrototypes();
		_init = createPrototypeManagerInit();
	}

/*
	public String getStringOfAllOperators()
	{
		return( _stringOfAllOperators );
	}
*/
	protected PrototypeManagerInitBase getPrototypeManagerInit()
	{
		return( _init );
	}

	protected abstract PrototypeManagerInitBase createPrototypeManagerInit();

	protected abstract MapOfPrototypesBase createMapOfPrototypes();

	public void clear()
	{
		_mapOfPrototypes.clear();
//		_stringOfAllOperators = "";
	}

	public PrototypeForCompletionBase get( String name )
	{
		return( _mapOfPrototypes.get( name ) );
	}

	public void put( PrototypeForCompletionBase value )
	{
		if( value != null )
		{
			_mapOfPrototypes.put( value );
//			if( value.getType().equals( PrototypeForCompletionFactory.OPERATOR ) )
//				_stringOfAllOperators = _stringOfAllOperators + value.getName();
		}
	}

	public PrototypeForCompletionBase[] getPrototypeRange( String preText )
	{
		return( _mapOfPrototypes.getPrototypeRange(preText) );
	}

	public void dispose()
	{
		_init.dispose();
	}
}
