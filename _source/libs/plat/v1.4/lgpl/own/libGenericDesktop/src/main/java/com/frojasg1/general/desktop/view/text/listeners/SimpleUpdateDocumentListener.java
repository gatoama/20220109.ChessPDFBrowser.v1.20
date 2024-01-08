/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.text.listeners;

import com.frojasg1.general.desktop.view.text.utils.TextViewFunctions;
import java.util.function.Consumer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SimpleUpdateDocumentListener implements DocumentListener
{
	protected JTextComponent _textComponent;
	protected Consumer<String> _updateFunction;

	public SimpleUpdateDocumentListener( JTextComponent textComponent,
										Consumer<String> updateFunction)
	{
		_textComponent = textComponent;
		_updateFunction = updateFunction;
	}

	protected String getText()
	{
		return( TextViewFunctions.instance().getText(_textComponent) );
	}

	public void update()
	{
		_updateFunction.accept( getText() );
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		
	}
}
