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
package com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree;

import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessViewConfigurationDummy implements ChessViewConfiguration
{
	@Override
	public boolean getHasToShowComments() {
		return( false );
	}

	@Override
	public void setHasToShowComments(boolean value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean getHasToShowNAGs() {
		return( false );
	}

	@Override
	public void setHasToShowNAGs(boolean value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ChessLanguageConfiguration getChessLanguageConfigurationToShow() {
		return( ChessLanguageConfiguration.getConfiguration( getConfigurationOfChessLanguageToShow() ) );
	}

	@Override
	public ChessLanguageConfiguration getChessLanguageConfigurationToParseTextFrom() {
		return( ChessLanguageConfiguration.getConfiguration( getConfigurationOfChessLanguageToParseTextFrom() ) );
	}

	@Override
	public String getConfigurationOfChessLanguageToShow() {
		return( ChessLanguageConfiguration.ENGLISH );
	}

	@Override
	public String getConfigurationOfChessLanguageToParseTextFrom() {
		return( ChessLanguageConfiguration.ENGLISH );
	}

	@Override
	public void setConfigurationOfChessLanguageToShow(String value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setConfigurationOfChessLanguageToParseTextFrom(String value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean getDetachedGameWindowsAlwaysOnTop() {
		return( false );
	}

	@Override
	public void setDetachedGameWindowsAlwaysOnTop(boolean value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean getShowPdfGameWhenNewGameSelected() {
		return( false );
	}

	@Override
	public void setShowPdfGameWhenNewGameSelected(boolean value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean getHasToShowSegments() {
		return( false );
	}

	@Override
	public boolean isAutocompletionForRegexActivated() {
		return( false );
	}

	@Override
	public void setIsAutocompletionForRegexActivated(boolean value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
