/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.zoom.filechooser;

import com.frojasg1.general.desktop.generic.dialogs.impl.StaticDesktopDialogsWrapper;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.renderers.TableCellRendererColorInversor;
import java.awt.Component;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JZoomFileChooserTableCellRendererColorInversor extends TableCellRendererColorInversor
{
		protected Integer _lastSelectedRow;
		protected Integer _lastSelectedColumn;
		protected JTable _table;

		public JZoomFileChooserTableCellRendererColorInversor( TableCellRenderer tcr, ColorInversor ci )
		{
			super(tcr, ci);
		}

		public JZoomFileChooserTableCellRendererColorInversor associateJTable( JTable table )
		{
/*			table.getSelectionModel().addListSelectionListener(
				evt -> { _lastSelectedRow = table.getSelectedRow();
						table.repaint();
				});
*/
			_table = table;

			return( this );
		}

		protected int getSelectedIndex( int selected, Integer lastSelected )
		{
			int result = selected;
			if( ( selected == -1 ) && ( lastSelected != null ) )
				result = lastSelected;

			return( result );
		}

		@Override
		protected void invertColors(Component result, boolean isSelected, boolean hasFocus,
							int row, int column)
		{
			int selectedRow = _table.getSelectedRow();
			int selectedColumn = _table.getSelectedColumn();
			if( Objects.equals(row, getSelectedIndex(selectedRow, _lastSelectedRow)) &&
				Objects.equals(column, getSelectedIndex(selectedColumn, _lastSelectedColumn)) )
			{
				if( selectedRow != -1 )
				{
					_lastSelectedRow = selectedRow;
					_lastSelectedColumn = selectedColumn;
				}
				if( _table.hasFocus() || ( selectedRow != -1 ) )
					super.invertColors( result, isSelected, hasFocus, row, column );
			}
		}

		public void resetSelection()
		{
			_lastSelectedRow = null;
			_lastSelectedColumn = null;
		}
}
