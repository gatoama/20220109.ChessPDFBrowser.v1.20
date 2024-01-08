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
public abstract class LazyHolderBase<CC>
{
	protected CC _value;


	public synchronized void reset()
	{
		set( null );
	}

	public synchronized void set( CC value )
	{
		_value = value;
	}
}
