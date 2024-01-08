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

import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.completion.MapOfPrototypesBase;
import com.frojasg1.general.completion.PrototypeManagerBase;
import com.frojasg1.general.completion.PrototypeManagerInitBase;


/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PrototypeManager extends PrototypeManagerBase
{
	protected RegexWholeFileModel _regexWholeContainer;

	public void initialize( RegexWholeFileModel regexWholeContainer )
	{
		init();

		_regexWholeContainer = regexWholeContainer;
		clear();
		getPrototypeManagerInit().initialize( _mapOfPrototypes, _regexWholeContainer );
	}

	@Override
	protected PrototypeManagerInit getPrototypeManagerInit()
	{
		return( (PrototypeManagerInit) super.getPrototypeManagerInit() );
	}
/*
	public String getStringOfAllOperators()
	{
		return( _stringOfAllOperators );
	}
*/
	protected PrototypeManagerInit createPrototypeManagerInit()
	{
		return( new PrototypeManagerInit() );
	}

	protected MapOfPrototypesBase createMapOfPrototypes()
	{
		return( new MapOfPrototypes() );
	}
}
