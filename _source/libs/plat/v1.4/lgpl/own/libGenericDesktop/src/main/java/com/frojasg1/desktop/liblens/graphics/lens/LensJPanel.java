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
package com.frojasg1.desktop.liblens.graphics.lens;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.desktop.liblens.graphics.ScreenImage;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Usuario
 */
public class LensJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase implements MouseMotionListener, MouseListener
{
	protected Lens a_lensTransformation = null;
	protected JPanel a_contentJPanel = null;

	protected Coordinate2D a_lensPosition = null;
	protected boolean a_moveLensWithMouse = false;

	protected Vector<Component> a_excludeComponentsFromPainting = new Vector<Component>();
	
	protected Object _mutex = null;

	protected boolean _getScreenShot = false;

	protected BufferedImage _screenShotImage = null;

	protected BufferedImage _sprite = null;
	protected Coordinate2D _spritePosition = null;

//	protected RepaintManager a_originalRepaintManager = null;
/*	
	protected class RepaintManagerLens extends RepaintManager
	{
		protected JTextComponent a_excludeComponent = null;
		protected Vector<JTextComponent> a_excludeComponents = new Vector<JTextComponent>();
		
		public RepaintManagerLens( )
		{
			super();
		}
			
		public void addDirtyRegion(JComponent c, int x, int y, int w, int h)
		{
			boolean ok=true;
			if( c instanceof JTextComponent )
			{
 				JTextComponent jtc = (JTextComponent) c;
				if( a_excludeComponents.indexOf(jtc) != -1 )
				{
					ok = jtc.getCaret().isVisible();
				}
			}
			if( ok ) super.addDirtyRegion( c, x, y, w, h );
		}
		
		public void M_addExcludeComponent( JTextComponent jtc )
		{
			a_excludeComponents.add(jtc);
		}
		
		public void M_clearExcludeComponents()
		{
			a_excludeComponents.clear();
		}
	}
*/
	public LensJPanel( JPanel panel, int radius, int mode, boolean moveLensWithMouse, Object mutex )
	{
		this( panel, radius, mode, moveLensWithMouse, mutex, false );
	}


	/**
	 * 
	 * @param panel		Panel over which it will be done the lens effect
	 * @param radius	Radius of the lens
	 * @param mode		it can take the next values: Lens.SA_MODE_AMPLIFY and Lens.SA_MODE_REDUCE
	 * @param moveLensWithMouse it indicates if the lens is moved with the mouse pointer
	 * @param getScreenShot indicates if the image has to be caught from the contentJPanel or from the screen itself. In the latest case, some extra actions have to be taken at the parent JFrame.
	 */
	public LensJPanel( JPanel panel, int radius, int mode, boolean moveLensWithMouse, Object mutex,
						boolean getScreenShot )
	{
		super.init();

		if( mutex != null ) _mutex = mutex;
		else _mutex = new Object();

		try
		{
			if( mode == Lens.SA_MODE_AMPLIFY ) a_lensTransformation = new Lens( radius, Lens.SA_MODE_AMPLIFY );
			else if( mode == Lens.SA_MODE_REDUCE ) a_lensTransformation = new Lens( radius, Lens.SA_MODE_REDUCE );
			else a_lensTransformation = new Lens( radius, mode );		// will throw exception
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		a_contentJPanel = panel;
		a_moveLensWithMouse = moveLensWithMouse;

		_getScreenShot = getScreenShot;

//		initComponents();

		initOwnComponents();

//		a_originalRepaintManager = RepaintManager.currentManager(a_contentJPanel);
//		RepaintManager.setCurrentManager( new RepaintManagerLens() );
		
//		addExcludedJTextComponents(a_contentJPanel);
	}
/*
	public void M_restoreOriginalRepaintManager()
	{
		if( RepaintManager.currentManager(a_contentJPanel) instanceof RepaintManagerLens )
		{
			RepaintManagerLens rml = (RepaintManagerLens) RepaintManager.currentManager(a_contentJPanel);
			rml.M_clearExcludeComponents();
		}

		if( a_originalRepaintManager != null ) RepaintManager.setCurrentManager(a_originalRepaintManager);
	}
*/	

	public void setBorderOfLens( int externalColor, int internalColor, int thickOfBorder )
	{
		a_lensTransformation.setGradatedBorder(externalColor, internalColor, thickOfBorder);
	}

	public void M_addComponentNotToPaint( Component comp )
	{
		int index = a_excludeComponentsFromPainting.indexOf( comp );
		if( index == -1 )
			a_excludeComponentsFromPainting.add(comp);
	}

	public void M_clearComponentsNotToPaint()
	{
		a_excludeComponentsFromPainting.clear();
	}

	public void M_setMouseListeners( Component comp )
	{
		comp.addMouseMotionListener( this );
		comp.addMouseListener( this );

		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_setMouseListeners( contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_setMouseListeners( jmnu.getMenuComponent( ii ) );
			}
		}
	}

	public void initOwnComponents()
	{
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanelComponentResized(evt);
            }
        });

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(a_contentJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(a_contentJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

//		this.add( a_contentJPanel );

		if( a_moveLensWithMouse )
		{
/*			java.awt.event.MouseMotionAdapter mma = new java.awt.event.MouseMotionAdapter() {
				public void mouseMoved(java.awt.event.MouseEvent evt) {
					jPanelMouseMoved(evt);
				}
			};
*/
			M_setMouseListeners( this );
		}
	}

    private void jPanelMouseMoved(java.awt.event.MouseEvent evt)
	{
        // TODO add your handling code here:

		if( a_moveLensWithMouse )
		{
			Coordinate2D newPosition = ( evt == null ? null : new Coordinate2D( evt.getX(), evt.getY() ) );
			if( ( a_lensPosition == null ) || ( newPosition == null ) ||
				! newPosition.equals( a_lensPosition ) )
			{
				if( newPosition != null )
				{
					int xx = newPosition.M_getX();
					int yy = newPosition.M_getY();
					if( evt.getComponent() != this )
					{
						xx = xx + evt.getComponent().getX();
						yy = yy + evt.getComponent().getY();

						Container cont = evt.getComponent().getParent();
						while( cont != this )
						{
							xx = xx + cont.getX();
							yy = yy + cont.getY();

							cont = cont.getParent();
						}
					}
					a_lensPosition = new Coordinate2D( xx, yy );
				}
				else
					a_lensPosition = null;

				repaint();
			}
		}
    }                               

    private void jPanelComponentResized(java.awt.event.ComponentEvent evt)
	{                                      
        // TODO add your handling code here:

		a_contentJPanel.setBounds( new Rectangle( 0, 0, evt.getComponent().getWidth(), evt.getComponent().getHeight() ) );
	}
	
	@Override
	public void paint(Graphics gc)
	{
		synchronized( _mutex )
		{
			try
			{
				M_paintLens( gc, a_lensTransformation, a_lensPosition );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	protected BufferedImage addSprite( BufferedImage bi )
	{
		BufferedImage result = bi;
		try
		{
			if( ( _sprite != null ) && ( _spritePosition != null ) )
				result = ImageFunctions.instance().addSpriteToImage(result, _sprite, _spritePosition );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	protected BufferedImage getContentImage()
	{
		BufferedImage result = null;
		if( ! _getScreenShot )
		{
			M_excludeComponentsFromPainting(true);
			result = ScreenImage.createImage( a_contentJPanel );
			M_excludeComponentsFromPainting(false);

			result = addSprite( result );
		}
		else
		{
			try
			{
//				BufferedImage screenShotImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				BufferedImage screenShotImage = _screenShotImage;

				// we create the BufferedImage
				Rectangle bounds = getBounds();
				result = new BufferedImage( bounds.width, bounds.height, BufferedImage.TYPE_INT_RGB );
				Graphics grp = result.getGraphics();

				// we limit the rectangle caught from the screen
				Point absolutePosition = this.getLocationOnScreen();
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				int screenWidth = (int) screenSize.getWidth();
				int screenHeight = (int) screenSize.getHeight();

				int x1 = IntegerFunctions.max( 0, -absolutePosition.x );
				int y1 = IntegerFunctions.max( 0, -absolutePosition.y );
				int x2 = IntegerFunctions.min( getWidth(), screenWidth - absolutePosition.x );
				int y2 = IntegerFunctions.min( getHeight(), screenHeight - absolutePosition.y );
				grp.drawImage( screenShotImage,
								x1, y1, x2, y2,
								absolutePosition.x + x1,
								absolutePosition.y + y1,
								absolutePosition.x + x2,
								absolutePosition.y + y2,
								null );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		return( result );
	}

	protected void M_paintLens( Graphics gc, Lens lens, Coordinate2D lensPosition )
	{
//		if( lensPosition != null )
		{
	//		M_activateCaretSelectionVisible( true, a_contentJPanel );
	//		M_setDirtyRegions(a_contentJPanel);
	//		M_excludeComponentsFromPainting(true, a_contentJPanel);


	//		M_excludeComponentsFromPainting(true);
	//		BufferedImage bi = ScreenImage.createImage( a_contentJPanel );
	//		M_excludeComponentsFromPainting(false);
			BufferedImage bi = getContentImage();

	//		M_excludeComponentsFromPainting(false, a_contentJPanel);
	//		M_activateCaretSelectionVisible( false, a_contentJPanel );

			Graphics gc1 = bi.getGraphics();

			int radius = lens.M_getRadius();

			if( lensPosition != null )
			{
				Coordinate2D lensPosition2 = new Coordinate2D( lensPosition.M_getX() - a_contentJPanel.getX(),
																lensPosition.M_getY() - a_contentJPanel.getY() );
				LensTransformationResult ltr = lens.M_getTransformedImage( bi, lensPosition2 );

				Coordinate2D upleft = ltr.M_getUpLeftCorner();
				Coordinate2D downright = ltr.M_getDownRightCorner();

				gc1.drawImage( ltr.M_getResultImage(),	lensPosition.M_getX() + a_contentJPanel.getX() - radius + upleft.M_getX(),
														lensPosition.M_getY() + a_contentJPanel.getY() - radius + upleft.M_getY(),
														lensPosition.M_getX() + a_contentJPanel.getX() - radius + downright.M_getX(),
														lensPosition.M_getY() + a_contentJPanel.getY() - radius + downright.M_getY(),
														upleft.M_getX(),
														upleft.M_getY(),
														downright.M_getX(),
														downright.M_getY(),
														null );

//				gc1.setColor(Color.BLACK);
//				gc1.drawOval( lensPosition.M_getX()-lens.M_getRadius(), lensPosition.M_getY()-lens.M_getRadius(),
//							2*lens.M_getRadius(), 2*lens.M_getRadius() );
			}

			gc.drawImage( bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null );
		}
	}
/*
	public BufferedImage M_getImage()
	{
		return( a_imageJPanel.M_getImage() );
	}


	protected void M_activateCaretSelectionVisible( boolean visible, Component comp )
	{
		if( comp instanceof Container	)
		{
			if ( comp instanceof JTextComponent )
			{
				JTextComponent jtc = (JTextComponent) comp;
//				jtc.getCaret().setVisible(visible);
				jtc.getCaret().setSelectionVisible(visible);
				jtc.setIgnoreRepaint(true);
				jtc.removeNotify();
			}

			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_activateCaretSelectionVisible( visible, contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_activateCaretSelectionVisible( visible, jmnu.getMenuComponent( ii ) );
			}
		}
	}
*/
	public void removeComponentNotToPaint( Component comp )
	{
		a_excludeComponentsFromPainting.remove( comp );
	}

	protected void M_excludeComponentsFromPainting( boolean selectionVisible )
	{
		Iterator<Component> it = a_excludeComponentsFromPainting.iterator();
		while( it.hasNext() )
		{
			M_excludeComponentFromPainting( selectionVisible, it.next() );
		}
	}

	protected void M_excludeComponentFromPainting( boolean selectionVisible, Component comp )
	{
		if( a_contentJPanel.isAncestorOf(comp) )
		{
			if ( comp instanceof JTextComponent )
			{
				JTextComponent jtc = (JTextComponent) comp;
//				jtc.getCaret().setVisible(visible);
				jtc.getCaret().setSelectionVisible(selectionVisible);
			}
			comp.setIgnoreRepaint(true);
			comp.removeNotify();
		}
/*		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_excludeComponentsFromPainting( selectionVisible, contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_excludeComponentsFromPainting( selectionVisible, jmnu.getMenuComponent( ii ) );
			}
		}
*/	}

	public Coordinate2D M_getLensCoordinates()
	{
		Coordinate2D result = null;
		if( ! a_moveLensWithMouse ) result = a_lensPosition;
		return( result );
	}

	public void M_setLensCoordinates( Coordinate2D newCoordinates )
	{
		a_lensPosition = newCoordinates;
	}

	public void M_setLensCoordiantes( int xx, int yy )
	{
		M_setLensCoordinates( new Coordinate2D( xx, yy ) );
	}
/*
	public void M_setDirtyRegions( Component comp )
	{
		if( comp instanceof Container	)
		{
			if ( comp instanceof JTextComponent )
			{
				JTextComponent jtc = (JTextComponent) comp;
				RepaintManager.currentManager(comp).markCompletelyDirty( jtc );
			}

			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_setDirtyRegions( contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_setDirtyRegions( jmnu.getMenuComponent( ii ) );
			}
		}
	}
	
	public void addExcludedJTextComponents( Component comp )
	{
		if( comp instanceof Container	)
		{
			if ( comp instanceof JTextComponent )
			{
				JTextComponent jtc = (JTextComponent) comp;
				if( RepaintManager.currentManager(comp) instanceof RepaintManagerLens )
				{
					RepaintManagerLens rml = (RepaintManagerLens) RepaintManager.currentManager(a_contentJPanel);
					rml.M_addExcludeComponent(jtc);
				}
			}

			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				addExcludedJTextComponents( contnr.getComponent(ii) );
			}

			if( comp instanceof JMenu )
			{
				JMenu jmnu = (JMenu) comp;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					addExcludedJTextComponents( jmnu.getMenuComponent( ii ) );
			}
		}
	}
*/

	@Override
	public void mouseDragged(MouseEvent me)
	{
		jPanelMouseMoved(me);
	}

	@Override
	public void mouseMoved(MouseEvent me)
	{
		jPanelMouseMoved(me);
	}

	public void setScreenShotImage( BufferedImage image )
	{
		_screenShotImage = image;
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
	}

	@Override
	public void mouseEntered(MouseEvent evt)
	{
		jPanelMouseMoved( evt );
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		jPanelMouseMoved( null );
	}

	public void setSprite( BufferedImage bi, Coordinate2D position )
	{
		_sprite = bi;
		_spritePosition = position;
	}
}
