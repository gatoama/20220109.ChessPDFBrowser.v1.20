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
package com.frojasg1.chesspdfbrowser.test;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.impl.EngineActionRequestImpl;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.impl.FullEngineActionResultImpl;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ParametrizedTypeMain {

	public static void main( String[] args )
	{
		ParametrizedTypeMain pt = new ParametrizedTypeMain();
		pt.start();
	}

	protected void start()
	{
		FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> result = new FullEngineActionResultImpl<>( null,
												new EngineActionRequestImpl<>( 1, new ChessEngineGoAttributes(), ChessEngineGoAttributes.class ),
												null,
												new ChessEngineResult() );

		callConsumer( (res) -> consumer( res ), result );
	}

	protected void consumer( FullEngineActionResult result )
	{
		System.out.println( "Success" );
	}

	protected <AA extends EngineActionArgs, RR extends EngineActionResult>
			void callConsumer( Consumer<FullEngineActionResult<AA,RR>> consumer,
								FullEngineActionResult<AA, RR> result )
	{
		consumer.accept( result );
	}
}
