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
package com.frojasg1.chesspdfbrowser.view.chess.regex.formatter;

import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.TagReplacementModel;
import com.frojasg1.general.desktop.view.document.formatter.FormatterListener;
import javax.swing.JComboBox;
import javax.swing.JTextPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexDocumentFormatterComboForTagsUpdater extends RegexDocumentFormatterComboForBlockToReplaceWithUpdater
{
	protected LineModel _lineModel = null;

	public RegexDocumentFormatterComboForTagsUpdater(
		JTextPane pane, ChangeZoomFactorServerInterface changeZoomFactorServer,
		LineModel lineModel )
	{
		super( pane, changeZoomFactorServer, lineModel.getParent().getParent().getBlockRegexBuilder() );
		_lineModel = lineModel;
	}

	protected FormatterListener createFormatterListener()
	{
		return( new RegexFormatterListener( this, getJTextPane() ) {
				@Override
				public void focusLost(java.awt.event.FocusEvent focusEvent)
				{
				}
		});
	}

	@Override
	public void setCombo( JComboBox comboForBlockToReplaceWith )
	{
		super.setCombo(comboForBlockToReplaceWith);

		comboActionPerformed(null);
	}

	@Override
	public void fillInComboBox()
	{
/*
		if( _comboForBlockToReplaceWith != null )
		{
			List<RegexToken> tokenList = ExecutionFunctions.instance().safeFunctionExecution( () -> getRegexTokenList() );

			String selectedBlockToReplaceWith = getComboSelection();
			String[] elements = getElementsForCombo( tokenList, selectedBlockToReplaceWith );

			ComboBoxFunctions.instance().fillComboBox(_comboForBlockToReplaceWith, elements, selectedBlockToReplaceWith);
		}
*/
	}

	@Override
	protected String getBlockToReplaceWith( String comboItem )
	{
		String result = null;
		TagReplacementModel trm = _lineModel.get( comboItem );
		if( trm != null )
			result = trm.getBlockToReplaceWith();

		return( result );
	}
}
