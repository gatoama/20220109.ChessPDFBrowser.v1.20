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
package com.frojasg1.applications.pdf2pgn.commandline;

import com.frojasg1.applications.pdf2pgn.commandline.args.Pdf2pgnApplicationContext;
import com.frojasg1.applications.pdf2pgn.commandline.args.Pdf2pgnArgsExtractor;
import com.frojasg1.applications.pdf2pgn.commandline.execution.Pdf2pgn;
import com.frojasg1.applications.pdf2pgn.commandline.result.Pdf2pgnResult;
import com.frojasg1.applications.pdf2pgn.commandline.startapp.OpenConfiguration;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.args.ArgsExtractorBase;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.OperationCancellation;
import com.frojasg1.general.progress.UpdatingProgress;
import com.frojasg1.general.streams.DeactivablePrintStream;
import com.frojasg1.general.streams.InOutErrStreamFunctions;
import com.frojasg1.libpdfbox.impl.PdfboxFactory;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class Main
{
	protected static final String APPLICATION_NAME = "pdf2pgn";

	protected Pdf2pgnArgsExtractor _argsExtractor = null;

	protected Pdf2pgnApplicationContext _applicationContext = null;

	Pdf2pgn _pdf2pgn;

	protected static DeactivablePrintStream _out = null;
	protected static DeactivablePrintStream _err = null;

	public static void main( String[] args )
	{
		try
		{
			replaceStreams();
			if( ! isDebug(args) )
				deactivateOutputs();

/*
			ApplicationConfiguration.create( APPLICATION_NAME );
			ApplicationConfiguration appliConf = ApplicationConfiguration.instance();
			InitGenericCommandLine.init( appliConf );
			BigMathInitResult res = BigMathLibraryInit.init( ApplicationConfiguration.instance() );
*/
			Main main = new Main();
			main.execute( getArgs( args ) );
		}
		catch( Exception ex )
		{
			activateOutputs();
			ex.printStackTrace();
			System.exit(1);
		}
	}

	protected static boolean isDebug( String[] args )
	{
		return( ArrayFunctions.instance().contains(args, ArgsExtractorBase.DEBUG ) );
	}

	protected static String[] getArgs( String[] args )
	{
//		String[] result = new String[]{ "-inputFilename=pdf2pgh.sh" };
		String[] result = args;
		return( result );
	}

	protected static void replaceStreams()
	{
		_out = new DeactivablePrintStream( System.out );
		_err = new DeactivablePrintStream( System.err );

		System.setOut( _out );
		System.setErr( _err );
	}

	protected static void deactivateOutputs()
	{
		activateOutputs( false );
	}

	protected static void activateOutputs()
	{
		activateOutputs( true );
	}

	protected static void activateOutputs( boolean value )
	{
		_out.setActive( value );
		_err.setActive( value );
	}

	protected void help()
	{
		activateOutputs();
		_argsExtractor.help();
	}

	protected Pdf2pgnArgsExtractor createAndProcessArgs( String[] args )
	{
		_argsExtractor = new Pdf2pgnArgsExtractor();

		try
		{
			_argsExtractor.process( args );
		}
		catch( Exception ex )
		{
			activateOutputs();
			System.out.println( ex.getMessage() );
			System.exit(1);
		}

		return( _argsExtractor );
	}

	protected void showHelpAndExitIfNecessary()
	{
		if( ( _argsExtractor.getArgs().length == 0 ) || _argsExtractor.helpPresent() )
		{
			help();
			System.exit(0);
		}
	}

	public void execute( String[] args ) throws ConfigurationException, IOException
	{
		init();

		_argsExtractor = createAndProcessArgs( args );

		showHelpAndExitIfNecessary();

		_argsExtractor.validate();

		updateContext();

		_pdf2pgn = createPdf2pgn( this::executed );
		_pdf2pgn.execute();
	}

	protected Pdf2pgn createPdf2pgn( Consumer<Pdf2pgnResult> callback )
	{
		Pdf2pgn result = new Pdf2pgn( getApplicationContext(), callback, createUpdatingProgress() );
		return( result );
	}

	public void executed( Pdf2pgnResult result )
	{
		int resultCode = 5;
		try
		{
			activateOutputs();
			if( result != null )
			{
				Exception ex = result.getException();
				if( ex != null )
					throw( ex );

				if( result.wasSuccessful())
					System.out.println( "Successful execution. Output filename: " + _argsExtractor.getOutputFileName() );
				else if( result.getException() != null )
					result.getException().printStackTrace();
				else
					System.err.println( "Internal error: " + result.getErrorMessage() );

				resultCode = result.wasSuccessful() ? 0 : 1;
			}
			else
			{
				System.err.println( "Empty result" );
				resultCode = 5;
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			resultCode = 3;
		}
		finally
		{
			_pdf2pgn.releaseResources();
			System.exit(resultCode);
		}

		System.exit(0);
	}

	protected void init() throws ConfigurationException, IOException
	{
		_applicationContext = createInitContext();
		OpenConfiguration oc = new OpenConfiguration(_applicationContext);
		oc.init();
		oc.importConfigurationIfNecessary();
		oc.initializeAfterImportingConfiguration();
	}

	protected void updateContext()
	{
		_applicationContext.setInputFileName( _argsExtractor.getInputFileName() );
		_applicationContext.setOutputFileName( _argsExtractor.getOutputFileName() );
		_applicationContext.setInitialPageNumber( _argsExtractor.getInitialPageNumber() );
		_applicationContext.setFinalPageNumber( _argsExtractor.getFinalPageNumber() );
		_applicationContext.setLanguageToParseGames( _argsExtractor.getChessInputLanguage() );
		_applicationContext.setLettersForPiecesToParseGames(_argsExtractor.getLettersForPieces());
	}

	protected Pdf2pgnApplicationContext getApplicationContext()
	{
		return( _applicationContext );
	}

	protected PrintStream out()
	{
		return( InOutErrStreamFunctions.instance().getOriginalOutStream() );
	}

	protected UpdatingProgress createUpdatingProgress()
	{
		UpdatingProgress result = new UpdatingProgress() {
			protected int _num = 0;
			protected int _lastPercent = 0;
			@Override
			public void up_childStarts() {
				if( _num == 0 )
					out().println( "Started to extract games" );
			}

			protected int getPercentage( double currentRoundCompletedOverOne )
			{
				return( (int) Math.floor( (_num + currentRoundCompletedOverOne ) / 0.03 ) );
			}

			@Override
			public void up_updateProgressFromChild(double completedOverOne) throws CancellationException {
				int percent = getPercentage( completedOverOne );
				if( percent != _lastPercent )
				{
					_lastPercent = percent;
					out().println( String.format( "Progress:   %d%%", _lastPercent ) );
				}
			}

			@Override
			public void up_childEnds() throws CancellationException {
				_num++;
				if( _num == 3 )
					out().println( "Started to extract games" );
			}

			@Override
			public void up_setOperationCancellation(OperationCancellation oc) {
			}

			@Override
			public OperationCancellation up_getOperationCancellation() {
				return( null );
			}

			@Override
			public void up_setDebug(boolean debug) {
			}
			
		};
		return( result );
	}

	protected Pdf2pgnApplicationContext createInitContext()
	{
		Pdf2pgnApplicationContext result = new Pdf2pgnApplicationContext();
		result.setApplicationName(APPLICATION_NAME);
		result.setPdfFactory( new PdfboxFactory() );

		return( result );
	}
}
