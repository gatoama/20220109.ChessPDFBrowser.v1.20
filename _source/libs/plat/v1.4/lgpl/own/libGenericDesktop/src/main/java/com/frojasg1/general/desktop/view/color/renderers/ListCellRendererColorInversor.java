/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color.renderers;

import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.combobox.renderer.ComboCellRendererBase;
import com.frojasg1.general.desktop.view.zoom.componentcopier.GenericCompCopier;
import com.frojasg1.general.reflection.ReflectionFunctions;
import java.awt.Color;
import java.awt.Component;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListCellRendererColorInversor implements ListCellRenderer
{
	protected ListCellRenderer _originalRenderer;
	protected ColorInversor _colorInversor;

	public ListCellRendererColorInversor( ListCellRenderer originalRenderer,
										ColorInversor colorInversor )
	{
		_originalRenderer = originalRenderer;
		_colorInversor = colorInversor;
//		invertOriginalBackground();
	}

	protected JLabel cloneLabel( JLabel label )
	{
		JLabel result = new JLabel();
		GenericCompCopier.instance().copyToNew(label, result);
		return( result );
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
												int index, boolean isSelected,
												boolean cellHasFocus)
	{
		JList jl;
		Component result = _originalRenderer.getListCellRendererComponent( list, value,
														index, isSelected, cellHasFocus );

		Color background = result.getBackground();
		Color systemSelectionBackground = UIManager.getColor("ComboBox.selectionBackground");
		boolean hasToChangeBackground = ( !isSelected && hasBeenChangedBackground( background ) ||
			isSelected && !Objects.equals( background, systemSelectionBackground )
			 );

		Color foreground = result.getForeground();
		Color systemSelectionForeground = UIManager.getColor("ComboBox.selectionForeground");
		boolean hasToChangeForeground = ( !isSelected && hasBeenChangedForeground( foreground ) ||
			isSelected && !Objects.equals( foreground, systemSelectionForeground)
			 );
		
		if( (hasToChangeBackground || hasToChangeForeground) &&
			( result instanceof JLabel ) )
			result = cloneLabel((JLabel) result);

		if( hasToChangeBackground)
			_colorInversor.invertBackground(result);

		if( hasToChangeForeground )
			_colorInversor.invertForeground(result);

		if( isSelected && ( index == -1 ) )
		{
			if( _colorInversor.areInverse( systemSelectionBackground,
											list.getSelectionBackground() ) )
				list.setSelectionBackground(systemSelectionBackground);

			if( _colorInversor.areInverse( systemSelectionForeground,
											list.getSelectionForeground() ) )
				list.setSelectionForeground(systemSelectionForeground);
		}

		return( result );
	}

	protected <CC> CC reflectionGet( Object obj, String getterFunctionName )
	{
		return( (CC) ReflectionFunctions.instance().invokeMethod(getterFunctionName, obj) );
	}

	protected boolean hasBeenChangedSelectionBackground( Component result, Color color )
	{
		return( !equalsOriginalColor( color, reflectionGet(result, "getSelectionBackground" ) ) );
	}

	protected boolean hasBeenChangedSelectionForeground( Component result, Color color )
	{
		return( !equalsOriginalColor( color, reflectionGet(result, "getSelectionForeground" ) ) );
	}

	protected boolean hasBeenChangedBackground( Color color )
	{
		return( !equalsOriginalColor( color, ComboCellRendererBase::getOriginalBackground ) );
	}

	protected boolean hasBeenChangedForeground( Color color )
	{
		return( !equalsOriginalColor( color, ComboCellRendererBase::getOriginalForeground ) );
	}

	protected boolean equalsOriginalColor( Color color, Function<ComboCellRendererBase, Color> getter )
	{
		Color originalColor = NullFunctions.instance().getIfNotNull(
			ClassFunctions.instance().cast(_originalRenderer, ComboCellRendererBase.class), getter );

		return( ( originalColor == null ) || Objects.equals( originalColor, color ) );
	}

	public void invertOriginalBackground()
	{
		_colorInversor.invertBackground( _originalRenderer );
	}

	public ListCellRenderer getOriginalListCellRenderer()
	{
		return( _originalRenderer );
	}

	public void setOriginalListCellRenderer(ListCellRenderer listCellRenderer)
	{
		_originalRenderer = listCellRenderer;
	}
}
