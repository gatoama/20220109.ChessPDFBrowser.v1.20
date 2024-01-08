/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.getters;

import java.util.function.BiFunction;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TwoParameterLazyHolder<I1, I2, CC> extends LazyHolderBase<CC>
{
	protected BiFunction<I1, I2, CC> _getterFunction;

	public TwoParameterLazyHolder( BiFunction<I1, I2, CC> getterFunction )
	{
		_getterFunction = getterFunction;
	}

	public CC get(I1 input1, I2 input2)
	{
		if( _value == null )
			_value = calculate(input1, input2);

		return( _value );
	}

	protected synchronized CC calculate(I1 input1, I2 input2)
	{
		if( _value == null )
			_value = _getterFunction.apply(input1, input2);

		return( _value );
	}
}
