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
package com.frojasg1.general.desktop.view.zoom.components;

import com.frojasg1.general.desktop.view.zoom.imp.listeners.CursorKeyListener;
import com.frojasg1.general.desktop.view.zoom.imp.listeners.TopRightCaretListener;
import com.frojasg1.general.desktop.view.zoom.ui.test.StyledEditorKitForTesting;
import java.awt.Dimension;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTextPane_CustomHorizSize extends JTextPane_Custom
{
	protected CursorKeyListener _cursorListener = null;
	protected TopRightCaretListener _caretListener = null;

	/**
     * Creates a new <code>JTextPane</code>.  A new instance of
     * <code>StyledEditorKit</code> is
     * created and set, and the document model set to <code>null</code>.
     */
    public JTextPane_CustomHorizSize()
	{
        super();
		init();
    }

    /**
     * Creates a new <code>JTextPane</code>, with a specified document model.
     * A new instance of <code>javax.swing.text.StyledEditorKit</code>
     *  is created and set.
     *
     * @param doc the document model
     */
    public JTextPane_CustomHorizSize(StyledDocument doc)
	{
        super(doc);
		init();
    }

    public JTextPane_CustomHorizSize(JTextPane other)
	{
        super( other );
		init();
	}

	protected void init()
	{
		_cursorListener = createCursorKeyListener();
		_caretListener = createTopRightCaretListener();
	}

	protected TopRightCaretListener createTopRightCaretListener()
	{
		TopRightCaretListener result = new TopRightCaretListener( this );
		result.init();

		return( result );
	}

	protected CursorKeyListener createCursorKeyListener()
	{
		CursorKeyListener result = new CursorKeyListener( this );
		result.init();

		return( result );
	}

	@Override
	public void setDocument( Document document )
	{
		super.setDocument( document );
	}

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return( false );
	}

	@Override
	public Dimension getPreferredSize()
	{
		return( super.getPreferredSize() );
	}

	@Override
	public void setPreferredSize( Dimension dimen )
	{
		super.setPreferredSize( dimen );
	}

	@Override
	protected EditorKit createDefaultEditorKit() {
        return new StyledEditorKitForTesting();
    }

	@Override
	public void requestFocus()
	{
		super.requestFocus();
	}
}
