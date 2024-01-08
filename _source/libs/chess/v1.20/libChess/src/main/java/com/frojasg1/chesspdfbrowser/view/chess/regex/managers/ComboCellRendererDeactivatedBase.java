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
package com.frojasg1.chesspdfbrowser.view.chess.regex.managers;

import com.frojasg1.general.desktop.view.combobox.renderer.ComboCellRendererBase;
import com.frojasg1.general.desktop.view.FontFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComboCellRendererDeactivatedBase extends ComboCellRendererBase {
	
		protected Font _deactivatedProfileFont = null;

		public ComboCellRendererDeactivatedBase( JComboBox combo )
		{
			super( combo );
		}

		protected Font getDeactivatedProfileFont( Font font )
		{
			if( ( _deactivatedProfileFont == null ) || ( _deactivatedProfileFont.getSize() != font.getSize() ) )
			{
				_deactivatedProfileFont = FontFunctions.instance().getStyledFont( font, Font.BOLD + Font.ITALIC );
			}

			return( _deactivatedProfileFont );
		}

		protected abstract boolean isValueActivated( String value );

		public Component getListCellRendererComponent(JList list, Object value, int index,
														boolean isSelected, boolean cellHasFocus) {
//			Font font = list.getFont();
			Font font = getCombo().getFont();
			Color fgColor = list.getForeground();

			boolean isProfileActivated = isValueActivated( (String) value );

			if( ! isProfileActivated )
			{
				font = getDeactivatedProfileFont( font );
				fgColor = Color.GRAY.brighter();
			}

			JLabel renderer = (JLabel) getDefaultCellRenderer().getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

			setOriginalColors( renderer );

			renderer.setForeground(fgColor);
			renderer.setText( (String) value );
			renderer.setFont( font );

			return renderer;
		}
}
