/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper;

import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEvent;
import com.frojasg1.general.listeners.filterevents.ListOfDefaultEventContextListenersBase;
import java.util.function.Function;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfJComponentWrapperEventContextListenersBase<JC extends JComponent, EV extends JComponentWrapperEvent<JC>>
	extends ListOfDefaultEventContextListenersBase<EV, Long> {

	public ListOfJComponentWrapperEventContextListenersBase()
	{
		super( Function.identity(), JComponentWrapperEvent::getEventType );
	}
}
