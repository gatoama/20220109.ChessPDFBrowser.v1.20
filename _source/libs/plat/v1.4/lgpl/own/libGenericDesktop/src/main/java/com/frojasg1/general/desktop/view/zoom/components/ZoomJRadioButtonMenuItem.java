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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJRadioButtonMenuItem extends JRadioButtonMenuItem
										implements ComponentWithIconForZoomInterface,
													ColorThemeChangeableStatusBuilder,
													CanInvertIcons
{
	protected ComponentWithIconForZoomOverriden _overridenMethods = null;
	protected DoubleReference _zoomFactor = new DoubleReference( 1.0D );

	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

    /**
     * Creates a <code>JRadioButtonMenuItem</code> with no set text or icon.
     */
    public ZoomJRadioButtonMenuItem() {
        this(null, null, false);
    }

    /**
     * Creates a <code>JRadioButtonMenuItem</code> with an icon.
     *
     * @param icon the <code>Icon</code> to display on the
     *          <code>JRadioButtonMenuItem</code>
     */
    public ZoomJRadioButtonMenuItem(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a <code>JRadioButtonMenuItem</code> with text.
     *
     * @param text the text of the <code>JRadioButtonMenuItem</code>
     */
    public ZoomJRadioButtonMenuItem(String text) {
        this(text, null, false);
    }

    /**
     * Creates a radio button menu item whose properties are taken from the
     * <code>Action</code> supplied.
     *
     * @param  a the <code>Action</code> on which to base the radio
     *          button menu item
     *
     * @since 1.3
     */
    public ZoomJRadioButtonMenuItem(Action a) {
        this();
        setAction(a);
    }

    /**
     * Creates a radio button menu item with the specified text
     * and <code>Icon</code>.
     *
     * @param text the text of the <code>JRadioButtonMenuItem</code>
     * @param icon the icon to display on the <code>JRadioButtonMenuItem</code>
     */
    public ZoomJRadioButtonMenuItem(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a radio button menu item with the specified text
     * and selection state.
     *
     * @param text the text of the <code>CheckBoxMenuItem</code>
     * @param selected the selected state of the <code>CheckBoxMenuItem</code>
     */
    public ZoomJRadioButtonMenuItem(String text, boolean selected) {
        this(text);
        setSelected(selected);
    }

    /**
     * Creates a radio button menu item with the specified image
     * and selection state, but no text.
     *
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public ZoomJRadioButtonMenuItem(Icon icon, boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates a radio button menu item that has the specified
     * text, image, and selection state.  All other constructors
     * defer to this one.
     *
     * @param text  the string displayed on the radio button
     * @param icon  the image that the button should display
     */
    public ZoomJRadioButtonMenuItem(String text, Icon icon, boolean selected) {
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
		breakpoint();

		_zoomFactor._value = zoomFactor;

		revalidate();

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
	public void paintComponent( Graphics gc )
	{
		super.paintComponent( gc );
	}


	@Override
	public void initAfterCopyingAttributes()
	{
		
	}

	@Override
	public void setFont( Font font )
	{
//		breakpoint();
		super.setFont( font );
	}

	@Override
	public void setMinimumSize( Dimension dimen )
	{
//		breakpoint();
		super.setMinimumSize(dimen);
	}

	@Override
	public void setMaximumSize( Dimension dimen )
	{
//		breakpoint();
		super.setMaximumSize(dimen);
	}

	@Override
	public void setPreferredSize( Dimension dimen )
	{
//		breakpoint();
		super.setPreferredSize( dimen );
	}

	@Override
	public void setSize( Dimension dimen )
	{
//		breakpoint();
		super.setSize( dimen );
	}

	@Override
	public void setBounds( Rectangle bounds )
	{
//		breakpoint();
		super.setBounds( bounds );
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
//		breakpoint();
		super.setBounds( xx, yy, width, height );
	}

	protected void breakpoint()
	{
		if( ( getText() != null ) && getText().equals( "75%" ) )
		{
			int kk=0;
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return( super.getPreferredSize() );
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
