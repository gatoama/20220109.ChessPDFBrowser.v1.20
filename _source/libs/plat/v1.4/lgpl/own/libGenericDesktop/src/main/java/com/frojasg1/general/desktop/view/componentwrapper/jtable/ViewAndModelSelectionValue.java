/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable;

import com.frojasg1.general.update.Updateable;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ViewAndModelSelectionValue implements Updateable {
	protected Function<Integer, Integer> _viewToModelTranslator;
	protected Function<Integer, Integer> _modelToViewTranslator;

	protected Integer _modelSelectedIndex;
	protected Integer _viewSelectedIndex;

	public ViewAndModelSelectionValue( Function<Integer, Integer> viewToModelTranslator,
										Function<Integer, Integer> modelToViewTranslator )
	{
		_viewToModelTranslator = viewToModelTranslator;
		_modelToViewTranslator = modelToViewTranslator;
	}

	public Integer getModelSelectedIndex()
	{
		return( _modelSelectedIndex );
	}

	public Integer getViewSelectedIndex()
	{
		return( _viewSelectedIndex );
	}

	public void setViewSelectedIndex( Integer value )
	{
		_viewSelectedIndex = value;
		int tmp = translate( value, _viewToModelTranslator );
		if( tmp != -1 )
			_modelSelectedIndex = tmp;
	}

	public void setModelSelectedIndex( Integer value )
	{
		_modelSelectedIndex = value;
		_viewSelectedIndex = translate( value, _modelToViewTranslator );
	}

	protected Integer translate( Integer viewIndex, Function<Integer, Integer> translator )
	{
		Integer result = null;
		if( viewIndex != null )
			result = translator.apply( viewIndex );
		return( result );
	}

	@Override
	public void update()
	{
		setModelSelectedIndex( getModelSelectedIndex() );
	}
}
