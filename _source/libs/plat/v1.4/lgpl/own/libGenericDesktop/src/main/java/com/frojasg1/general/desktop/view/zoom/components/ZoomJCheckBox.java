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
import javax.swing.JCheckBox;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJCheckBox extends JCheckBox implements ComponentWithIconForZoomInterface,
															ColorThemeChangeableStatusBuilder,
															CanInvertIcons
{
	protected ComponentWithIconForZoomOverriden _overridenMethods = null;
	protected DoubleReference _zoomFactor = new DoubleReference( 1.0D );

	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

    public ZoomJCheckBox () {
        this(null, null, false);
    }

    /**
     * Creates an initially unselected check box with an icon.
     *
     * @param icon  the Icon image to display
     */
    public ZoomJCheckBox(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a check box with an icon and specifies whether
     * or not it is initially selected.
     *
     * @param icon  the Icon image to display
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public ZoomJCheckBox(Icon icon, boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates an initially unselected check box with text.
     *
     * @param text the text of the check box.
     */
    public ZoomJCheckBox (String text) {
        this(text, null, false);
    }

    /**
     * Creates a check box where properties are taken from the
     * Action supplied.
     *
     * @since 1.3
     */
    public ZoomJCheckBox(Action a) {
        this();
        setAction(a);
    }


    /**
     * Creates a check box with text and specifies whether
     * or not it is initially selected.
     *
     * @param text the text of the check box.
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public ZoomJCheckBox (String text, boolean selected) {
        this(text, null, selected);
    }

    /**
     * Creates an initially unselected check box with
     * the specified text and icon.
     *
     * @param text the text of the check box.
     * @param icon  the Icon image to display
     */
    public ZoomJCheckBox(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a check box with text and icon,
     * and specifies whether or not it is initially selected.
     *
     * @param text the text of the check box.
     * @param icon  the Icon image to display
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public ZoomJCheckBox (String text, Icon icon, boolean selected) {
        super(text, icon, selected);
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
		_overridenMethods.invertColors(colorInversor);
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
