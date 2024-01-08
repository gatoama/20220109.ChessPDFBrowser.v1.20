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
package com.frojasg1.chesspdfbrowser.enginewrapper;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.EngineWrapperActionBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.general.functional.interfaces.TriConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineActionExecutorData<AA extends EngineActionArgs, RR extends EngineActionResult>
{
	protected int _requestType = -1;
	protected TriConsumer<EngineActionExecutorData<AA ,RR>, EngineActionRequest,
							Consumer<FullEngineActionResult<AA,RR>> > _actionExecutionFunction = null;

	protected Supplier<EngineWrapperActionBase<AA, RR> > _actionBuilder = null;

	protected Class<AA> _argsClass = null;
	protected Class<RR> _actionResultClass = null;

	public EngineActionExecutorData( int requestType,
									TriConsumer<EngineActionExecutorData<AA, RR>, EngineActionRequest, Consumer<FullEngineActionResult<AA,RR>> > actionExecutionFunction,
									Supplier<EngineWrapperActionBase<AA, RR>> actionBuilder,
									Class<AA> argsClass,
									Class<RR> actionResultClass )
	{
		_requestType = requestType;
		_actionExecutionFunction = actionExecutionFunction;
		_actionBuilder = actionBuilder;
		_argsClass = argsClass;
		_actionResultClass = actionResultClass;
	}

	public int getRequestType() {
		return _requestType;
	}

	public Class<AA> getArgsClass()
	{
		return( _argsClass );
	}

	public Class<RR> getActionResultClass()
	{
		return( _actionResultClass );
	}

	public TriConsumer<EngineActionExecutorData<AA, RR>, EngineActionRequest,
						Consumer<FullEngineActionResult<AA,RR> > > getActionExecutionFunction() {
		return _actionExecutionFunction;
	}

	public Supplier<EngineWrapperActionBase<AA, RR>> getActionBuilder() {
		return _actionBuilder;
	}
}
