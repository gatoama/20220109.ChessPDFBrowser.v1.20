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
package com.frojasg1.general.undoredo.imp;

import com.frojasg1.general.undoredo.UndoRedoElementInterface;
import com.frojasg1.general.undoredo.UndoRedoHistoryInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UndoRedoHistoryImp< URE extends UndoRedoElementInterface >
					implements UndoRedoHistoryInterface< URE >
{
	protected LinkedList< Collection<URE> > _listToUndo;
	protected LinkedList< Collection<URE> > _listToRedo;
//	protected UR _undoRedoObject;

	public UndoRedoHistoryImp( )
	{
		_listToUndo = new LinkedList< Collection<URE> >();
		_listToRedo = new LinkedList< Collection<URE> >();
	}

	protected Collection<URE> createCollection( URE urei )
	{
		Collection< URE > result = new ArrayList< URE >();
		result.add( urei );

		return( result );
	}

	@Override
	public void setNewUndoElement( URE urei )
	{
		if( hasToAddToUndoList( urei ) )
		{
			setNewUndoElement( createCollection(urei) );
		}
	}

	@Override
	public void setNewUndoElement( Collection<URE> ureiCol )
	{
//		if( hasToAddToUndoList( urei ) )
		{
//			outNewElements( "NewUndoElement", ureiCol );
			_listToUndo.addLast( ureiCol );
		}
	}

	@Override
	public void setNewRedoElement( URE urei )
	{
		setNewRedoElement( createCollection( urei ) );
	}

	@Override
	public void setNewRedoElement( Collection<URE> ureiCol )
	{
//		outNewElements( "NewRedoElement", ureiCol );
		_listToRedo.addLast( ureiCol );
	}
/*
	@Override
	public void replaceElementToUndo( URE urei )
	{
		if( hasElementsToUndo() )
		{
			_listToUndo.removeLast();
		}

		_listToUndo.addLast(urei);
	}
*/
	protected void outNewElements( String typeOfElement, Collection<URE> ureiCol )
	{
		Iterator<URE> it = ureiCol.iterator();
		while( it.hasNext() )
			System.out.println( typeOfElement + " --> " + it.next().toString() );
	}

	@Override
	public boolean hasElementsToUndo()
	{
		return( _listToUndo.size() > 0 );
	}

	@Override
	public boolean hasElementsToRedo()
	{
		return( _listToRedo.size() > 0 );
	}

	@Override
	public Collection<URE> removeElementToUndo()
	{
		Collection<URE> result = null;
		if( hasElementsToUndo() )
		{
			result = _listToUndo.removeLast();
		}
		return( result );
	}

	@Override
	public Collection<URE> removeElementToRedo()
	{
		Collection<URE> result = null;
		if( hasElementsToRedo() )
		{
			result = _listToRedo.removeLast();
		}
		return( result );
	}
/*
	@Override
	public void undo()
	{
		if( hasElementsToUndo() )
		{
			URE elem = _listToUndo.removeLast();
			undo( elem );
			setNewRedoElement( elem );
		}
	}

	@Override
	public void redo()
	{
		if( hasElementsToRedo() )
		{
			URE elem = _listToRedo.removeLast();
			redo( elem );
		}
	}

	@Override
	public void undo( URE elem )
	{
		_undoRedoObject.undo( elem );
	}

	@Override
	public void redo( URE elem )
	{
		_undoRedoObject.redo( elem );
	}
*/
	protected boolean hasToAddToUndoList( URE element )
	{
		return( true );
	}

	@Override
	public void clearRedoList()
	{
		_listToRedo.clear();
	}

	@Override
	public void clearUndoList()
	{
		_listToUndo.clear();
	}

}
