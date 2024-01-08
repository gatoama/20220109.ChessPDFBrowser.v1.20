/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.io.model.GenericIOModelContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.frojasg1.general.collection.ModifiedStatus;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	MM - Model
	MC - Model Context
*/
public abstract class GenericIOBase<MM extends ModifiedStatus, MC extends GenericIOModelContext<MM>>
	extends GenericIOBaseParent<MM, MC>
	implements GenericIO<MM, MC>
{
	protected LinesAndModelTranslator<MM, MM> _linesModelTranslator;

	public GenericIOBase( LinesAndModelTranslator<MM, MM> linesModelTranslator )
	{
		super( linesModelTranslator );
		_linesModelTranslator = linesModelTranslator;
	}

	public LinesAndModelTranslator<MM, MM> getLinesModelTranslator() {
		return _linesModelTranslator;
	}

	public void setLinesModelTranslator(LinesAndModelTranslator<MM, MM> _linesModelTranslator) {
		this._linesModelTranslator = _linesModelTranslator;
	}

	@Override
	public void write(MC modelContext, String fileName) throws IOException {
		Exception ex = ExecutionFunctions.instance().safeMethodExecution( () -> write( modelContext, new FileOutputStream(fileName) ) );

		if( ex == null )
			modelContext.setFileName(fileName);
		else
			throw( new RuntimeException( "Error when saving model", ex ) );
	}

	@Override
	public boolean erase( String fileName )
	{
		return( new File( fileName ).delete() );
	}

	@Override
	public void write(MC modelContext, OutputStream os) throws IOException {
		List<String> lines = createLines(modelContext);

		write(modelContext, lines, os);
	}

	protected List<String> createLines( MC modelContext )
	{
		List<String> result = null;
		if( modelContext != null )
		{
			LinesAndModelTranslator<MM, MM> translator = getLinesModelTranslator();
			if( translator != null )
				result = translator.modelToLines(modelContext.getModel());
		}

		if( result == null )
			result = new ArrayList<>();

		return( result );
	}

	@Override
	public abstract MC createEmptyModelContext();

	protected void write(MC modelContext, List<String> lines, OutputStream os) throws IOException {
		try( OutputStream os1 = os )
		{
			if( lines != null )
				for( String line: lines )
					os1.write( (line + "\n").getBytes(modelContext.getCharsetName()) );
		}
	}
}
