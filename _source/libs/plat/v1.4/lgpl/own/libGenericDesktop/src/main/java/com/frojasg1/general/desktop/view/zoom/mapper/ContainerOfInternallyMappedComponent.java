/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.zoom.mapper;

import javax.swing.JPopupMenu;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ContainerOfInternallyMappedComponent {

	public void addInternallyMappedComponent( InternallyMappedComponent im );

	public void addPopupMenu( JPopupMenu jPopupMenu );
}
