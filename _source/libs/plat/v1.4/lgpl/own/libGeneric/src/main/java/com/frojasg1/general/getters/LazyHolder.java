/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.getters;

import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LazyHolder<CC> extends LazyHolderBase<CC>
{
	protected Supplier<CC> _getterFunction;

	public LazyHolder( Supplier<CC> getterFunction )
	{
		_getterFunction = getterFunction;
	}

	public CC get()
	{
		if( _value == null )
			_value = calculate();

		return( _value );
	}

	protected synchronized CC calculate()
	{
		if( _value == null )
			_value = _getterFunction.get();

		return( _value );
	}
}
