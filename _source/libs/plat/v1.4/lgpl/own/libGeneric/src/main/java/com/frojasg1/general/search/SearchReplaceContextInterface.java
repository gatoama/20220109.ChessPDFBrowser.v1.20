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

import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface SearchReplaceContextInterface
{
	public TextUndoRedoInterface getUndoRedoManager();
//	public void setUndoRedoManager( TextUndoRedoInterface turi );

	public void initialize();

	public SearchReplaceTextInterface.SearchResultInterface search( SearchReplaceTextInterface.SearchSettingsInterface ssi, boolean forward ) throws RegExException;
	public SearchReplaceTextInterface.ReplaceResultInterface replace( SearchReplaceTextInterface.ReplaceSettingsInterface settings ) throws RegExException;
	public SearchReplaceTextInterface.ReplaceAndFindNextResultInterface replaceAndFindNext( SearchReplaceTextInterface.ReplaceSettingsInterface settings, boolean forward ) throws RegExException;
	public SearchReplaceTextInterface.ReplaceResultInterface[] replaceAll( SearchReplaceTextInterface.ReplaceSettingsInterface settings ) throws RegExException;
}
