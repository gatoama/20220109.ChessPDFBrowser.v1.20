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

import com.frojasg1.chesspdfbrowser.model.regex.RegexOfBlockModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.desktop.view.combobox.renderer.ComboCellRendererBase;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentForChildComboContentServer;
import com.frojasg1.general.desktop.view.combobox.chained.impl.ChainedParentChildComboBoxManagerBase;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TagComboManager_old extends ChainedParentChildComboBoxManagerBase
{
	protected RegexWholeFileModel _regexConfWholeContainer = null;

	public TagComboManager_old( String key, TextComboBoxContent contents,
						RegexWholeFileModel regexWholeConfContainer )
	{
		this( key, contents, null, regexWholeConfContainer );
	}

	public TagComboManager_old( String key, TextComboBoxContent contents,
						ChainedParentComboBoxGroupManager parent,
						RegexWholeFileModel regexWholeConfContainer )
	{
		super( key, contents, null, parent );

		_regexConfWholeContainer = regexWholeConfContainer;
	}

	protected ListCellRenderer createRendererForCombos( JComboBox combo )
	{
//		return( new TagRegexComboListCellRenderer( combo ) );
		return( null );
	}

	protected RegexOfBlockModel get( String tagName )
	{
		String profileName = getParent().getSelectedItem();

//		return( _regexConfWholeContainer.getRegexConf( profileName, tagName) );
		return( null );
	}

	protected class TagRegexComboListCellRenderer extends ComboCellRendererBase
	{
		public TagRegexComboListCellRenderer( JComboBox combo )
		{
			super( combo );
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index,
							boolean isSelected, boolean cellHasFocus)
		{
			String valueStr = (String) value;
			Font font = getCombo().getFont();
			if( get( valueStr ) != null )
				font = FontFunctions.instance().getStyledFont(font, Font.BOLD );

			JLabel renderer = (JLabel) getDefaultCellRenderer().getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

			renderer.setFont(font);

			return( renderer );
		}
	}
}
