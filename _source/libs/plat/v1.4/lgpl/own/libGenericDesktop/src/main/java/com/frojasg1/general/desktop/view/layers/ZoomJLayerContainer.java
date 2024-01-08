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
package com.frojasg1.general.desktop.view.layers;

import com.frojasg1.general.desktop.view.ViewFunctions;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
@Deprecated
public class ZoomJLayerContainer extends JComponent
{
	protected static final int SIZE = 0;
	protected static final int PREFERRED_SIZE = 1;
	protected static final int MINIMUM_SIZE = 2;
	protected static final int MAXIMUM_SIZE = 3;

	protected ZoomDimension[] _oldDimensions = new ZoomDimension[4];

	protected ZoomJLayerUI _ui = null;

	protected JLayer<JComponent> _jLayer = null;

	protected JComponent _comp = null;

	public ZoomJLayerContainer( JComponent comp )
	{
		_comp = comp;
		_ui = new ZoomJLayerUI( );
		_jLayer = new JLayer<JComponent>( comp, _ui );

		add( _jLayer );

		_oldDimensions[SIZE] = new ZoomDimension( _ui.getZoom(), comp.getSize() );
		_oldDimensions[PREFERRED_SIZE] = new ZoomDimension( _ui.getZoom(), comp.getPreferredSize() );
		_oldDimensions[MINIMUM_SIZE] = new ZoomDimension( _ui.getZoom(), comp.getMinimumSize() );
		_oldDimensions[MAXIMUM_SIZE] = new ZoomDimension( _ui.getZoom(), comp.getMaximumSize() );
	}

	public void installUIToContents()
	{
		_ui.installUI(_jLayer );
	}

	protected void updateSizesOfComponents( double zoom )
	{
		updateGenericSizeOfComponents( SIZE, zoom );
		updateGenericSizeOfComponents( PREFERRED_SIZE, zoom );
		updateGenericSizeOfComponents( MINIMUM_SIZE, zoom );
		updateGenericSizeOfComponents( MAXIMUM_SIZE, zoom );
	}
	
	protected void setZoom_internal( double zoom )
	{
		if( zoom > 0 )
		{
			updateSizesOfComponents( zoom );

			_ui.setZoom(zoom);
		}
	}

	public void setZoom( double zoom )
	{
		if( !SwingUtilities.isEventDispatchThread() )
		{
			SwingUtilities.invokeLater( new Runnable() {
					public void run()
					{
						setZoom_internal( zoom );
					}
			});
		}
		else
			setZoom_internal( zoom );
	}
	
	public double getZoom()
	{
		return( _ui.getZoom() );
	}

	@Override
	public void paintComponent( Graphics gp )
	{
/*
		BufferedImage bi = ScreenImage.createImage( _jLayer );

		Graphics gc1 = bi.getGraphics();

		gc1.drawImage( bi, 0, 0, null );
*/
		super.paintComponent( gp );
		_jLayer.paint( gp );
	}

	protected void updateGenericSizeOfComponents( int indexOfArray, Dimension dim, double newZoom )
	{
		if( dim != null )
		{
			Dimension dimForComponent = ViewFunctions.instance().getNewDimension( dim, null, 1/newZoom );
			switch( indexOfArray )
			{
				case SIZE:
				{
					_jLayer.setSize( dim );
					_comp.setSize( dimForComponent );
				}
				break;

				case PREFERRED_SIZE:
				{
					_jLayer.setPreferredSize( dim );
					_comp.setPreferredSize( dimForComponent );
				}
				break;

				case MINIMUM_SIZE:
				{
					_jLayer.setMinimumSize( dim );
					_comp.setMinimumSize(dimForComponent );
				}
				break;

				case MAXIMUM_SIZE:
				{
					_jLayer.setMaximumSize( dim );
					_comp.setMaximumSize( ViewFunctions.instance().getNewDimension( dim, null, 1/newZoom ));
				}
				break;
			};
		}
	}

	protected Dimension getLastDimension( int indexOfArray, double newZoom )
	{
		Dimension result = null;
		Dimension oldDimen = _oldDimensions[indexOfArray].getLastDimension();
		if( oldDimen != null )
		{
			double oldZoom = _oldDimensions[indexOfArray].getOldZoom();
			result = ViewFunctions.instance().getNewDimension( oldDimen, null, newZoom / oldZoom );
		}

		return( result );
	}

	protected void updateGenericSizeOfComponents( int indexOfArray, double newZoom )
	{
		Dimension dim = getLastDimension( indexOfArray, newZoom );
		updateGenericSizeOfComponents( indexOfArray, dim, newZoom );
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
		super.setBounds( xx, yy, width, height );

		Dimension dim = new Dimension( width, height );
		updateGenericSizeOfComponents( SIZE, dim, getZoom() );

		_oldDimensions[SIZE] = new ZoomDimension( getZoom(), dim );
	}

	@Override
	public void setSize( int width, int height )
	{
		super.setSize( width, height );

		Dimension dim = new Dimension( width, height );
		updateGenericSizeOfComponents( SIZE, dim, getZoom() );

		_oldDimensions[SIZE] = new ZoomDimension( getZoom(), dim );
	}

	@Override
	public void setPreferredSize( Dimension dim )
	{
		super.setPreferredSize( dim );

		updateGenericSizeOfComponents( PREFERRED_SIZE, dim, getZoom() );

		_oldDimensions[PREFERRED_SIZE] = new ZoomDimension( getZoom(), dim );
	}

	@Override
	public void setMinimumSize( Dimension dim )
	{
		super.setMinimumSize( dim );

		updateGenericSizeOfComponents( MINIMUM_SIZE, dim, getZoom() );

		_oldDimensions[MINIMUM_SIZE] = new ZoomDimension( getZoom(), dim );
	}

	@Override
	public void setMaximumSize( Dimension dim )
	{
		super.setMaximumSize( dim );

		updateGenericSizeOfComponents( MAXIMUM_SIZE, dim, getZoom() );

		_oldDimensions[MAXIMUM_SIZE] = new ZoomDimension( getZoom(), dim );
	}

	protected static class ZoomDimension
	{
		protected double _oldZoom = 1.0D;
		protected Dimension _lastDimension = null;

		public ZoomDimension( double oldZoom, Dimension lastDimension )
		{
			_oldZoom = oldZoom;
			_lastDimension = lastDimension;
		}

		public double getOldZoom()
		{
			return( _oldZoom );
		}

		public Dimension getLastDimension()
		{
			return( _lastDimension );
		}
	}
}
