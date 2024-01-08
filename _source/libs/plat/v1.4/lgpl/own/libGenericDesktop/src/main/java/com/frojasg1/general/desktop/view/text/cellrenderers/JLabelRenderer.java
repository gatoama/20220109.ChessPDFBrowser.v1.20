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
package com.frojasg1.general.desktop.view.text.cellrenderers;

import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JLabelRenderer extends JLabel
							   implements TableCellRenderer
{
	protected Dimension _preferredSize = null;

	public JLabelRenderer(Font font) {

		setOpaque( true );

		setFont( font );

		Border border = getBorder();
		Border margin = new EmptyBorder(3,3,3,3);
		setBorder(new CompoundBorder(border, margin));
	}

	protected Color getBackgroundColor( JTable table, boolean isSelected, int row, int column )
	{
		Color result = null;
		if(isSelected)
			result = table.getSelectionBackground();
		else
			result = table.getBackground();

		return( result );
	}

	protected Color getForegroundColor( JTable table, boolean isSelected, int row, int column )
	{
		Color result = null;
		if(isSelected)
			result = table.getSelectionForeground();
		else
			result = table.getForeground();

		return( result );
	}

	public Component getTableCellRendererComponent(
							JTable table, Object string,
							boolean isSelected, boolean hasFocus,
							int row, int column) {
		this.setBackground(getBackgroundColor( table, isSelected, row, column ));
		this.setForeground(getForegroundColor( table, isSelected, row, column ));

		String str = (String) string;

		this.setText( str );

		Integer value = IntegerFunctions.parseInt( str );
		if( value != null )
			this.setHorizontalAlignment( SwingConstants.RIGHT );
		else
			this.setHorizontalAlignment( SwingConstants.LEFT );

		return this;
	}

	@Override
	public void setPreferredSize( Dimension size )
	{
		super.setPreferredSize(size);
	}
}
