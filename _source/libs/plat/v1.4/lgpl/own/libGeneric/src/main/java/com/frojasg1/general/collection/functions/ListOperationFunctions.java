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
package com.frojasg1.general.collection.functions;

import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.CollectionFunctions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author Usuario
 */
public class ListOperationFunctions 
{
	protected static class LazyHolder
	{
		public static final ListOperationFunctions INSTANCE = new ListOperationFunctions();
	}

	public static ListOperationFunctions instance()
	{
		return( LazyHolder.INSTANCE );
	}

	public <RR> List<RR> getListOfSelectedRecordsRemovingThem( List<RR> inputThatWillBeModified,
																int[] indicesToRemove )
	{
		ListOperationFunctions.instance();
		List<RR> result = new ArrayList<RR>();

		if( ( inputThatWillBeModified != null ) &&
			( indicesToRemove != null ) &&
			( indicesToRemove.length > 0 ) )
		{
			Arrays.sort(indicesToRemove);

			for( int ii=indicesToRemove.length - 1; ii >= 0; ii-- )
				result.add( inputThatWillBeModified.remove( indicesToRemove[ii] ) );
		}

		Collections.reverse(result);

		return( result );
	}

	public <RR> List<RR> copyAndRemoveIndices( List<RR> list, int[] indicesToRemove )
	{
		List<RR> result = copy( list );

		getListOfSelectedRecordsRemovingThem( result, indicesToRemove );

		return( result );
	}

	public int calculateIndexAfterRemoval( int indexWhereToMove, int[] indexesToRemove )
	{
		int result = indexWhereToMove;
		
		for( Integer value: indexesToRemove )
		{
			if( value < indexWhereToMove )
				result --;
			else
				break;
		}

		return( result );
	}

	public int calculateIndexAfterInsertion( int index, int indexWhereMoved,
											int lengthMoved )
	{
		int result = index;

		if( index >= indexWhereMoved )
			index += lengthMoved;

		return( result );
	}

	protected int getIndexForOrderedInsertion( int[] array, int value )
	{
		return( ArrayFunctions.instance().getIndexForOrderedInsertion( array, value ) );
	}

	public int calculateIndexAfterMoving( int index,
										int indexWhereMoved, int[] indicesToMove )
	{
		int result = index;
		if( ( indicesToMove != null ) && ( indicesToMove.length > 0 ) )
		{
			indexWhereMoved = calculateIndexAfterRemoval( indexWhereMoved, indicesToMove );

			int indexWhereIndexIs = ArrayFunctions.instance().getFirstIndexOf(indicesToMove, index);
			if( indexWhereIndexIs > -1 )
				result = indexWhereMoved + indexWhereIndexIs;
			else if( index < indexWhereMoved )
				result = index - getIndexForOrderedInsertion( indicesToMove, index );
			else if( index >= indexWhereMoved )
				result = index + indicesToMove.length - getIndexForOrderedInsertion( indicesToMove, index );
		}

		return( result );
	}

	public <RR> void move( List<RR> inputThatWillBeModified,
							int[] indexesToRemove, int indexWhereToMove )
	{
		List<RR> elemsToBeMoved = getListOfSelectedRecordsRemovingThem( inputThatWillBeModified,
																		indexesToRemove );

		indexWhereToMove = calculateIndexAfterRemoval( indexWhereToMove, indexesToRemove );

		inputThatWillBeModified.addAll( indexWhereToMove, elemsToBeMoved );
	}

	public <RR> ListRecordMoveContext<RR> createListRecordMoveContext(
		int selectedIndex, List<RR> list, int[] selection, int indexWhereToMove)
	{
		ListRecordMoveContext<RR> result = new ListRecordMoveContext<>();
		result.setSelectedIndex(selectedIndex);
		result.setList(list);
		result.setSelection(selection);
		result.setIndexWhereToMove(indexWhereToMove);

		return( result );
	}

	public <RR> ListStateContextBase<RR> createListStateContextBase(
		Integer selectedIndex, List<RR> list, int[] selection)
	{
		ListStateContextBase<RR> result = new ListStateContextBase<>();
		result.setSelectedIndex(selectedIndex);
		result.setList(list);
		result.setSelection(selection);

		return( result );
	}

	protected <RR> Integer getIndexOfSame( List<RR> list, RR obj )
	{
		return( CollectionFunctions.instance().getIndexOfSame(list, obj) );
	}

	protected <RR> Integer getIndexOfSame( List<RR> list, Integer index, List<RR> modifiedList )
	{
		Integer result = null;
		if( index != null )
		{
			RR obj = CollectionFunctions.instance().get(list, index);
			if( obj != null )
				result = getIndexOfSame( modifiedList, obj );
		}

		return( result );
	}

	protected <RR> List<RR> copyAndSort( List<RR> list, Comparator<RR> comparator )
	{
		List<RR> result = copy( list );

		Collections.sort( result, comparator );

		return( result );
	}

	public <RR> ListStateContextBase<RR> delete( ListStateContextBase<RR> input )
	{
		Integer selectedIndex = input.getSelectedIndex();
		List<RR> list = input.getList();
		int[] selection = input.getSelection();

		List<RR> modifiedList = copyAndRemoveIndices( input.getList(),
										input.getSelection() );
		Integer newSelectedIndex = getIndexOfSame( list, selectedIndex, modifiedList );
		int[] newSelection = new int[0];

		return( createListStateContextBase( newSelectedIndex, modifiedList, newSelection) );
	}

	protected <RR> List<RR> copy( List<RR> list )
	{
		List<RR> result = null;
		if( list != null )
			result = new ArrayList<>( list );

		return( result );
	}

	public <RR> ListRecordMoveContext<RR> move( ListRecordMoveContext<RR> input )
	{
		Integer selectedIndex = input.getSelectedIndex();
		List<RR> list = input.getList();
		int[] selection = input.getSelection();
		int indexWhereToMove = input.getIndexWhereToMove();

		List<RR> modifiedList = copy( list );
		move( modifiedList, selection, indexWhereToMove );
		Integer newSelectedIndex = getIndexOfSame( list, selectedIndex, modifiedList );
		int[] newSelection = ArrayFunctions.instance().translateArray(selection,
									value -> getIndexOfSame(list, value, modifiedList) );
		int indexWhereMoved = calculateIndexAfterRemoval( indexWhereToMove, selection );

		return( createListRecordMoveContext( newSelectedIndex, modifiedList, newSelection, indexWhereMoved) );
	}

	public <RR> ListStateContextBase<RR> sort( ListStateContextBase<RR> input,
												Comparator<RR> comparator )
	{
		Integer selectedIndex = input.getSelectedIndex();
		List<RR> list = input.getList();
		int[] selection = input.getSelection();

		List<RR> modifiedList = copyAndSort( input.getList(), comparator );
		Integer newSelectedIndex = getIndexOfSame( list, selectedIndex, modifiedList );
		int[] newSelection = ArrayFunctions.instance().translateArray(selection,
									value -> getIndexOfSame(list, value, modifiedList) );

		return( createListStateContextBase( newSelectedIndex, modifiedList, newSelection) );
	}

	protected <RR> ListStateContextBase<RR> genericOperation( ListStateContextBase<RR> input,
															Supplier<List<RR>> listModifierFunction )
	{
		Integer selectedIndex = input.getSelectedIndex();
		List<RR> list = input.getList();
		int[] selection = input.getSelection();

		List<RR> modifiedList = listModifierFunction.get();
		Integer newSelectedIndex = getIndexOfSame( list, selectedIndex, modifiedList );
		int[] newSelection = ArrayFunctions.instance().translateArray(selection,
									value -> getIndexOfSame(list, value, modifiedList) );

		return( createListStateContextBase( newSelectedIndex, modifiedList, newSelection) );
	}
}
