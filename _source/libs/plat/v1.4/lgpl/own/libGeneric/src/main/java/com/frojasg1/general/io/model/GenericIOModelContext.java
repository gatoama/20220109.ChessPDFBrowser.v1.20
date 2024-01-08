/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model;

import com.frojasg1.general.collection.ModifiedStatus;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface GenericIOModelContext<MM extends ModifiedStatus> {
	
	public String getCharsetName();
	public GenericIOModelContext<MM> setCharsetName(String charsetName);

	public String getFileName();
	public GenericIOModelContext<MM> setFileName(String fileName);

	public MM getModel();
	public GenericIOModelContext<MM> setModel( MM model );

	public boolean hasBeenModfied();
}
