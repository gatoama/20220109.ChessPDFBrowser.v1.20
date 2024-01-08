/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.zoom.filechooser;

import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;
import com.frojasg1.general.desktop.view.scrollpane.ZoomJScrollPaneFunctions;
import com.frojasg1.general.desktop.view.zoom.components.ZoomJComboBox;
import com.frojasg1.general.desktop.view.zoom.components.ZoomJScrollPane;
import com.frojasg1.general.desktop.view.zoom.ui.ColorInversorMetalToggleButtonUI;
import com.frojasg1.general.desktop.view.zoom.ui.ZoomMetalButtonUI;
import com.frojasg1.general.desktop.view.zoom.ui.ZoomMetalComboBoxUI;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ConvertDialogComponents {
	
	protected static ConvertDialogComponents INSTANCE = new ConvertDialogComponents();

	public static ConvertDialogComponents instance()
	{
		return( INSTANCE );
	}

	public void changeToColorInvertibleComponents( Component comp, boolean isDarkMode )
	{
		ComponentFunctions.instance().browseComponentHierarchy(comp,
			comp2 -> this.processComponentToChangeToColorInvertible(comp2, isDarkMode) );
	}

	protected Component processComponentToChangeToColorInvertible( Component comp,
																	boolean isDarkMode )
	{
		if( ( comp instanceof JComboBox ) && !( comp instanceof ZoomJComboBox ) )
			convertComboBox( (JComboBox) comp, isDarkMode );
		else if( ( comp instanceof JScrollPane ) && !( comp instanceof ZoomJScrollPane ) )
			convertScrollPane( (JScrollPane) comp, isDarkMode );
		else if( comp instanceof JToggleButton )
			convertToggleButton( (JToggleButton) comp );
		else if( ( comp instanceof JButton ) )
			convertJButton( (JButton) comp );

		return( null );
	}

	protected void convertToggleButton( JToggleButton comp )
	{
		comp.setUI( new ColorInversorMetalToggleButtonUI() );
	}

	protected void convertJButton( JButton comp )
	{
		comp.setUI( new ZoomMetalButtonUI() );
	}

	protected void convertComboBox( JComboBox combo, boolean isDarkMode )
	{
//		if( isDarkMode )
		{
			ColorInversor ci = getColorInversor( combo );
			ColorThemeChangeableBase colorThemeChangeable = new ColorThemeChangeableBase();
			colorThemeChangeable.setDarkMode(isDarkMode, ci);
			colorThemeChangeable.setLatestWasDark(isDarkMode);
			combo.setUI( new ZoomMetalComboBoxUI(colorThemeChangeable) );
			ComponentFunctions.instance().browseComponentHierarchy(combo,
				comp2 ->{ if(comp2 instanceof JList) {
						JList jl = (JList) comp2;
						if(isDarkMode) {
							ci.invertColor(jl.getSelectionBackground(), jl::setSelectionBackground );
							ci.invertColor(jl.getSelectionForeground(), jl::setSelectionForeground );
						}
					}
					return null;
				});
		}
	}

	protected ColorInversor getColorInversor( Component comp )
	{
		return( FrameworkComponentFunctions.instance().getColorInversor(comp) );
	}

	public void convertScrollPane( JScrollPane sp, boolean isDarkMode )
	{
		if( isDarkMode )
		{
			ZoomJScrollPaneFunctions.instance().updateHorizontalScrollBarUi(sp, true, false);
			ZoomJScrollPaneFunctions.instance().updateVerticalScrollBarUi(sp, true, false);
		}
	}
}
