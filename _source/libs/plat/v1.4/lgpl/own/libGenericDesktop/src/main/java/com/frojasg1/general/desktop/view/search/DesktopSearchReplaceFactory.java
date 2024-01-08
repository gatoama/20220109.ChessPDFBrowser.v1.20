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
package com.frojasg1.general.desktop.view.search;

import com.frojasg1.general.desktop.search.DesktopSearchReplaceText;
import com.frojasg1.general.search.SearchReplaceContextInterface;
import com.frojasg1.general.search.SearchReplaceFactory;
import com.frojasg1.general.search.SearchReplaceTextInterface;
import com.frojasg1.general.search.imp.SearchReplaceContext;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopSearchReplaceFactory implements SearchReplaceFactory
{
	@Override
	public SearchReplaceTextInterface createSearchReplaceTextObject( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		SearchReplaceTextInterface result = new DesktopSearchReplaceText( undoRedoManagerOfTextComp );
		result.initialize();
//		result.setUndoRedoManager( undoRedoManagerOfTextComp );
		return( result );
	}

	public SearchReplaceContextInterface createSearchReplaceContext( SearchReplaceTextInterface srti )
	{
		return( new SearchReplaceContext( srti ) );
	}

	public SearchReplaceContextInterface createSearchReplaceContext( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		return( createSearchReplaceContext( createSearchReplaceTextObject( undoRedoManagerOfTextComp ) ) );
	}
}
