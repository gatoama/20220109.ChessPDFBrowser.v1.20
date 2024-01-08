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
package com.frojasg1.chesspdfbrowser.enginewrapper.configuration;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.xml.model.KeyModel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineInstanceConfiguration implements EngineActionArgs, KeyModel<String>
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	public static final int UCI = 0;
	public static final int XBOARD = 1;

	protected String _name = null;

	protected int _engineType = -1;

	protected String _engineCommandForLaunching = null;
	protected ChessEngineConfiguration _chessEngineConfiguration = null;

	// for Copier
	public EngineInstanceConfiguration()
	{
		
	}

	public void init( EngineInstanceConfiguration that )
	{
		_name = that._name;
		_engineType = that._engineType;
		_engineCommandForLaunching = that._engineCommandForLaunching;

		_chessEngineConfiguration = _copier.copy( that._chessEngineConfiguration );
	}

	public String getName()
	{
		return( _name );
	}

	public void setName( String name )
	{
		_name = name;
	}

	@Override
	public String getKey()
	{
		return( getName() );
	}

	@Override
	public void setKey( String key )
	{
		setName( key );
	}

	public String getEngineCommandForLaunching() {
		return _engineCommandForLaunching;
	}

	public void setEngineCommandForLaunching(String engineCommandForLaunching) {
		this._engineCommandForLaunching = engineCommandForLaunching;
	}

	public ChessEngineConfiguration getChessEngineConfiguration() {
		return _chessEngineConfiguration;
	}

	public void setChessEngineConfiguration(ChessEngineConfiguration _chessEngineConfiguration) {
		this._chessEngineConfiguration = _chessEngineConfiguration;
	}

	public int getEngineType()
	{
		return( _engineType );
	}

	public void setEngineType( int engineType )
	{
		_engineType = engineType;
	}
}
