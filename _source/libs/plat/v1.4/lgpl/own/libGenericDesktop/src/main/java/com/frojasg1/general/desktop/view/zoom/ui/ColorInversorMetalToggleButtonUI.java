/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.zoom.ui;

import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.zoom.helpers.ComponentSimpleColorInversor;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ColorInversorMetalToggleButtonUI extends MetalToggleButtonUI
{
	protected JComponent _parent;
	protected ComponentSimpleColorInversor _helper = null;

	public ColorInversorMetalToggleButtonUI()
	{
		this( ComponentSimpleColorInversor.instance() );
	}

	public ColorInversorMetalToggleButtonUI(ComponentSimpleColorInversor helper)
	{
		super();
		_helper = helper;
	}

	public void installUI(JComponent c) {
		_parent = c;

		super.installUI(c);
	}

	public static ComponentUI createUI( JComponent x ) {
        return new ColorInversorMetalToggleButtonUI();
    }


    protected Color invertColorIfNecessary( Color color ) {
		return( _helper.invertColorIfNecessary( _parent, color ) );
	}

	// https://stackoverflow.com/questions/5808022/changing-the-background-color-of-a-selected-jtogglebutton
	@Override
    protected Color getSelectColor() {
		return( invertColorIfNecessary( super.getSelectColor() ) );
    }

	@Override
    protected Color getDisabledTextColor() {
		return( invertColorIfNecessary( super.getDisabledTextColor() ) );
    }

	@Override
    protected Color getFocusColor() {
		return( invertColorIfNecessary( super.getFocusColor() ) );
    }
}
