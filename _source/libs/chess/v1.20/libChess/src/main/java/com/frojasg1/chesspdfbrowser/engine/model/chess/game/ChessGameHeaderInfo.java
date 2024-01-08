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
package com.frojasg1.chesspdfbrowser.engine.model.chess.game;

import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Usuario
 */
public class ChessGameHeaderInfo extends HashMap< String, String >
{
	protected static final Pattern PATT_TO_EXTRACT_PAGE_NUMBER_FROM_CONTROL_NAME = Pattern.compile( "Page:(.*) Game:(.*)" );

	public static final int MANDATORY = 0;
	public static final int PLAYER_RELATED_INFORMATION = 1;
	public static final int EVENT_RELATED_INFORMATION = 2;
	public static final int OPENING_RELATED_INFORMATION = 3;
	public static final int TIME_AND_DATE_RELATED_INFORMATION = 4;
	public static final int TIME_CONTROL = 5;
	public static final int ALTERNATIVE_STARTING_POSITION = 6;
	public static final int GAME_CONCLUSION = 7;
	public static final int MISCELLANEOUS = 8;
	public static final int TAGS_TO_EXTRACT_FROM_PDF_GAMES = 999;
	
	
	public static final String EVENT_TAG = "Event";
	public static final String SITE_TAG = "Site";
	public static final String DATE_TAG = "Date";
	public static final String ROUND_TAG = "Round";
	public static final String WHITE_TAG = "White";
	public static final String BLACK_TAG = "Black";
	public static final String RESULT_TAG = "Result";

	static public String[] _mandatoryTagArray = new String[] {	EVENT_TAG, SITE_TAG, DATE_TAG,
																ROUND_TAG, WHITE_TAG,
																BLACK_TAG, RESULT_TAG };

	public static final String WHITETITLE_TAG = "WhiteTitle";
	public static final String WHITEELO_TAG = "WhiteElo";
	public static final String WHITEUSCF_TAG = "WhiteUSCF";
	public static final String WHITENA_TAG = "WhiteNA";
	public static final String WHITETYPE_TAG = "WhiteType";
	public static final String BLACKTITLE_TAG = "BlackTitle";
	public static final String BLACKELO_TAG = "BlackElo";
	public static final String BLACKUSCF_TAG = "BlackUSCF";
	public static final String BLACKNA_TAG = "BlackNA";
	public static final String BLACKTYPE_TAG = "BlackType";

	static public String[] _playerRelatedInformationTagArray = new String[] {	WHITETITLE_TAG, WHITEELO_TAG, WHITEUSCF_TAG,
																				WHITENA_TAG, WHITETYPE_TAG,
																				BLACKTITLE_TAG, BLACKELO_TAG,
																				BLACKUSCF_TAG, BLACKNA_TAG,
																				BLACKTYPE_TAG };


	public static final String EVENTDATE_TAG = "EventDate";
	public static final String EVENTSPONSOR_TAG = "EventSponsor";
	public static final String SECTION_TAG = "Section";
	public static final String STAGE_TAG = "Stage";
	public static final String BOARD_TAG = "Board";

	static public String[] _eventRelatedInformationTagArray = new String[] {	EVENTDATE_TAG, EVENTSPONSOR_TAG,
																				SECTION_TAG, STAGE_TAG,
																				BOARD_TAG };

	public static final String OPENING_TAG = "Opening";
	public static final String VARIATION_TAG = "Variation";
	public static final String SUBVARIATION_TAG = "SubVariation";
	public static final String ECO_TAG = "ECO";
	public static final String NIC_TAG = "NIC";

	static public String[] _openingRelatedInformationTagArray = new String[] {	OPENING_TAG, VARIATION_TAG,
																					SUBVARIATION_TAG, ECO_TAG,
																					NIC_TAG };

	public static final String TIME_TAG = "Time";
	public static final String UTCTIME_TAG = "UTCTime";
	public static final String UTCDATE_TAG = "UTCDate";

	static public String[] _timeAndDateRelatedInformationTagArray = new String[] {	TIME_TAG, UTCTIME_TAG,
																					UTCDATE_TAG };

	public static final String TIMECONTROL_TAG = "TimeControl";
	
	static public String[] _timeControlTagArray = new String[] {	TIMECONTROL_TAG };

	public static final String SETUP_TAG = "SetUp";
	public static final String FEN_TAG = "FEN";

	static public String[] _alternativeStartingPositionTagArray = new String[] {	SETUP_TAG, FEN_TAG };


	public static final String TERMINATION_TAG = "Termination";

	static public String[] _gameConclusionTagArray = new String[] {	TERMINATION_TAG };

	public static final String ANNOTATOR_TAG = "Annotator";
	public static final String MODE_TAG = "Mode";
	public static final String PLYCOUNT_TAG = "PlyCount";

	static public String[] _miscellaneousTagArray = new String[] {	ANNOTATOR_TAG, MODE_TAG, PLYCOUNT_TAG };

	static public String[][] _optionalTagArrays= new String[][] {	_playerRelatedInformationTagArray,
																	_eventRelatedInformationTagArray,
																	_openingRelatedInformationTagArray,
																	_timeAndDateRelatedInformationTagArray,
																	_timeControlTagArray,
																	_alternativeStartingPositionTagArray,
																	_gameConclusionTagArray,
																	_miscellaneousTagArray
																};

	static public String[] _tagsToExtractFromPdfGames = {
										EVENT_TAG,
										SITE_TAG,
										DATE_TAG,
										ROUND_TAG,
										WHITE_TAG,
										BLACK_TAG,
										RESULT_TAG,
										WHITEELO_TAG,
										BLACKELO_TAG,
										ECO_TAG
	};

	// the following are special tags created to be used only at this application
	public static final String CONTROL_NAME_TAG = "ControlName";
	public static final String FILE_NAME_TAG = "FileName";

	protected String _controlName = null;
	protected String _pdfBaseFileName = null;

	protected Integer _pageIndex = null;

	protected ProfileModel _profileModelHeaderComesFrom = null;

	public ChessGameHeaderInfo()
	{
		super();
		put( RESULT_TAG, "*" );
	}

	public ChessGameHeaderInfo( ChessGameHeaderInfo other )
	{
		this();

		copy( other );
	}

	public void copy( ChessGameHeaderInfo other )
	{
		clear();
		putAll( other );

		this._controlName = other._controlName;
		this._pdfBaseFileName = other._pdfBaseFileName;
		this._profileModelHeaderComesFrom = other._profileModelHeaderComesFrom;
	}

	public String getDescriptionOfGame()
	{
		String event = get( EVENT_TAG );
		String eventDate = get( EVENTDATE_TAG );
		String round = get( ROUND_TAG );
		String whitePlayer = get( WHITE_TAG );
		String blackPlayer = get( BLACK_TAG );
		String whiteELO = get( WHITEELO_TAG );
		String blackELO = get( BLACKELO_TAG );

		String result = "";

		if( ( getControlName() != null ) && !getControlName().isEmpty() )
		{
			result = String.format( "[ %s ]", getControlName() );
			if( event != null )
				result = result + "  -  " + event;
		}
		else if( event != null )
			result = event;

		if( ( round != null ) && (round.length()>0) )
		{
			result = result + " (" + round + ")";
		}

		if( ( eventDate != null ) && ( eventDate.length()>0 ) )
		{
			result = eventDate + " " + result;
		}

		if( ( ( whitePlayer != null ) && (whitePlayer.length()>0) ) ||
			( ( blackPlayer != null ) && (blackPlayer.length()>0) ) )
		{
			if( ( whitePlayer == null ) || ( whitePlayer.length()==0 ) )
				whitePlayer = "unknown";

			if( ( blackPlayer == null ) || ( blackPlayer.length()==0 ) )
				blackPlayer = "unknown";

			if( ( ( whiteELO != null ) && ( whiteELO.length()>0 ) ) ||
				( ( blackELO != null ) && ( blackELO.length()>0 ) ) )
			{
				if( ( whiteELO == null ) || ( whiteELO.length() == 0 ) )
					whiteELO = "-";
				
				if( ( blackELO == null ) || ( blackELO.length() == 0 ) )
					blackELO = "-";

				whitePlayer = whitePlayer + " (" + whiteELO + ")";
				blackPlayer = blackPlayer + " (" + blackELO + ")";
			}
			
			result = result + " " + whitePlayer + " - " + blackPlayer;
		}

		return( result );
	}

	/**
	 * 
	 * @param groupId	this parameter may take the next values:
	 *					MANDATORY
	 *					PLAYER_RELATED_INFORMATION
	 *					EVENT_RELATED_INFORMATION
	 *					OPENING_RELATED_INFORMATION
	 *					TIME_AND_DATE_RELATED_INFORMATION
	 *					TIME_CONTROL
	 *					ALTERNATIVE_STARTING_POSITION
	 *					MISCELLANEOUS
	 *					
	 * @return 
	 */
	public static String[] getTAGgroup( int groupId )
	{
		String[] result = null;

		switch( groupId )
		{
			case MANDATORY:							result = _mandatoryTagArray; break;
			case PLAYER_RELATED_INFORMATION:		result = _playerRelatedInformationTagArray; break;
			case EVENT_RELATED_INFORMATION:			result = _eventRelatedInformationTagArray; break;
			case OPENING_RELATED_INFORMATION:		result = _openingRelatedInformationTagArray; break;
			case TIME_AND_DATE_RELATED_INFORMATION:	result = _timeAndDateRelatedInformationTagArray; break;
			case TIME_CONTROL:						result = _timeControlTagArray; break;
			case ALTERNATIVE_STARTING_POSITION:		result = _alternativeStartingPositionTagArray; break;
			case GAME_CONCLUSION:					result = _gameConclusionTagArray; break;
			case MISCELLANEOUS:						result = _miscellaneousTagArray; break;
			case TAGS_TO_EXTRACT_FROM_PDF_GAMES:	result = _tagsToExtractFromPdfGames; break;
		}

		return( result );
	}

	public String getControlName()
	{
		if( _controlName == null )
			_controlName = get( CONTROL_NAME_TAG );

		return( _controlName );
	}

	public void setControlName( String controlName )
	{
		_controlName = controlName;

		_pageIndex = parsePageIndex(controlName);

		if( controlName != null )
			put( CONTROL_NAME_TAG, controlName );
	}

	public Integer getPageIndexAtPdf()
	{
		return( _pageIndex );
	}

	protected String getPageNumberStr( String controlName )
	{
		String result = null;
		
		if( controlName != null )
		{
			Matcher matcher = PATT_TO_EXTRACT_PAGE_NUMBER_FROM_CONTROL_NAME.matcher( controlName );

			if( matcher.matches() )
			{
				matcher.find(0);
				result = matcher.group(1);
			}
		}

		return( result );
	}

	protected Integer parsePageIndex( String controlName )
	{
		Integer result = IntegerFunctions.parseInt( getPageNumberStr( controlName ) );

		if( result == null )
			result = -1;
		else
			result = result - 1;

		return( result );
	}

	public String getPdfBaseFileName() {
		return _pdfBaseFileName;
	}

	public void setPdfBaseFileName(String pdfBaseFileName) {
		_pdfBaseFileName = pdfBaseFileName;

		if( pdfBaseFileName != null )
			put( FILE_NAME_TAG, pdfBaseFileName );
	}

	public void setProfileModel( ProfileModel profileModelHeaderComesFrom )
	{
		_profileModelHeaderComesFrom = profileModelHeaderComesFrom;
	}

	public ProfileModel getProfileModel()
	{
		return( _profileModelHeaderComesFrom );
	}
}
