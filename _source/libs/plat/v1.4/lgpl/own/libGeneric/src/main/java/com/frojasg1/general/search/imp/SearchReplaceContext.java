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
package com.frojasg1.general.search.imp;

import com.frojasg1.general.search.RegExException;
import com.frojasg1.general.search.SearchReplaceContextInterface;
import com.frojasg1.general.search.SearchReplaceTextInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoListener;
import com.frojasg1.general.view.ViewTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SearchReplaceContext implements SearchReplaceContextInterface, TextUndoRedoListener
{
	protected ViewTextComponent _textComp = null;

	protected SearchReplaceTextInterface _searchReplaceManager = null;
//	protected TextUndoRedoInterface _undoRedoManager = null;

	protected boolean _newSearch = true;

	public SearchReplaceContext( SearchReplaceTextInterface srti )
	{
		_searchReplaceManager = srti;

		TextUndoRedoInterface undoRedoManager = _searchReplaceManager.getUndoRedoManager();
		if( undoRedoManager != null )
		{
			_textComp = undoRedoManager.getView();

			undoRedoManager.registerListener(this);
		}
		else
			_textComp = null;
//		setUndoRedoManager_final( srti.getUndoRedoManager() );
	}

	@Override
	public void initialize()
	{
		
	}

	@Override
	public TextUndoRedoInterface getUndoRedoManager()
	{
//		return( _undoRedoManager );
		return( _searchReplaceManager.getUndoRedoManager() );
	}
/*
	protected final void setUndoRedoManager_final( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		if( _undoRedoManager != null )
		{
			_undoRedoManager.unregisterListener(this);
		}

		_undoRedoManager = undoRedoManagerOfTextComp;
		if( _undoRedoManager != null )
		{
			_textComp = _undoRedoManager.getView();

			_undoRedoManager.registerListener(this);
		}
		else
			_textComp = null;

		_searchReplaceManager.setUndoRedoManager(undoRedoManagerOfTextComp);
	}

	@Override
	public void setUndoRedoManager( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		if( _undoRedoManager != null )
		{
			_undoRedoManager.unregisterListener(this);
		}

		_undoRedoManager = undoRedoManagerOfTextComp;
		if( _undoRedoManager != null )
		{
			setTextComponent( _undoRedoManager.getView() );

			_undoRedoManager.registerListener(this);
		}
		else
			_textComp = null;

		_searchReplaceManager.setUndoRedoManager(undoRedoManagerOfTextComp);
	}
*/
	public void setTextComponent( ViewTextComponent vtc )
	{
		_textComp = vtc;
	}

	@Override
	public void undoListHasChanged()
	{
		_newSearch = true;
	}

	@Override
	public void redoListHasChanged()
	{
		_newSearch = true;
	}


	@Override
	public void originalElementHasChanged()
	{
		_newSearch = true;
	}

	@Override
	public void caretHasChanged()
	{
		_newSearch = true;
	}

	@Override
	public SearchReplaceTextInterface.SearchResultInterface search( SearchReplaceTextInterface.SearchSettingsInterface ssi,
																	boolean forward ) throws RegExException
	{
		SearchReplaceTextInterface.SearchResultInterface result = null;

		try
		{
			if( ssi.getTextToLookFor().length() > 0 )
			{
				if( _newSearch )
				{
					result = _searchReplaceManager.findString( //_undoRedoManager,
																ssi, _textComp.getCaretPosition(), forward );
				}
				else
				{
					result = _searchReplaceManager.findNext( ssi, forward );
				}
			}
		}
		catch( RegExException ree )
		{
			ree.printStackTrace();
			throw( ree );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		try
		{
			if( ( result == null ) || !result.resultsDifferentFromNoResults() )
			{
				_textComp.setCaretPosition( _searchReplaceManager.getInitialPosition() );
				_newSearch = true;
			}
			else
			{
				_textComp.setCaretPosition( result.getStart() );
				_textComp.setSelectionBounds( result.getStart(), result.getMatchedString().length() );
				_newSearch = false;
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( result );
	}

	@Override
	public SearchReplaceTextInterface.ReplaceResultInterface replace( SearchReplaceTextInterface.ReplaceSettingsInterface settings )
				throws RegExException
	{
		SearchReplaceTextInterface.ReplaceResultInterface result = null;

		try
		{
			_searchReplaceManager.replace(settings);
			result = _searchReplaceManager.getLastReplacement();
		}
		catch( RegExException ree )
		{
			ree.printStackTrace();
			throw( ree );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		try
		{
			if( result != null )
			{
				_textComp.setCaretPosition( result.getStart() );
				_textComp.setSelectionBounds( result.getStart(), result.getStringToReplaceTo().length() );

				_searchReplaceManager.getUndoRedoManager().overwriteUndoElement(result);

				_newSearch = false;
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( result );
	}

	@Override
	public SearchReplaceTextInterface.ReplaceAndFindNextResultInterface replaceAndFindNext( SearchReplaceTextInterface.ReplaceSettingsInterface settings,
																							boolean forward ) throws RegExException
	{
		SearchReplaceTextInterface.ReplaceAndFindNextResultInterface result = null;
		
		SearchReplaceTextInterface.SearchResultInterface searchResult = null;
		SearchReplaceTextInterface.ReplaceResultInterface replaceResult = null;

		RegExException ree = null;
		try
		{
			searchResult = _searchReplaceManager.replaceAndFindNext( settings, forward );
		}
		catch( RegExException ree1 )
		{
			ree = ree1;
			ree.printStackTrace();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		replaceResult = _searchReplaceManager.getLastReplacement();

		try
		{
			if( replaceResult != null )
			{
				_searchReplaceManager.getUndoRedoManager().overwriteUndoElement(replaceResult);
				
				if( ( searchResult != null ) && ( searchResult.resultsDifferentFromNoResults() ) )
				{
					_textComp.setCaretPosition( searchResult.getStart() );
					_textComp.setSelectionBounds( searchResult.getStart(), searchResult.getMatchedString().length() );

					_newSearch = false;
				}
				else
				{
					_textComp.setCaretPosition( _searchReplaceManager.getInitialPosition() );

					_newSearch = true;
				}
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		if( (searchResult!=null) || (replaceResult!=null) )
		{
			result = new ReplaceAndFindNextResultImp( replaceResult, searchResult );
			
			if( ree != null )
			{
				ree.setReplaceAndFindResult(result);
				throw( ree );
			}
		}

		return( result );
	}

	@Override
	public SearchReplaceTextInterface.ReplaceResultInterface[] replaceAll( SearchReplaceTextInterface.ReplaceSettingsInterface settings )
				throws RegExException
	{
		SearchReplaceTextInterface.ReplaceResultInterface[] result = null;

		try
		{
			result = _searchReplaceManager.replaceAll( //_undoRedoManager,
														settings );
		}
		catch( RegExException ree )
		{
			ree.printStackTrace();
			throw( ree );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		try
		{
			if( ( result != null ) && ( result.length > 0 ) )
			{
//				_textComp.setCaretPosition( result.getStart() );
//				_textComp.setSelectionBounds( result.getStart(), result.getStringToReplaceTo().length() );
				_searchReplaceManager.getUndoRedoManager().overwriteUndoElement(result);

				_newSearch = false;
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( result );
	}

	public static class ReplaceAndFindNextResultImp implements SearchReplaceTextInterface.ReplaceAndFindNextResultInterface
	{
		protected SearchReplaceTextInterface.ReplaceResultInterface _replaceResult = null;
		protected SearchReplaceTextInterface.SearchResultInterface _searchResult = null;

		public ReplaceAndFindNextResultImp( SearchReplaceTextInterface.ReplaceResultInterface rr,
											SearchReplaceTextInterface.SearchResultInterface sri )
		{
			_replaceResult = rr;
			_searchResult = sri;
		}

		@Override
		public SearchReplaceTextInterface.ReplaceResultInterface getReplaceResult()
		{
			return( _replaceResult );
		}

		@Override
		public SearchReplaceTextInterface.SearchResultInterface getSearchResult()
		{
			return( _searchResult );
		}
	}

}
