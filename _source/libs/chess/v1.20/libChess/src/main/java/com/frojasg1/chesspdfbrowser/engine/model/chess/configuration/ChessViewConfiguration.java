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
package com.frojasg1.chesspdfbrowser.engine.model.chess.configuration;

import com.frojasg1.chesspdfbrowser.view.chess.completion.CompletionConfiguration;

/**
 *
 * @author Usuario
 */
public interface ChessViewConfiguration extends CompletionConfiguration
{
//	public ChessMoveMatcher getChessMoveMatcherForView();
//	public ChessMoveNotation getChessMoveNotationForView();
	public boolean getHasToShowComments();
	public void setHasToShowComments( boolean value );

	public boolean getHasToShowNAGs();
	public void setHasToShowNAGs( boolean value );
	
	public ChessLanguageConfiguration getChessLanguageConfigurationToShow();
	public ChessLanguageConfiguration getChessLanguageConfigurationToParseTextFrom();

	public String getConfigurationOfChessLanguageToShow();
	public String getConfigurationOfChessLanguageToParseTextFrom();

	public void setConfigurationOfChessLanguageToShow( String value );
	public void setConfigurationOfChessLanguageToParseTextFrom( String value );

	public boolean getDetachedGameWindowsAlwaysOnTop();
	public void setDetachedGameWindowsAlwaysOnTop( boolean value );

	public boolean getShowPdfGameWhenNewGameSelected();
	public void setShowPdfGameWhenNewGameSelected( boolean value );

	public boolean getHasToShowSegments();

	public boolean isAutocompletionForRegexActivated();
	public void setIsAutocompletionForRegexActivated(boolean value);
}
