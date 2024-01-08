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
package com.frojasg1.chesspdfbrowser.view.chess.regex.managers;

import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.view.chess.regex.controller.RegexComboControllerBase;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.impl.ChainedParentChildComboBoxManagerBase;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ProfileComboManager extends ChainedParentChildComboBoxManagerBase
{
	public static final String CHILD_KEY_FOR_PARENT = "PROFILE_KEY";

	protected RegexComboControllerBase _controller = null;

	public ProfileComboManager(ChainedParentComboBoxGroupManager parent)
	{
		super( CHILD_KEY_FOR_PARENT, null, null, parent );
	}

	public void init( RegexComboControllerBase controller )
	{
		super.init();

		_controller = controller;
		assignContentServer();
	}

	@Override
	public void init()
	{
		throw( new RuntimeException( "You cannot use this init function. Use init( RegexComboControllerBase controller ) instead." ) );
	}

	protected void assignContentServer()
	{
		_contentServer = null; //( key, list ) -> _regexConfWholeContainer.getContentForCombosOfProfile( list.get(0) );
	}

	@Override
	protected ListCellRenderer createRendererForCombos( JComboBox combo )
	{
		return( new ComboCellRenderer( combo ) );
	}

	protected RegexWholeFileModel getWholeFileModel()
	{
		return( _controller.getRegexWholeFileModel() );
	}

	protected boolean isProfileActivated( String profileName )
	{
		boolean result = false;
		RegexWholeFileModel rwfm = getWholeFileModel();
		if( rwfm != null )
		{
			ProfileModel profileModel = rwfm.get( profileName );

			if( profileModel != null )
				result = profileModel.isActive();
		}

		return( result );
	}

	class ComboCellRenderer extends ComboCellRendererDeactivatedBase
	{
		public ComboCellRenderer( JComboBox combo )
		{
			super( combo );
		}

		@Override
		protected boolean isValueActivated(String value) {
			return( isProfileActivated( value ) );
		}
	}
}
