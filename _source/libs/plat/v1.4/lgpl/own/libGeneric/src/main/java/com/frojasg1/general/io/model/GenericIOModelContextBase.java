/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model;

import com.frojasg1.general.collection.ModifiedStatus;
import com.frojasg1.general.FileFunctions;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericIOModelContextBase<MM extends ModifiedStatus> implements GenericIOModelContext<MM> {

	protected String _fileName;
	protected String _charsetName;
	protected MM _model;

	@Override
	public String getCharsetName() {
		return( _charsetName );
	}

	@Override
	public GenericIOModelContextBase<MM> setCharsetName(String charsetName) {
		_charsetName = charsetName;
		return( this );
	}

	@Override
	public String getFileName() {
		return( _fileName );
	}

	@Override
	public GenericIOModelContextBase<MM> setFileName(String fileName) {
		_fileName = fileName;

		return( this );
	}

	@Override
	public MM getModel() {
		return( _model );
	}

	@Override
	public GenericIOModelContextBase<MM> setModel(MM model) {
		_model = model;

		return( this );
	}

	@Override
	public boolean hasBeenModfied()
	{
		boolean result = false;
		if( getModel() != null )
			result = getModel().hasBeenModified();

		return( result );
	}

	protected String getDirName( String fileName )
	{
		String result = null;
		if( fileName != null )
			result = FileFunctions.instance().getDirName(fileName);

		return( result );
	}
}
