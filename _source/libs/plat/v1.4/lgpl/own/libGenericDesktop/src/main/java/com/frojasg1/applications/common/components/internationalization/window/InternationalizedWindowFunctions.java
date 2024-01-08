/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.applications.common.components.internationalization.window;

import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.GenericDesktopConstants;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class InternationalizedWindowFunctions implements InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "InternationalizedWindowFunctions.properties";

	public static final String CONF_VALIDATION_ERROR = "VALIDATION_ERROR";

	protected static InternationalizedWindowFunctions _instance = null;

	protected InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																												 GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR );

	public static void changeInstance( InternationalizedWindowFunctions instance )
	{
		_instance = instance;
	}

	public static InternationalizedWindowFunctions instance()
	{
		if( _instance == null )
			_instance = new InternationalizedWindowFunctions();
		
		return( _instance );
	}

	public InternationalizedWindowFunctions()
	{
		registerInternationalizedStrings();
	}

	protected void registerInternationalizedStrings()
	{
		registerInternationalString( CONF_VALIDATION_ERROR, "VALIDATION ERROR" );
	}

	public String validate( ViewComponent window, ExecutionFunctions.UnsafeMethod function )
	{
		String errorMessage = null;
		Exception ex1 = null;
		try
		{
			function.run();
		}
		catch( ValidationException ve )
		{
			ex1 = ve;
			ve.printStackTrace();
			errorMessage = ve.getMessage();
		}
		catch( Exception ex )
		{
			ex1 = ex;
			errorMessage = ex.getMessage();
			ex.printStackTrace();
		}

		if( ex1 != null )
		{
			processExceptionInValidation( window, ex1 );
			if( ex1 instanceof ValidationException )
				processValidationException( (ValidationException) ex1 );
		}

		return( errorMessage );
	}

	public void processExceptionInValidation( ViewComponent window, Exception ex )
	{
		boolean showError = true;
		if( ex instanceof ValidationException )
			showError = ! ( (ValidationException) ex ).getDoNotShowWarning();

		if( showError )
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog(window,
						ex.getMessage(),
						getInternationalString(CONF_VALIDATION_ERROR),
						DialogsWrapper.ERROR_MESSAGE );
	}

	public InternationalizedWindow getInternationalizedWindowAncestor( Component comp )
	{
		InternationalizedWindow result = null;
		if( comp != null )
		{
			Component ancestor = ViewFunctions.instance().getRootAncestor(comp);
			if( ancestor instanceof InternationalizedWindow )
				result = (InternationalizedWindow) ancestor;
		}

		return( result );
	}

	protected void processValidationException( ValidationException ve )
	{
		if( ve != null )
		{
			InternationalizedWindow intWin = getInternationalizedWindowAncestor( ve.getComponentWithException() );

			if( intWin != null )
				SwingUtilities.invokeLater( () -> intWin.focusAndHighlightComponent( ve.getViewComponentWithException() ) );
		}
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}
}
