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

import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import com.frojasg1.general.validator.ValidatorReactor;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusListener;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class RealTimeTextComponentValidatorReactor
	implements ValidatorReactor<JTextComponent>, ColorThemeInvertible
{
	protected JTextComponent _textComp = null;

	protected FocusListener _focusListener = null;
//	protected KeyListener _keyListener = null;
	protected DocumentListener _documentListener = null;

	protected Color _okForegroundColor = Color.BLACK;


	public RealTimeTextComponentValidatorReactor( JTextComponent textComp )
	{
		_textComp = textComp;
	}

	protected void setOkForegroundColor( Color color )
	{
		_okForegroundColor = color;
	}

	protected FocusAdapter createFocusListener()
	{
		return( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                checkAndReact();
            }
        });
	}
/*
	protected KeyAdapter createKeyListener()
	{
		return( new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                checkAndReact();
            }
        });
	}
*/

	protected DocumentListener createDocumentListener()
	{
		return( new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				checkAndReact();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkAndReact();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

		});
	}

	protected void addListeners()
	{
		_focusListener = createFocusListener();
//		_keyListener = createKeyListener();
		_documentListener = createDocumentListener();

		if( _focusListener != null )
			_textComp.addFocusListener(_focusListener);
//		if( _keyListener != null )
//			_textComp.addKeyListener(_keyListener);
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
//		if( _keyListener != null )
//			_textComp.removeKeyListener(_keyListener);
		if( _documentListener != null )
			_textComp.getDocument().removeDocumentListener(_documentListener);

		_focusListener = null;
//		_keyListener = null;
		_documentListener = null;
	}

	public void dispose()
	{
		removeListeners();
		_textComp = null;
	}

	protected void checkAndReact()
	{
		if( validate( _textComp ) )
			executeOk( _textComp );
		else
			executeNok( _textComp );
	}

	@Override
	public void executeOk( JTextComponent textComp )
	{
		_textComp.setForeground(_okForegroundColor);
	}

	@Override
	public void executeNok( JTextComponent textComp )
	{
		_textComp.setForeground(Color.RED);
	}

	@Override
	public void invertColors( ColorInversor colorInversor )
	{
		_okForegroundColor = colorInversor.invertColor(_okForegroundColor);
		SwingUtilities.invokeLater( this::checkAndReact );
	}
}
