/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable;

import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEvent;
import javax.swing.JTable;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperEventBase extends JComponentWrapperEvent<JTable> {

	public JTableWrapperEventBase( long eventType )
	{
		super( eventType );
	}
}
