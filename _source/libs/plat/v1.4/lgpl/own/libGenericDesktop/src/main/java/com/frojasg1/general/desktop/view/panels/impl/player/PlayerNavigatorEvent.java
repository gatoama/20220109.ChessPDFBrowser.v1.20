/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.impl.player;

import com.frojasg1.general.desktop.view.panels.evt.PanelEventBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PlayerNavigatorEvent extends PanelEventBase<PlayerNavigatorEventType>
{
	public PlayerNavigatorEvent( PlayerNavigatorEventType type )
	{
		super(type);
	}
}
