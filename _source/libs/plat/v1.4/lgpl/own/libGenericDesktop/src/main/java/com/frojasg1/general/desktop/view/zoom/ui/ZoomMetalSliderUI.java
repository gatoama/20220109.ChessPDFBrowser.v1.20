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

import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.lookAndFeel.ToolTipLookAndFeel;
import com.frojasg1.general.desktop.lookAndFeel.ToolTipMetalOceanTheme;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.layers.ZoomJLayerUI;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalSliderUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomMetalSliderUI extends MetalSliderUI implements ComponentUIforZoomInterface
{
	protected DoubleReference _zoomFactor = null;

	public ZoomMetalSliderUI()
	{
		super();
	}

    public static ComponentUI createUI( JComponent x ) {
        return new ZoomMetalSliderUI();
    }

	@Override
	public void init()
	{}

	@Override
	public void setZoomFactorReference( DoubleReference zoomFactor )
	{
		_zoomFactor = zoomFactor;
	}

	@Override
	public void setZoomFactor(double zoomFactor)
	{
		_zoomFactor._value = zoomFactor;
	}

	@Override
	public double getZoomFactor()
	{
		return( _zoomFactor._value );
	}

	@Override
	public DoubleReference getZoomFactorReference() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Dimension getThumbSize()
	{
		Dimension result = ViewFunctions.instance().getNewDimension( super.getThumbSize(),
																	null, _zoomFactor._value );

		return( result );
	}

	@Override
    public void paint( Graphics g, JComponent c )
	{
		calculateGeometry();
		super.paint(g, c);
	}

	@Override
    public void paintThumb(Graphics g)  {
        Rectangle knobBounds = thumbRect;

//        g.translate( knobBounds.x, knobBounds.y );

		Icon icon = null;
		if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            icon = horizThumbIcon;
        }
        else {
            icon = vertThumbIcon;
        }

		BufferedImage bi = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(),
												BufferedImage.TYPE_INT_ARGB );
		Graphics g1 = bi.getGraphics();

		if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            horizThumbIcon.paintIcon( slider, bi.getGraphics(), 0, 0 );
        }
        else {
            vertThumbIcon.paintIcon( slider, bi.getGraphics(), 0, 0 );
        }

		Rectangle tr = new Rectangle( 0, 0, icon.getIconWidth(), icon.getIconHeight() );
		Rectangle newRectangle = ViewFunctions.instance().getNewRectangle(tr, null, _zoomFactor._value );
		BufferedImage bi_tx = ImageFunctions.instance().resizeImageAccurately(bi, (int)newRectangle.getWidth(),
														(int)newRectangle.getHeight() );

		Point center = ViewFunctions.instance().getCenter(knobBounds);
		g.drawImage( bi_tx,	center.x - newRectangle.width / 2,
							center.y - newRectangle.height / 2,
							center.x + newRectangle.width / 2,
							center.y + newRectangle.height / 2,
							0,
							0,
							bi_tx.getWidth(),
							bi_tx.getHeight(),
							null );
//        g.translate( -knobBounds.x, -knobBounds.y );

		
	}

    public void paintTrack(Graphics g)  {
/*        if (MetalLookAndFeel.usingOcean()) {
            oceanPaintTrack(g);
            return;
        }
*/
        Color trackColor = !slider.isEnabled() ? MetalLookAndFeel.getControlShadow() :
                           slider.getForeground();

//        boolean leftToRight = MetalUtils.isLeftToRight(slider);
        boolean leftToRight = isLeftToRight(slider);

        g.translate( trackRect.x, trackRect.y );

        int trackLeft = 0;
        int trackTop = 0;
        int trackRight;
        int trackBottom;

        // Draw the track
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            trackBottom = (trackRect.height - 1) - getThumbOverhang();
            trackTop = trackBottom - (getTrackWidth() - 1);
            trackRight = trackRect.width - 1;
        }
        else {
            if (leftToRight) {
                trackLeft = (trackRect.width - getThumbOverhang()) -
                                                         getTrackWidth();
                trackRight = (trackRect.width - getThumbOverhang()) - 1;
            }
            else {
                trackLeft = getThumbOverhang();
                trackRight = getThumbOverhang() + getTrackWidth() - 1;
            }
            trackBottom = trackRect.height - 1;
        }

		Color color;
		ToolTipMetalOceanTheme theme = ToolTipLookAndFeel.instance().getTheme();
        if ( slider.isEnabled() ) {
			color = theme.getOriginalControlDarkShadow();
//            g.setColor( MetalLookAndFeel.getControlDarkShadow() );
			g.setColor( color );
            g.drawRect( trackLeft, trackTop,
                        (trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1 );

			color = theme.getOriginalControlHighlight();
            g.setColor( color );
//            g.setColor( MetalLookAndFeel.getControlHighlight() );
            g.drawLine( trackLeft + 1, trackBottom, trackRight, trackBottom );
            g.drawLine( trackRight, trackTop + 1, trackRight, trackBottom );

			color = theme.getOriginalControlShadow();
            g.setColor( color );
//            g.setColor( MetalLookAndFeel.getControlShadow() );
            g.drawLine( trackLeft + 1, trackTop + 1, trackRight - 2, trackTop + 1 );
            g.drawLine( trackLeft + 1, trackTop + 1, trackLeft + 1, trackBottom - 2 );
        }
        else {
			color = theme.getOriginalControlShadow();
            g.setColor( color );
//            g.setColor( MetalLookAndFeel.getControlShadow() );
            g.drawRect( trackLeft, trackTop,
                        (trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1 );
        }

        // Draw the fill
        if ( filledSlider ) {
            int middleOfThumb;
            int fillTop;
            int fillLeft;
            int fillBottom;
            int fillRight;

            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                middleOfThumb = thumbRect.x + (thumbRect.width / 2);
                middleOfThumb -= trackRect.x; // To compensate for the g.translate()
                fillTop = !slider.isEnabled() ? trackTop : trackTop + 1;
                fillBottom = !slider.isEnabled() ? trackBottom - 1 : trackBottom - 2;

                if ( !drawInverted() ) {
                    fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
                    fillRight = middleOfThumb;
                }
                else {
                    fillLeft = middleOfThumb;
                    fillRight = !slider.isEnabled() ? trackRight - 1 : trackRight - 2;
                }
            }
            else {
                middleOfThumb = thumbRect.y + (thumbRect.height / 2);
                middleOfThumb -= trackRect.y; // To compensate for the g.translate()
                fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
                fillRight = !slider.isEnabled() ? trackRight - 1 : trackRight - 2;

                if ( !drawInverted() ) {
                    fillTop = middleOfThumb;
                    fillBottom = !slider.isEnabled() ? trackBottom - 1 : trackBottom - 2;
                }
                else {
                    fillTop = !slider.isEnabled() ? trackTop : trackTop + 1;
                    fillBottom = middleOfThumb;
                }
            }

            if ( slider.isEnabled() ) {
                g.setColor( slider.getBackground() );
                g.drawLine( fillLeft, fillTop, fillRight, fillTop );
                g.drawLine( fillLeft, fillTop, fillLeft, fillBottom );

				color = theme.getOriginalControlShadow();
				g.setColor( color );
//				g.setColor( MetalLookAndFeel.getControlShadow() );
                g.fillRect( fillLeft + 1, fillTop + 1,
                            fillRight - fillLeft, fillBottom - fillTop );
            }
            else {
				color = theme.getOriginalControlShadow();
				g.setColor( color );
//				g.setColor( MetalLookAndFeel.getControlShadow() );
                g.fillRect(fillLeft, fillTop, fillRight - fillLeft, fillBottom - fillTop);
            }
        }

        g.translate( -trackRect.x, -trackRect.y );
    }

    static boolean isLeftToRight( Component c ) {
        return c.getComponentOrientation().isLeftToRight();
    }
}
