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
package com.frojasg1.chesspdfbrowser.enginewrapper.uci.go;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessEngineGoAttributes implements EngineActionArgs
{
	protected int _correlationId = 0;

	protected Integer _depth = null;
	protected Integer _mate = null;
	protected Integer _moveTime = null;

	protected Integer _wTime = null;
	protected Integer _wInc = null;

	protected Integer _bTime = null;
	protected Integer _bInc = null;

	public ChessEngineGoAttributes deepCopy()
	{
		ChessEngineGoAttributes result= new ChessEngineGoAttributes();
		result._correlationId = _correlationId;

		result._depth = _depth;
		result._mate = _mate;
		result._moveTime = _moveTime;

		result._wTime = _wTime;
		result._wInc = _wInc;

		result._bTime = _bTime;
		result._bInc = _bInc;

		return( result );
	}

	public void increaseCorrelationId()
	{
		_correlationId++;
	}

	public Integer getCorrelationId() {
		return _correlationId;
	}

	public void setCorrelationId(Integer _correlationId) {
		this._correlationId = _correlationId;
	}

	public Integer getDepth() {
		return _depth;
	}

	public void setDepth(Integer _depth) {
		this._depth = _depth;
	}

	public Integer getMate() {
		return _mate;
	}

	public void setMate(Integer _mate) {
		this._mate = _mate;
	}

	public Integer getMoveTime() {
		return _moveTime;
	}

	public void setMoveTime(Integer _moveTime) {
		this._moveTime = _moveTime;
	}

	public Integer getwTime() {
		return _wTime;
	}

	public void setwTime(Integer _wTime) {
		this._wTime = _wTime;
	}

	public Integer getwInc() {
		return _wInc;
	}

	public void setwInc(Integer _wInc) {
		this._wInc = _wInc;
	}

	public Integer getbTime() {
		return _bTime;
	}

	public void setbTime(Integer _bTime) {
		this._bTime = _bTime;
	}

	public Integer getbInc() {
		return _bInc;
	}

	public void setbInc(Integer _bInc) {
		this._bInc = _bInc;
	}

	public String getCommandString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append( "go " );

		append( sb, "wtime", _wTime );
		append( sb, "btime", _bTime );
		append( sb, "winc", _wInc );
		append( sb, "binc", _bInc );
		append( sb, "depth", _depth );
		append( sb, "mate", _mate );
		if( _moveTime != null )
			append( sb, "movetime", _moveTime );

		return( sb.toString() );
	}

	protected <CC> void append( StringBuilder sb, String attribName, CC attrib )
	{
		if( attrib != null )
			sb.append( attribName ).append( " " ).append( attrib ).append( " " );
	}
}
