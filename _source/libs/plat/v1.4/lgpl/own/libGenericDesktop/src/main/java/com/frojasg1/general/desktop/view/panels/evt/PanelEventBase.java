/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.evt;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	ET - EventType
*/
public class PanelEventBase<ET> {
	protected ET _type;

	public PanelEventBase(ET _type) {
		this._type = _type;
	}

	public ET getType() {
		return _type;
	}
}
