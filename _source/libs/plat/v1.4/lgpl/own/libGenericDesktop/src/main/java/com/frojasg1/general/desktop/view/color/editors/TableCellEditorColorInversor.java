/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color.editors;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.zoom.componentcopier.GenericCompCopier;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TableCellEditorColorInversor implements TableCellEditor
{
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TableCellEditorColorInversor.class);

	protected TableCellEditor _origEditor;
	protected ColorInversor _colorInversor;
	protected BaseApplicationConfigurationInterface _appliConf;

	public TableCellEditorColorInversor( TableCellEditor origEditor,
										ColorInversor colorInversor,
										BaseApplicationConfigurationInterface appliConf)
	{
		_origEditor = origEditor;
		_colorInversor = colorInversor;
		_appliConf = appliConf;
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( GenericFunctions.instance().getAppliConf() );
	}

	protected <TC extends JTextComponent> JTextComponent cloneTextComponent( TC original )
	{
		TC result = original;
		
		try
		{
			result = (TC) original.getClass().newInstance();
			GenericCompCopier.instance().copyToNew(original, result);
		}
		catch( Exception ex )
		{
			LOGGER.error( "Error clonning JTextComponent: {}", original.getClass() );
			result = original;
		}

		return( result );
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		Component result = _origEditor.getTableCellEditorComponent( table, value,
														isSelected, row, column );
		if( result instanceof JTextComponent )
			result = cloneTextComponent((JTextComponent) result);

		zoomEditor( result );

		if( getAppliConf().isDarkModeActivated() )
			invertColors( result, isSelected, row, column );

		return( result );
	}

	protected void zoomEditor( Component result )
	{
		if( getAppliConf().getZoomFactor() != 1.0d )
			result.setFont( FontFunctions.instance().getZoomedFont(
				result.getFont(),
				getAppliConf().getZoomFactor())
			);
	}

	protected void invertColors(Component result, boolean isSelected,
							int row, int column) {
//		_colorInversor.invertSingleColorsGen(result);
		_colorInversor.invertSingleComponentColors(result);
//		_colorInversor.invertBackground(result);
//		_colorInversor.invertForeground(result);
//		if( isSelected )
//		{
//			_colorInversor.invertSelectionBackground(result);
//			_colorInversor.invertSelectionForeground(result);
//		}
	}

	@Override
	public Object getCellEditorValue() {
		return( _origEditor.getCellEditorValue() );
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return( _origEditor.isCellEditable(anEvent) );
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return( _origEditor.shouldSelectCell(anEvent) );
	}

	@Override
	public boolean stopCellEditing() {
		return( _origEditor.stopCellEditing() );
	}

	@Override
	public void cancelCellEditing() {
		_origEditor.cancelCellEditing();
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		_origEditor.addCellEditorListener(l);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		_origEditor.removeCellEditorListener(l);
	}
}
