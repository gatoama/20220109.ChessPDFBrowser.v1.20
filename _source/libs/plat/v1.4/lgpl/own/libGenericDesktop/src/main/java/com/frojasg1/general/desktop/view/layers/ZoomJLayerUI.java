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

import com.frojasg1.general.desktop.view.layers.events.ZoomEvent;
import com.frojasg1.general.desktop.view.layers.events.ZoomMouseEvent;
import com.frojasg1.general.desktop.view.layers.events.ZoomMouseWheelEvent;
import com.frojasg1.general.desktop.view.layers.zoom.ZoomComponent;
import com.frojasg1.general.desktop.view.zoom.ui.ZoomMetalButtonUI;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LayerUI;
import javax.swing.plaf.synth.SynthSliderUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
	//https://stackoverflow.com/questions/21439341/zooming-jlayeredpane-via-jlayer-and-the-layerui
@Deprecated
public class ZoomJLayerUI extends LayerUI<JComponent>
{
	protected double _zoom = 1; // Changing this value seems to have no effect

	protected JComponent _comp = null;

	protected static final int MOUSE_EVENT=0;
	protected static final int MOUSE_MOTION_EVENT=1;
	protected static final int MOUSE_WHEEL_EVENT=2;

	Map< JComponent, MouseListeners > _mapListeners = new HashMap< JComponent, MouseListeners >();
	
	public ZoomJLayerUI()
	{}

    public static ComponentUI createUI( JComponent x ) {
        return new ZoomJLayerUI();
    }

	public void setZoom( double zoom )
	{
		_zoom = zoom;
	}

	public double getZoom()
	{
		return( _zoom );
	}
	
	@Override
	public void paint(Graphics g, JComponent c)
	{
		Graphics2D g2 = (Graphics2D) g.create();
		g2.scale(_zoom, _zoom);
		super.paint(g2, c);
		g2.dispose();
	}

	@Override
	public void installUI(JComponent c)
	{
		super.installUI(c);
		if( c instanceof JLayer )
		{
			JLayer jlayer = (JLayer) c;
			JComponent jComp = getJComp( jlayer );
			ZoomComponent zc = getZoomComponent( jComp );
			if( zc != null )
			{
				jlayer.setLayerEventMask(
				AWTEvent.MOUSE_EVENT_MASK | AWTEvent.ACTION_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
										);

				createSaveAndUnregisterListeners( jComp );
//				zc.disableEvents_public( AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK );
			}
		}
	}

	protected void createSaveAndUnregisterListeners( JComponent comp )
	{
		MouseListeners ml = getMouseListeners( comp );
		if( ml == null )
		{
			ml = new MouseListeners( comp );
			_mapListeners.put( comp, ml );
		}
		else
		{
			ml.getAndUnregisterListeners();
		}
	}

	protected MouseListeners getMouseListeners( JComponent comp )
	{
		return( _mapListeners.get( comp ) );
	}

	protected void removeMouseListeners( JComponent comp )
	{
		if( comp != null )
			_mapListeners.remove( comp );
	}

	@Override
	public void uninstallUI(JComponent c)
	{
		if( c instanceof JLayer )
		{
			JLayer jlayer = (JLayer) c;
			jlayer.setLayerEventMask(0);

			JComponent comp = getJComp( jlayer );
			removeMouseListeners( comp );
		}
		super.uninstallUI(c);
	}

	@Override
	public void processMouseEvent(MouseEvent e, JLayer<? extends JComponent> l)
	{
		processGenericMouseEvent( MOUSE_EVENT, e, l );
	}

	protected void	processMouseMotionEvent(MouseEvent e, JLayer<? extends JComponent> l)
	{
		processGenericMouseEvent( MOUSE_MOTION_EVENT, e, l );
		
	}

	protected void	processMouseWheelEvent(MouseWheelEvent e, JLayer<? extends JComponent> l)
	{
		processGenericMouseEvent( MOUSE_WHEEL_EVENT, e, l );
		
	}

	protected JComponent getJComp( JLayer<? extends JComponent> layer )
	{
		JComponent result = null;
		if( layer != null )
			result = layer.getView();

		return( result );
	}

	protected ZoomComponent getZoomComponent( JComponent jComp )
	{
		ZoomComponent result = null;
		if( jComp instanceof ZoomComponent )
		{
			result = (ZoomComponent) jComp;
		}

		return( result );
	}

	protected MouseEvent createZoomMouseEvent( MouseEvent me, JLayer<? extends JComponent> layer )
	{
		MouseEvent result = null;

		if( me != null )
		{
			Component source = layer.getView();
			double zoomFactor = getZoom();

			int id2 = me.getID();
			long when = new Date().getTime();
			int modifiers = me.getModifiers();
			int xx = (int) ( me.getX() / zoomFactor );
			int yy = (int) ( me.getY() / zoomFactor );
			int xAbs = me.getXOnScreen() + xx - me.getX();
			int yAbs = me.getYOnScreen() + yy - me.getY();
			int clickCount = me.getClickCount();
			boolean popupTrigger = me.isPopupTrigger();
			int button = me.getButton();

			result = new ZoomMouseEvent(source, id2, when, modifiers,
					xx, yy, xAbs, yAbs,
					clickCount, popupTrigger, button);
		}

		return( result );
	}

	protected MouseEvent createZoomMouseMotionEvent( MouseEvent me, JLayer<? extends JComponent> layer )
	{
		return( createZoomMouseEvent( me, layer ) );
	}

	protected MouseEvent createZoomMouseWheelEvent( MouseEvent me, JLayer<? extends JComponent> layer )
	{
		MouseEvent result = null;

		if( me instanceof MouseWheelEvent )
		{
			MouseWheelEvent mwe = (MouseWheelEvent) me;
			double zoomFactor = getZoom();

			Component source = layer.getView();
			int id2 = me.getID();
			long when = new Date().getTime();
			int modifiers = me.getModifiers();
			int xx = (int) ( me.getX() / zoomFactor );
			int yy = (int) ( me.getY() / zoomFactor );
			int xAbs = me.getXOnScreen() + xx - me.getX();
			int yAbs = me.getYOnScreen() + yy - me.getY();
			int clickCount = me.getClickCount();
			boolean popupTrigger = me.isPopupTrigger();
			int scrollType = mwe.getScrollType();
			int scrollAmount = mwe.getScrollAmount();
			int wheelRotation = mwe.getWheelRotation();
			double preciseWheelRotation = mwe.getPreciseWheelRotation();

			result = new ZoomMouseWheelEvent ( source, id2, when, modifiers,
								xx, yy, xAbs, yAbs, clickCount, popupTrigger,
								scrollType, scrollAmount, wheelRotation, preciseWheelRotation );
		}

		return( result );
	}

	protected MouseEvent createZoomMouseEvent( int typeOfEvent, MouseEvent me,
												JLayer<? extends JComponent> layer )
	{
		MouseEvent result = null;

		switch( typeOfEvent )
		{
			case MOUSE_EVENT:
			{
				result = createZoomMouseEvent( me, layer );
			}
			break;

			case MOUSE_MOTION_EVENT:
			{
				result = createZoomMouseMotionEvent( me, layer );
			}
			break;

			case MOUSE_WHEEL_EVENT:
			{
				result = createZoomMouseWheelEvent( me, layer );
			}
			break;
		};

		return( result );
	}

	protected void registerListeners()
	{
		
	}

	protected void processGenericMouseEvent( int typeOfEvent, MouseEvent me,
												JLayer<? extends JComponent> layer )
	{
		JComponent jComp = getJComp( layer );
		ZoomComponent zc = getZoomComponent( jComp );

		if( zc != null )
		{
			if( ! ( me instanceof ZoomEvent ) )
			{
				MouseEvent newMouseEvent = createZoomMouseEvent( typeOfEvent, me, layer );
//				zc.enableEvents_public( AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK );

				MouseListeners ml = getMouseListeners( jComp );
				if( ( ml != null ) && ( newMouseEvent != null ) )
				{
					String log = JLayerUtils.toString(me);
					System.out.println( "evento anterior  --->  " + log );
					System.out.println( "evento nuevo     --->  " + JLayerUtils.toString(newMouseEvent) );
					ml.registerAndInitializeListeners();
					jComp.dispatchEvent( newMouseEvent );
					ml.getAndUnregisterListeners();
				}
//				zc.disableEvents_public( AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK );
			}
		}
	}

	protected static class MouseListeners
	{
		protected MouseListener[] _mouseListenersArray = null;
		protected MouseMotionListener[] _mouseMotionListenersArray = null;
		protected MouseWheelListener[] _mouseWheelListenersArray = null;
		JComponent _comp = null;

		public MouseListeners( JComponent comp )
		{
			_comp = comp;
			initializeListeners();
			getAndUnregisterListeners();
		}

		protected void initializeListeners()
		{
			_mouseListenersArray = new MouseListener[0];
			_mouseMotionListenersArray = new MouseMotionListener[0];
			_mouseWheelListenersArray = new MouseWheelListener[0];
		}

		protected void getListeners()
		{
			MouseListener[] mltmp = _comp.getMouseListeners();
			MouseListener[] newMl = new MouseListener[ mltmp.length + _mouseListenersArray.length ];
			System.arraycopy( mltmp, 0, newMl, 0, mltmp.length );
			System.arraycopy( _mouseListenersArray, 0, newMl, mltmp.length, _mouseListenersArray.length );
			_mouseListenersArray = newMl;

			MouseMotionListener[] mmlTmp = _comp.getMouseMotionListeners();
			MouseMotionListener[] newMml = new MouseMotionListener[ mmlTmp.length + _mouseMotionListenersArray.length ];
			System.arraycopy( mmlTmp, 0, newMml, 0, mmlTmp.length );
			System.arraycopy( _mouseMotionListenersArray, 0, newMml, mmlTmp.length, _mouseMotionListenersArray.length );
			_mouseMotionListenersArray = _comp.getMouseMotionListeners();

			MouseWheelListener[] mwlTmp = _comp.getMouseWheelListeners();
			MouseWheelListener[] newMwl = new MouseWheelListener[ mwlTmp.length + _mouseWheelListenersArray.length ];
			System.arraycopy( mwlTmp, 0, newMwl, 0, mwlTmp.length );
			System.arraycopy( _mouseWheelListenersArray, 0, newMwl, mwlTmp.length, _mouseWheelListenersArray.length );
			_mouseWheelListenersArray = newMwl;
		}

		protected void unregisterListeners()
		{
			for( int ii=0; ii<_mouseListenersArray.length; ii++ )
			{
				_comp.removeMouseListener( _mouseListenersArray[ii] );
			}

			for( int ii=0; ii<_mouseMotionListenersArray.length; ii++ )
			{
				_comp.removeMouseMotionListener( _mouseMotionListenersArray[ii] );
			}

			for( int ii=0; ii<_mouseWheelListenersArray.length; ii++ )
			{
				_comp.removeMouseWheelListener( _mouseWheelListenersArray[ii] );
			}
		}

		public void getAndUnregisterListeners()
		{
			getListeners();
			unregisterListeners();
		}

		public void registerAndInitializeListeners()
		{
			registerListeners();
			initializeListeners();
		}

		protected void registerListeners()
		{
			for( int ii=0; ii<_mouseListenersArray.length; ii++ )
			{
				_comp.addMouseListener( _mouseListenersArray[ii] );
			}

			for( int ii=0; ii<_mouseMotionListenersArray.length; ii++ )
			{
				_comp.addMouseMotionListener( _mouseMotionListenersArray[ii] );
			}

			for( int ii=0; ii<_mouseWheelListenersArray.length; ii++ )
			{
				_comp.addMouseWheelListener( _mouseWheelListenersArray[ii] );
			}
		}
	}


}
