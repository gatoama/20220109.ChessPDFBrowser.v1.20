/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model.list;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.io.*;
import com.frojasg1.general.io.model.GenericIOModelContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	RRO - Record for ouptut. It can be a parent of RRI, to let write any implementation
	RRI - Record for input. The exact type.
	MMI - Model for input (it is the most particular, as it reads from disk exactly the type expected)
	MCI - Model Context for input
*/
public abstract class LinesIOListModelGenericIOBase<RRO,
											RRI extends RRO, MMI extends IOListModelBase<RRI>, MCI extends GenericIOModelContext<MMI>>
	extends GenericIOBaseParent<MMI, MCI>
	implements LinesIOListModelGenericIO<RRO, RRI, MMI, MCI>
{
	protected LinesIOListModelTranslator<RRO, RRI, MMI> _linesModelTranslator;

	public LinesIOListModelGenericIOBase( LinesIOListModelTranslator<RRO, RRI, MMI> linesModelTranslator )
	{
		super( linesModelTranslator );
		_linesModelTranslator = linesModelTranslator;
	}

	public LinesIOListModelTranslator<RRO, RRI, MMI> getLinesModelTranslator() {
		return _linesModelTranslator;
	}

	public void setLinesModelTranslator(LinesIOListModelTranslator<RRO, RRI, MMI> _linesModelTranslator) {
		this._linesModelTranslator = _linesModelTranslator;
	}

	@Override
	public boolean erase( String fileName )
	{
		return( new File( fileName ).delete() );
	}

	protected List<String> createLines( GenericIOModelContext<? extends IOListModelBase<? extends RRO>> modelContext )
	{
		List<String> result = null;
		if( modelContext != null )
		{
			LinesIOListModelTranslator<RRO, RRI, MMI> translator = getLinesModelTranslator();
			if( translator != null )
				result = translator.modelToLines(modelContext.getModel());
		}

		if( result == null )
			result = new ArrayList<>();

		return( result );
	}

	@Override
	public abstract MCI createEmptyModelContext();

	protected String getCharsetName( GenericIOModelContext<? extends IOListModelBase<? extends RRO>> modelContext )
	{
		String result = null;
		
		if( modelContext != null )
			result = modelContext.getCharsetName();

		if( result == null )
			result = StandardCharsets.UTF_8.name();

		return( result );
	}

	protected void write(GenericIOModelContext<? extends IOListModelBase<? extends RRO>> modelContext,
						List<String> lines, OutputStream os) throws IOException {
		try( OutputStream os1 = os )
		{
			if( lines != null )
				for( String line: lines )
					os1.write( (line + "\n").getBytes( getCharsetName(modelContext) ) );
		}
	}

	@Override
	public void write( GenericIOModelContext<? extends IOListModelBase<? extends RRO>> modelContext, String fileName ) throws IOException
	{
		Exception ex = ExecutionFunctions.instance().safeMethodExecution( () -> write( modelContext, new FileOutputStream(fileName) ) );

		if( ex == null )
			modelContext.setFileName(fileName);
		else
			throw( new RuntimeException( "Error when saving model", ex ) );
	}

	@Override
	public void write( GenericIOModelContext<? extends IOListModelBase<? extends RRO>> modelContext, OutputStream os ) throws IOException
	{
		List<String> lines = createLines(modelContext);

		write(modelContext, lines, os);
	}
}
