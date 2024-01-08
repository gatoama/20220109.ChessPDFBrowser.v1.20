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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.combomanager;

import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.impl.ChainedParentChildComboBoxManagerBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineComboManager extends ChainedParentChildComboBoxManagerBase
{
	public static final String CHILD_KEY_FOR_PARENT = "BLOCK_KEY";

	public EngineComboManager(ChainedParentComboBoxGroupManager parent)
	{
		super( CHILD_KEY_FOR_PARENT, null, null, parent );
	}

	@Override
	public void init()
	{
		super.init();

		assignContentServer();
	}

	protected void assignContentServer()
	{
		_contentServer = null; //( key, list ) -> _regexConfWholeContainer.getContentForCombosOfProfile( list.get(0) );
	}
}
