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
package com.frojasg1.general.completion;
/*
import com.frojasg1.libraries.calcul.bigmath.BigMathConstants;
import com.frojasg1.libraries.calcul.bigmath.functions.imp.CustomFunction;
import com.frojasg1.libraries.calcul.bigmath.text.completion.prototypes.PrototypeForCompletion;
import com.frojasg1.libraries.calcul.bigmath.text.completion.prototypes.PrototypeForCompletionFactory;
import com.frojasg1.libraries.calcul.engine.Functions;
import com.frojasg1.libraries.calcul.engine.VariableStore;
*/
import com.frojasg1.general.completion.MapOfPrototypesBase;
import com.frojasg1.general.listeners.map.MapChangeListener;
import com.frojasg1.general.listeners.map.MapChangeObserved;
import com.frojasg1.general.reference.Reference;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class PrototypeManagerInitBase
{
	protected MapOfPrototypesBase _mapOfPrototypes;

//	protected Reference< MapChangeListener< String, CustomFunction > > _userFunctionListener = new Reference<>(null);
//	protected Reference< MapChangeListener< String, BigDecimal > > _variableListener = new Reference<>(null);

	public PrototypeManagerInitBase()
	{
//		init();
	}
/*
	public void init()
	{
		addListeners();
	}
*/
	public void dispose()
	{
		removeListeners();
	}

	protected abstract void removeListeners();

	protected abstract void addListeners();

	protected <KK> void addListener( MapChangeObserved< String, KK > observed,
								MapChangeListener< String, KK > listener,
								Reference< MapChangeListener< String, KK > > oldListener )
	{
		removeListener( observed, oldListener );

		observed.addListenerGen(listener);
		oldListener._value = listener;
	}

	protected <KK> void removeListener( MapChangeObserved< String, KK > observed,
								Reference< MapChangeListener< String, KK > > oldListener )
	{
		if( oldListener._value != null )
			observed.removeListenerGen(oldListener._value);

		oldListener._value = null;
	}
/*
	protected UserFunctionMapChangeListener createUserFunctionsListener()
	{
		UserFunctionMapChangeListener result = new UserFunctionMapChangeListener(){
			@Override
			public void elementPut( MapChangeObserved<String, CustomFunction> observed, String key, CustomFunction value )
			{
				if( _mapOfPrototypes != null )
				{
					PrototypeForCompletion pfc =  getPrototypeForCompletionOfExpectedType( key, PrototypeForCompletionFactory.USER_FUNCTION );

					_mapOfPrototypes.put( createUserFunctionPrototypeForCompletion( Functions.instance().getCustomFunction(key) ) );
				}
			}

			@Override
			public void elementRemoved( MapChangeObserved<String, CustomFunction> observed, String key )
			{
				if( _mapOfPrototypes != null )
					_mapOfPrototypes.removeExact( key, PrototypeForCompletionFactory.USER_FUNCTION );
			}

			@Override
			public void cleared( MapChangeObserved<String, CustomFunction> observed )
			{
				
			}
		};
		return( result );
	}


	protected VariableMapChangeListener createVariableMapListener()
	{
		VariableMapChangeListener result = new VariableMapChangeListener(){
			@Override
			public void elementPut( MapChangeObserved<String, BigDecimal> observed, String key, BigDecimal value )
			{
				if( _mapOfPrototypes == null )
				{
					PrototypeForCompletion pfc =  getPrototypeForCompletionOfExpectedType( key, PrototypeForCompletionFactory.VARIABLE );

					if( pfc == null )
						_mapOfPrototypes.put( createVariablePrototypeForCompletion( key ) );
				}
			}

			@Override
			public void elementRemoved( MapChangeObserved<String, BigDecimal> observed, String key )
			{
				if( _mapOfPrototypes == null )
					_mapOfPrototypes.removeExact( key, PrototypeForCompletionFactory.VARIABLE );
			}

			@Override
			public void cleared( MapChangeObserved<String, BigDecimal> observed )
			{
				
			}
		};
		return( result );
	}

	protected VariableMapChangeListener createVariableMapListener()
	{
		VariableMapChangeListener result = new VariableMapChangeListener(){
			@Override
			public void elementPut( MapChangeObserved<String, BigDecimal> observed, String key, BigDecimal value )
			{
				if( _mapOfPrototypes == null )
				{
					PrototypeForCompletion pfc =  getPrototypeForCompletionOfExpectedType( key, PrototypeForCompletionFactory.VARIABLE );

					if( pfc == null )
						_mapOfPrototypes.put( createVariablePrototypeForCompletion( key ) );
				}
			}

			@Override
			public void elementRemoved( MapChangeObserved<String, BigDecimal> observed, String key )
			{
				if( _mapOfPrototypes == null )
					_mapOfPrototypes.removeExact( key, PrototypeForCompletionFactory.VARIABLE );
			}

			@Override
			public void cleared( MapChangeObserved<String, BigDecimal> observed )
			{
				
			}
		};
		return( result );
	}
*/

/*
	protected void fillUserFunctions( MapOfPrototypesBase mapOfPrototypes, Functions functions )
	{
		for( String cfName: functions.getCustomFunctionSet() )
			mapOfPrototypes.put( createUserFunctionPrototypeForCompletion( functions.getCustomFunction(cfName) ) );
	}

	protected void fillTokens( MapOfPrototypesBase mapOfPrototypes, String[] allTokenDes )
	{
		for( String tokenDes: allTokenDes )
			mapOfPrototypes.put( createTokenPrototypeForCompletion( tokenDes ) );
	}

	protected void fillVariables( MapOfPrototypesBase mapOfPrototypes, VariableStore vs )
	{
		for( String variableName: vs.getVariableSet() )
			mapOfPrototypes.put( createVariablePrototypeForCompletion( variableName ) );
	}

	protected void fillConstants( MapOfPrototypesBase mapOfPrototypes, BigMathConstants bmConstants )
	{
		for( String constantName: bmConstants.getConstantNameEntrySet() )
			mapOfPrototypes.put( createConstantPrototypeForCompletion( constantName ) );
	}

	protected PrototypeForCompletion createPrototypeForCompletion( String name, String type )
	{
		return( PrototypeForCompletionFactory.instance().createObject( name, type ) );
	}

	protected PrototypeForCompletion createUserFunctionPrototypeForCompletion( CustomFunction cf )
	{
		PrototypeForCompletion result = null;
		if( cf != null )
		{
			result = createPrototypeForCompletion( cf.getName(), PrototypeForCompletionFactory.USER_FUNCTION );

			Collection<String> variables = null;
			
			try
			{
				variables = cf.getFunctionDefinition().getFunctionVariableList();
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}

			for( String variable: variables )
				result.addParam(variable);
		}

		return( result );
	}

	protected PrototypeForCompletion createTokenPrototypeForCompletion( String tokenDes )
	{
		PrototypeForCompletion result = null;
		if( tokenDes != null )
		{
			result = createPrototypeForCompletion( tokenDes, PrototypeForCompletionFactory.TOKEN );
		}

		return( result );
	}

	protected PrototypeForCompletion createVariablePrototypeForCompletion( String variableName )
	{
		PrototypeForCompletion result = null;
		if( variableName != null )
		{
			result = createPrototypeForCompletion( variableName, PrototypeForCompletionFactory.VARIABLE );
		}

		return( result );
	}

	protected PrototypeForCompletion createConstantPrototypeForCompletion( String constantName )
	{
		PrototypeForCompletion result = null;
		if( constantName != null )
		{
			result = createPrototypeForCompletion( constantName, PrototypeForCompletionFactory.CONSTANT );
		}

		return( result );
	}

	protected interface UserFunctionMapChangeListener extends MapChangeListener< String, CustomFunction >
	{
		
	}

	protected interface VariableMapChangeListener extends MapChangeListener< String, BigDecimal >
	{
		
	}
*/
}
