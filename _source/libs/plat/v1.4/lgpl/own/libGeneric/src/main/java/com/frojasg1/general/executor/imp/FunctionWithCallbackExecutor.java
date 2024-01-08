/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.executor.imp;

import com.frojasg1.general.ExecutionFunctions;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FunctionWithCallbackExecutor<RR> extends ExecutorBase
{
	protected ExecutionFunctions.UnsafeFunction<RR> _function;
	protected Consumer<RR> _callback;

	public FunctionWithCallbackExecutor( ExecutionFunctions.UnsafeFunction<RR> function, Consumer<RR> callback )
	{
		_function = function;
		_callback = callback;
	}

	@Override
	public void run()
	{
		RR result = ExecutionFunctions.instance().safeFunctionExecution( _function::run );

		if( ( _callback != null ) && !getHasToStop() )
			_callback.accept(result);
	}
}
