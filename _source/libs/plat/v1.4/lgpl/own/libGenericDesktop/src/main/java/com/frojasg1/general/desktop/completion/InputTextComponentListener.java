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
package com.frojasg1.general.desktop.completion;

//import com.frojasg1.libraries.calcul.bigmath.formatter.help.expression.ExpressionExternalFormatter;
import com.frojasg1.general.desktop.completion.api.InputTextCompletionManager;
import com.frojasg1.general.document.formatter.ExternalTextFormatter;
import com.frojasg1.general.desktop.generic.view.DesktopViewTextComponent;
import com.frojasg1.general.desktop.generic.view.SimpleViewTextComponent;
//import com.frojasg1.libraries.calcul.bigmath.text.completion.InputTextCompletionManager;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Objects;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class InputTextComponentListener implements DocumentListener, KeyListener,
																MouseListener, FocusListener,
																CaretListener, MouseWheelListener,
																ComponentListener
{
	protected ExternalTextFormatter _externalFormatter = null;

	protected InputTextCompletionManager _completionManager = null;

	protected DesktopViewTextComponent _viewTextComponent = null;

	protected JTextComponent _textComp = null;

	protected Integer _lastCaretPosition = null;
	protected int _caretPositionDelta = 0;

	public void setCompletionManager( InputTextCompletionManager completionManager )
	{
		_completionManager = completionManager;
	}

	public void setNewJTextComponent( JTextComponent jtc )
	{
		if( _textComp != null )
			removeListenersaddListenersInputTextPaneFormatter();

		_viewTextComponent = createViewTextComponent( jtc );

		_textComp = jtc;

		if( _textComp != null )
		{
			_lastCaretPosition = _textComp.getCaretPosition();
			addListenersInputTextPaneFormatter();
		}
	}

	protected DesktopViewTextComponent createViewTextComponent( JTextComponent jtc )
	{
		DesktopViewTextComponent result = null;
		if( jtc != null )
			result = new SimpleViewTextComponent( jtc );
		
		return( result );
	}

	public void dispose()
	{
		removeListenersaddListenersInputTextPaneFormatter();
	}

	protected void addListenersInputTextPaneFormatter()
	{
		if( _textComp != null )
		{
			_textComp.addCaretListener(this);
			_textComp.getDocument().addDocumentListener(this);

			Component comp = _textComp;
			while( comp != null )
			{
				comp.addComponentListener(this);
				comp.addMouseListener(this);
				comp.addMouseWheelListener(this);
				comp.addFocusListener(this);
				comp.addKeyListener(this);
				comp = comp.getParent();
			}
		}
	}

	protected void removeListenersaddListenersInputTextPaneFormatter()
	{
		if( _textComp != null )
		{
			_textComp.removeCaretListener(this);
			_textComp.getDocument().removeDocumentListener(this);

			Component comp = _textComp;
			while( comp != null )
			{
				comp.removeComponentListener(this);
				comp.removeMouseListener(this);
				comp.removeMouseWheelListener(this);
				comp.removeFocusListener(this);
				comp.removeKeyListener(this);
				comp = comp.getParent();
			}
		}
	}

	public DesktopViewTextComponent getViewTextComponent()
	{
		return( _viewTextComponent );
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		_caretPositionDelta += e.getLength();
		if( _completionManager != null )
		{
			SwingUtilities.invokeLater( () -> {
				int lastCaretPosition = getViewTextComponent().getCaretPosition() + e.getLength();
				_completionManager.processTypedInputText( getViewTextComponent().getText(),
															lastCaretPosition,
															getViewTextComponent().getCharacterBounds( lastCaretPosition ) );
			}
										);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		_caretPositionDelta -= e.getLength();
		if( _completionManager != null )
		{
			SwingUtilities.invokeLater( () -> {
				int caretPosition = getViewTextComponent().getCaretPosition() - e.getLength();

				_completionManager.processTypedInputText( getViewTextComponent().getText(),
															caretPosition,
															getViewTextComponent().getCharacterBounds( caretPosition ) );
			}
										);
		}
	}

	@Override
	public void caretUpdate(CaretEvent e)
	{
		SwingUtilities.invokeLater( this::processCaretUpdate );
	}

	public void processCaretUpdate()
	{
		if( _completionManager != null )
		{
			DesktopViewTextComponent viewTextComponent = getViewTextComponent();
			int caretPosition = viewTextComponent.getCaretPosition();

			if( !isExpectedCaretPosition(caretPosition) )
				_completionManager.hideEverything();
			else
				_completionManager.newCaretPosition( viewTextComponent.getText(),
														caretPosition,
														viewTextComponent.getCharacterBounds( caretPosition ) );

			_lastCaretPosition = getViewTextComponent().getCaretPosition();
			_caretPositionDelta = 0;
		}
	}

	protected boolean isExpectedCaretPosition( int caretPosition )
	{
		return( Objects.equals( calculateExpectedCaretPosition(), caretPosition ) );
	}

	protected Integer calculateExpectedCaretPosition()
	{
		Integer result = _lastCaretPosition;
		if( result != null )
			result += _caretPositionDelta;
		
		return( result );
	}

	protected boolean areThereCompletionOptions()
	{
		return( _completionManager.getCompletionWindow().isVisible() &&
				( _completionManager.getCompletionWindow().getSelectedCompletion() != null ) );
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if( _completionManager != null )
		{
			if( ! e.isConsumed() )
			{
				boolean consumed = false;
				
				if( e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE )
				{
					consumed = true;
					_completionManager.escape();
				}
				else if( areThereCompletionOptions() )
				{
					consumed = true;
					switch( e.getExtendedKeyCode() )
					{
						case KeyEvent.VK_PAGE_UP:	_completionManager.pageUp();	break;
						case KeyEvent.VK_PAGE_DOWN:	_completionManager.pageDown();	break;
						case KeyEvent.VK_UP:	_completionManager.lineUp();	break;
						case KeyEvent.VK_DOWN:	_completionManager.lineDown();	break;
						case KeyEvent.VK_ENTER:	_completionManager.selectCurrent();	break;

						default: consumed = false;
					}
				}

				if( consumed )
					e.consume();
			}
		}

//		SwingUtilities.invokeLater( () ->SwingUtilities.invokeLater( () ->_pane.requestFocus() ) );
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
		if( event.getWheelRotation() < 0 )
			_completionManager.pageUp();
		else if( event.getWheelRotation() > 0 )
			_completionManager.pageDown();
	}

	@Override
	public void componentResized(ComponentEvent arg0)
	{
		if( _completionManager != null )
			_completionManager.relocateCompletionWindow();
	}

	@Override
	public void componentMoved(ComponentEvent arg0)
	{
		if( _completionManager != null )
			_completionManager.relocateCompletionWindow();
	}

	@Override
	public void componentShown(ComponentEvent arg0)
	{
	}

	@Override
	public void componentHidden(ComponentEvent arg0)
	{
	}

	@Override
	public void focusGained(java.awt.event.FocusEvent evt)
	{
//		formatDocument();
		Component opposite = evt.getOppositeComponent();
/*
		System.out.println( String.format( "%s ------> %s ( %s )",
							ComponentFunctions.instance().getComponentString( opposite ),
							this.getClass().getName(),
							ComponentFunctions.instance().getComponentString( evt.getComponent() ) ));
*/
	}

	@Override
	public void focusLost(java.awt.event.FocusEvent evt)
	{
//		formatDocument();
		Component opposite = evt.getOppositeComponent();
/*
		System.out.println( String.format( "%s ( %s ) ------> %s",
							this.getClass().getName(),
							ComponentFunctions.instance().getComponentString( evt.getComponent() ),
							ComponentFunctions.instance().getComponentString( opposite ) ));
*/
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
