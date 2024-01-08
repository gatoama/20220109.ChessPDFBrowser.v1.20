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
package com.frojasg1.libpdf.threads;

import com.frojasg1.libpdf.api.PdfDocumentWrapper;

/**
 *
 * @author Usuario
 */
public class LoadPDFThread extends Thread
{
	protected LoadPdfControllerInterface _controller = null;
	protected String _fileName = null;
	protected PdfDocumentWrapper _pdfDocument = null;

	public LoadPDFThread( LoadPdfControllerInterface controller, String fileName,
							PdfDocumentWrapper pdfDocument )
	{
		_controller = controller;
		_fileName = fileName;
		_pdfDocument = pdfDocument;
	}

	@Override
	public void run()
	{
		try
		{
			if( _controller != null )	_controller.startLoading();

			_pdfDocument.loadPdf( _fileName );

			if( _controller != null )
				_controller.newPdfLoaded( _pdfDocument );

		}
		catch( Throwable th )
		{
			th.printStackTrace();
			if( _controller != null )
			{
				_controller.showLoadingError( "Error loading PDF file " + _fileName + ". ERROR: " + th.getMessage(), "Error opening file" );
			}
		}
		finally
		{
/*
			if( _pdfDocument != null )
			{
				try
				{
					_pdfDocument.close();
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
*/
			if( _controller != null )
				_controller.endLoading();
		}
	}

	public PdfDocumentWrapper getPDFDocument()
	{
		return( _pdfDocument );
	}
}
