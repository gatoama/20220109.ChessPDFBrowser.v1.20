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
package com.frojasg1.applications.pdf2pgn.commandline.result;

import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class Pdf2pgnResult
{
	protected boolean _wasSuccessful = false;
	protected Exception _exception = null;
	protected String _errorMessage = null;
	protected List<ChessGame> _listOfGames = null;

	public boolean wasSuccessful() {
		return _wasSuccessful;
	}

	public void setWasSuccessful(boolean _wasSuccessful) {
		this._wasSuccessful = _wasSuccessful;
	}

	public Exception getException() {
		return _exception;
	}

	public void setException(Exception _exception) {
		this._exception = _exception;
	}

	public String getErrorMessage() {
		return _errorMessage;
	}

	public void setErrorMessage(String _errorMessage) {
		this._errorMessage = _errorMessage;
	}

	public List<ChessGame> getListOfGames() {
		return _listOfGames;
	}

	public void setListOfGames(List<ChessGame> _listOfGames) {
		this._listOfGames = _listOfGames;
	}
}
