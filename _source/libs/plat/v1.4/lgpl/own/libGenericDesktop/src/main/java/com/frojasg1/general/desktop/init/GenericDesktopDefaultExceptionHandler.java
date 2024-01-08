/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.init;

import com.frojasg1.generic.GenericFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericDesktopDefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

	protected static final String OUT_OF_MEMORY = "Out of memory!!";
	private static Logger LOGGER = LoggerFactory.getLogger(GenericDesktopDefaultExceptionHandler.class);

	protected int _outOfMemoryShown = 0;

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOGGER.info("Unhandled exception caught!", e);

		if( (_outOfMemoryShown < 7) && ( e instanceof OutOfMemoryError ) )
		{
			_outOfMemoryShown++;
			GenericFunctions.instance().getDialogsWrapper().showError(null, OUT_OF_MEMORY );
		}
	}
}
