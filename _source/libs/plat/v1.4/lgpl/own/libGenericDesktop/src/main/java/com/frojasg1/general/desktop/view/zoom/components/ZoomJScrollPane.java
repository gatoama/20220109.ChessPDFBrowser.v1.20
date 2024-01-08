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

import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusBuilder;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;
import com.frojasg1.general.desktop.view.scrollpane.ZoomJScrollPaneFunctions;
import com.frojasg1.general.desktop.view.zoom.ZoomComponentInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.CustomComponent;
import com.frojasg1.general.desktop.view.zoom.ui.ZoomMetalScrollBarUI;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJScrollPane extends JScrollPane implements CustomComponent,
	ColorThemeChangeableStatusBuilder {

	protected DoubleReference _zoomFactor = new DoubleReference(1.0D);

	protected int _originalHorizontalPreferredScrollBarHeight = -1;
	protected int _originalVerticalPreferredScrollBarWidth = -1;

	protected ColorThemeChangeableBase _colorThemeStatus;
//	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

	/**
	 * Creates a <code>JScrollPane</code> that displays the view component in a
	 * viewport whose view position can be controlled with a pair of scrollbars.
	 * The scrollbar policies specify when the scrollbars are displayed, For
	 * example, if <code>vsbPolicy</code> is
	 * <code>VERTICAL_SCROLLBAR_AS_NEEDED</code> then the vertical scrollbar
	 * only appears if the view doesn't fit vertically. The available policy
	 * settings are listed at {@link #setVerticalScrollBarPolicy} and
	 * {@link #setHorizontalScrollBarPolicy}.
	 *
	 * @see #setViewportView
	 *
	 * @param view the component to display in the scrollpanes viewport
	 * @param vsbPolicy an integer that specifies the vertical scrollbar policy
	 * @param hsbPolicy an integer that specifies the horizontal scrollbar
	 * policy
	 */
	public ZoomJScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
		_colorThemeStatus = createColorThemeChangeableStatus();

		changeUI(getHorizontalScrollBar());
		changeUI(getVerticalScrollBar());
	}

	/**
	 * Creates a <code>JScrollPane</code> that displays the contents of the
	 * specified component, where both horizontal and vertical scrollbars appear
	 * whenever the component's contents are larger than the view.
	 *
	 * @see #setViewportView
	 * @param view the component to display in the scrollpane's viewport
	 */
	public ZoomJScrollPane(Component view) {
		this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	/**
	 * Creates an empty (no viewport view) <code>JScrollPane</code> with
	 * specified scrollbar policies. The available policy settings are listed at
	 * {@link #setVerticalScrollBarPolicy} and
	 * {@link #setHorizontalScrollBarPolicy}.
	 *
	 * @see #setViewportView
	 *
	 * @param vsbPolicy an integer that specifies the vertical scrollbar policy
	 * @param hsbPolicy an integer that specifies the horizontal scrollbar
	 * policy
	 */
	public ZoomJScrollPane(int vsbPolicy, int hsbPolicy) {
		this(null, vsbPolicy, hsbPolicy);
	}

	/**
	 * Creates an empty (no viewport view) <code>JScrollPane</code> where both
	 * horizontal and vertical scrollbars appear when needed.
	 */
	public ZoomJScrollPane() {
		this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	@Override
	public void setZoomFactor(double zoomFactor) {
		try {
			JScrollBar hsb = getHorizontalScrollBar();
			hsb = zoomScrollBar(hsb, zoomFactor);
			if (hsb != null) {
				setHorizontalScrollBar(hsb);
			}

			JScrollBar vsb = getVerticalScrollBar();
			vsb = zoomScrollBar(vsb, zoomFactor);
			if (vsb != null) {
				setVerticalScrollBar(vsb);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		_zoomFactor._value = zoomFactor;

		for (int ii = 0; ii < getComponentCount(); ii++) {
			if (getComponent(ii) == null) {
				System.out.println("null Component");
			}
		}

		repaint();
	}

	protected ColorInversor getColorInversor()
	{
		return( FrameworkComponentFunctions.instance().getColorInversor( this ) );
	}

	protected boolean wasLatestModeDark() {
		return ((_colorThemeStatus != null) && _colorThemeStatus.wasLatestModeDark());
	}

	protected boolean isDarkMode() {
		return ((_colorThemeStatus != null) && _colorThemeStatus.isDarkMode());
	}

	protected void forceChangeUI(JScrollBar sb) {
		ZoomJScrollPaneFunctions.instance().forceChangeUI(sb, isDarkMode(), wasLatestModeDark());
	}

	protected void changeUI(JScrollBar sb) {
		if (getScrollBarUI(sb) == null) {
			forceChangeUI(sb);
		}
	}

	/*
	protected void setUIname( JScrollBar sb )
	{
		ZoomMetalScrollBarUI ui = getScrollBarUI( sb );
		if( ui != null )
			ui.setScrollPane(this);
	}

	@Override
	public void setName( String name )
	{
		super.setName( name );
		setUIname( getHorizontalScrollBar() );
		setUIname( getVerticalScrollBar() );
	}
	 */

	protected JScrollBar zoomScrollBar(JScrollBar sb, double zoomFactor) {
		JScrollBar result = null;

		if (sb != null) {
			if (zoomFactor != _zoomFactor._value) {
				if (sb.getOrientation() == JScrollBar.HORIZONTAL) {
					result = zoomHorizontalScrollBar(sb, zoomFactor);
				} else if (sb.getOrientation() == JScrollBar.VERTICAL) {
					result = zoomVerticalScrollBar(sb, zoomFactor);
				}
			}
		}

		return (result);
	}

	protected JScrollBar zoomHorizontalScrollBar(JScrollBar hsb, double zoomFactor) {
		JScrollBar result = null;

		if (hsb != null) {
			if (_originalHorizontalPreferredScrollBarHeight < 0) {
				_originalHorizontalPreferredScrollBarHeight = hsb.getPreferredSize().height;
			}

			Dimension size = hsb.getPreferredSize();
//			result = super.createHorizontalScrollBar();
			result = createHorizontalScrollBar();
			result.setPreferredSize(new Dimension(size.width,
				IntegerFunctions.zoomValueCeil(_originalHorizontalPreferredScrollBarHeight, zoomFactor)
			)
			);
		}

		return (result);
	}

	@Override
	public void setHorizontalScrollBar(JScrollBar sb) {
		super.setHorizontalScrollBar(sb);
	}
/*
	protected JScrollBar copyScrollBar( JScrollBar original, int orientation )
	{
		JScrollBar result = new ScrollBar( orientation );
		if( original != null )
		{
			result.setBackground( noResourceColor( result.getBackground() ) );
			result.setForeground( noResourceColor( result.getForeground() ) );
		}
		forceChangeUI(result);

//		if( isDarkMode() )
//			FrameworkComponentFunctions.instance().getColorInversor(this).invertSingleColorsGen(result);

		return( result );
	}
*/

	protected void updateScrollBarUi(JScrollBar originalSb, JScrollBar newSb)
	{
		ZoomJScrollPaneFunctions.instance().updateScrollBarUi(originalSb, newSb,
															isDarkMode(),
															wasLatestModeDark());
	}

	@Override
	public JScrollBar createHorizontalScrollBar() {
//		JScrollBar result = updateHorizontalScrollBarUi();
		ScrollBar result = new ScrollBar(JScrollBar.HORIZONTAL);
		updateScrollBarUi( getHorizontalScrollBar(), result );
//		result.switchToZoomUI();
//		JScrollBar result = super.createHorizontalScrollBar();
		
//		changeUI(result);
//		applyColorMode(result);

		return (result);
	}

	@Override
	public JScrollBar createVerticalScrollBar() {
//		JScrollBar result = updateVerticalScrollBarUi( this );
		ScrollBar result = new ScrollBar(JScrollBar.VERTICAL);
		updateScrollBarUi( getVerticalScrollBar(), result );
//		result.switchToZoomUI();
//		JScrollBar result = super.createVerticalScrollBar();

//		changeUI(result);
//		applyColorMode(result);

		return (result);
	}
/*
	protected void applyColorMode( JScrollBar sb )
	{
		if( isDarkMode() )
			FrameworkComponentFunctions.instance().getColorInversor(this).invertSingleColorsGen(sb);
		changeUI(sb);
	}
*/
	protected JScrollBar zoomVerticalScrollBar(JScrollBar vsb, double zoomFactor) {
		JScrollBar result = null;

		if (vsb != null) {
			if (_originalVerticalPreferredScrollBarWidth < 0) {
				_originalVerticalPreferredScrollBarWidth = vsb.getPreferredSize().width;
			}

			Dimension size = vsb.getPreferredSize();
//			result = super.createVerticalScrollBar();
			result = createVerticalScrollBar();
			result.setPreferredSize(new Dimension(
				IntegerFunctions.zoomValueCeil(_originalVerticalPreferredScrollBarWidth, zoomFactor),
				size.width)
			);
		}

		return (result);
	}

	@Override
	public void switchToZoomUI() {

	}

	@Override
	public void setUI(ComponentUI ui) {
		super.setUI(ui);
	}

	@Override
	public double getZoomFactor() {
		return (_zoomFactor._value);
	}

	@Override
	public void setZoomFactorReference(DoubleReference zoomFactor) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public DoubleReference getZoomFactorReference() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	protected void initBeforeCopyingAttributes(JScrollBar sb) {
		if (sb instanceof ZoomComponentInterface) {
			ZoomComponentInterface zci = (ZoomComponentInterface) sb;
			zci.initBeforeCopyingAttributes();
		}
	}

	@Override
	public void initBeforeCopyingAttributes() {
		initBeforeCopyingAttributes(getHorizontalScrollBar());
		initBeforeCopyingAttributes(getVerticalScrollBar());
	}

	protected void initAfterCopyingAttributes(JScrollBar sb) {
		if (sb instanceof ZoomComponentInterface) {
			ZoomComponentInterface zci = (ZoomComponentInterface) sb;
			zci.initAfterCopyingAttributes();
		}
	}

	@Override
	public void initAfterCopyingAttributes() {
		initAfterCopyingAttributes(getHorizontalScrollBar());
		initAfterCopyingAttributes(getVerticalScrollBar());
	}

	/*
	@Override
	public void paint( Graphics grp )
	{
		_colorThemeStatus.paint(grp);
	}
	 */
	@Override
	public ColorThemeChangeableBase createColorThemeChangeableStatus() {
		if (_colorThemeStatus == null) //			_colorThemeStatus = new ColorThemeChangeableForCustomComponent( this, grp -> super.paint(grp) , false);
		{
			_colorThemeStatus = new ColorThemeChangeableBase();
		}

		return (_colorThemeStatus);
	}

	@Override
	public void invertColors(ColorInversor colorInversor) {
//		colorInversor.invertSingleColorsGen( getViewport() );
		colorInversor.invertSingleColorsGen( this );
		invertColorsOfScrollBarFromInvertColors(colorInversor, this.getHorizontalScrollBar());
		invertColorsOfScrollBarFromInvertColors(colorInversor, this.getVerticalScrollBar());
	}

	protected ZoomMetalScrollBarUI getScrollBarUI(JScrollBar sb) {
		ZoomMetalScrollBarUI result = null;
		if (sb != null) {
			ComponentUI ui = sb.getUI();
			if (ui instanceof ZoomMetalScrollBarUI) {
				result = (ZoomMetalScrollBarUI) ui;
			}
		}

		return (result);
	}

	protected void invertColorsOfScrollBarFromInvertColors(ColorInversor colorInversor, JScrollBar sb ) {
//		if( getScrollBarUI(sb) != null )
//			getScrollBarUI(sb).invertColors(colorInversor);

//		boolean invertColorsGen = ( colorInversor.getColorThemeChangeableGetter( this, null ).apply( sb ) == null );
		invertColorsOfScrollBarInternal( colorInversor, sb );
	}

	protected void invertColorsOfScrollBarInternal(ColorInversor colorInversor, JScrollBar sb ) {
//		if( invertColorsGen )
//			colorInversor.invertSingleColorsGen(sb);
//		ZoomMetalScrollBarUI ui = getScrollBarUI( sb );
//		if( ui != null )
//			ui.invertColors(colorInversor);
		forceChangeUI(sb);
	}


	@Override
	public void setBackground( Color color )
	{
		super.setBackground( color );
	}


	public class ScrollBar extends JScrollPane.ScrollBar
//		implements ZoomComponentInterface,
//		ResizeSizeComponent,
//		ColorThemeInvertible //											ColorThemeChangeableStatusBuilder
	{

//		protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

		/**
		 * Creates a scrollbar with the specified orientation. The options are:
		 * <ul>
		 * <li><code>ScrollPaneConstants.VERTICAL</code>
		 * <li><code>ScrollPaneConstants.HORIZONTAL</code>
		 * </ul>
		 *
		 * @param orientation an integer specifying one of the legal orientation
		 * values shown above
		 * @since 1.4
		 */
		public ScrollBar(int orientation) {
			super(orientation);
//			_colorThemeStatus = createColorThemeChangeableStatus();
		}
		
		@Override
		public void setBackground( Color color )
		{
			super.setBackground(color);
		}
/*
		@Override
		public void initBeforeCopyingAttributes() {
		}

		@Override
		public void initAfterCopyingAttributes() {
			ComponentUI ui = getUI();
			if (ui instanceof ZoomMetalScrollBarUI) {
				ZoomMetalScrollBarUI zmsbui = (ZoomMetalScrollBarUI) ui;
				zmsbui.initAfterCopyingAttributes();
			}
		}

		@Override
		public void switchToZoomUI() {
			ZoomFunctions.instance().switchToZoomUI(this, _zoomFactor);
		}

		@Override
		public void setZoomFactorReference(DoubleReference zoomFactor) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setZoomFactor(double zoomFactor) {
		}

		@Override
		public double getZoomFactor() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public DoubleReference getZoomFactorReference() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setUI(ComponentUI ui) {
			if (ui instanceof ScrollBarUI) {
				super.setUI((ScrollBarUI) ui);
			} else {
				super.setUI(ui);
			}
		}

		@Override
		public void paint(Graphics grp) {
//			_colorThemeStatus.paint(grp);
			super.paint(grp);
		}

//		@Override
//		public ColorThemeChangeableForCustomComponent createColorThemeChangeableStatus()
//		{
//			if( _colorThemeStatus == null )
//				_colorThemeStatus = new ColorThemeChangeableForCustomComponent( this, grp -> super.paint(grp) , false);
//			return( _colorThemeStatus );
//		}

		@Override
		public void setBackground( Color color )
		{
			super.setBackground( color );
		}

		@Override
		public void invertColors(ColorInversor colorInversor) {
			colorInversor.invertSingleColorsGen(this);

			ComponentUI ui = getUI();
			if (ui instanceof ZoomMetalScrollBarUI) {
				ZoomMetalScrollBarUI zmsbui = (ZoomMetalScrollBarUI) ui;
				zmsbui.invertColors(colorInversor);
			}
		}
*/
	}

}
