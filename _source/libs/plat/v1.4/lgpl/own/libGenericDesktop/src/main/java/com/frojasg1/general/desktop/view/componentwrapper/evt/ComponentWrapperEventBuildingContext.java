/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.evt;

import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentWrapperEventBuildingContext<EVT> {
	protected Long _eventType;
	protected Class<EVT> _eventClass;
	protected Supplier<EVT> _constructor;

	public Long getEventType() {
		return _eventType;
	}

	public void setEventType(Long _eventType) {
		this._eventType = _eventType;
	}

	public Class<EVT> getEventClass() {
		return _eventClass;
	}

	public void setEventClass(Class<EVT> _eventClass) {
		this._eventClass = _eventClass;
	}

	public Supplier<EVT> getConstructor() {
		return _constructor;
	}

	public void setConstructor(Supplier<EVT> _constructor) {
		this._constructor = _constructor;
	}
}
