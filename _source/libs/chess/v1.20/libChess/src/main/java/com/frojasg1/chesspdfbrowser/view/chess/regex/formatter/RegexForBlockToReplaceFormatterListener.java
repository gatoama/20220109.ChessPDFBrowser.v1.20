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

import com.frojasg1.general.desktop.view.document.formatter.ZoomDocumentFormatterOnTheFlyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexForBlockToReplaceFormatterListener extends ZoomDocumentFormatterOnTheFlyListener
{
	public RegexForBlockToReplaceFormatterListener( RegexDocumentFormatterComboForBlockToReplaceWithUpdater parent,
												JTextPane pane )
	{
		super( parent, pane );
	}

	public RegexDocumentFormatterComboForBlockToReplaceWithUpdater getDocumentFormatter()
	{
		return( (RegexDocumentFormatterComboForBlockToReplaceWithUpdater) super.getDocumentFormatter() );
	}

	@Override
	public void focusLost(java.awt.event.FocusEvent focusEvent)
	{
		super.focusLost( focusEvent );

		getDocumentFormatter().fillInComboBox();
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		super.insertUpdate( e );

		getDocumentFormatter().invokeSizeChangedListeners();
//		invokeSizeChangedListeners();
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		super.insertUpdate( e );
		getDocumentFormatter().invokeSizeChangedListeners();

//		invokeSizeChangedListeners();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void changedUpdate(DocumentEvent e)
	{
		//Plain text components do not fire these events
	}
}
