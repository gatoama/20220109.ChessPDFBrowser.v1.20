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
package com.frojasg1.general.desktop.view.jtable;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.editors.TableCellEditorColorInversor;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableFunctions
{
	protected static JTableFunctions _instance;

	public static void changeInstance( JTableFunctions instance )
	{
		_instance = instance;
	}

	public static JTableFunctions instance()
	{
		if( _instance == null )
			_instance = new JTableFunctions();
		
		return( _instance );
	}

	public TableCellRenderer getTableCellRenderer( JTable table, int column )
	{
		TableCellRenderer result = null;

		if( ( table.getColumnModel() != null ) &&
			( table.getColumnModel().getColumnCount() > column ) )
		{
			result = table.getCellRenderer(0, column);
		}

		return( result );
	}

	public void zoomCellRenderer(TableCellRenderer tcr,	double zoomFactor )
	{
		if( tcr instanceof Component )
		{
			zoomCellRenderer( tcr, ((Component) tcr).getPreferredSize().height,
								zoomFactor );
		}
	}

	protected void zoomCellRenderer(TableCellRenderer tcr, Integer originalCellPreferredHeight,
								double zoomFactor )
	{
		if( ( tcr instanceof Component ) && ( originalCellPreferredHeight != null ) )
		{
			Component tcrComp = (Component) tcr;

			tcrComp.setPreferredSize( new Dimension( tcrComp.getPreferredSize().width,
				IntegerFunctions.zoomValueCeil(originalCellPreferredHeight, zoomFactor ) ) );
		}
	}

	public void zoomTableRows(JTable jTable, double zoomFactor )
	{
		zoomCellRenderer(getTableCellRenderer( jTable, 0 ),	zoomFactor );
		zoomTableRowHeight( jTable, jTable.getRowHeight(), zoomFactor );
	}

	public void zoomTableRows(JTable jTable, Integer originalCellPreferredHeight,
								Integer originalRowHeightForTable, double zoomFactor )
	{
		if( originalCellPreferredHeight != null )
		{
			zoomCellRenderer(getTableCellRenderer( jTable, 0 ),
				originalCellPreferredHeight, zoomFactor );
		}

		zoomTableRowHeight( jTable, originalRowHeightForTable, zoomFactor );
	}

	public void zoomTableRowHeight(JTable jTable, double zoomFactor )
	{
		zoomTableRowHeight(jTable, jTable.getRowHeight(), zoomFactor );
	}

	protected void zoomTableRowHeight(JTable jTable,
								Integer originalRowHeightForTable,
								double zoomFactor )
	{
		double newHeight = Math.round( originalRowHeightForTable * zoomFactor );
		jTable.setRowHeight( ( new Double( newHeight ) ).intValue() );
	}

	public void switchCellEditors( JTable table )
	{
		ColorInversor ci = getColorInversor( table );
		Map<TableCellEditor, TableCellEditor> map = new HashMap<>();
		TableCellEditor origEditor = table.getCellEditor();
		table.setCellEditor( getSwitchedCellEditor( map, origEditor, ci ) );

		for( int ii=0; ii<table.getColumnCount(); ii++ )
		{
			TableColumn tc = table.getColumnModel().getColumn(ii);
			origEditor = tc.getCellEditor();
			tc.setCellEditor(getSwitchedCellEditor( map, origEditor, ci ));
		}
	}

	protected TableCellEditor getSwitchedCellEditor( Map<TableCellEditor, TableCellEditor> map,
													TableCellEditor origEditor,
													ColorInversor ci)
	{
		TableCellEditor result = map.get(origEditor);

		if( result == null )
		{
			result = createSwitchedCellEditor(origEditor, ci);
			map.put(origEditor, result);
		}

		return( result );
	}

	protected ColorInversor getColorInversor( Component comp )
	{
		return( FrameworkComponentFunctions.instance().getColorInversor(comp) );
	}

	protected TableCellEditor createSwitchedCellEditorFromOriginal( TableCellEditor origEditor,
																	ColorInversor ci)
	{
		return( new TableCellEditorColorInversor(origEditor, ci, getAppliConf()) );
	}

	protected TableCellEditor createSwitchedCellEditor( TableCellEditor origEditor,
														ColorInversor ci )
	{
		TableCellEditor result = origEditor;
		if( (origEditor != null ) &&
			!( origEditor instanceof TableCellEditorColorInversor ) )
		{
			result = createSwitchedCellEditorFromOriginal(origEditor, ci);
		}

		return( result );
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( GenericFunctions.instance().getAppliConf() );
	}
}
