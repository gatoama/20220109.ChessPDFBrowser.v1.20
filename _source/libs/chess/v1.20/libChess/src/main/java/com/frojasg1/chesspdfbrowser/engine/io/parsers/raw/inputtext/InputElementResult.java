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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext;

import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputTextLine;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.SegmentKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class InputElementResult
{
	protected static final int MINIMUM_NUMBER_OF_TIMES_A_TITLE_MUST_BE_REPEATED = 4;
	protected static final int MINIMUM_PERCENTAGE_FOR_A_CANDIDATE_TO_BE_OMITTED_TO_BE_SELECTED = 33;

		// this map counts the times a string appear as the first line of the page.
		// we want to skip that line if it is repeated many times (then it is a title, and it is not part of the games)
	protected Map<String, AtomicInteger> _candidatesOfLinesToSkip = null;
	protected LinkedList<InputElement> _inputElementList = null;

	protected boolean _hasToAddPageNumber = false;

	protected List<Integer> _startOfPagePositions = null;

	protected Integer[] _arrayOfPositionsOfStartOfPage = null;

	protected int _initialPageToScanForGames = 0;
	protected int _finalPageToScanForGames = 0;

	protected Set<String> _linesToSkip = null;

	protected Iterator<InputElement> _inputElementIterator = null;
	protected Iterator<InputElement> _internalInputElementIterator = null;
	protected int _internalIndex = -1;

	public void init( boolean hasToAddPageNumber, int initialPageToScanForGames,
						int finalPageToScanForGames )
	{
		_candidatesOfLinesToSkip = new HashMap<>();
		_inputElementList = new LinkedList<>();
		_hasToAddPageNumber = hasToAddPageNumber;
		
		if( hasToAddPageNumber )
		{
			_startOfPagePositions = new ArrayList<>();
			_startOfPagePositions.add(0);
		}

		_initialPageToScanForGames = initialPageToScanForGames;
		_finalPageToScanForGames = finalPageToScanForGames;
	}

	public Iterator<InputElement> getInputElementListIterator()
	{
		_internalIndex = -1;
		_internalInputElementIterator = _inputElementList.iterator();
		Iterator<InputElement> result = new Iterator<InputElement>() {
			@Override
			public boolean hasNext()
			{
				return( _internalInputElementIterator.hasNext() );
			}

			@Override
			public InputElement next()
			{
				_internalIndex++;
				return( _internalInputElementIterator.next() );
			}
		};

		return( result );
	}

	public int getCurrentPageIndex()
	{
		return( this.getPageIndexOfPosition(_internalIndex) );
	}

	public boolean hasToAddPageNumber()
	{
		return( !_inputElementList.isEmpty() && _hasToAddPageNumber );
	}

	public List<InputElement> getInputElementList()
	{
		return( _inputElementList );
	}

	protected void addCandidateOfLineToSkip( String line )
	{
		AtomicInteger ai = getOrCreateCandidateOfLineToSkip( line );
		ai.incrementAndGet();
	}

	protected Map<String, AtomicInteger> getMapOfCandidatesOfLinesToSkip()
	{
		return( _candidatesOfLinesToSkip );
	}

	protected AtomicInteger getOrCreateCandidateOfLineToSkip( String line )
	{
		AtomicInteger result = _candidatesOfLinesToSkip.get( line );
		if( result == null )
		{
			result = new AtomicInteger(0);
			_candidatesOfLinesToSkip.put( line, result );
		}

		return( result );
	}

	protected SegmentKey getLastElementSegmentKey()
	{
		SegmentKey result = null;
		if( ( _inputElementList != null ) && !_inputElementList.isEmpty() )
		{
			InputElement elem = _inputElementList.getLast();
			result = elem.getSegmentKey();
		}
		return( result );
	}

	protected void addAllCandidatesOfLinesToSkip( Map<String, AtomicInteger> other )
	{
		for( Map.Entry<String, AtomicInteger> otherEntry: other.entrySet() )
			getOrCreateCandidateOfLineToSkip( otherEntry.getKey() ).addAndGet( otherEntry.getValue().get() );
	}

	protected void addBlankLine()
	{
		_inputElementList.add( new InputTextLine( "\n", getLastElementSegmentKey() ) );
	}

	public void addPartialResult( InputElementResult other )
	{
		if( hasToAddPageNumber() )
		{
			addBlankLine();
			_startOfPagePositions.add( _inputElementList.size() );
		}

		if( other != null )
		{
			addAllCandidatesOfLinesToSkip( other.getMapOfCandidatesOfLinesToSkip() );
			_inputElementList.addAll( other.getInputElementList() );
		}
	}

	public void endOfCapture()
	{
		addPartialResult(null);

		_arrayOfPositionsOfStartOfPage = _startOfPagePositions.toArray( new Integer[ _startOfPagePositions.size() ] );
		_linesToSkip = getSetOfLinesToSkip(_candidatesOfLinesToSkip);
	}

	public int getPositionForPageIndex( int pageIndex )
	{
		return( _startOfPagePositions.get( pageIndex ) );
	}

	protected boolean matchesPageFromPosition( int arrayIndex, int position )
	{
		boolean result = ( _arrayOfPositionsOfStartOfPage[ arrayIndex ] <= position );
		if( arrayIndex < (_arrayOfPositionsOfStartOfPage.length-1) )
			result = result && ( _arrayOfPositionsOfStartOfPage[arrayIndex+1] > position );
		
		return( result );
	}

	public int getPageIndexOfPosition( int position )
	{
		int ini = 0;
		int end = _arrayOfPositionsOfStartOfPage.length - 1;
		int result = -1;
		while( result == -1 )
		{
			int mid = ( ini + end ) / 2;
			
			if( matchesPageFromPosition( ini, position ) )
			{
				result = ini;
			}
			else if( matchesPageFromPosition( end, position ) )
			{
				result = end;
			}
			else if( matchesPageFromPosition( mid, position ) )
			{
				result = mid;
			}
			else if( ( end - ini ) < 2 )
			{
				result = mid;
				System.out.println( "Error, page not found. Position: " + position + ". We break the loop to avoid infinite looping" );
			}
			else if( _arrayOfPositionsOfStartOfPage[mid] >= position )
			{
				end = mid;
			}
			else
			{
				ini = mid;
			}
		}
		return( result + getFromPageIndex() );
	}


	protected int getIntValue( Integer value, int defaultValue )
	{
		return( ( value == null ) ? defaultValue : value );
	}

	protected int getFromPageIndex()
	{
		return( getIntValue( _initialPageToScanForGames, 1 ) - 1 );
	}

	protected int getToPageIndex()
	{
		return( _finalPageToScanForGames - 1 );
	}

	protected int getTotalAmountOfPagesToParse()
	{
		return( getToPageIndex() + 1 - getFromPageIndex() );
	}

	protected Set<String> getSetOfLinesToSkip( Map<String, AtomicInteger> hashCandidateToSkip )
	{
		Set<String> result = new HashSet<>();

		for( Map.Entry<String, AtomicInteger> entry: hashCandidateToSkip.entrySet() )
		{
			AtomicInteger ai = entry.getValue();
			if( ( ai.get() >= MINIMUM_NUMBER_OF_TIMES_A_TITLE_MUST_BE_REPEATED ) &&
				 ( ( ( ai.get() * 100 ) / hashCandidateToSkip.size() ) >= MINIMUM_PERCENTAGE_FOR_A_CANDIDATE_TO_BE_OMITTED_TO_BE_SELECTED )
					 )
			{
				result.add( entry.getKey() );
			}
		}

		return( result );
	}

	public boolean isDiscardable( String line )
	{
		return( _linesToSkip.contains( line ) );
	}
}
