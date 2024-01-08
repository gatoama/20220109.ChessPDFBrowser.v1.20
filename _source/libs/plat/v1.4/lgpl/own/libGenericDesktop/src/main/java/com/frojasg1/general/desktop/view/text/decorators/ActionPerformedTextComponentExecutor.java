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
package com.frojasg1.general.desktop.view.text.decorators;

import com.frojasg1.general.ExecutionFunctions;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusListener;
import java.util.Objects;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ActionPerformedTextComponentExecutor
{
	protected JTextComponent _textComp = null;

	protected FocusListener _focusListener = null;
	protected ActionListener _actionListener = null;
	protected DocumentListener _documentListener = null;

	protected String _prevText = null;

	public ActionPerformedTextComponentExecutor( JTextComponent textComp )
	{
		_textComp = textComp;
	}

	protected FocusAdapter createFocusListener()
	{
		return( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                executeIfNeeded();
            }
        });
	}

	protected ActionListener createActionListener()
	{
		return( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                executeIfNeeded();
            }
        });
	}

	protected DocumentListener createDocumentListener()
	{
		return( new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if( !_textComp.hasFocus() )
					executeIfNeeded();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
	}

	protected String getText()
	{
		return( ExecutionFunctions.instance().safeFunctionExecution( () -> _textComp.getDocument().getText(0, _textComp.getDocument().getLength() ) ) );
	}

	protected void executeIfNeeded()
	{
		if( !Objects.equals( getText(), _prevText ) )
		{
			updateText();
			execute();
		}
	}

	public abstract void execute();

	protected void executed()
	{
		updateText();
	}

	protected void updateText()
	{
		_prevText = getText();
	}

	protected void addListeners()
	{
		_focusListener = createFocusListener();
//		_keyListener = createKeyListener();

		if( _focusListener != null )
			_textComp.addFocusListener(_focusListener);
//		if( _keyListener != null )
//			_textComp.addKeyListener(_keyListener);
		if( _textComp instanceof JTextField )
		{
			_actionListener = createActionListener();
			if( _actionListener != null )
				( ( JTextField)_textComp ).addActionListener(_actionListener);
		}

		_documentListener = createDocumentListener();
		if( _documentListener != null )
			_textComp.getDocument().addDocumentListener(_documentListener);
	}

	public void init()
	{
		addListeners();
	}

	protected void removeListeners()
	{
		if( _focusListener != null )
			_textComp.removeFocusListener(_focusListener);

		if( _actionListener != null )
			( ( JTextField)_textComp ).removeActionListener(_actionListener);

		if( _documentListener != null )
			_textComp.getDocument().removeDocumentListener(_documentListener);

		_focusListener = null;
		_actionListener = null;
		_documentListener = null;
	}

	public void dispose()
	{
		removeListeners();
		_textComp = null;
	}
}
