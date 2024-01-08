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
package com.frojasg1.general.desktop.view.zoom.ui;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusBuilder;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusGetter;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableForCustomComponent;
import com.frojasg1.general.desktop.view.zoom.ZoomComponentInterface;
import com.frojasg1.general.desktop.view.zoom.components.ZoomJScrollPane;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomMetalComboBoxIcon;
import com.frojasg1.general.named.Named;
import com.frojasg1.general.reflection.ReflectionFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomMetalComboBoxUI extends MetalComboBoxUI implements Named {
    protected static Object NO_BUTTON_ROLLOVER =
        ExecutionFunctions.instance().safeFunctionExecution( () -> 
			ReflectionFunctions.instance().createInstance( "sun.swing.StringUIClientPropertyKey", String.class, "NoButtonRollover" ) );

	protected ColorThemeChangeableStatusGetter _parent;

	public ZoomMetalComboBoxUI( ColorThemeChangeableStatusGetter parent )
	{
		_parent = parent;
	}

    public static ComponentUI createUI( JComponent x ) {
		ColorThemeChangeableStatusGetter parent = null;
		if( x instanceof ColorThemeChangeableStatusGetter)
			parent = (ColorThemeChangeableStatusGetter) x;
 
		return new ZoomMetalComboBoxUI(parent);
    }

	protected ZoomMetalComboBoxIcon createMetalComboBoxIcon()
	{
		return( new ZoomMetalComboBoxIcon() );
	}

	@Override
	public String getName()
	{
		String result = null;
		if( this.popup instanceof Component )
			result = ( (Component) this.popup ).getName();
		return( result );
	}

	@Override
	public void setName( String name )
	{
		if( this.popup instanceof Component )
			( (Component) this.popup ).setName( name );
	}

	protected JButton createArrowButton() {
        boolean iconOnly = (comboBox.isEditable() ||
                            usingOcean());
		ZoomMetalComboBoxIcon icon = createMetalComboBoxIcon();
		
		ZoomMetalComboBoxButton button = new ZoomMetalComboBoxButton( comboBox,
																		icon,
																		iconOnly,
																		currentValuePane,
																		listBox );
		icon.setParentButton( button );

		if( wasLatestModeDark() )
			button.createColorThemeChangeableStatus().setDarkMode(true, null);
			
        button.setMargin( new Insets( 0, 1, 1, 3 ) );
        if (usingOcean()) {
			// TODO: update it for Java-16
			// Disabled rollover effect.
			if( NO_BUTTON_ROLLOVER != null )
				button.putClientProperty(NO_BUTTON_ROLLOVER,
										 Boolean.TRUE);
        }
        updateButtonForOcean(button);
        return button;
    }
/*
	protected Double getZoomFactor()
	{
		Double result = null;

		BaseApplicationConfigurationInterface appliConf = GenericFunctions.instance().getAppliConf();
		if( appliConf != null )
			result = appliConf.getZoomFactor();
//		if( _parent instanceof ZoomComponentInterface )
//			result = ( (ZoomComponentInterface) _parent ).getZoomFactor();

		return( result );
	}
*/
	protected boolean wasLatestModeDark()
	{
		return( _parent.wasLatestModeDark() );
	}

	private void updateButtonForOcean(JButton button) {
        if (usingOcean()) {
            // Ocean renders the focus in a different way, this
            // would be redundant.
            button.setFocusPainted(comboBox.isEditable());
        }
    }

	protected boolean usingOcean()
	{
		return( MetalLookAndFeel.getCurrentTheme() instanceof OceanTheme );
	}

	protected ComboPopup createPopup() {
        return new BasicComboPopup( comboBox ) {

			protected ZoomJScrollPane createZoomedScroller(double zoomFactor) {
				ZoomJScrollPane sp = new ZoomJScrollPane( list,
										ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
										ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
				sp.switchToZoomUI();
				((ZoomComponentInterface) sp).setZoomFactor(zoomFactor);
				sp.setHorizontalScrollBar(null);
				sp.setName( comboBox.getName() );
				return sp;
			}

			protected JScrollPane createScroller() {
				JScrollPane result = null;

				Double zoomFactor = 1.0D; //getZoomFactor();
				if( zoomFactor != null )
					result = createZoomedScroller(zoomFactor);
				else
					result = super.createScroller();

				return result;
			}

			@Override
			public void setSize( int width, int height )
			{
				super.setSize( width, height );
			}

			@Override
			public void setSize( Dimension newSize )
			{
				super.setSize( newSize );
			}

			@Override
			public void setBounds( Rectangle newBounds )
			{
				super.setBounds( newBounds );
			}

			@Override
			public void setBounds( int xx, int yy, int width, int height )
			{
				super.setBounds( xx, yy, width, height );
			}

			@Override
			public void setPreferredSize( Dimension dimen )
			{
				super.setPreferredSize( dimen );
			}

			@Override
			public void setName( String name )
			{
				super.setName(name);
				if( this.scroller != null )
					this.scroller.setName(name);
			}

			@Override
			public String getName()
			{
				String result = null;
				if( this.scroller != null )
					result = this.scroller.getName();
				else
					result = super.getName();

				return( result );
			}
		};
    }

    public PropertyChangeListener createPropertyChangeListener() {
        return new ZoomMetalPropertyChangeListener();
    }

    /**
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of {@code MetalComboBoxUI}.
     */
    public class ZoomMetalPropertyChangeListener extends MetalPropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();

			Color previous = null;
			if( propertyName.equals( "background" ) )
				previous = arrowButton.getBackground();
			else if( propertyName.equals( "foreground" ) )
				previous = arrowButton.getForeground();

			super.propertyChange( e );

			if( propertyName.equals( "background" ) )
				arrowButton.setBackground(previous);
			else if( propertyName.equals( "foreground" ) )
				arrowButton.setForeground(previous);
		}
    }

	public class ZoomMetalComboBoxButton extends MetalComboBoxButton
												implements ColorThemeChangeableStatusBuilder,
															ColorThemeInvertible
	{
		protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

		public ZoomMetalComboBoxButton( JComboBox cb, Icon i,
									CellRendererPane pane, JList list ) {
			super(cb, i, pane, list);
			init();
		}

		public ZoomMetalComboBoxButton( JComboBox cb, Icon i, boolean onlyIcon,
									CellRendererPane pane, JList list ) {
			super( cb, i, onlyIcon, pane, list );
			init();
		}

		protected void init()
		{
			_colorThemeStatus = createColorThemeChangeableStatus();
//			initModel();

			this.setUI( new MetalButtonUI() {
				@Override
				protected Color getSelectColor() {
					Color result = super.getSelectColor();
					if( _parent.isDarkMode() )
						result = FrameworkComponentFunctions.instance().getColorInversor(ZoomMetalComboBoxButton.this)
							.invertColor(result);

					return result;
				}
			});
		}
/*
		protected void initModel()
		{
			DefaultButtonModel model = new DefaultButtonModel() {

				@Override
				public void setArmed( boolean armed ) {
					super.setArmed( isPressed() ? true : armed );
					SwingUtilities.invokeLater( ZoomMetalComboBoxButton.this::repaint );
				}

				@Override
				public void setPressed( boolean value ) {
					super.setPressed( value );
					SwingUtilities.invokeLater( ZoomMetalComboBoxButton.this::repaint );
				}
			};
			setModel( model );
		}
*/

		@Override
		public ColorThemeChangeableForCustomComponent createColorThemeChangeableStatus()
		{
			if( _colorThemeStatus == null )
				_colorThemeStatus = new ColorThemeChangeableForCustomComponent( this, grp -> super.paint(grp) , false)
					{
						@Override
						protected BufferedImage invertImage( BufferedImage image )
						{
							BufferedImage result;
							ColorInversor ci = FrameworkComponentFunctions.instance().getColorInversor( getComponent() );
							if( getComponent().isEnabled() )
								result = ci.invertImage( image );
							else
								result = ci.putOutImage(image, 0.5);

							return( result );
						}
					};
			return( _colorThemeStatus );
		}

		@Override
		public void paint(Graphics grp) {
			_colorThemeStatus.paint(grp);
		}

		@Override
		public void invertColors(ColorInversor colorInversor)
		{
			// Intentionally left blank
		}

		@Override
		public void setBackground( Color color )
		{
			super.setBackground( color );
		}
	}
}
