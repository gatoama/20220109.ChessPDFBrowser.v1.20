/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color.renderers;

import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.zoom.componentcopier.GenericCompCopier;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TableCellRendererColorInversor implements TableCellRenderer
{
	protected TableCellRenderer _originalRenderer;
	protected ColorInversor _colorInversor;
	protected String _name;

	public TableCellRendererColorInversor( TableCellRenderer originalRenderer,
										ColorInversor colorInversor )
	{
		_originalRenderer = originalRenderer;
		_colorInversor = colorInversor;
	}

	public TableCellRenderer getOriginalTableCellRenderer()
	{
		return( _originalRenderer );
	}

	public void setOriginalTableCellRenderer(TableCellRenderer tableCellRenderer)
	{
		_originalRenderer = tableCellRenderer;
	}

	public String getName()
	{
		return( _name );
	}

	public void setName( String name )
	{
		_name = name;
	}

	protected JLabel cloneLabel( JLabel label )
	{
		JLabel result = new JLabel();
		GenericCompCopier.instance().copyToNew(label, result);
		return( result );
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component result = _originalRenderer.getTableCellRendererComponent( table, value,
														isSelected, hasFocus, row, column );
		if( result instanceof JLabel )
			result = cloneLabel((JLabel) result);

		invertColors( result, isSelected, hasFocus, row, column );

		return( result );
	}

	protected void invertColors(Component result, boolean isSelected, boolean hasFocus,
							int row, int column) {
//		_colorInversor.invertSingleColorsGen(result);
		_colorInversor.invertBackground(result);
		_colorInversor.invertForeground(result);
//		if( isSelected )
//		{
//			_colorInversor.invertSelectionBackground(result);
//			_colorInversor.invertSelectionForeground(result);
//		}
	}
}
