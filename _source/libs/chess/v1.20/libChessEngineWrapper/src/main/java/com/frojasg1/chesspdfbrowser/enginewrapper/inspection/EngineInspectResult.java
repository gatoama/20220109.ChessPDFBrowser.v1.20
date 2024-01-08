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
package com.frojasg1.chesspdfbrowser.enginewrapper.inspection;

import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineInspectResult
{
	protected boolean _isUci = false;
	protected boolean _isXboard = false;

	protected ChessEngineConfiguration _chessEngineConfiguration = null;

	protected String _name = null;

	public boolean isUci() {
		return _isUci;
	}

	public void setIsUci(boolean _isUci) {
		this._isUci = _isUci;
	}

	public boolean isXboard() {
		return _isXboard;
	}

	public void setIsXboard(boolean _isXboard) {
		this._isXboard = _isXboard;
	}

	public ChessEngineConfiguration getChessEngineConfiguration() {
		return _chessEngineConfiguration;
	}

	public void setChessEngineConfiguration(ChessEngineConfiguration _chessEngineConfiguration) {
		this._chessEngineConfiguration = _chessEngineConfiguration;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}
}
