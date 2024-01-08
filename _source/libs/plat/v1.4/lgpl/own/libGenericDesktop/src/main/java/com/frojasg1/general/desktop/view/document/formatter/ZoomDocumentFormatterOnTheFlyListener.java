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
package com.frojasg1.general.desktop.view.document.formatter;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomDocumentFormatterOnTheFlyListener 	implements FormatterListener,
																DocumentListener, KeyListener,
																MouseListener, FocusListener,
																CaretListener, MouseWheelListener,
																ComponentListener
{
	protected ZoomDocumentFormatter _formatter = null;
	protected JTextPane _pane = null;

	public ZoomDocumentFormatterOnTheFlyListener( ZoomDocumentFormatter parent,
												JTextPane pane )
	{
		_formatter = parent;
		_pane = pane;

		setNewJTextPane( pane );
		addListeners();
	}

	@Override
	public void focusGained(java.awt.event.FocusEvent focusEvent)
	{
//		formatDocument();
	}

	@Override
	public void focusLost(java.awt.event.FocusEvent focusEvent)
	{
		formatDocument();
	}

	public void dispose()
	{
		removeListeners();

		_formatter = null;
		_pane = null;
	}

	public ZoomDocumentFormatter getDocumentFormatter()
	{
		return( _formatter );
	}

	protected void addListeners()
	{
		if( ( _pane != null ) &&
			! Arrays.stream( _pane.getKeyListeners() ).anyMatch( (listener) -> listener == this ) )
		{
			_pane.getDocument().addDocumentListener( this );
//			_pane.addKeyListener(this);
			_pane.addMouseListener(this);
			_pane.addFocusListener(this);
			_pane.addCaretListener( this );
			_pane.addMouseWheelListener( this );
			_pane.addComponentListener(this);
		}
	}

	protected void removeListeners()
	{
		if( _pane != null )
		{
			_pane.getDocument().removeDocumentListener( this );
			_pane.removeKeyListener(this);
			_pane.removeMouseListener(this);
			_pane.removeFocusListener(this);
			_pane.removeCaretListener(this);
			_pane.removeMouseWheelListener( this );
			_pane.removeComponentListener(this);
		}
	}

	@Override
	public void setNewJTextPane( JTextPane jtp )
	{
		if( jtp != _pane )
		{
			removeListeners();

			_pane = jtp;

			addListeners();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		formatDocument();
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		formatDocument();
	}

	@Override
	public void changedUpdate(DocumentEvent e)
	{
		//Plain text components do not fire these events
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
			
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if( ( e.getExtendedKeyCode() == KeyEvent.VK_LEFT ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_RIGHT ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_UP ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_DOWN ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_KP_DOWN ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_KP_LEFT ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_KP_RIGHT ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_KP_UP ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_END ) ||
			( e.getExtendedKeyCode() == KeyEvent.VK_BEGIN )
		  )
		{
			formatDocument();
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		formatDocument();
	}

	@Override
	public void caretUpdate(CaretEvent e)
	{
		formatDocument();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
		
	}

	public void formatDocument()
	{
		_formatter.formatDocument();
	}
}
