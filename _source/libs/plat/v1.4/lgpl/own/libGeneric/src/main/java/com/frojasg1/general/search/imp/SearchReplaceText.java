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

import com.frojasg1.general.GeneralFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.search.RegExException;
import com.frojasg1.general.search.SearchReplaceTextInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoListener;
import com.frojasg1.general.view.ViewTextComponent;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * 
 * This Class is not dessigned to support multiple threads replacing strings on the same Text component.
 * It is designed to be used for only one thread each object and Text component of these class.
 */
public class SearchReplaceText implements SearchReplaceTextInterface, TextUndoRedoListener
{
	protected boolean _hasBeenModified = false;
	protected TextUndoRedoInterface _undoRedoManager = null;
	protected ViewTextComponent _textComp = null;
	protected SearchSettingsInterface _searchSettings = null;	// can be of a child class, as ReplaceSettingsInterface
	protected SearchSettingsInterface _searchSettingsOriginal = null;	// can be of a child class, as ReplaceSettingsInterface
	protected int _initialPosition = -1;
	protected boolean _forward = true;
	protected SearchResultInterface _lastResult = null;
	protected boolean _canReplaceLastSearch = false;

	protected int _replaceDoneCount = 0;

//	protected Lock _lock = new ReentrantLock();
//	protected Condition _conditionToObtainResult_replaceAll = _lock.newCondition();
//	protected Condition _conditionToObtainResult_replace = _lock.newCondition();

	protected ReplaceResultInterface[] _tmpResult_replaceAll = null;
	protected ReplaceResultInterface _tmpResult_replace = null;
	protected RegExException _tmp_ree;

	protected ReplaceResultInterface _lastReplacement = null;

	protected boolean _isReplaceAll = false;

	public SearchReplaceText( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		_undoRedoManager = undoRedoManagerOfTextComp;
	}

	@Override
	public void initialize()
	{
		if( _undoRedoManager != null )
		{
			_textComp = _undoRedoManager.getView();
			_undoRedoManager.registerListener(this);
		}
		else
			_textComp = null;
	}

	@Override
	public TextUndoRedoInterface getUndoRedoManager( )
	{
		return( _undoRedoManager );
	}
/*
	@Override
	public void setUndoRedoManager( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		if( _undoRedoManager != null )
			_undoRedoManager.unregisterListener(this);

		_undoRedoManager = undoRedoManagerOfTextComp;
		if( _undoRedoManager != null )
		{
			_textComp = _undoRedoManager.getView();
			_undoRedoManager.registerListener(this);
		}
		else
			_textComp = null;
	}
*/
	@Override
	public int getInitialPosition()
	{
		return( _initialPosition );
	}
	
	@Override
	public ReplaceResultInterface getLastReplacement()
	{
		return( _lastReplacement );
	}

	@Override
	public SearchResultInterface findString(//TextUndoRedoInterface undoRedoManagerOfTextComp,
													SearchSettingsInterface searchSettings,
													int initialPosition, boolean forward) throws RegExException
	{
		initializeAttributes( //undoRedoManagerOfTextComp,
								searchSettings, initialPosition, forward );

		SearchResultInterface result = internal_searchForString( _searchSettings, _initialPosition, _forward );

		_canReplaceLastSearch = true;
		_lastResult = result;
		_hasBeenModified = false;
		return( result );
	}

	@Override
	public SearchResultInterface findNext(SearchSettingsInterface searchSettings, boolean forward) throws RegExException
	{
		_searchSettings = searchSettings;
		SearchResultInterface result = null;

		if( canDoNextAction() && ( forward == _forward ) )
		{
			result = internal_searchForString( _searchSettings,
												getNextPosition( _lastResult, _forward ),
												_forward );
		}
		else if( !_isReplaceAll )
		{
			if( ( _textComp.getCaretPosition() >= 0 ) &&
				( _textComp.getCaretPosition() < _textComp.getText().length() ) )
			{
				_initialPosition = _textComp.getCaretPosition();
			}

			result = findString( //_undoRedoManager,
										_searchSettings,
										_initialPosition, forward );
		}

		_canReplaceLastSearch = ( result != null ) && result.resultsDifferentFromNoResults();
		_lastResult = result;
		return( result );
	}

	protected int getNextPosition( SearchResultInterface lastResult, boolean forward )
	{
		int result = getNextPosition( lastResult.getStart(), lastResult.getMatchedString().length(),
									lastResult.getCompleteText().length(), forward );

		return( result );
	}

	protected int getNextPosition( int start, int matchedStrLength, int totalLength,
									boolean forward )
	{
		int result = start;
		
		if( forward )
		{
			result = result + matchedStrLength;
			if( result >= totalLength )
				result = 0;
		}
		else
		{
			result = result - matchedStrLength;
			if( result < 0 )
				result = totalLength - 1;
		}

		return( result );
	}

	protected int getSign( int position, boolean forward )
	{
		int result = (int) Math.signum( position - _initialPosition );
		if( result == 0 )
			result = ( forward ? 1 : -1 );

		return( result );
	}

	protected Matcher getMatcher( String regEx, boolean matchCase, String text ) throws RegExException
	{
		Pattern pattern = null;

		try
		{
			if( matchCase )
				pattern = Pattern.compile( regEx );
			else
				pattern = Pattern.compile( regEx, Pattern.CASE_INSENSITIVE );
		}
		catch( Throwable th )
		{
			throw( new RegExException( th.getMessage() ) );
		}

		Matcher matcher = pattern.matcher( text );

		return( matcher );
	}
	
	protected SearchResultInterface searchForRegEx( String regEx, boolean matchCase, int position ) throws RegExException
	{
		SearchResultInterface result = null;

		if( ( regEx == null ) || ( regEx.length() == 0 ) )
			return( result );

		String text = _textComp.getText();
		Matcher matcher = getMatcher( regEx, matchCase, text );

		// we limit the search if we have just started to find again from the start of the string.
		if( position < _initialPosition )
			matcher = matcher.region( 0, _initialPosition );

		boolean found = matcher.find( position );
		if( found )
		{
			result = new SearchResult( matcher.start(), matcher.group(), text );
		}

		if( ( result == null ) && ( position >= _initialPosition ) && ( _initialPosition > 0 ) )
		{
			result = searchForRegEx( regEx, matchCase, 0 );

			if( result == null )
			{
				result = new SearchResultNoResults();
			}
		}

		return( result );
	}

	protected SearchResultInterface directSearch( String strToLookFor,
													boolean matchCase,
													int position,
													boolean forward )
	{
		SearchResultInterface result = null;

		if( ( strToLookFor == null ) || ( strToLookFor.length() == 0 ) )
			return( result );

		String text = _textComp.getText();

		int increment = 0;
		int limit = -1;
		int pos = position;
		int posTmp = pos;
		boolean found = false;
		String textToCompare = null;
		if( forward )
		{
			increment = 1;

			if( position < _initialPosition )
				limit = _initialPosition;
			else
				limit = text.length() + 1 - strToLookFor.length();
			
			while( !found && ( posTmp < limit ) )
			{
				pos = posTmp;

				int length = IntegerFunctions.min( strToLookFor.length(), text.length() - pos );
				textToCompare = text.substring( pos, pos + length );

				if( matchCase )
					found = textToCompare.equals( strToLookFor );
				else
					found = textToCompare.equalsIgnoreCase( strToLookFor );

				posTmp += increment;
			}
		}
		else
		{
			increment = -1;
			if( position > _initialPosition )
				limit = _initialPosition;
			else
				limit = 0;

			while( !found && ( posTmp >= limit ) )
			{
				pos = posTmp;

				int length = IntegerFunctions.min( strToLookFor.length(), text.length() - pos );
				textToCompare = text.substring( pos, pos + length );

				if( matchCase )
					found = textToCompare.equals( strToLookFor );
				else
					found = textToCompare.equalsIgnoreCase( strToLookFor );

				posTmp += increment;
			}
		}

		if( !found )
		{
			boolean hasToRepeat = false;
			if( forward && ( position >= _initialPosition ) && ( _initialPosition > 0 ) )
			{
				position = 0;
				hasToRepeat = true;
			}
			else if( !forward && ( position <= _initialPosition ) && ( _initialPosition < text.length() ) )
			{
				position = text.length();
				hasToRepeat = true;
			}

			if( hasToRepeat )
				result = directSearch( strToLookFor, matchCase, position, forward );
		}
		else
			result = new SearchResult( pos, textToCompare, text );

		return( result);
	}

	protected SearchResultInterface internal_searchForString( SearchSettingsInterface searchSettings,
																int position,
																boolean forward ) throws RegExException
	{
		SearchResultInterface result = null;

		int signOfPosition = getSign( position, forward );

		int increment = ( forward ? 1 : -1 );
		String text = _textComp.getText();
		boolean end = false;
		while( ! end )
		{
			if( searchSettings.getUseRegEx() )
			{
				// always forward.
				result = searchForRegEx( searchSettings.getRegEx(), searchSettings.getMatchCase(), position );
			}
			else
			{
				result = directSearch( searchSettings.getTextToLookFor(),
										searchSettings.getMatchCase(),
										position, forward );
			}

			boolean matchWholeWordsAndNotWholeWord = searchSettings.getMatchWholeWords() &&
														!isWholeWord( result );

			int signOfResult = ( ( result == null ) ? 0 : getSign( result.getStart(), forward ) );
			if( ( result == null ) ||
					( signOfPosition != signOfResult ) && ( forward && ( signOfPosition == -1 )  || !forward && ( signOfPosition == 1 ) ) ||
					( matchWholeWordsAndNotWholeWord && ( forward && ( result.getStart() == text.length() - 1 ) ||
														( !forward && ( result.getStart() == 0 ) ) )
					)
				)
			{
				GeneralFunctions.instance().beep();
				result = new SearchResultNoResults();
			}

			end = ( result == null ) ||
					!result.resultsDifferentFromNoResults() ||
					!matchWholeWordsAndNotWholeWord;

			if( !end && ( result != null ) && ( result.resultsDifferentFromNoResults() ) )
			{
				position = result.getStart() + increment;
			}
		}

		return( result );
	}

	protected boolean isWholeWord( SearchResultInterface searchResult )
	{
		boolean result = (searchResult != null ) && searchResult.resultsDifferentFromNoResults();

		if( result && ( searchResult.getMatchedString() != null ) &&
				( searchResult.getMatchedString().length() > 0 ) )
		{
			result = isWordBoundary( searchResult.getStart() - 1, searchResult.getCompleteText() ) &&
						isWordBoundary( searchResult.getStart() + searchResult.getMatchedString().length() - 1,
										searchResult.getCompleteText() );
		}

		return( result );
	}

	protected boolean isWordBoundary( int pos, String completeText )
	{
		boolean result = ( ( pos < 0 ) || ( pos > ( completeText.length() - 2 ) ) );
		if( ! result )
		{
//			result = completeText.substring( pos, pos + 2 ).matches( ".\\b." );
			result = !completeText.substring( pos, pos + 2 ).matches( ".\\B." );
		}

		return( result );
	}

	@Override
	public SearchResultInterface findStringToReplace(//TextUndoRedoInterface undoRedoManagerOfTextComp,
													ReplaceSettingsInterface replaceSettings,
													int initialPosition, boolean forward) throws RegExException
	{
		SearchResultInterface result = findString( //undoRedoManagerOfTextComp,
													replaceSettings,
													initialPosition, forward );
		return( result );
	}

	protected void initializeAttributes( //TextUndoRedoInterface undoRedoManagerOfTextComp,
											SearchSettingsInterface searchOrReplaceSettings,
											int initialPosition, boolean forward )
	{
//		setUndoRedoManager( undoRedoManagerOfTextComp );
		_searchSettings = searchOrReplaceSettings;
		_searchSettingsOriginal = _searchSettings.createCopy();
		_initialPosition = initialPosition;
		_forward = forward;
		_lastResult = null;
		_hasBeenModified = false;
		_canReplaceLastSearch = false;
	}
/*
	protected ReplaceResultInterface[] replaceAll_forNonEDT(TextUndoRedoInterface undoRedoManagerOfTextComp,
															ReplaceSettingsInterface replaceSettings )
	{
		_lock.lock();
		try
		{
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						_tmpResult_replaceAll = replaceAll( undoRedoManagerOfTextComp, replaceSettings );
					}
					catch( Throwable th )
					{
						th.printStackTrace();
						_tmpResult_replaceAll = null;
					}
					finally
					{
						_conditionToObtainResult_replaceAll.signal();
					}
				}
			});

			_conditionToObtainResult_replaceAll.await();
			return( _tmpResult_replaceAll );
		}
		catch( InterruptedException ie )
		{
			ie.printStackTrace();
			return( null );
		}
		finally
		{
			_lock.unlock();
		}
	}
*/
	
	@Override
	public ReplaceResultInterface[] replaceAll(//TextUndoRedoInterface undoRedoManagerOfTextComp,
												ReplaceSettingsInterface replaceSettings ) throws RegExException
	{
/*		if( ! SwingUtilities.isEventDispatchThread() )
		{
			return( replaceAll_forNonEDT( undoRedoManagerOfTextComp, replaceSettings ) );
		}
*/
		boolean forward = true;
		ArrayList<ReplaceResultInterface> list = new ArrayList<ReplaceResultInterface>();

		try
		{
			_isReplaceAll = true;
			SearchResultInterface sri = findStringToReplace( //undoRedoManagerOfTextComp,
															replaceSettings, 0, forward );
			while( ( sri != null ) && sri.resultsDifferentFromNoResults() /* && !_hasBeenModified */ )
			{
				sri = replaceAndFindNext( replaceSettings, forward );
				list.add(_lastReplacement);
			}
		}
		finally
		{
			_isReplaceAll = false;
		}

		ReplaceResultInterface[] array = list.toArray( new ReplaceResultInterface[0] );
//		list.toArray( array );

		return( array );
	}
/*
	protected ReplaceResultInterface replace_forNonEDT()
	{
		_lock.lock();
		try
		{
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						_tmpResult_replace = replace();
					}
					catch( Throwable th )
					{
						th.printStackTrace();
						_tmpResult_replace = null;
					}
					finally
					{
						_conditionToObtainResult_replace.signal();
					}
				}
			});

			_conditionToObtainResult_replace.await();
			return( _tmpResult_replace );
		}
		catch( InterruptedException ie )
		{
			ie.printStackTrace();
			return( null );
		}
		finally
		{
			_lock.unlock();
		}
	}
*/
	@Override
	public ReplaceResultInterface replace( ReplaceSettingsInterface settings ) throws RegExException
	{
/*		if( ! SwingUtilities.isEventDispatchThread() )
		{
			return( replace_forNonEDT() );
		}
*/
		_searchSettings = settings;

		ReplaceResultInterface result = null;
		if( canDoNextAction() &&
//			( _searchSettings instanceof ReplaceSettingsInterface ) &&
			_canReplaceLastSearch )
		{
//			ReplaceSettingsInterface settings = ( ReplaceSettingsInterface ) _searchSettings;
			String strToReplace = _lastResult.getMatchedString();

			String resultingString = null;
			if( settings.getUseRegEx() )
			{
				String regExToReplaceTo = settings.getRegExToReplaceTo();
				Matcher matcher = getMatcher( settings.getRegEx(), settings.getMatchCase(), strToReplace );

				resultingString = matcher.replaceFirst( regExToReplaceTo );
			}
			else
			{
				resultingString = settings.getStringToReplaceTo();
			}

			_canReplaceLastSearch = false;
			_replaceDoneCount = getReplaceDoneCount( result );

			result = new ReplaceResult( _lastResult.getStart(), strToReplace, _lastResult.getCompleteText(), resultingString );
			doReplacement( result );
		}

		_lastReplacement = result;
		return( result );
	}

	protected void doReplacement( ReplaceResultInterface rri )
	{
		if( rri.getStart() < _initialPosition )
		{
			int difference = rri.getStringToReplaceTo().length() - rri.getMatchedString().length();
			_initialPosition += difference;
		}

		_textComp.replaceText( rri.getStart(), rri.getMatchedString(), rri.getStringToReplaceTo() );
	}

	protected int getReplaceDoneCount( ReplaceResultInterface rri )
	{
		int result = 0;

		if( rri != null )
		{
			if( rri.getStringToReplaceTo().length() > 0 )
				result++;

			if( rri.getMatchedString().length() > 0 )
				result++;
		}
		return( result );
	}

	protected boolean canDoNextAction()
	{
		return( (!_hasBeenModified || _isReplaceAll ) && ( _lastResult != null ) &&
			_lastResult.resultsDifferentFromNoResults() &&
			_searchSettings.equals( _searchSettingsOriginal ) );
	}

	@Override
	public SearchResultInterface replaceAndFindNext( ReplaceSettingsInterface replaceSettings, boolean forward ) throws RegExException
	{
		_searchSettings = replaceSettings;
		replace( replaceSettings );

		if( _lastReplacement != null )
		{
			SearchResultInterface sri = findNext( replaceSettings, forward );
			_lastResult = sri;
		}
		else
			_lastResult = null;

		return( _lastResult );
	}

	@Override
	public void undoListHasChanged()
	{
	}

	@Override
	public void redoListHasChanged()
	{
	}

	@Override
	public void originalElementHasChanged()
	{
		if( _replaceDoneCount == 0 )
		{
			_hasBeenModified = true;
		}
		else
			_replaceDoneCount--;
	}

	@Override
	public void caretHasChanged()
	{
//		_hasBeenModified = true;
	}
}
