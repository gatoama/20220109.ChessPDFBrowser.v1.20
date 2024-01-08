/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.mouse;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MouseAdapterBase extends MouseAdapter
{
	protected MouseEventType _previousMouseEvent;

	public MouseEventType getPreviousMouseEvent() {
		return _previousMouseEvent;
	}

	public void setPreviousMouseEvent(MouseEventType _previousMouseEvent) {
		this._previousMouseEvent = _previousMouseEvent;
	}

	protected boolean previousEventWas( MouseEventType mouseEventType )
	{
		return( getPreviousMouseEvent() == mouseEventType );
	}

	protected boolean previousEventWasAny( MouseEventType ... mouseEventTypes )
	{
		return( Arrays.stream(mouseEventTypes).anyMatch( this::previousEventWas ) );
	}

	public void mouseClickedChild(MouseEvent e) { }
    public void mousePressedChild(MouseEvent e) { }
    public void mouseReleasedChild(MouseEvent e) { }
    public void mouseEnteredChild(MouseEvent e) { }
    public void mouseExitedChild(MouseEvent e) { }
    public void mouseWheelMovedChild(MouseWheelEvent e) { }
    public void mouseDraggedChild(MouseEvent e) { }
    public void mouseMovedChild(MouseEvent e) { }

		
	@Override
    public final void mouseClicked(MouseEvent e)
	{
		mouseClickedChild(e);

		setPreviousMouseEvent(MouseEventType.MOUSE_CLICKED);
	}

	@Override
    public final void mousePressed(MouseEvent e)
	{
		mousePressedChild(e);

		setPreviousMouseEvent(MouseEventType.MOUSE_PRESSED);
	}

	@Override
    public final void mouseReleased(MouseEvent e)
	{
		mouseReleasedChild(e);

		setPreviousMouseEvent(MouseEventType.MOUSE_RELEASED);
	}

	@Override
    public final void mouseEntered(MouseEvent e)
	{
		mouseEnteredChild(e);

		setPreviousMouseEvent(MouseEventType.MOUSE_ENTERED);
	}

	@Override
    public final void mouseExited(MouseEvent e)
	{
		mouseExitedChild(e);

		setPreviousMouseEvent(MouseEventType.MOUSE_EXITED);
	}

	@Override
    public final void mouseWheelMoved(MouseWheelEvent e)
	{
		mouseWheelMovedChild(e);

		setPreviousMouseEvent(MouseEventType.MOUSE_WHEEL_MOVED);
	}

	@Override
    public final void mouseDragged(MouseEvent e)
	{
		mouseDraggedChild(e);

		setPreviousMouseEvent(MouseEventType.MOUSE_DRAGGED);
	}

	@Override
    public final void mouseMoved(MouseEvent e)
	{
		mouseMovedChild(e);

		setPreviousMouseEvent(MouseEventType.MOUSE_MOVED);
	}
}
