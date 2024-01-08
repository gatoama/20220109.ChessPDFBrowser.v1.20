/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.evt;

import com.frojasg1.general.desktop.view.componentwrapper.EventTypeAttribute;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JComponentWrapperEvent<JC extends JComponent> implements EventTypeAttribute
{
	protected JC _jComponent;
	protected long _eventType;

	public JComponentWrapperEvent( long eventType )
	{
		_eventType = eventType;
	}

	public JC getjComponent() {
		return _jComponent;
	}

	public void setjComponent(JC _jComponent) {
		this._jComponent = _jComponent;
	}

	@Override
	public long getEventType() {
		return _eventType;
	}

	protected void setEventType(long _eventType) {
		this._eventType = _eventType;
	}
}
