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
package com.frojasg1.chesspdfbrowser.view.chess.completion;
/*
import com.frojasg1.libraries.calcul.bigmath.BigMathConstants;
import com.frojasg1.libraries.calcul.bigmath.functions.imp.CustomFunction;
import com.frojasg1.libraries.calcul.bigmath.text.completion.prototypes.PrototypeForCompletionBase;
import com.frojasg1.libraries.calcul.bigmath.text.completion.prototypes.PrototypeForCompletionBaseFactory;
import com.frojasg1.libraries.calcul.engine.Functions;
import com.frojasg1.libraries.calcul.engine.VariableStore;
*/
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.view.chess.completion.PrototypeManagerInit.BlockMapChangeListener;
import com.frojasg1.general.completion.MapOfPrototypesBase;
import com.frojasg1.general.completion.PrototypeForCompletionBase;
import com.frojasg1.general.completion.PrototypeManagerInitBase;
import com.frojasg1.general.listeners.map.MapChangeListener;
import com.frojasg1.general.listeners.map.MapChangeObserved;
import com.frojasg1.general.reference.Reference;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PrototypeManagerInit extends PrototypeManagerInitBase
{
	protected RegexWholeFileModel _regexWholeContainer;

	protected Reference< MapChangeListener< String, String > > _blockRegexListener = new Reference<>(null);

	public PrototypeManagerInit()
	{
//		init();
	}
/*
	public void init()
	{
		addListeners();
	}
*/

	@Override
	protected void removeListeners()
	{
		removeListeners( _regexWholeContainer );
	}

	protected void removeListeners( RegexWholeFileModel regexWholeContainer )
	{
		if( regexWholeContainer != null )
			removeListener( regexWholeContainer.getBlockConfigurationContainer().getComboBoxContent(),
				_blockRegexListener );
	}

	@Override
	protected void addListeners()
	{
		addListeners( _regexWholeContainer );
	}

	protected void addListeners( RegexWholeFileModel regexWholeContainer )
	{
		if( regexWholeContainer != null )
			addListener( regexWholeContainer.getBlockConfigurationContainer().getComboBoxContent(),
							createBlockRegexMapListener(), _blockRegexListener );
	}

	protected String formatBlockName( String blockName )
	{
		String result = null;

		if( blockName != null )
			result = String.format( "%%%s%%", blockName );

		return( result );
	}

	protected BlockMapChangeListener createBlockRegexMapListener()
	{
		BlockMapChangeListener result = new BlockMapChangeListener(){
			@Override
			public void elementPut( MapChangeObserved<String, String> observed, String key, String value )
			{
				if( _mapOfPrototypes != null )
				{
					String newItem = formatBlockName( key );
					PrototypeForCompletionBase pfc =  getPrototypeForCompletionBaseOfExpectedType( newItem, PrototypeForCompletionFactory.BLOCK_REGEX );

					if( pfc == null )
						_mapOfPrototypes.put( createBlockRegexPrototypeForCompletionBase( newItem ) );
				}
			}

			@Override
			public void elementRemoved( MapChangeObserved<String, String> observed, String key )
			{
				if( _mapOfPrototypes != null )
				{
					String newItem = formatBlockName( key );
//					_mapOfPrototypes.removeExact( key, PrototypeForCompletionBaseFactory.BLOCK_REGEX );
					_mapOfPrototypes.removeFirst( key );
				}
			}

			@Override
			public void cleared( MapChangeObserved<String, String> observed )
			{
				
			}
		};
		return( result );
	}

	protected PrototypeForCompletionBase getPrototypeForCompletionBaseOfExpectedType( String name, String type )
	{
		PrototypeForCompletionBase result =  null;
		
		if( _mapOfPrototypes != null )
		{
			result = _mapOfPrototypes.getFirst( name );

			if( ( result != null ) &&
				!result.getType().equals(type ) )
			{
				throw( new IllegalArgumentException( "Existing word for completion existed previously: " +
													result.getType() +
													" But it was expected type: " + type +
													" User function " + name + " cannot be removed." ) );
			}
		}

		return( result );
	}

	public void initialize( MapOfPrototypesBase mapOfPrototypes, RegexWholeFileModel regexWholeContainer )
	{
		removeListeners( _regexWholeContainer );
		addListeners( regexWholeContainer );

		_mapOfPrototypes = mapOfPrototypes;
		_regexWholeContainer = regexWholeContainer;

		mapOfPrototypes.clear();

		fillBlockRegexNames( mapOfPrototypes, _regexWholeContainer.getBlockConfigurationContainer().getComboBoxContent().getListOfItems() );
/*
		fillUserFunctions( mapOfPrototypes, Functions.instance() );
//		fillTokens( mapOfPrototypes, LexicalAnalyser.ALL_TOKEN_STRINGS );	// they are parsed from help.xml
		fillVariables( mapOfPrototypes, VariableStore.instance() );
		fillConstants( mapOfPrototypes, BigMathConstants.instance() );
*/
	}

	protected void fillBlockRegexNames( MapOfPrototypesBase mapOfPrototypes, List<String> listOfBlockRegexNames )
	{
		for( String blockRegexName: listOfBlockRegexNames )
			mapOfPrototypes.put( createBlockRegexPrototypeForCompletionBase( formatBlockName( blockRegexName ) ) );
	}

	protected PrototypeForCompletionBase createBlockRegexPrototypeForCompletionBase( String blockRegexName )
	{
		PrototypeForCompletionBase result = null;
		if( blockRegexName != null )
		{
			result = createPrototypeForCompletionBase( blockRegexName, PrototypeForCompletionFactory.BLOCK_REGEX );
		}

		return( result );
	}

	protected PrototypeForCompletionBase createPrototypeForCompletionBase( String name, String type )
	{
		return( PrototypeForCompletionFactory.instance().createObject( name, type ) );
	}

	protected interface BlockMapChangeListener extends MapChangeListener< String, String >
	{
		
	}
}
