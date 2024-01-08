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

import com.frojasg1.general.CallStackFunctions;
import com.frojasg1.general.desktop.view.color.CanInvertIcons;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusBuilder;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableForCustomComponent;
import com.frojasg1.general.desktop.view.zoom.ResizeSizeComponent;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.reflection.ReflectionFunctions;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;
import static javax.swing.SwingConstants.EAST;
import static javax.swing.SwingConstants.NORTH;
import static javax.swing.SwingConstants.SOUTH;
import static javax.swing.SwingConstants.WEST;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalScrollButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomMetalScrollButton_forScrollBar extends MetalScrollButton
												implements ComponentWithIconForZoomInterface,
															ColorThemeChangeableStatusBuilder,
															ResizeSizeComponent,
															CanInvertIcons
{
	protected ComponentWithIconForZoomOverriden _overridenMethods = null;
	protected DoubleReference _zoomFactor = new DoubleReference( 1.0D );

	protected int _originalButtonWidth;

	protected boolean _freeStanding = false;

	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

	protected int _buttonWidth;

	protected static Color _shadowColor;
	protected static Color _highlightColor;

//	JScrollBar jcb;
//	JScrollPane jcp;

	public ZoomMetalScrollButton_forScrollBar(int direction, int width, boolean freeStanding )
	{
		super( direction, width, freeStanding );	// it will be cloned by reflexion (and changed if needed)

		initAfterCopyingAttributes();
		_colorThemeStatus = createColorThemeChangeableStatus();
		_buttonWidth = width;
		_shadowColor = UIManager.getColor("ScrollBar.darkShadow");
		_highlightColor = UIManager.getColor("ScrollBar.highlight");
		_freeStanding = freeStanding;
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
//		_overridenMethods.switchToZoomUI();
	}

	@Override
	public void setZoomFactor( double zoomFactor )
	{
		_zoomFactor._value = zoomFactor;

		setButtonWidth( IntegerFunctions.zoomValueCeil(_originalButtonWidth, zoomFactor) );

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
		_originalButtonWidth = getButtonWidth();
	}

	protected void setButtonWidth( int value )
	{
//		boolean success = ReflectionFunctions.instance().setAttribute( "buttonWidth", this,
//																		MetalScrollButton.class,
//																		value );
		_buttonWidth = value;
/*
		if( ! success )
		{
			try
			{
				throw( new RuntimeException( "Error updating private field of MetalScrollButton. " +
												"Probably this field has changed between versions of java" ) );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
*/
	}

	@Override
	public void setFreeStanding(boolean value)
	{
		_freeStanding = value;
		super.setFreeStanding(value);
	}

	protected boolean isFreeStanding( )
	{
//		return( getRefection( "isFreeStanding", Boolean.class ) );
		return( _freeStanding );
	}

	

	protected Color getShadowColor( )
	{
//		return( getRefection( "shadowColor", Color.class ) );
		return( _shadowColor );
	}

	protected Color getHighlightColor( )
	{
//		return( getRefection( "highlightColor", Color.class ) );
		return( _highlightColor );
	}
/*
	protected <CC> CC getRefection( String attributeName, Class<? extends CC> clazz )
	{
		CC result = ReflectionFunctions.instance().getAttribute( attributeName,
																clazz,
																this,
																MetalScrollButton.class);

		if( result == null )
		{
			try
			{
				throw( new RuntimeException( "Error getting private field of MetalScrollButton. " +
												"Probably this field has changed between versions of java" ) );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		return( result );
	}
*/
	@Override
	public int getButtonWidth()
	{
//		return( super.getButtonWidth() );
		return( _buttonWidth );
	}

	@Override
	public void setSize( int width, int height )
	{
//		CallStackFunctions.instance().dumpCallStack( String.format( "ZoomMetalScrollButton_forScrollBar setSize( %d, %d )", width, height ) );
		super.setSize( width, height );
	}

	@Override
	public void setSize( Dimension size )
	{
		super.setSize( size );
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
//		MetalFileChooserUI ui;
//		CallStackFunctions.instance().dumpCallStack( String.format( "ZoomMetalScrollButton_forScrollBar setBounds( %d, %d, %d, %d )",
//				xx, yy, width, height ) );
		super.setBounds( xx, yy, width, height );
	}

	public void paintInternal( Graphics g )
	{
		Color shadowColor = getShadowColor();
		Color highlightColor = getHighlightColor();
		boolean isFreeStanding = isFreeStanding();
		
		boolean leftToRight = MetalUtils.isLeftToRight(this);
		boolean isEnabled = getParent().isEnabled();

		Color arrowColor = isEnabled ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlDisabled();
		boolean isPressed = getModel().isPressed();
		int width = getWidth();
		int height = getHeight();
		int w = width;
		int h = height;
		int arrowHeight = (height+1) / 4;
		int arrowWidth = (height+1) / 2;

		if ( isPressed )
		{
			g.setColor( MetalLookAndFeel.getControlShadow() );
		}
		else
		{
			g.setColor( getBackground() );
		}

		g.fillRect( 0, 0, width, height );

		if ( getDirection() == NORTH )
		{
			if ( !isFreeStanding ) {
				height +=1;
				g.translate( 0, -1 );
				width += 2;
				if ( !leftToRight ) {
					g.translate( -1, 0 );
				}
			}

			// Draw the arrow
			g.setColor( arrowColor );
			int startY = ((h+1) - arrowHeight) / 2;
			int startX = (w / 2);

			g.translate(startX, startY);
			g.fillPolygon(new int[]{0, 1, arrowHeight + 1, -arrowHeight},
							new int[]{0, 0, arrowHeight, arrowHeight}, 4);
			g.translate(-startX, -startY);

			if (isEnabled) {
				g.setColor( highlightColor );

				if ( !isPressed )
				{
					g.drawLine( 1, 1, width - 3, 1 );
					g.drawLine( 1, 1, 1, height - 1 );
				}

				g.drawLine( width - 1, 1, width - 1, height - 1 );

				g.setColor( shadowColor );
				g.drawLine( 0, 0, width - 2, 0 );
				g.drawLine( 0, 0, 0, height - 1 );
				g.drawLine( width - 2, 2, width - 2, height - 1 );
			} else {
				MetalUtils.drawDisabledBorder(g, 0, 0, width, height+1);
			}
			if ( !isFreeStanding ) {
				height -= 1;
				g.translate( 0, 1 );
				width -= 2;
				if ( !leftToRight ) {
					g.translate( 1, 0 );
				}
			}
		}
		else if ( getDirection() == SOUTH )
		{
			if ( !isFreeStanding ) {
				height += 1;
				width += 2;
				if ( !leftToRight ) {
					g.translate( -1, 0 );
				}
			}

			// Draw the arrow
			g.setColor( arrowColor );

			int startY = (((h+1) - arrowHeight) / 2)+ arrowHeight-1;
			int startX = (w / 2);
			g.translate(startX, startY);
			g.fillPolygon(new int[]{0, 1, arrowHeight + 1, -arrowHeight},
							new int[]{0, 0, -arrowHeight, -arrowHeight}, 4);
			g.translate(-startX, -startY);

			if (isEnabled) {
				g.setColor( highlightColor );

				if ( !isPressed )
				{
					g.drawLine( 1, 0, width - 3, 0 );
					g.drawLine( 1, 0, 1, height - 3 );
				}

				g.drawLine( 1, height - 1, width - 1, height - 1 );
				g.drawLine( width - 1, 0, width - 1, height - 1 );

				g.setColor( shadowColor );
				g.drawLine( 0, 0, 0, height - 2 );
				g.drawLine( width - 2, 0, width - 2, height - 2 );
				g.drawLine( 2, height - 2, width - 2, height - 2 );
			} else {
				MetalUtils.drawDisabledBorder(g, 0,-1, width, height+1);
			}

			if ( !isFreeStanding ) {
				height -= 1;
				width -= 2;
				if ( !leftToRight ) {
					g.translate( 1, 0 );
				}
			}
		}
		else if ( getDirection() == EAST )
		{
			if ( !isFreeStanding ) {
				height += 2;
				width += 1;
			}

			// Draw the arrow
			g.setColor( arrowColor );

			int startX = (((w+1) - arrowHeight) / 2) + arrowHeight-1;
			int startY = (h / 2);

			g.translate(startX, startY);
			g.fillPolygon(new int[]{0, 0, -arrowHeight, -arrowHeight},
							new int[]{0, 1, arrowHeight + 1, -arrowHeight}, 4);
			g.translate(-startX, -startY);

			if (isEnabled) {
				g.setColor( highlightColor );

				if ( !isPressed )
				{
					g.drawLine( 0, 1, width - 3, 1 );
					g.drawLine( 0, 1, 0, height - 3 );
				}

				g.drawLine( width - 1, 1, width - 1, height - 1 );
				g.drawLine( 0, height - 1, width - 1, height - 1 );

				g.setColor( shadowColor );
				g.drawLine( 0, 0,width - 2, 0 );
				g.drawLine( width - 2, 2, width - 2, height - 2 );
				g.drawLine( 0, height - 2, width - 2, height - 2 );
			} else {
				MetalUtils.drawDisabledBorder(g,-1,0, width+1, height);
			}
			if ( !isFreeStanding ) {
				height -= 2;
				width -= 1;
			}
		}
		else if ( getDirection() == WEST )
		{
			if ( !isFreeStanding ) {
				height += 2;
				width += 1;
				g.translate( -1, 0 );
			}

			// Draw the arrow
			g.setColor( arrowColor );

			int startX = (((w+1) - arrowHeight) / 2);
			int startY = (h / 2);

			g.translate(startX, startY);
			g.fillPolygon(new int[]{0, 0, arrowHeight, arrowHeight},
						new int[]{0, 1, arrowHeight + 1, -arrowHeight}, 4);
			g.translate(-startX, -startY);

			if (isEnabled) {
				g.setColor( highlightColor );


				if ( !isPressed )
				{
					g.drawLine( 1, 1, width - 1, 1 );
					g.drawLine( 1, 1, 1, height - 3 );
				}

				g.drawLine( 1, height - 1, width - 1, height - 1 );

				g.setColor( shadowColor );
				g.drawLine( 0, 0, width - 1, 0 );
				g.drawLine( 0, 0, 0, height - 2 );
				g.drawLine( 2, height - 2, width - 1, height - 2 );
			} else {
				MetalUtils.drawDisabledBorder(g,0,0, width+1, height);
			}

			if ( !isFreeStanding ) {
				height -= 2;
				width -= 1;
				g.translate( 1, 0 );
			}
		}
	}

	@Override
	public void setBackground( Color color )
	{
		super.setBackground(color);
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
			_colorThemeStatus = new ColorThemeChangeableForCustomComponent( this, grp -> paintInternal(grp), false );
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

	@Override
	public Dimension getPreferredSize()
	{
		int buttonWidth = getButtonWidth();
		boolean isFreeStanding = isFreeStanding();

		if ( getDirection() == NORTH )
		{
			return new Dimension( buttonWidth, buttonWidth - 2 );
		}
		else if ( getDirection() == SOUTH )
		{
			return new Dimension( buttonWidth, buttonWidth - (isFreeStanding ? 1 : 2) );
		}
		else if ( getDirection() == EAST )
		{
			return new Dimension( buttonWidth - (isFreeStanding ? 1 : 2), buttonWidth );
		}
		else if ( getDirection() == WEST )
		{
			return new Dimension( buttonWidth - 2, buttonWidth );
		}
		else
		{
			return new Dimension( 0, 0 );
		}
	}
}
