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
package com.frojasg1.applications.common.components.resizecomp;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.general.desktop.view.combobox.utils.ComboBoxFunctions;
import com.frojasg1.general.zoom.ZoomParam;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class ResizeRelocateItem_JComboBox extends ResizeRelocateItem
{
	protected int _originalRowHeightForTable = 1;

	protected Integer _originalCellPreferredHeight = null;

	protected Integer _originalComboPopupFontSize = null;

	public ResizeRelocateItem_JComboBox( JComboBox comp, int flags, ResizeRelocateItem_parent parent,
								boolean postpone_initialization, boolean isAlreadyZoomed ) throws InternException
	{
		super( comp,flags, parent, postpone_initialization, isAlreadyZoomed );

		initializeWithJCombo( getComponent() );
	}

	public JPopupMenu getComboPopup()
	{
		return( ComboBoxFunctions.instance().getComboPopup( getComponent() ) );
	}

	public final void initializeWithJCombo( JComboBox combo ) throws InternException
	{
//		combo.setPreferredSize( combo.getSize() );
		Font font = getComboPopup().getFont();
		if( font != null )
		{
			_originalComboPopupFontSize = font.getSize();
		}
	}

	@Override
	public void execute( ZoomParam zp )
	{
		super.execute( zp );

//		SwingUtilities.invokeLater( () -> resizeRelocate( getComboPopup(), zp ) );

		resizeRelocate( getComboPopup(), zp );
	}

	@Override
	protected void zoomFonts( double zoomFactor )
	{
		super.zoomFonts( zoomFactor );

		if( _originalComboPopupFontSize != null )
		{
			Font newFont = getZoomedFont( getComboPopup().getFont(), (double) _originalComboPopupFontSize, zoomFactor );
			if( newFont != null )
				getComboPopup().setFont( newFont );
		}
	}

	@Override
	public JComboBox getComponent()
	{
		return( (JComboBox) super.getComponent() );
	}
}

