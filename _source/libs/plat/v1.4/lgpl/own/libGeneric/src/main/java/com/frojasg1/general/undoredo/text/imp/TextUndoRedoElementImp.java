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
package com.frojasg1.general.undoredo.text.imp;

import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.undoredo.text.TextUndoRedoElementInterface;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TextUndoRedoElementImp implements TextUndoRedoElementInterface
{
	protected int _start = -1;
	protected String _previousStr;
	protected String _newStr;

	public TextUndoRedoElementImp( int start, String previous, String current )
	{
		_start = start;
		_previousStr = previous;
		_newStr = current;
	}

	@Override
	public int getStartOfElement()	{ return( _start ); }

	@Override
	public String getPreviousStringElement() { return( _previousStr ); }

	@Override
	public String getNewStringElement() { return( _newStr ); }

	protected boolean previousStrEquals( TextUndoRedoElementInterface other )
	{
		boolean result = StringFunctions.instance().stringsEquals( _previousStr, other.getPreviousStringElement() );
		return( result );
	}

	protected boolean newStrEquals( TextUndoRedoElementInterface other )
	{
		boolean result = StringFunctions.instance().stringsEquals( _newStr, other.getNewStringElement() );
		return( result );
	}

	@Override
	public boolean equals(Object other)
	{
		boolean result = false;

		if( other instanceof TextUndoRedoElementInterface )
		{
			TextUndoRedoElementInterface other2 = (TextUndoRedoElementInterface) other;
			result = ( _start == other2.getStartOfElement() ) &&
						previousStrEquals( other2 ) &&
						newStrEquals( other2 );
		}

		return( result );
	}

	@Override
	public void setStartOfElement(int start)	{ _start = start; }

	@Override
	public void setPreviousStringElement(String previousStr) { _previousStr = previousStr; }

	@Override
	public void setNewStringElement(String newStr) { _newStr = newStr; }

	@Override
	public boolean isEmpty()
	{
		boolean result = ( ( _previousStr == null ) || ( _previousStr.length() == 0 ) ) &&
							( ( _newStr == null ) || ( _newStr.length() == 0 ) );
		return( result );
	}

	@Override
	public boolean isPureInsertion()
	{
		return( ( ( _previousStr == null ) || ( _previousStr.length() == 0 ) ) &&
				( _newStr != null ) && ( _newStr.length() > 0 ) ) &&
//				( _start > 0 );
				( _start >= 0 );
	}

	@Override
	public boolean isPureRemoval()
	{
		return( ( ( _newStr == null ) || ( _newStr.length() == 0 ) ) &&
				( _previousStr != null ) && ( _previousStr.length() > 0 ) ) &&
//				( _start > 0 );
				( _start >= 0 );
	}

	@Override
	public boolean isReplacement()
	{
		return( ( _newStr != null ) && ( _newStr.length() > 0 ) &&
				( _previousStr != null ) && ( _previousStr.length() > 0 ) &&
//				( _start > 0 );
				( _start >= 0 ) );
	}

	@Override
	public String toString()
	{
		return( String.format( "start: %d, previousString: '%s', newString: '%s'",
								_start,
								_previousStr,
								_newStr )
			);
	}
}
