/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class WrapperEventSource
{
	protected Object _source;
	protected WrapperEventSource _parentSource;

	public Object getSource() {
		return _source;
	}

	public void setSource(Object _source) {
		this._source = _source;
	}

	public WrapperEventSource getParentSource() {
		return _parentSource;
	}

	public void setParentSource(WrapperEventSource _parentSource) {
		this._parentSource = _parentSource;
	}
}
