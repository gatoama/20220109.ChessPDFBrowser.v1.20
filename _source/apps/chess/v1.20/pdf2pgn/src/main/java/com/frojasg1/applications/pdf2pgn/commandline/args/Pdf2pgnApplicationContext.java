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
package com.frojasg1.applications.pdf2pgn.commandline.args;

import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class Pdf2pgnApplicationContext extends ApplicationInitContext
{
	protected String _inputFileName = null;
	protected String _outputFileName = null;
	protected Integer _initialPageNumber = null;
	protected Integer _finalPageNumber = null;
	protected String _languageToParseGames = null;
	protected String _lettersForPiecesToParseGames = null;

	public String getInputFileName() {
		return _inputFileName;
	}

	public void setInputFileName(String _inputFileName) {
		this._inputFileName = _inputFileName;
	}

	public String getOutputFileName() {
		return _outputFileName;
	}

	public void setOutputFileName(String _outputFileName) {
		this._outputFileName = _outputFileName;
	}

	public Integer getInitialPageNumber() {
		return _initialPageNumber;
	}

	public void setInitialPageNumber(Integer _initialPageNumber) {
		this._initialPageNumber = _initialPageNumber;
	}

	public Integer getFinalPageNumber() {
		return _finalPageNumber;
	}

	public void setFinalPageNumber(Integer _finalPageNumber) {
		this._finalPageNumber = _finalPageNumber;
	}

	public String getLanguageToParseGames() {
		return _languageToParseGames;
	}

	public void setLanguageToParseGames(String _languageToParseGames) {
		this._languageToParseGames = _languageToParseGames;
	}

	public String getLettersForPiecesToParseGames() {
		return _lettersForPiecesToParseGames;
	}

	public void setLettersForPiecesToParseGames(String _lettersForPiecesToParseGames) {
		this._lettersForPiecesToParseGames = _lettersForPiecesToParseGames;
	}
}
