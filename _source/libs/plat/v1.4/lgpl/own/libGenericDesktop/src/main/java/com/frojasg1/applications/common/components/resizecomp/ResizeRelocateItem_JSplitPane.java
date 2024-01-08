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
package com.frojasg1.applications.common.components.resizecomp;

import com.frojasg1.general.zoom.ZoomParam;
import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author Usuario
 */
public class ResizeRelocateItem_JSplitPane extends ResizeRelocateItem
											implements MouseListener, MouseMotionListener
{
	protected int _lastDraggedDividerLocation = -1;
	protected Dimension _jspSizeOfLastDraggedDividerLocationEvent = null;
	protected Dimension _jspSizeOfLastForExcess = null;

	protected int _newDividerLocation = -1;
	protected JSplitPane _jsp = null;
	protected boolean _inDrag = false;
	protected boolean _lastWasDrag = false;

	protected int _originalDividerSize = -1;

	protected Dimension _previousSize = null;

	protected boolean _savedStateOfDividerLocation = false;

	protected ResizeRelocateItem_JSplitPane( JSplitPane comp, int flags,
												ResizeRelocateItem_parent parent,
												boolean postpone_initialization,
												boolean isAlreadyZoomed ) throws InternException
	{
		super( comp, flags, parent, postpone_initialization, isAlreadyZoomed );
		_jsp = comp;
		saveStateOfLastDraggedDividerLocation();
		_originalDividerSize = comp.getDividerSize();

		SplitPaneUI spui = comp.getUI();
		if (spui instanceof BasicSplitPaneUI)
		{
			// Setting a mouse listener directly on split pane does not work, because no events are being received.
			((BasicSplitPaneUI) spui).getDivider().addMouseListener(this);
			((BasicSplitPaneUI) spui).getDivider().addMouseMotionListener(this);
		}
	}

	@Override
	public void dispose()
	{
		SplitPaneUI spui = _jsp.getUI();
		if (spui instanceof BasicSplitPaneUI)
		{
			((BasicSplitPaneUI) spui).getDivider().removeMouseListener(this);
			((BasicSplitPaneUI) spui).getDivider().removeMouseMotionListener(this);
		}

		super.dispose();
	}

	protected boolean hasToChangeBounds( double zoomFactor, Rectangle parentBounds )
	{
		return( super.hasToChangeBounds( zoomFactor, parentBounds ) ||
				_savedStateOfDividerLocation );
	}

	@Override
	public void execute( ZoomParam zp )
	{
//		_lastZoomParam = zp;

		double zoomFactor = zp.getZoomFactor();

		this.setIsAlreadyZoomed( zoomFactor == _previousZoomFactor );

//		if( ( _previousSize == null ) || !_previousSize.equals( _jsp.getSize() ) ||
//			( _previousZoomFactor != zoomFactor ) )
		{
	/*
			Rectangle parentBounds = getParentBounds( _component );
			Rectangle newBounds = calculateNewBoundsOnScreen( zoomFactor, parentBounds );
			setBounds( newBounds, zoomFactor );
	*/
			_previousSize = execute_basic( zoomFactor );
/*
			if( zoomFactor != _previousZoomFactor )
			{
				_lastDraggedDividerLocation = IntegerFunctions.zoomValueInt( _lastDraggedDividerLocation, zoomFactor/_previousZoomFactor );
				_jspSizeOfLastDraggedDividerLocationEvent = ViewFunctions.instance().getNewDimension(_jspSizeOfLastDraggedDividerLocationEvent, zoomFactor/_previousZoomFactor);
			}
*/
			_previousZoomFactor = zoomFactor;
		}

		setIsAlreadyZoomed( false );
	}
/*
	@Override
	public int getDividerLocation( double zoomFactor )
	{
		Rectangle parentBounds = getParentBounds( _component, zoomFactor );
		Rectangle newBounds = getOriginalComponentBounds( zoomFactor );

		if( !isAlreadyZoomed() )
			newBounds = super.calculateNewBoundsOnScreen( zoomFactor, parentBounds );

		int result = calculateNewDividerLocation( zoomFactor, newBounds );

		return( result );
	}
*/

	@Override
	public Dimension execute_basic( double zoomFactor )
	{
		Dimension result = super.execute_basic( zoomFactor );
		_savedStateOfDividerLocation = false;

		return( result );
	}

	public void saveStateOfLastDraggedDividerLocation()
	{
		_lastDraggedDividerLocation = _jsp.getDividerLocation();
		_newDividerLocation = _lastDraggedDividerLocation;

		_jspSizeOfLastDraggedDividerLocationEvent = _jsp.getSize();
		_jspSizeOfLastForExcess = _jspSizeOfLastDraggedDividerLocationEvent;

		_savedStateOfDividerLocation = true;
	}

	@Override
	public Rectangle calculateOnlyNewBounds( double zoomFactor, Rectangle parentBounds )
	{
		boolean calculateDividerLocation = false;
		return( calculateNewBoundsAndDividerLocation( zoomFactor, parentBounds, calculateDividerLocation ) );
	}

	@Override
	public Rectangle calculateNewBounds( double zoomFactor, Rectangle parentBounds )
	{
		boolean calculateDividerLocation = true;
		return( calculateNewBoundsAndDividerLocation( zoomFactor, parentBounds, calculateDividerLocation ) );
	}


	public Rectangle calculateNewBoundsAndDividerLocation( double zoomFactor,
															Rectangle parentBounds,
															boolean calculateDividerLocation )
	{
		Rectangle newBounds = null;

		getOriginalComponentBounds( zoomFactor );

		if( _jsp.getParent() instanceof JSplitPane )
			newBounds = parentBounds;
		else
			newBounds = super.calculateNewBounds( zoomFactor, parentBounds );

		if( calculateDividerLocation )
		{
			int dividerLocation = calculateNewDividerLocation( zoomFactor, newBounds );
			_jsp.setDividerSize( IntegerFunctions.zoomValueInt(_originalDividerSize, zoomFactor) );

			_newDividerLocation = dividerLocation;

			if( zoomFactor == _previousZoomFactor )
			{
				int excessLeftOrTop = -1;
				int excessRightOrBottom = -1;
				if( _jsp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
				{
					excessLeftOrTop = calculateHorizontalExcess( _jsp.getLeftComponent(), calculateBounds_JSplitPane( _jsp, newBounds, dividerLocation, LEFT ) );
					excessRightOrBottom = calculateHorizontalExcess( _jsp.getRightComponent(), calculateBounds_JSplitPane( _jsp, newBounds, dividerLocation, RIGHT ) );
				}
				else if( _jsp.getOrientation() == JSplitPane.VERTICAL_SPLIT )
				{
					excessLeftOrTop = calculateVerticalExcess( _jsp.getTopComponent(), calculateBounds_JSplitPane( _jsp, newBounds, dividerLocation, TOP ) );
					excessRightOrBottom = calculateVerticalExcess( _jsp.getBottomComponent(), calculateBounds_JSplitPane( _jsp, newBounds, dividerLocation, BOTTOM ) );
				}

				if( excessLeftOrTop + excessRightOrBottom < 0 )	// this is the worst case. We cannot do nothing and the position of the dividerLocation will be reset
				{

				}
				else if( excessLeftOrTop < 0 )
				{
					_newDividerLocation = dividerLocation - excessLeftOrTop;
				}
				else if( excessRightOrBottom < 0 )
				{
					_newDividerLocation = dividerLocation + excessRightOrBottom;
				}
			}
		}

		return( newBounds );
	}

	public void setDividerLocationWithoutZoom( int dividerLocationWithoutZoom, Dimension sizeOfJSplitPane )
	{
		_newDividerLocation = IntegerFunctions.zoomValueInt( dividerLocationWithoutZoom, _previousZoomFactor ); ;
		_lastDraggedDividerLocation = _newDividerLocation;

		if( sizeOfJSplitPane != null )
			_jspSizeOfLastForExcess =
				ViewFunctions.instance().getNewDimension( sizeOfJSplitPane, _previousZoomFactor );
	}

	public int calculateHorizontalExcess( Component comp, Rectangle triedNewBounds )
	{
		int result = (int) ( triedNewBounds.getWidth() - comp.getMinimumSize().getWidth() );

		return( result );
	}

	public int calculateVerticalExcess( Component comp, Rectangle triedNewBounds )
	{
		int result = (int) ( triedNewBounds.getHeight() - comp.getMinimumSize().getHeight() );

		return( result );
	}
	
	public int calculateNewDividerLocation( double zoomFactor, Rectangle newBounds )
	{
		int result = calculateDividerLocationWithoutZoom( zoomFactor, newBounds );

		if( _previousZoomFactor != zoomFactor )
		{
			result = IntegerFunctions.zoomValueInt( result, zoomFactor/_previousZoomFactor );
//			_lastDraggedDividerLocation = IntegerFunctions.zoomValueInt( _lastDraggedDividerLocation, zoomFactor/_previousZoomFactor );
		}

		return( result );
	}

	public int calculateDividerLocationWithoutZoom( double zoomFactor, Rectangle newBounds )
	{
		int result = -1;

/*		if( isServingResizureFromListenerOfUser() )
		{
			result = _newDividerLocation;
		}
		else */if( !isAlreadyZoomed() )
		{
			result = _newDividerLocation;
		}
		else
		{
			result = _lastDraggedDividerLocation;

			if( _jspSizeOfLastDraggedDividerLocationEvent.equals( _jspSizeOfLastForExcess ) )
			{
				if( _jsp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
				{
					int boundsWidth = newBounds.width;
					int lastDraggedSize = IntegerFunctions.zoomValueInt( _jspSizeOfLastForExcess.width, zoomFactor/_previousZoomFactor );
					double expansionLeft = _jsp.getResizeWeight() * ( boundsWidth - lastDraggedSize );
					result = result + (int) Math.round(expansionLeft);
				}
				else if( _jsp.getOrientation() == JSplitPane.VERTICAL_SPLIT )
				{
					int boundsHeight = newBounds.height;
					int lastDraggedSize = IntegerFunctions.zoomValueInt( _jspSizeOfLastForExcess.height, zoomFactor/_previousZoomFactor );
					double expansionTop = _jsp.getResizeWeight() * ( boundsHeight - lastDraggedSize );
					result = result + (int) Math.round(expansionTop);
				}
			}
		}

		return( result );
	}

	protected void resizeChangingPreferredSize( Component comp, double zoomFactor, Rectangle parentBounds )
	{
		if( comp != null )
		{
			ResizeRelocateItem rri = null;
			Dimension newSize = null;
			if( ( _parent != null ) && ( ( rri = _parent.getResizeRelocateComponentItem(comp) ) != null ) )
			{
				Rectangle newBounds = rri.calculateOnlyNewBounds( zoomFactor, parentBounds );
				newSize = new Dimension( (int) newBounds.getWidth(), (int) newBounds.getHeight() );
			}
			else
			{
				newSize = new Dimension( (int) parentBounds.getWidth(), (int) parentBounds.getHeight() );
			}

			Dimension checkedNewSize = new Dimension( (int) Math.max( Math.min( newSize.getWidth(), comp.getMaximumSize().getWidth() ),
																		comp.getMinimumSize().getWidth() ),
														(int) Math.max( Math.min( newSize.getHeight(), comp.getMaximumSize().getHeight() ),
																		comp.getMinimumSize().getHeight() )
													);
			comp.setPreferredSize( checkedNewSize );
		}
	}

	protected void setBounds( Rectangle newBounds, double zoomFactor )
	{
		if( SwingUtilities.isEventDispatchThread() )
		{
			double rw = _jsp.getResizeWeight();

//			if( ! _inDrag )
			{
				_jsp.setResizeWeight( 0.0D );

//				SwingUtilities.invokeLater( () -> restOfSetBounds( newBounds, zoomFactor, rw ) );
				restOfSetBounds( newBounds, zoomFactor, rw );
			}
//			else
//				restOfSetBounds( newBounds, zoomFactor, rw );
		}
		else
			SwingUtilities.invokeLater( () -> setBounds( newBounds, zoomFactor ) );
	}

	protected void restOfSetBounds( Rectangle newBounds, double zoomFactor, double rw )
	{
		if( _jsp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
		{
			resizeChangingPreferredSize( _jsp.getLeftComponent(), zoomFactor, calculateBounds_JSplitPane( _jsp, newBounds, _newDividerLocation, LEFT ) );
			resizeChangingPreferredSize( _jsp.getRightComponent(), zoomFactor, calculateBounds_JSplitPane( _jsp, newBounds, _newDividerLocation, RIGHT ) );
		}
		else if( _jsp.getOrientation() == JSplitPane.VERTICAL_SPLIT )
		{
			resizeChangingPreferredSize( _jsp.getTopComponent(), zoomFactor, calculateBounds_JSplitPane( _jsp, newBounds, _newDividerLocation, TOP ) );
			resizeChangingPreferredSize( _jsp.getBottomComponent(), zoomFactor, calculateBounds_JSplitPane( _jsp, newBounds, _newDividerLocation, BOTTOM ) );
		}

		super.setBounds(newBounds, zoomFactor);

		setNewDividerLocation( _jsp, _newDividerLocation );

		_jsp.resetToPreferredSizes();

//		if( ! _inDrag )
			_jsp.setResizeWeight( rw );

		if( //_lastWasDrag ||
			! isAlreadyZoomed() )//||
//			isServingResizureFromListenerOfUser() )
		{
			_jspSizeOfLastDraggedDividerLocationEvent = new Dimension( newBounds.width, newBounds.height );
			_jspSizeOfLastForExcess = _jspSizeOfLastDraggedDividerLocationEvent;
			_lastDraggedDividerLocation = _newDividerLocation;
		}

		_lastWasDrag = false;
	}

	protected void setNewDividerLocation( JSplitPane jsp, int newDividerLocation )
	{
//		if( SwingUtilities.isEventDispatchThread() )
		{
//			double rw = jsp.getResizeWeight();
//			jsp.setResizeWeight( 0.0D );
			
			jsp.setDividerLocation( newDividerLocation );

//			jsp.setResizeWeight( rw );
		}
//		else
		{
//			SwingUtilities.invokeLater( () -> setNewDividerLocation( jsp, newDividerLocation ) );
		}
	}

	// Five methods from MouseListener:
	/** Called when the mouse has been clicked on a component. */
	public void mouseClicked(MouseEvent e)
	{
	}

	/** Called when the mouse enters a component. */
	public void mouseEntered(MouseEvent e)
	{
		_inDrag = false;
	}

	/** Called when the mouse exits a component. */
	public void mouseExited(MouseEvent e)
	{
		_inDrag = false;
	}

	/** Called when the mouse has been pressed. */
	public void mousePressed(MouseEvent e)
	{
		_inDrag = true;
		_lastWasDrag = true;
	}

	/** Called when the mouse has been released. */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		_inDrag = false;
		saveStateOfLastDraggedDividerLocation();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		this.execute(_newExpectedZoomParam);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
	}
}
