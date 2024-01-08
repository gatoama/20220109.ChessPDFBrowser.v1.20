/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.getters;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class OneParameterLazyHolder<I1, CC> extends LazyHolderBase<CC>
{
	protected Function<I1, CC> _getterFunction;

	public OneParameterLazyHolder( Function<I1, CC> getterFunction )
	{
		_getterFunction = getterFunction;
	}

	public CC get(I1 input)
	{
		if( _value == null )
			_value = calculate(input);

		return( _value );
	}

	protected synchronized CC calculate(I1 input)
	{
		if( _value == null )
			_value = _getterFunction.apply(input);

		return( _value );
	}
}
