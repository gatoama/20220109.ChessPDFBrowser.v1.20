/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.general.lib3d.components;

import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.lib3d.components.api.Canvas3dJPanelApi;
import com.frojasg1.general.lib3d.scenarios.Scenario3dBase;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ImageComponent;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Screen3D;

/**
 *
 *  http://www.java2s.com/Code/Java/3D/PrintCanvas3D.htm
 */
public class Canvas3dJPanel extends JPanel implements Canvas3dJPanelApi, ComponentListener
{
	protected JFrame _frame = null;
	
	// http://www.java2s.com/Code/Java/3D/PrintCanvas3D.htm
	protected OffScreenCanvas3D _offScreenCanvas3D;
	protected JPanel _canvas3DContentJPanel = null;
	protected Scenario3dBase _scenario = null;

	protected Integer _colorFrom = null;
	protected Integer _colorTo = null;

	protected boolean _isPaintingContent = false;

	public void init( Scenario3dBase scenario )
	{
		_scenario = scenario;
		Canvas3D canvas3D = scenario.getCanvas3D();
		_frame = new JFrame();
		_canvas3DContentJPanel = createCanvas3DContentJPanel();
		_canvas3DContentJPanel.add( canvas3D );

		_offScreenCanvas3D = new OffScreenCanvas3D(scenario.getSimpleUniverse().getPreferredConfiguration(), true);
		updateOffScreenCanvas3DSize();

		_frame.getContentPane().add( _canvas3DContentJPanel );

		scenario.getSimpleUniverse().getViewer().getView().addCanvas3D(_offScreenCanvas3D);;
		addListeners();
	}

	protected void updateOffScreenCanvas3DSize()
	{
		Screen3D sOn = _scenario.getCanvas3D().getScreen3D();
		Screen3D sOff = _offScreenCanvas3D.getScreen3D();
		Dimension dim = sOn.getSize();
		sOff.setSize(dim);
		sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());
		sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());
	}

	@Override
	public void setColorTranslation( Integer colorFrom, Integer colorTo )
	{
		_colorFrom = colorFrom;
		_colorTo = colorTo;
	}

	protected JPanel createCanvas3DContentJPanel()
	{
		JPanel result = new JPanel() {
			@Override
			public void paint(Graphics gc)
			{
				super.paint(gc);
				if( !_isPaintingContent )
					Canvas3dJPanel.this.repaint();
			}
		};

		result.setLayout( new BorderLayout() );

		return( result );
	}

	protected void addListeners()
	{
		addComponentListener(this);
	}

	protected void removeListeners()
	{
		removeComponentListener(this);
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		_canvas3DContentJPanel.setSize( getSize() );
		SwingUtilities.invokeLater( ()->updateOffScreenCanvas3DSize() );
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
	}


	@Override
	public void paint(Graphics gc)
	{
		super.paint(gc);

		_isPaintingContent = true;
		BufferedImage bi = getContentImage();
		_isPaintingContent = false;

		gc.drawImage( bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null );

	}

	@Override
	public BufferedImage getContentImage()
	{
		Dimension dimen = getSize();
		return( getContentImage( dimen.width, dimen.height ) );
	}

	@Override
	public BufferedImage getContentImage( int width, int height )
	{
		BufferedImage result = null;

		_offScreenCanvas3D.setOffScreenLocation( new Point(0,0) );

		result = _offScreenCanvas3D.doRender( width, height );

		return( result );
	}

	// http://www.java2s.com/Code/Java/3D/PrintCanvas3D.htm
	class OffScreenCanvas3D extends Canvas3D {
	  OffScreenCanvas3D(GraphicsConfiguration graphicsConfiguration,
		  boolean offScreen) {

		super(graphicsConfiguration, offScreen);
	  }

		BufferedImage doRender(int width, int height) {
			BufferedImage bImage = null;
			if( ( width > 0 ) && ( height > 0 ) )
			{
				bImage = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);

				ImageComponent2D buffer = new ImageComponent2D(
					ImageComponent.FORMAT_RGBA, bImage);

				setOffScreenBuffer(buffer);
				renderOffScreenBuffer();
				waitForOffScreenRendering();
				bImage = getOffScreenBuffer().getImage();
				bImage = ImageFunctions.instance().resizeImage(bImage, bImage.getWidth(),
											bImage.getHeight(), _colorFrom,
											_colorTo, null);
			}

			return bImage;
		  }

	  public void postSwap() {
		// No-op since we always wait for off-screen rendering to complete
	  }
	}

}
