/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.threads;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SynchronizedFunctions {

	protected static class LazyHolder
	{
		public static final SynchronizedFunctions INSTANCE = new SynchronizedFunctions();
	}

	public static SynchronizedFunctions instance()
	{
		return( LazyHolder.INSTANCE );
	}

	public <CC> void threadSafeMethod( CC source, Consumer<CC> method )
	{
		if( source != null )
		{
			synchronized( source )
			{
				method.accept(source);
			}
		}
	}

	/*
		<CC> - Class of source
		<RR> - Result
	*/
	public <CC, RR> RR threadSafeFunction( CC source, Function<CC, RR> function )
	{
		RR result = null;
		if( source != null )
		{
			synchronized( source )
			{
				result = function.apply(source);
			}
		}

		return( result );
	}
}
