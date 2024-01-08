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
package com.frojasg1.general.desktop.view.zoom.components;

import com.frojasg1.general.desktop.view.color.CanInvertIcons;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusBuilder;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableForCustomComponent;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJCheckBoxMenuItem extends JCheckBoxMenuItem implements ComponentWithIconForZoomInterface,
															ColorThemeChangeableStatusBuilder,
															CanInvertIcons
{
	protected ComponentWithIconForZoomOverriden _overridenMethods = null;
	protected DoubleReference _zoomFactor = new DoubleReference( 1.0D );

	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

   /**
     * Creates an initially unselected check box menu item with no set text or icon.
     */
    public ZoomJCheckBoxMenuItem() {
        this(null, null, false);
    }

    /**
     * Creates an initially unselected check box menu item with an icon.
     *
     * @param icon the icon of the CheckBoxMenuItem.
     */
    public ZoomJCheckBoxMenuItem(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates an initially unselected check box menu item with text.
     *
     * @param text the text of the CheckBoxMenuItem
     */
    public ZoomJCheckBoxMenuItem(String text) {
        this(text, null, false);
    }

    /**
     * Creates a menu item whose properties are taken from the
     * Action supplied.
     *
     * @since 1.3
     */
    public ZoomJCheckBoxMenuItem(Action a) {
        this();
        setAction(a);
    }

    /**
     * Creates an initially unselected check box menu item with the specified text and icon.
     *
     * @param text the text of the CheckBoxMenuItem
     * @param icon the icon of the CheckBoxMenuItem
     */
    public ZoomJCheckBoxMenuItem(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a check box menu item with the specified text and selection state.
     *
     * @param text the text of the check box menu item.
     * @param b the selected state of the check box menu item
     */
    public ZoomJCheckBoxMenuItem(String text, boolean b) {
        this(text, null, b);
    }

    /**
     * Creates a check box menu item with the specified text, icon, and selection state.
     *
     * @param text the text of the check box menu item
     * @param icon the icon of the check box menu item
     * @param b the selected state of the check box menu item
     */
    public ZoomJCheckBoxMenuItem(String text, Icon icon, boolean b) {
        super(text, icon, b);
		_colorThemeStatus = createColorThemeChangeableStatus();
    }

	@Override
	public void initBeforeCopyingAttributes()
	{
		if( _overridenMethods == null )
			_overridenMethods = createOverridenMethodsObject();
	}

	protected ComponentWithIconForZoomOverriden createOverridenMethodsObject()
	{
		return( new ComponentWithIconForZoomOverriden( this, _zoomFactor ) );
	}

	@Override
	public void setUI( ComponentUI ui )
	{
		super.setUI( ui );
	}

	@Override
	public Icon getDisabledIcon()
	{
		return( _overridenMethods.getDisabledIcon() );
	}

	@Override
	public Icon getDisabledSelectedIcon()
	{
		return( _overridenMethods.getDisabledSelectedIcon() );
	}

	@Override
	public Icon getIcon()
	{
		return( _overridenMethods.getIcon() );
	}

	@Override
	public Icon getPressedIcon()
	{
		return( _overridenMethods.getPressedIcon() );
	}

	@Override
	public Icon getRolloverIcon()
	{
		return( _overridenMethods.getRolloverIcon() );
	}

	@Override
	public Icon getRolloverSelectedIcon()
	{
		return( _overridenMethods.getRolloverSelectedIcon() );
	}

	@Override
	public Icon getSelectedIcon()
	{
		return( _overridenMethods.getSelectedIcon() );
	}


	@Override
	public void switchToZoomUI()
	{
		_overridenMethods.switchToZoomUI();
	}

	@Override
	public void setZoomFactor( double zoomFactor )
	{
		_zoomFactor._value = zoomFactor;

		repaint();
	}

	@Override
	public double getZoomFactor()
	{
		return( _zoomFactor._value );
	}

	@Override
	public Icon superGetDisabledIcon() {
		return( super.getDisabledIcon() );
	}

	@Override
	public Icon superGetDisabledSelectedIcon() {
		return( super.getDisabledSelectedIcon() );
	}

	@Override
	public Icon superGetIcon() {
		return( super.getIcon() );
	}

	@Override
	public Icon superGetPressedIcon() {
		return( super.getPressedIcon() );
	}

	@Override
	public Icon superGetRolloverIcon() {
		return( super.getRolloverIcon() );
	}

	@Override
	public Icon superGetRolloverSelectedIcon() {
		return( super.getRolloverSelectedIcon() );
	}

	@Override
	public Icon superGetSelectedIcon() {
		return( super.getSelectedIcon() );
	}

	@Override
	public void setZoomFactorReference(DoubleReference zoomFactor) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public DoubleReference getZoomFactorReference() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void initAfterCopyingAttributes()
	{
		
	}

	@Override
	public void paint( Graphics grp )
	{
		_colorThemeStatus.paint(grp);
	}

	@Override
	public ColorThemeChangeableForCustomComponent createColorThemeChangeableStatus()
	{
		if( _colorThemeStatus == null )
			_colorThemeStatus = new ColorThemeChangeableForCustomComponent( this, grp -> super.paint(grp) , false);
		return( _colorThemeStatus );
	}

	@Override
	public void invertColors(ColorInversor colorInversor)
	{
		// commented as it paint inverts colors if necessary
//		_overridenMethods.invertColors(colorInversor);
	}

	@Override
	public boolean canInvertIcons()
	{
		return( _overridenMethods.canInvertIcons() );
	}

	@Override
	public void setCanInvertIcons(boolean _canInvertIcons)
	{
		_overridenMethods.setCanInvertIcons( _canInvertIcons );
	}
}
