/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface BoundedValueWrapper<VV> {

	public void setBounds( VV min, VV max );
	public VV getMin();
	public VV getMax();
}
