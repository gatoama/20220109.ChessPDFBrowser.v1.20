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
package com.frojasg1.general.desktop.view.combobox.renderer;

import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComboCellRendererBase implements ListCellRenderer, InternallyMappedComponent
{
	protected DefaultListCellRenderer _defaultRenderer = new DefaultListCellRenderer();
	protected JComboBox _combo = null;
	
	protected Color _originalForeground;
	protected Color _originalBackground;

	protected boolean _alreadyMapped = false;

	public ComboCellRendererBase( JComboBox combo )
	{
		_combo = combo;
	}

	@Override
	public void setComponentMapper( ComponentMapper mapper )
	{
		_combo = mapper.mapComponent(_combo);

		_alreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}

	public JComboBox getCombo()
	{
		return( _combo );
	}

	protected DefaultListCellRenderer getDefaultCellRenderer()
	{
		return( _defaultRenderer );
	}

	protected String toString( Object value )
	{
		String result = null;
		if( value != null )
			result = value.toString();

		return( result );
	}

	public Component getListCellRendererComponent(JList list, Object value, int index,
													boolean isSelected, boolean cellHasFocus) {
			Font font = getCombo().getFont();
			Color fgColor = list.getForeground();

			JLabel renderer = (JLabel) getDefaultCellRenderer().getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

			setOriginalColors( renderer );

			renderer.setForeground(fgColor);
			renderer.setText( toString( value ) );
			renderer.setFont( font );

			return renderer;
		}

	public Color getOriginalForeground() {
		return _originalForeground;
	}

	public void setOriginalForeground(Color _originalForeground) {
		this._originalForeground = _originalForeground;
	}

	public Color getOriginalBackground() {
		return _originalBackground;
	}

	public void setOriginalBackground(Color _originalBackground) {
		this._originalBackground = _originalBackground;
	}

	protected void setOriginalColors( Component renderer )
	{
		setOriginalBackground( renderer.getBackground() );
		setOriginalForeground( renderer.getForeground() );
	}
	
}
