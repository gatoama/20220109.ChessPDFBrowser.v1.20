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
package com.frojasg1.general.search;

import com.frojasg1.general.undoredo.UndoRedoReplacementInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface SearchReplaceTextInterface
{
	public int getInitialPosition();
	public void initialize();

//	public void setUndoRedoManager( TextUndoRedoInterface undoRedoManagerOfTextComp );
	public TextUndoRedoInterface getUndoRedoManager( );

		// SearchResultInterface can be a ReplaceResultInterface in case the SearchSettingsInterface was a ReplaceSettingsInterface.
	public SearchResultInterface findString( //TextUndoRedoInterface undoRedoManagerOfTextComp,
													SearchSettingsInterface searchSettings,
													int initialPosition, boolean forward ) throws RegExException;
	public SearchResultInterface findNext( SearchSettingsInterface searchSettings, boolean forward ) throws RegExException;

	public SearchResultInterface findStringToReplace( //TextUndoRedoInterface undoRedoManagerOfTextComp,
													ReplaceSettingsInterface replaceSettings,
													int initialPosition, boolean forward ) throws RegExException;
	public ReplaceResultInterface[] replaceAll( //TextUndoRedoInterface undoRedoManagerOfTextComp,
													ReplaceSettingsInterface replaceSettings ) throws RegExException;
	public ReplaceResultInterface replace( ReplaceSettingsInterface replaceSettings ) throws RegExException;	// just replace the previous search.
	public SearchResultInterface replaceAndFindNext( ReplaceSettingsInterface replaceSettings, boolean forward ) throws RegExException;	// just replace the previous search.

	public ReplaceResultInterface getLastReplacement();

	public interface SearchSettingsInterface
	{
		public void setUseRegEx( boolean value );
		public void setMatchWholeWords( boolean value );
		public void setMatchCase( boolean value );
		public void setTextToLookFor( String textToMatch );
		public void setRegEx( String regex );

		public boolean getUseRegEx();
		public boolean getMatchWholeWords();
		public boolean getMatchCase();
		public String getTextToLookFor();
		public String getRegEx();

		public SearchSettingsInterface createCopy();
	}

	public interface ReplaceSettingsInterface extends SearchSettingsInterface
	{
		public void setStringToReplaceTo( String value );
		public String getStringToReplaceTo();

		public void setRegExToReplaceTo( String regEx );
		public String getRegExToReplaceTo();

		public ReplaceSettingsInterface createCopy();
	}

	public interface SearchResultInterface
	{
		public boolean resultsDifferentFromNoResults();
		public int getStart();
		public String getMatchedString();
		public String getCompleteText();
	}

	public interface ReplaceResultInterface extends SearchResultInterface, UndoRedoReplacementInterface
	{
		public String getStringToReplaceTo();
	}

	public interface ReplaceAndFindNextResultInterface
	{
		public ReplaceResultInterface getReplaceResult();
		public SearchResultInterface getSearchResult();
	}
}
