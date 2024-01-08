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
package com.frojasg1.general.desktop.startapp;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.application.version.DesktopApplicationVersion;
import com.frojasg1.general.desktop.generic.dialogs.impl.DesktopDialogsWrapper;
import com.frojasg1.general.desktop.execution.newversion.NewVersionQueryExecution;
import com.frojasg1.general.desktop.lookAndFeel.ToolTipLookAndFeel;
import com.frojasg1.general.desktop.view.license.GenericLicenseJDialog;
import com.frojasg1.general.desktop.view.splash.GenericBasicSplash;
import com.frojasg1.general.desktop.view.whatisnew.WhatIsNewJDialogBase;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.startapp.OpenConfigurationBase;
import java.awt.Component;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class StartApplicationBase<AC extends GenericDesktopInitContext>
{
	protected static final int DEFAULT_MINIMUM_NUMBER_OF_STEPS = 4;

	protected static final int WATING_FOR_IMPORT_CONFIGURATION = 1;
	protected static final int WATING_FOR_CHECK_LICENSES = 2;
	protected static final int WATING_FOR_ACCEPT_LICENSES = 3;
	protected static final int WATING_FOR_NEW_VERSION_QUERY = 4;
	protected static final int WATING_FOR_WHAT_IS_NEW = 5;
	protected static final int WATING_FOR_CREATE_MAIN_WINDOW = 6;
	protected static final int WATING_FOR_SHOW_MAIN_WINDOW = 7;
	
	protected boolean _exceptionThrown = false;

	protected GenericBasicSplash _splash = null;

	protected OpenConfigurationDesktopBase _oc = null;

	protected volatile int _totalNumberOfStepsToStart = -1;
	protected int _numberOfStepsCompleted = 0;

	protected boolean _licensesHaveBeenAccepted = false;

	protected Component _mainWindow = null;

	protected Boolean _wasANewVersion = null;

	protected Boolean _hasToShowWhatIsNew = null;

	protected AC _applicationContext;

	protected String _applicationName = null;

	public StartApplicationBase()
	{
	}

	public void setApplicationContext( AC applicationContext )
	{
		_applicationContext = applicationContext;
		if( applicationContext != null )
			_applicationName = applicationContext.getApplicationName();
	}

	protected String getApplicationName()
	{
		return( _applicationName );
	}

	protected void setApplicationName(String applicationName) {
		this._applicationName = applicationName;
	}

	protected AC getApplicationContext()
	{
		return( _applicationContext );
	}

	protected AC refineApplicationContext( AC applicationContext )
	{
		// can be overriden by derived classes
		return( applicationContext );
	}

	protected LookAndFeel getLookAndFeel()
	{
		return( ToolTipLookAndFeel.instance() );
	}

	protected void setLookAndFeel()
	{
		try
		{
			javax.swing.UIManager.setLookAndFeel( getLookAndFeel() );
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
	}

	public void startApplication( )
	{
		setDefaultExceptionHandler();
		setLookAndFeel();

		openConfigurationAndShowSplash();
	}

	protected void setDefaultExceptionHandler()
	{
		Thread.setDefaultUncaughtExceptionHandler( createUncaughtExceptionHandler() );
	}

	protected UncaughtExceptionHandler createUncaughtExceptionHandler()
	{
		return( new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println( "Uncaught" );
				e.printStackTrace();
			}
		});
	}

	protected abstract GenericBasicSplash createSplash();
	protected abstract OpenConfigurationDesktopBase createOpenConfiguration() throws ConfigurationException;

	protected OpenConfigurationDesktopBase createOpenConfigurationAndSetBaseParams() throws ConfigurationException
	{
		OpenConfigurationDesktopBase result = createOpenConfiguration();
		result.setWasANewVersionFunction( this::wasANewVersion );

		return( result );
	}


	protected void setCurrentActivityFromLabel( String label )
	{
		if( _splash != null )
			SwingUtilities.invokeLater( () -> _splash.setCurrentActivityFromLabel( label ) );
	}

	public OpenConfigurationBase getOpenConfiguration()
	{
		return( _oc );
	}

	protected void openConfigurationAndShowSplash()
	{
		try
		{
			_oc = createOpenConfigurationAndSetBaseParams();
			_oc.init();
			_totalNumberOfStepsToStart = getTotalNumberOfSteps();	// it needs _oc and ApplicationConfiguration to be created previously.

			_splash = createSplash();

			setCurrentActivityFromLabel( GenericBasicSplash.CONF_OPENING_CONFIGURATION );
//			_oc.setParent( _splash );

			java.awt.EventQueue.invokeLater(new Runnable()  {
				public void run() {
					try
					{
						incrementStepsCompleted();

						if( _splash != null )
						{
							SwingUtilities.invokeLater( () -> _splash.setVisible( true ) );
						}
					}
					catch( Throwable th )
					{
						th.printStackTrace();
					}
				}
			});

			GenericWaitWorker gww = new GenericWaitWorker( 500, WATING_FOR_IMPORT_CONFIGURATION );
			gww.execute();
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected abstract BaseApplicationConfigurationInterface getAppliConf();

	protected void initializeAfterImportingConfigurationAndPostprocessPreviousConfigurationAfterInit() throws ConfigurationException
	{
		_oc.initializeAfterImportingConfigurationAndPostprocessPreviousConfigurationAfterInit();
	}

	protected void eraseLanguageConfigurationFileRecursive( String fileName )
	{
		ExecutionFunctions.instance().safeMethodExecution( () ->
			FileFunctions.instance().eraseRecursive( getAppliConf().getDefaultLanguageBaseConfigurationFolder(),
													fileName ) );
	}

	protected void importConfiguration()
	{
		new Thread( () -> importConfiguration_internal() ).start();
	}

	protected void updateLatestStartedRelease()
	{
		getAppliConf().addDownloadFileWhatIsNewShown(getCurrentDownloadFile());
		ExecutionFunctions.instance().safeMethodExecution( () -> saveApplicationConfiguration() );
	}

	protected void importConfiguration_internal()
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			if( ( _splash != null ) && _oc.isNewVersion() )
				_splash.setCurrentActivityFromLabel( _splash.CONF_IMPORTING_CONFIGURATION );

			if( _oc.importConfigurationIfNecessary() )
			{
				this.incrementStepsCompleted();
			}

			if( ! isSameAsLastExecutionRelease() )
				_oc.removeParticularDocumentsOfMinorRelease();

			updateLatestStartedRelease();

			_licensesHaveBeenAccepted = getAppliConf().getLicensesHaveBeenAccepted();

			initializeAfterImportingConfigurationAndPostprocessPreviousConfigurationAfterInit();

			GenericWaitWorker gww = new GenericWaitWorker( 150, WATING_FOR_CHECK_LICENSES );
			SwingUtilities.invokeLater( () -> invoke( gww ) );
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected void invoke(GenericWaitWorker gww)
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			gww.execute();
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected void checkLicenses()
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			incrementStepsCompleted();

			if( !_licensesHaveBeenAccepted )
			{
				setCurrentActivityFromLabel( GenericBasicSplash.CONF_SHOWING_LICENSES );

				GenericWaitWorker gww = new GenericWaitWorker( 100, WATING_FOR_ACCEPT_LICENSES );
				gww.execute();
			}
			else
			{
				setCurrentActivityFromLabel( GenericBasicSplash.CONF_LOOKING_FOR_A_NEW_VERSION );

				GenericWaitWorker gww = new GenericWaitWorker( 350, WATING_FOR_NEW_VERSION_QUERY );
				gww.execute();
			}
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected abstract GenericLicenseJDialog createLicenseJDialog(Consumer<InternationalizationInitializationEndCallback> initializationCallback);
	protected abstract void saveApplicationConfiguration() throws ConfigurationException;

	protected void acceptLicenses()
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			GenericLicenseJDialog licenseJDialog = createLicenseJDialog(this::acceptLicenses);

			// case of simple application with no license for the user to accept
			if( licenseJDialog == null )
				licensesHaveBeenAccepted();
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected void acceptLicenses(InternationalizationInitializationEndCallback iiec)
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			GenericLicenseJDialog licenseJDialog = (GenericLicenseJDialog) iiec;
			licenseJDialog.setVisibleWithLock( true );

			if( !licenseJDialog.getLicensesHaveBeenAcceptedAndWeHaveToStart() )
			{
				_oc.eraseConfiguration();
				System.exit(1);
			}

			licenseJDialog.closeAndReleaseWindow();

			licensesHaveBeenAccepted();
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected void licensesHaveBeenAccepted() throws Exception
	{
		saveApplicationConfiguration();

		incrementStepsCompleted();

		setCurrentActivityFromLabel( GenericBasicSplash.CONF_LOOKING_FOR_A_NEW_VERSION );

		GenericWaitWorker gww = new GenericWaitWorker( 1000, WATING_FOR_NEW_VERSION_QUERY );
		gww.execute();
	}

	protected void newVersionQuery()
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			JFrame parentJFrame = null;
			boolean isStart = true;
			boolean onlyShowResultWhenThereIsANewDownloadableVersion = true;
			NewVersionQueryExecution executor = new NewVersionQueryExecution( parentJFrame,
																				getAppliConf(),
																				isStart,
																				onlyShowResultWhenThereIsANewDownloadableVersion,
																				this::processNewVersionQuery);

			new Thread( executor ).start();
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected void processNewVersionQuery( NewVersionQueryExecution executor )
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			if( executor.wasUrlClicked() )
				System.exit(0);

			incrementStepsCompleted();

			setCurrentActivityFromLabel( GenericBasicSplash.CONF_SHOWING_WHAT_IS_NEW );

			GenericWaitWorker gww = new GenericWaitWorker( 350, WATING_FOR_WHAT_IS_NEW );
			gww.execute();
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected WhatIsNewJDialogBase createWhatIsNewJDialogBase(Consumer<InternationalizationInitializationEndCallback> initializationCallback)
	{
		return( new WhatIsNewJDialogBase( getAppliConf(), initializationCallback ) );
	}

	protected void executeWhatIsNewAndNext( InternationalizationInitializationEndCallback iiec )
	{
		try
		{
			WhatIsNewJDialogBase win = (WhatIsNewJDialogBase) iiec;

			win.setVisibleWithLock(true);

			goToNextOfWhatIsNewStep();
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected void goToNextOfWhatIsNewStep() throws Exception
	{
		incrementStepsCompleted();

		setCurrentActivityFromLabel( GenericBasicSplash.CONF_CREATING_MAIN_WINDOW );

		GenericWaitWorker gww = new GenericWaitWorker( 100, WATING_FOR_CREATE_MAIN_WINDOW );
		gww.execute();
	}

	protected void whatIsNewStep()
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			if( hasToShowWhatIsNew() )
				createWhatIsNewJDialogBase(this::executeWhatIsNewAndNext );
			else
				goToNextOfWhatIsNewStep();
		}
		catch( Exception th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected abstract Component createMainWindow() throws ConfigurationException;

	protected void createMainWindowStep()
	{
		if( _exceptionThrown )
			System.exit(1);

		try
		{
			setApplicationContext( refineApplicationContext( getApplicationContext() ) );

			_mainWindow = createMainWindow();
			incrementStepsCompleted();

			GenericWaitWorker gww = new GenericWaitWorker( 500, WATING_FOR_SHOW_MAIN_WINDOW );
			gww.execute();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected String getProblemLaunchingApplicationString()
	{
		String result = null;

		if( _splash != null )
			result = _splash.getInternationalString( _splash.CONF_PROBLEM_LAUNCHING_THE_APPLICATION );
		else
			result = "Problem launching the application";

		return( result );
	}

	protected void eraseAboutRtfs()
	{
		safeRecursiveErasure( "AboutText.rtf" );
	}

	protected void safeRecursiveErasure( String singleFileName )
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> eraseRecursive(singleFileName) );
	}

	protected void eraseRecursive( String singleFileName ) throws IOException
	{
		FileFunctions.instance().eraseRecursive( getAppliConf().getDefaultLanguageBaseConfigurationFolder(), singleFileName );
	}

	protected void eraseConfigurationFilesForNewVersion()
	{
		eraseAboutRtfs();
	}

	protected void showMainWindow()
	{
		if( _exceptionThrown )
			System.exit(1);

		eraseConfigurationFilesForNewVersion();
		ExecutionFunctions.instance().safeMethodExecution( () -> eraseAboutRtfs() );

		try
		{
			_mainWindow.setVisible(true);

			GenericBasicSplash.free();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			showProblemLaunchingApplicationMessage();

			System.exit(1);
		}
	}

	protected void showProblemLaunchingApplicationMessage()
	{
		DesktopDialogsWrapper.instance().showMessageDialog(null,
						getProblemLaunchingApplicationString(),
						"ERROR", DialogsWrapper.ERROR_MESSAGE );
	}

	protected int getMinimumNumberOfSteps()
	{
		return( DEFAULT_MINIMUM_NUMBER_OF_STEPS );
	}

	protected String getCurrentDownloadFile()
	{
		return( DesktopApplicationVersion.instance().getDownloadFile() );
	}

	protected boolean isSameAsLastExecutionRelease()
	{
		boolean result = true;

		try
		{
			String currentDownloadFile = getCurrentDownloadFile();

			result = Objects.equals( getAppliConf().getLastExecutionDownloadFileName(), currentDownloadFile );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	protected boolean hasToShowWhatIsNew()
	{
		if( _hasToShowWhatIsNew == null )
		{
			try
			{
				String currentDownloadFile = getCurrentDownloadFile();

				_hasToShowWhatIsNew = !getAppliConf().hasBeenShownWhatIsNewOfDownloadFile(currentDownloadFile);
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}

			if( _wasANewVersion == null )
				_wasANewVersion = _hasToShowWhatIsNew;
		}

		return( _hasToShowWhatIsNew != null ? _hasToShowWhatIsNew : false );
	}

	protected Boolean wasANewVersion()
	{
		return( _wasANewVersion );
	}

	protected int calculateTotalNumberOfStepsToAdd()
	{
		int result = 0;

		if( _oc.hasToImport() )
			result++;

		if( !getAppliConf().getLicensesHaveBeenAccepted() )
			result++;

		if( hasToShowWhatIsNew() )
			result++;

		return( result );
	}
		
	protected int getTotalNumberOfSteps()
	{
		int totalNumberOfSteps = getMinimumNumberOfSteps();
		totalNumberOfSteps += calculateTotalNumberOfStepsToAdd();

		return( totalNumberOfSteps );
	}

	protected int getProgress( int numberOfCompletedSteps )
	{
		return( ( 100 * numberOfCompletedSteps ) / _totalNumberOfStepsToStart );
	}

	protected void incrementStepsCompleted()
	{
		if( _splash != null )
		{
			SwingUtilities.invokeLater( () -> _splash.setProgress( getProgress( ++_numberOfStepsCompleted ) ) );
		}
	}
/*
	protected abstract class StartAppBaseWorker extends GenericSwingWorker implements PropertyChangeListener
	{
		public static final String NEW_ACTION_LABEL_PROPERTY_NAME = "newactionlabel";
		public static final String NEW_ACTION_STRING_PROPERTY_NAME = "newactionstring";
		public static final String NEW_STEP_COMPLETED_PROPERTY_NAME = "newstepcompleted";

		protected StartAppBaseWorker( Component parent )
		{
			super( parent );
			addPropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (NEW_ACTION_LABEL_PROPERTY_NAME.equals( evt.getPropertyName() ) )
			{
				_splash.setCurrentActivityFromLabel( (String) evt.getNewValue() );
			}
			else if( NEW_ACTION_STRING_PROPERTY_NAME.equals( evt.getPropertyName() ) )
			{
				_splash.setCurrentActivity( (String) evt.getNewValue() );
			}
			else if( NEW_STEP_COMPLETED_PROPERTY_NAME.equals( evt.getPropertyName() ) )
			{
				incrementStepsCompleted();
			}
		}

		public void newStepCompleted()
		{
			firePropertyChange(NEW_STEP_COMPLETED_PROPERTY_NAME, null, null);
		}

		public void setCurrentActivityFromLabel( String label )
		{
			firePropertyChange(NEW_ACTION_LABEL_PROPERTY_NAME, null, label);
		}

		public void setCurrentActivity( String currentActivityString )
		{
			firePropertyChange(NEW_ACTION_STRING_PROPERTY_NAME, null, currentActivityString);
		}
	}
*/
	protected class GenericWaitWorker
	{
		int _millisecondsToWait = -1;
		int _nextStep = -1;
		
		protected GenericWaitWorker( int millisecondsToWait, int nextStep )
		{
			_millisecondsToWait = millisecondsToWait;
			_nextStep = nextStep;
		}

		protected void execute() throws Exception
		{
			new Thread( () ->
			{
				try
				{
					Thread.sleep( _millisecondsToWait );
					SwingUtilities.invokeLater( () -> done() );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			} ).start();
		}

		protected void done()
		{
			switch( _nextStep )
			{
				case WATING_FOR_IMPORT_CONFIGURATION:	importConfiguration();		break;
				case WATING_FOR_CHECK_LICENSES:			checkLicenses();			break;
				case WATING_FOR_ACCEPT_LICENSES:		acceptLicenses();			break;
				case WATING_FOR_NEW_VERSION_QUERY:		newVersionQuery();			break;
				case WATING_FOR_WHAT_IS_NEW:			whatIsNewStep();			break;
				case WATING_FOR_CREATE_MAIN_WINDOW:		createMainWindowStep();		break;
				case WATING_FOR_SHOW_MAIN_WINDOW:		showMainWindow();			break;
			}
		}
	}

}
