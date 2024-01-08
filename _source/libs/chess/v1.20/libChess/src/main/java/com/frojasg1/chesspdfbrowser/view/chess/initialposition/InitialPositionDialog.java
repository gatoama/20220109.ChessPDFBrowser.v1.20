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
package com.frojasg1.chesspdfbrowser.view.chess.initialposition;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.King;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.engine.position.impl.ChessGamePositionImpl;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class InitialPositionDialog extends InternationalizedJDialog
									implements AcceptCancelRevertControllerInterface
{
	protected static final String CONF_INTERNAL_ERROR = "INTERNAL_ERROR";
	protected static final String CONF_BAD_FORMAT_FOR_ENPASSANT_SQUARE = "BAD_FORMAT_FOR_ENPASSANT_SQUARE";
	protected static final String CONF_BLACK_CANNOT_BE_IN_CHECK = "BLACK_CANNOT_BE_IN_CHECK";
	protected static final String CONF_WHITE_CANNOT_BE_IN_CHECK = "WHITE_CANNOT_BE_IN_CHECK";
	protected static final String CONF_PROBLEM_GETTING_FEN = "PROBLEM_GETTING_FEN";
	protected static final String CONF_PLIES_WITHOUT_PROGRESS_BADLY_CONFIGURED = "PLIES_WITHOUT_PROGRESS_BADLY_CONFIGURED";
	protected static final String CONF_EDIT_INITIAL_POSITION = "EDIT_INITIAL_POSITION";
	protected static final String CONF_MOVE_NUMBER_BADLY_CONFIGURED = "MOVE_NUMBER_BADLY_CONFIGURED";

	public static final String a_configurationBaseFileName = "InitialPositionDialog";

	protected InitialPositionBoardPanel _initialPositionBoardPanel = null;
	protected PiecesToChoosePanel _piecesToChoosePanel = null;
	protected GlobalCoordinatesInDrag _globalCoordinatesInDrag = null;

	protected ChessGamePosition _positionResult = null;
	protected ChessGamePositionBase _originalInitialPosition = null;
	
	protected static InitialPositionDialog _instance = null;

	protected ChessGame _chessGame = null;

	public static InitialPositionDialog createInstance( java.awt.Frame parent,
														ApplicationConfiguration applicationConfiguration )
	{
		if( _instance == null )
			_instance = new InitialPositionDialog( parent, true, applicationConfiguration );

		return( _instance );
	}

	public static InitialPositionDialog instance()
	{
		return( _instance );
	}

	public InitialPositionDialog(JDialog parent, boolean modal,
					ApplicationConfiguration applicationConfiguration ) {
		this( parent, modal, applicationConfiguration, null );
	}

	/**
	 * Creates new form InitialPositionDialog
	 */
	public InitialPositionDialog(JDialog parent, boolean modal,
					ApplicationConfiguration applicationConfiguration,
					Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		super(parent, modal, applicationConfiguration, null, initializationEndCallBack,
			true);

		initComponents();

		internalInit();

		setWindowConfiguration();

		if( modal )
			setAlwaysOnTop( true );
	}

	public InitialPositionDialog(java.awt.Frame parent, boolean modal,
					ApplicationConfiguration applicationConfiguration ) {
		this( parent, modal, applicationConfiguration, null );
	}

	/**
	 * Creates new form InitialPositionDialog
	 */
	public InitialPositionDialog(java.awt.Frame parent, boolean modal,
					ApplicationConfiguration applicationConfiguration,
					Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		super(parent, modal, applicationConfiguration, null, initializationEndCallBack,
			true);

		initComponents();

		internalInit();

		setWindowConfiguration();

		if( modal )
			setAlwaysOnTop( true );
	}

	@Override
	public ApplicationConfiguration getAppliConf()
	{
		return( (ApplicationConfiguration) super.getAppliConf() );
	}

	protected void setDefaultTitle()
	{
		setTitle( getInternationalString( CONF_EDIT_INITIAL_POSITION )  );
	}

	protected void setTitle( ChessGame cg )
	{
		boolean assigned = false;
		if( ( cg != null ) && ( cg.getChessGameHeaderInfo() != null ) )
		{
			String gameDes = cg.getChessGameHeaderInfo().getDescriptionOfGame();
			
			if( gameDes != null )
			{
				setTitle( getInternationalString( CONF_EDIT_INITIAL_POSITION ) +
							" - " + gameDes );
				assigned = true;
			}
		}

		if( ! assigned )
			setDefaultTitle();
	}

	public void setChessGame( ChessGame cg )
	{
		_chessGame = cg;
		
		setTitle( cg );

		if( cg != null )
			setInitialPosition( cg.getInitialPosition() );
	}

	protected ChessGamePosition createCopy( ChessGamePositionBase cgp ) throws ChessParserException, ChessModelException
	{
		ChessGamePosition result = new ChessGamePosition();

		String fenString = null;

		if( cgp instanceof ChessGamePositionImpl )
		{
			result.setFenPositionBase( cgp.getFenBoardStringBase() );
			fenString = result.getFenString();
		}
		else if( cgp instanceof ChessGamePosition )
		{
			fenString = ( (ChessGamePosition) cgp ).getFenString();
		}

		result.setFenPosition(fenString);

		return( result );
	}

	protected void setUnrecognizedPositions( ChessGamePositionImpl detectedPosition )
	{
		_initialPositionBoardPanel.clearUnrecognizedPositions();

		for( int jj=1; jj<=ChessGamePositionImpl.NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=ChessGamePositionImpl.NUM_OF_ROWS; ii++ )
				if( detectedPosition.getCorrelationResultAtPosition(jj, ii) == null )
					_initialPositionBoardPanel.addUnrecognizedPosition(jj, ii);
	}

	public void setInitialPosition( ChessGamePositionBase cgp )
	{
		SwingUtilities.invokeLater( () -> {
			setDefaultTitle();

			setInitialPosition_internal( cgp );
			repaint();
		} );
	}

	protected void setInitialPosition_internal( ChessGamePositionBase cgp )
	{
		if( cgp == null )
			cgp = ChessGamePosition.getInitialPosition();

		try
		{
			_originalInitialPosition = cgp;

			ChessGamePosition copy = createCopy( cgp );

			if( cgp instanceof ChessGamePositionImpl )
			{
				setUnrecognizedPositions( (ChessGamePositionImpl) cgp );
			}

			_initialPositionBoardPanel.getChessBoard().setPosition( copy );

			_piecesToChoosePanel.setWhiteCanCastleKingsSide( copy.getWhiteCanCastleKingSide() );
			_piecesToChoosePanel.setWhiteCanCastleQueenSide( copy.getWhiteCanCastleQueenSide() );
			_piecesToChoosePanel.setBlackCanCastleKingsSide( copy.getBlackCanCastleKingSide() );
			_piecesToChoosePanel.setBlackCanCastleQueenSide( copy.getBlackCanCastleQueenSide() );

			if( ( copy.getIsWhitesTurn() == null ) || copy.getIsWhitesTurn() )
				_piecesToChoosePanel.setWhiteToPlay();
			else
				_piecesToChoosePanel.setBlackToPlay();

			_piecesToChoosePanel.setEnPassantMove( copy.getEnPassantSquare_str() );

			_piecesToChoosePanel.setPliesWithoutProgress( copy.getNumberOfPliesWithoutProgress() );

			int moveNumber = 0;

			if( ( _chessGame != null ) &&
				( _chessGame.getMoveTreeGame().getNumberOfChildren() > 0 ) ) // if it has no children, we let modify the move number.
			{
				int plyNumber = _chessGame.getMoveTreeGame().getLevel() + 1;
				moveNumber = (plyNumber + 1)/2;
			}
			_piecesToChoosePanel.setMoveNumber( moveNumber );

			newChangeInPosition();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public ChessGamePosition getPositionResult()
	{
		return( _positionResult );
	}

	protected void setMoveNumberToInitialPosition( ChessGamePosition cgp ) throws ValidationException
	{
		JTextField textField = _piecesToChoosePanel.getMoveNumberJTextField();

		Integer moveNumber = IntegerFunctions.parseInt( textField.getText() );

		if( moveNumber != null )
			cgp.setMoveNumber( moveNumber );
		else
			throw( new ValidationException( getInternationalString( CONF_MOVE_NUMBER_BADLY_CONFIGURED ),
											textField )   );
	}

	protected void setEnPassantMoveToInitialPosition( ChessGamePosition cgp ) throws ValidationException
	{
		Component textField = _piecesToChoosePanel.getEnPassantJTextField();
		
		String enPassantSquare = _piecesToChoosePanel.getEnPassantMove();
		if( ( enPassantSquare != null ) && ( enPassantSquare.length() > 0 ) )
		{
			if( enPassantSquare.length() == 2 )
			{
				int column = ChessGameMove.getColumnIndex(enPassantSquare.substring( 0, 1 ) );
				int row = ChessGameMove.getRowIndex(enPassantSquare.substring( 1, 2 ) );

				try
				{
					cgp.checkAndSetEnPassant(column, row);
				}
				catch( Throwable th )
				{
					throw( new ValidationException( th.getMessage(), textField ) );
				}
			}
			else
			{
				throw( new ValidationException( getInternationalString( CONF_BAD_FORMAT_FOR_ENPASSANT_SQUARE ),
												textField ) );
			}
		}
	}

	public ChessGamePosition createChessGamePosition() throws ChessMoveException, ValidationException
	{
		return( createChessGamePosition(true) );
	}

	public ChessGamePosition createChessGamePosition( boolean throwException ) throws ChessMoveException, ValidationException
	{
		ChessGamePosition result = new ChessGamePosition( _initialPositionBoardPanel.getChessBoard() );

		_initialPositionBoardPanel.setWhitePlaysFromTheBottom( _piecesToChoosePanel.getWhitePlaysFromBottom() );

		King king = null;
		if( _piecesToChoosePanel.isWhiteToPlay() )
		{
			result.setIsWhitesTurn();
			king = _initialPositionBoardPanel.getChessBoard().getKing( ChessPiece.BLACK );
			if( throwException && ( king != null ) && king.isInCheck() )
				throw( new RuntimeException( getInternationalString(CONF_BLACK_CANNOT_BE_IN_CHECK ) ) );
		}
		else
		{
			result.setIsBlacksTurn();
			king = _initialPositionBoardPanel.getChessBoard().getKing( ChessPiece.WHITE );
			if( throwException && ( king != null ) && king.isInCheck() )
				throw( new RuntimeException( getInternationalString( CONF_WHITE_CANNOT_BE_IN_CHECK ) ) );
		}

		setMoveNumberToInitialPosition( result );
		
		setEnPassantMoveToInitialPosition( result );

		Boolean whiteCanCastleKingSide = result.getWhiteCanCastleKingSide();
		Boolean whiteCanCastleQueenSide = result.getWhiteCanCastleQueenSide();
		Boolean blackCanCastleKingSide = result.getBlackCanCastleKingSide();
		Boolean blackCanCastleQueenSide = result.getBlackCanCastleQueenSide();

		if( whiteCanCastleKingSide == null )
		{
			result.setWhiteCanCastleKingSide( _piecesToChoosePanel.whiteCanCastleKingside() );
		}

		if( whiteCanCastleQueenSide == null )
		{
			result.setWhiteCanCastleQueenSide( _piecesToChoosePanel.whiteCanCastleQueenside() );
		}

		if( blackCanCastleKingSide == null )
		{
			result.setBlackCanCastleKingSide( _piecesToChoosePanel.blackCanCastleKingside() );
		}

		if( blackCanCastleQueenSide == null )
		{
			result.setBlackCanCastleQueenSide( _piecesToChoosePanel.blackCanCastleQueenside() );
		}

		int numberOfPliesWithoutProgress = _piecesToChoosePanel.getNumberOfPliesWithoutProgress();
		if( numberOfPliesWithoutProgress != -1 )
			result.setPliesWithoutProgress( numberOfPliesWithoutProgress );
		else if( throwException )
			throw( new ValidationException( getInternationalString( CONF_PLIES_WITHOUT_PROGRESS_BADLY_CONFIGURED ),
											_piecesToChoosePanel.getNumberOfPliesWithoutProgressJTextField() )   );

		try
		{
			result.getFenString();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			if( throwException )
				throw( new RuntimeException( th ) );
		}

		return( result );
	}

	public String getFenString()
	{
		String result = null;

		try
		{
			ChessGamePosition cgp = createChessGamePosition();
			result = cgp.getFenString();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			result = th.getMessage();
		}

		return( result );
	}

	public void newChangeInPosition()
	{
		String fenString = getFenString();

		_piecesToChoosePanel.setFenString( fenString );
	}

	public void accept()
	{
		try
		{
			_positionResult = createChessGamePosition();
			formWindowClosing( true );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog(this,
											th.getMessage() + ". " +
											getInternationalString( CONF_PROBLEM_GETTING_FEN ),
											getInternationalString(ChessStringsConf.CONF_INTERNAL_ERROR ),
											DialogsWrapper.ERROR_MESSAGE );

			if( th instanceof ValidationException )
			{
				ValidationException ve = (ValidationException) th;

/*
				Component comp = ve.getComponentWithException();
				if( comp != null )
				{
					boolean showFocus = true;
					a_intern.setFocus( comp, showFocus );
				}
*/
				SwingUtilities.invokeLater( () -> highlightComponent( ve.getViewComponentWithException() ) );
			}
		}
	}

	public void cancel()
	{
		_positionResult = null;
		formWindowClosing( true );
	}
	
	protected void internalInit()
	{
		try
		{
			_globalCoordinatesInDrag = new GlobalCoordinatesInDrag( this );
			_initialPositionBoardPanel = new InitialPositionBoardPanel( _globalCoordinatesInDrag );
			_initialPositionBoardPanel.setFigureSetChangedObserved( getAppliConf() );
			_piecesToChoosePanel = new PiecesToChoosePanel( this, _globalCoordinatesInDrag );
			_piecesToChoosePanel.setFigureSetChangedObserved( getAppliConf() );
/*
			Icon icon = _piecesToChoosePanel.getAcceptCancelRevertPanel().getCancelButton().getIcon();
			BufferedImage bi = new BufferedImage(
				icon.getIconWidth(),
				icon.getIconHeight(),
				BufferedImage.TYPE_INT_RGB);
			Graphics g = bi.createGraphics();
			// paint the Icon to the BufferedImage.
			icon.paintIcon(null, g, 0,0);
			g.dispose();
			setOverlappedImage( bi, new Point( 50, 50 ) );
*/			

			jPanel1.add( _piecesToChoosePanel );
			_piecesToChoosePanel.setBounds( 0, 0,
											_piecesToChoosePanel.getComponent(0).getWidth(),
											_piecesToChoosePanel.getComponent(0).getHeight() );

			jPanel1.add( _initialPositionBoardPanel );
			_initialPositionBoardPanel.setBounds( 0, _piecesToChoosePanel.getHeight(),
												_piecesToChoosePanel.getWidth(),
												getContentPane().getHeight() - _piecesToChoosePanel.getHeight() );

			jPanel1.setBounds( 0, 0, _piecesToChoosePanel.getWidth(), getContentPane().getHeight() );

			pack();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected void setWindowConfiguration( )
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT +
															ResizeRelocateItem.RESIZE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( _piecesToChoosePanel, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putAll( _piecesToChoosePanel.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( _initialPositionBoardPanel, ResizeRelocateItem.RESIZE_TO_RIGHT +
																				ResizeRelocateItem.RESIZE_TO_BOTTOM );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									a_configurationBaseFileName,
									this,
									getParent(),
									null,
									true,
									mapRRCI );

		registerInternationalString(CONF_INTERNAL_ERROR, "Internal ERROR" );
		registerInternationalString(CONF_BAD_FORMAT_FOR_ENPASSANT_SQUARE, "Bad value for enPassant square" );
		registerInternationalString(CONF_BLACK_CANNOT_BE_IN_CHECK, "If white to move, black cannot be in check" );
		registerInternationalString(CONF_WHITE_CANNOT_BE_IN_CHECK, "If black to move, white cannot be in check" );
		registerInternationalString(CONF_PROBLEM_GETTING_FEN, "Problem getting FEN string" );
		registerInternationalString(CONF_PLIES_WITHOUT_PROGRESS_BADLY_CONFIGURED, "Plies without progress badly configured" );
		registerInternationalString(CONF_EDIT_INITIAL_POSITION, "Edit initial position" );
		registerInternationalString(CONF_MOVE_NUMBER_BADLY_CONFIGURED, "Move number badly configured" );

		a_intern.setMaxWindowWidthNoLimit( false );

//		setIcon( "com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png" );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Change initial position");
        setMaximumSize(new java.awt.Dimension(720, 2147483647));
        setMinimumSize(new java.awt.Dimension(710, 755));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(710, 675));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setLayout(null);
        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 670, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		_positionResult = null;
		formWindowClosing( true );

    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

	@Override
	public void formWindowClosing( boolean closeWindow )
	{
		if( a_intern != null )
		{
			try
			{
				a_intern.saveConfiguration();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		if( closeWindow )
		{
			setVisible(false);
//			dispose();
//			releaseResources();
		}
	}

	public void releaseResources()
	{
		a_intern=null;	// for the garbage collector to free the memory of the internationallization object and after the memory of this form
	}

	@Override
	public void accept(InformerInterface panel)
	{
		accept();
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		cancel();
	}

	@Override
	public void revert(InformerInterface panel)
	{
		setInitialPosition( _originalInitialPosition );
		_initialPositionBoardPanel.refresh();
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jPanel1 = compMapper.mapComponent( jPanel1 );
	}
}
