/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable;

import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.update.Updateable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ViewAndModelSelectionArray implements Updateable {
	protected Function<Integer, Integer> _viewToModelTranslator;
	protected Function<Integer, Integer> _modelToViewTranslator;

	protected int[] _modelSelection;
	protected int[] _viewSelection;

	protected int _validityOfExpectedInitialValue;
	protected int[] _expectedViewSelection;
	protected int _validityOfExpected;

	public ViewAndModelSelectionArray( Function<Integer, Integer> viewToModelTranslator,
										Function<Integer, Integer> modelToViewTranslator,
										int validityOfExpectedInitialValue )
	{
		_viewToModelTranslator = viewToModelTranslator;
		_modelToViewTranslator = modelToViewTranslator;
		_validityOfExpectedInitialValue = validityOfExpectedInitialValue;
	}

	public boolean isEmpty()
	{
		return( ( _modelSelection == null ) || ( _modelSelection.length == 0 ) );
	}

	public int[] getModelSelection()
	{
		return( _modelSelection );
	}

	public int[] getViewSelection()
	{
		return( _viewSelection );
	}

	protected int[] updateArray( int[] previous, int[] tmp )
	{
		int[] result = tmp;
		if( result == null )
			result = previous;
		else if( previous != null )
		{
			if( previous.length == tmp.length )
				for( int ii=0; ii<tmp.length; ii++ )
					if( tmp[ii] == -1 )
						tmp[ii] = previous[ii];
		}

		return( result );
	}

	public void setViewSelection( int[] array )
	{
		if( !Arrays.equals(array, _expectedViewSelection) )
		{
			_viewSelection = array;
			int[] tmp = viewArrayToModel(array);

			_modelSelection = updateArray(_modelSelection, tmp);
		}

		resetExpectedViewSelection();
	}

	protected void resetExpectedViewSelection()
	{
		if( _validityOfExpected > 0 )
			_validityOfExpected--;
		else
			_expectedViewSelection = null;
	}

	public void setModelSelection( int[] array )
	{
		_modelSelection = array;
		_viewSelection = modelArrayToView(array);
	}

	public int[] viewArrayToModel( int[] viewArray )
	{
		return( translateArray(viewArray, _viewToModelTranslator) );
	}

	public int[] modelArrayToView( int[] viewArray )
	{
		return( translateArray(viewArray, _modelToViewTranslator) );
	}

	protected int[] translateArray( int[] array,
									Function<Integer, Integer> elementTranslatorFunction )
	{
		return( ArrayFunctions.instance().translateArray(array, elementTranslatorFunction) );
	}

	public int[] calculateExpectedViewSelection()
	{
		int[] result = null;
		int[] modelSelection = getModelSelection();
		if( modelSelection != null )
		{
			List<Integer> list = new ArrayList<>();
			for( int index: modelSelection )
			{
				int viewIndex = modelToView(index);
				if( viewIndex != -1 )
					list.add(viewIndex);
			}
			result = new int[ list.size() ];
			int index=0;
			for(int viewIndex: list)
				result[index++] = viewIndex;
		}

		return( result );
	}

	protected int modelToView(int modelIndex)
	{
		return( _modelToViewTranslator.apply(modelIndex) );
	}

	protected void setExpectedViewSelection( int[] array )
	{
		_expectedViewSelection = array;
		startValidityOfExpected();
	}

	protected void startValidityOfExpected()
	{
		_validityOfExpected = _validityOfExpectedInitialValue;
	}

	@Override
	public void update()
	{
		setModelSelection( getModelSelection() );
		setExpectedViewSelection( calculateExpectedViewSelection() );
	}
}
