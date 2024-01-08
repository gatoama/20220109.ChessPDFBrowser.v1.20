/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.transfer.component;

import com.frojasg1.general.desktop.mouse.MouseAdapterBase;
import com.frojasg1.general.desktop.mouse.MouseEventType;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.transfer.DragAndDropEvent;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.function.Predicate;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DragAndDropComponentContextBase<EV extends DragAndDropEvent>
		implements ReleaseResourcesable
{

	protected MouseAdapter _mouseListener;
	protected KeyAdapter _keyListener;

	protected Point _dragPoint;

	protected JComponent _component;

	protected boolean _isDraggingFiles = false;
	protected boolean _isDragging = false;

	protected Predicate<Point> _canStartDraggingFunction;

	protected Runnable _dropListener;
	protected Runnable _deleteListener;

	protected Runnable _startDraggingListener;
	protected Runnable _stopDraggingListener;

	public DragAndDropComponentContextBase( JComponent component )
	{
		_component = component;
	}

	protected DragAndDropComponentContextBase<EV> init()
	{
		addListeners();

		return( this );
	}

	protected void addListeners()
	{
		if( _mouseListener == null )
		{
			_mouseListener = createMouseListener();
			getComponent().addMouseListener(_mouseListener);
			getComponent().addMouseWheelListener(_mouseListener);
			getComponent().addMouseMotionListener(_mouseListener);
		}

		if( _keyListener == null )
		{
			_keyListener = createKeyListener();
			getComponent().addKeyListener(_keyListener);
		}
	}

	public Runnable getStartDraggingListener() {
		return _startDraggingListener;
	}

	public void setStartDraggingListener(Runnable _startDraggingListener) {
		this._startDraggingListener = _startDraggingListener;
	}

	public Runnable getStopDraggingListener() {
		return _stopDraggingListener;
	}

	public void setStopDraggingListener(Runnable _stopDraggingListener) {
		this._stopDraggingListener = _stopDraggingListener;
	}

	public Runnable getDropListener() {
		return _dropListener;
	}

	public void setDropListener(Runnable _dropListener) {
		this._dropListener = _dropListener;
	}

	public boolean isDragging() {
		return _isDragging;
	}

	public void setIsDragging(boolean value) {
		boolean prev = isDragging();

		this._isDragging = value;
		
		if( !prev && value )
			notifyStartDragging();
		else if( prev && !value )
			notifyStopDragging();
	}

	public boolean canStartDragging( Point point ) {
		boolean result = false;
		if( _canStartDraggingFunction != null )
			result = _canStartDraggingFunction.test( point );

		return( result );
	}

	public void setCanStartDraggingFunction(Predicate<Point> _canDragFunction) {
		this._canStartDraggingFunction = _canDragFunction;
	}

	public JComponent getComponent() {
		return _component;
	}

	protected MouseAdapter createMouseListener()
	{
		return( new DragAndDropMouseAdapter() );
	}

	protected KeyAdapter createKeyListener()
	{
		return( new DragAndDropKeyAdapter() );
	}

	protected void setDragPoint( Point point )
	{
		_dragPoint = point;
	}

	protected void updateDragPoint( Point point )
	{
		if( point == null )
		{
			setIsDragging( false );
			setIsDraggingFiles( false );
		}

		if( getDragPoint() != null )
		{
			setDragPoint( point );
			getComponent().repaint();
		}
	}

	public Point getDragPoint()
	{
		return( _dragPoint );
	}

	public void processDragAndDropEvent( EV evt )
	{
		if( evt != null )
		{
			if( evt.getType().isCanImportEvt() )
			{
				setDragPoint( evt.getDropLocation() );
				setIsDraggingFiles( true );
			}
			else if( evt.getType().isDroppedEvt() )
			{
				updateDragPoint( null );
//				setIsDraggingFiles( false );
			}
		}
	}

	public boolean isDraggingFiles() {
		return _isDraggingFiles;
	}

	public void setIsDraggingFiles(boolean _isDraggingFiles) {
		this._isDraggingFiles = _isDraggingFiles;
	}

	@Override
	public void releaseResources()
	{
		removeListeners();
	}

	protected void removeListeners()
	{
		getComponent().removeMouseListener(_mouseListener);
		getComponent().removeMouseWheelListener(_mouseListener);
		getComponent().removeMouseMotionListener(_mouseListener);

		getComponent().removeKeyListener(_keyListener);
	}

	protected void notifyGen( Runnable listener )
	{
		if( listener != null )
			listener.run();
	}

	protected void notifyStartDragging()
	{
		notifyGen( getStartDraggingListener() );
	}

	protected void notifyStopDragging()
	{
		notifyGen( getStopDraggingListener() );
	}

	protected void notifyDrop()
	{
		notifyGen( getDropListener() );
	}

	protected void notifyDeletePressed()
	{
		notifyGen( getDeleteListener() );
	}

	public Runnable getDeleteListener() {
		return _deleteListener;
	}

	public void setDeleteListener(Runnable _deleteListener) {
		this._deleteListener = _deleteListener;
	}

	protected class DragAndDropMouseAdapter extends MouseAdapterBase
	{

		@Override
		public void mouseReleasedChild(MouseEvent evt)
		{
			if( isDragging() )
				notifyDrop();
			else
				notifyStopDragging();

			SwingUtilities.invokeLater( () -> updateDragPoint( null ) );
		}

		@Override
		public void mousePressedChild(MouseEvent evt)
		{
			Point point = evt.getPoint();
			if( canStartDragging( point ) )
			{
				setDragPoint( point );
//				setIsDragging( true );
			}
		}

		@Override
	    public void mouseWheelMovedChild(MouseWheelEvent e)
		{
			updateDragPoint( e.getPoint() );
		}

		@Override
		public void mouseDraggedChild(MouseEvent e)
		{
			Point point = e.getPoint();
			if( ! ComponentFunctions.instance().getClip( _component ).contains(point) )
				point = null;
			else if( !isDraggingFiles() && ( getDragPoint() != null ) )
				setIsDragging( true );

			updateDragPoint( point );
		}

		@Override
		public void mouseMovedChild(MouseEvent e)
		{
			
//			if( previousEventWasAny( MouseEventType.MOUSE_WHEEL_MOVED,
//										MouseEventType.MOUSE_DRAGGED ) )
			if(MouseFunctions.isMainButtonPressed(e))
			{
				Point point = e.getPoint();
				updateDragPoint( point );
			}
			else
				updateDragPoint( null );
		}

		@Override
		public void mouseExitedChild(MouseEvent e)
		{
			updateDragPoint( null );
		}
	}

	protected class DragAndDropKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			if( e.getKeyCode() == KeyEvent.VK_ESCAPE )
				updateDragPoint(null);
			else if( e.getKeyCode() == KeyEvent.VK_DELETE )
				notifyDeletePressed();
		}
	}
}
