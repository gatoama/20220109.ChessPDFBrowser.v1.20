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

import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizableComponentInterface;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedListener;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedObserved;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Bishop;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.King;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Knight;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Pawn;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Queen;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Rook;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessBoardImages;
import com.frojasg1.desktop.liblens.graphics.ScreenImage;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatus;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class PiecesToChoosePanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase  implements MouseListener,
														MouseMotionListener,
														ResizableComponentInterface,
														InternallyMappedComponent,
														FigureSetChangedListener
{
	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected InitialPositionDialog _parent = null;
	protected GlobalCoordinatesInDrag _globalCoordinatesInDrag = null;
	
	protected Map< Component, PieceData > _mapToPieceData = null;

	protected MapResizeRelocateComponentItem _mapRRCI = null;

	protected double _currentZoomFactorForComponentResized = 1.0D;

	protected FigureSetChangedObserved _figureSetChangedObserved;


	/**
	 * Creates new form PiecesToChoosePanel
	 */
	public PiecesToChoosePanel( InitialPositionDialog parent,
								GlobalCoordinatesInDrag gcid )
	{
		super.init();

		_parent = parent;
		_globalCoordinatesInDrag = gcid;
		_globalCoordinatesInDrag.setPiecesToChoosePanel(this);
		initComponents();

		initOwnComponents();
//		initPieceButtons();

		_mapRRCI = createResizeRelocateInfo();

		recursiveAddListeners( this );
	}

	public AcceptCancelRevertPanel getAcceptCancelRevertPanel()
	{
		return( _acceptPanel );
	}
	
	protected void initOwnComponents()
	{
//		System.out.println( "PiecesToChoosePanel" );
		_acceptPanel = new AcceptCancelRevertPanel( _parent );
		_acceptPanel.getResizeRelocateInfo(); // to make internal buttons resize.

		jPanel5.add( _acceptPanel );
		makeAcceptPanelFillWholeParent();
	}

	protected void makeAcceptPanelFillWholeParent()
	{
		ContainerFunctions.instance().addComponentToCompletelyFillParent(jPanel5, _acceptPanel);
	}

	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		if( _mapRRCI == null )
		{
			_mapRRCI = createResizeRelocateInfo();
		}

		return( _mapRRCI );
	}

	protected MapResizeRelocateComponentItem createResizeRelocateInfo()
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();

		try
		{
			mapRRCI.putResizeRelocateComponentItem( jP_contentPanel, ResizeRelocateItem.RESIZE_TO_RIGHT +
																ResizeRelocateItem.RESIZE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jPanel5, 0 );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.RESIZE_TO_RIGHT +
																	ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	
		return( mapRRCI );
	}

	public int getNumberOfPliesWithoutProgress()
	{
		int result = -1;
		try
		{
			int tmp = Integer.parseInt( jTF_movesWithoutProgress.getText() );
			if( ( tmp > -1 ) && ( tmp < 100 ) )
				result = tmp;
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		return( result );
	}

	public JTextField getNumberOfPliesWithoutProgressJTextField()
	{
		return( jTF_movesWithoutProgress );
	}
	
	public JTextField getEnPassantJTextField()
	{
		return( jTF_enPassantMove );
	}

	public JTextField getMoveNumberJTextField()
	{
		return( jTF_moveNumber );
	}

	public String getEnPassantMove()
	{
		return( jTF_enPassantMove.getText() );
	}

	public void setMoveNumber( int moveNumber )
	{
		if( moveNumber == 0 )	// the moveNumber only can be configured in case in the ChessGame there is not any move.
		{
			moveNumber = 1;
			jTF_moveNumber.setText( String.valueOf(moveNumber) );
//			jTF_moveNumber.setEditable( true );
		}
		else
		{
			jTF_moveNumber.setText( String.valueOf(moveNumber) );
//			jTF_moveNumber.setEditable( true );
		}
	}

	public boolean isWhiteToPlay()
	{
		return( jRB_whiteToPlay.isSelected() );
	}

	public boolean whiteCanCastleKingside()
	{
		return( jcb_whi_castle_king_side.isSelected() );
	}

	public boolean whiteCanCastleQueenside()
	{
		return( jcb_whi_castle_queen_side.isSelected() );
	}

	public boolean blackCanCastleKingside()
	{
		return( jcb_bla_castle_king_side.isSelected() );
	}

	public boolean blackCanCastleQueenside()
	{
		return( jcb_bla_castle_queen_side.isSelected() );
	}

	public void setWhiteCanCastleKingsSide( Boolean value )
	{
		setCastleCheckboxSelected( jcb_whi_castle_king_side, value );
	}
	
	public void setWhiteCanCastleQueenSide( Boolean value )
	{
		setCastleCheckboxSelected( jcb_whi_castle_queen_side, value );
	}
	
	public void setBlackCanCastleKingsSide( Boolean value )
	{
		setCastleCheckboxSelected( jcb_bla_castle_king_side, value );
	}

	public void setBlackCanCastleQueenSide( Boolean value )
	{
		setCastleCheckboxSelected( jcb_bla_castle_queen_side, value );
	}
	
	public void setCastleCheckboxSelected( JCheckBox jcb, Boolean value )
	{
		if( value == null )
			value = true;

		jcb.setSelected(value);
	}
	
	public void setPliesWithoutProgress( Integer numberOfPliesWithoutProgress )
	{
		if( numberOfPliesWithoutProgress == null )
			numberOfPliesWithoutProgress = 0;

		jTF_movesWithoutProgress.setText( String.valueOf(numberOfPliesWithoutProgress) );
	}
	
	public void setWhiteToPlay()
	{
		jRB_whiteToPlay.setSelected(true);
	}

	public void setBlackToPlay()
	{
		jRB_blackToPlay.setSelected(true);
	}

	public void setEnPassantMove( String enPassantMove )
	{
		if( enPassantMove == null )
			enPassantMove = "";

		jTF_enPassantMove.setText( enPassantMove );
	}

	protected ColorThemeChangeableStatus getColorChangeableStatus(Component comp)
	{
		return( _parent.getInternationalization().getColorThemeChangeable(comp) );
	}
/*
	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _parent.getAppliConf() );
	}
*/
	protected void addImageToButton( JButton button, String resourceName )
	{
		try
		{
			getColorChangeableStatus(button).setDoNotInvertColors(true);
			BufferedImage img = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName));
			int transparentArgbColor = img.getRGB(0, 0);
			BufferedImage resizedImage = ImageFunctions.instance().resizeImage( img, button.getWidth()-2, button.getHeight()-2, transparentArgbColor, 0xFFF8E2E2, null );
			BufferedImage finalImage = resizedImage;
//			if( getAppliConf().isDarkModeActivated() )
			if( isDarkMode() )
				finalImage = getColorInversor().putOutImageColor(finalImage, 0.5d);

			button.setIcon(new ImageIcon(finalImage));
			button.setMargin(new Insets(1, 1, 1, 1));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void initPieceButtons()
	{
		addImageToButton(jB_white_King, ChessBoardImages.instance().getResourceNameForWhiteKing());
		addImageToButton(jB_white_Queen, ChessBoardImages.instance().getResourceNameForWhiteQueen());
		addImageToButton(jB_white_Rook, ChessBoardImages.instance().getResourceNameForWhiteRook());
		addImageToButton(jB_white_Bishop, ChessBoardImages.instance().getResourceNameForWhiteBishop());
		addImageToButton(jB_white_Knight, ChessBoardImages.instance().getResourceNameForWhiteKnight());
		addImageToButton(jB_white_Pawn, ChessBoardImages.instance().getResourceNameForWhitePawn());
		addImageToButton(jB_black_King, ChessBoardImages.instance().getResourceNameForBlackKing());
		addImageToButton(jB_black_Queen, ChessBoardImages.instance().getResourceNameForBlackQueen());
		addImageToButton(jB_black_Rook, ChessBoardImages.instance().getResourceNameForBlackRook());
		addImageToButton(jB_black_Bishop, ChessBoardImages.instance().getResourceNameForBlackBishop());
		addImageToButton(jB_black_Knight, ChessBoardImages.instance().getResourceNameForBlackKnight());
		addImageToButton(jB_black_Pawn, ChessBoardImages.instance().getResourceNameForBlackPawn());

		_mapToPieceData = new HashMap<Component, PieceData>();
		_mapToPieceData.put( jB_white_King, new PieceData( ChessPiece.WHITE, King.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_white_Queen, new PieceData( ChessPiece.WHITE, Queen.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_white_Rook, new PieceData( ChessPiece.WHITE, Rook.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_white_Bishop, new PieceData( ChessPiece.WHITE, Bishop.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_white_Knight, new PieceData( ChessPiece.WHITE, Knight.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_white_Pawn, new PieceData( ChessPiece.WHITE, Pawn.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_black_King, new PieceData( ChessPiece.BLACK, King.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_black_Queen, new PieceData( ChessPiece.BLACK, Queen.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_black_Rook, new PieceData( ChessPiece.BLACK, Rook.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_black_Bishop, new PieceData( ChessPiece.BLACK, Bishop.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_black_Knight, new PieceData( ChessPiece.BLACK, Knight.PIECE_TYPE_CODE ) );
		_mapToPieceData.put( jB_black_Pawn, new PieceData( ChessPiece.BLACK, Pawn.PIECE_TYPE_CODE ) );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jP_contentPanel = new javax.swing.JPanel();
        jB_black_Queen = new javax.swing.JButton();
        jB_black_Rook = new javax.swing.JButton();
        jB_black_Bishop = new javax.swing.JButton();
        jB_black_Pawn = new javax.swing.JButton();
        jB_black_Knight = new javax.swing.JButton();
        jB_white_Pawn = new javax.swing.JButton();
        jB_white_Knight = new javax.swing.JButton();
        jB_white_Bishop = new javax.swing.JButton();
        jB_white_Rook = new javax.swing.JButton();
        jB_white_Queen = new javax.swing.JButton();
        jCB_whitePlaysFromBottom = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jRB_whiteToPlay = new javax.swing.JRadioButton();
        jRB_blackToPlay = new javax.swing.JRadioButton();
        jcb_whi_castle_king_side = new javax.swing.JCheckBox();
        jcb_whi_castle_queen_side = new javax.swing.JCheckBox();
        jcb_bla_castle_king_side = new javax.swing.JCheckBox();
        jcb_bla_castle_queen_side = new javax.swing.JCheckBox();
        jTF_movesWithoutProgress = new javax.swing.JTextField();
        jL_pliesWIthoutProgress = new javax.swing.JLabel();
        jB_initialPosition = new javax.swing.JButton();
        jB_onlyKings = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jTF_enPassantMove = new javax.swing.JTextField();
        jL_enPassantSquare = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jB_pasteFENposition = new javax.swing.JButton();
        jTF_FENstring = new javax.swing.JTextField();
        jL_FENstring = new javax.swing.JLabel();
        jTF_moveNumber = new javax.swing.JTextField();
        jL_moveNumber = new javax.swing.JLabel();
        jB_white_King = new javax.swing.JButton();
        jB_black_King = new javax.swing.JButton();
        jB_flipBoard = new javax.swing.JButton();

        setToolTipText("");
        setMinimumSize(new java.awt.Dimension(658, 240));
        setPreferredSize(new java.awt.Dimension(658, 240));
        setLayout(null);

        jP_contentPanel.setMinimumSize(new java.awt.Dimension(700, 240));
        jP_contentPanel.setPreferredSize(new java.awt.Dimension(700, 240));
        jP_contentPanel.setLayout(null);

        jB_black_Queen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_black_Queen.setFocusable(false);
        jB_black_Queen.setName(""); // NOI18N
        jP_contentPanel.add(jB_black_Queen);
        jB_black_Queen.setBounds(60, 160, 40, 40);

        jB_black_Rook.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_black_Rook.setFocusable(false);
        jB_black_Rook.setName(""); // NOI18N
        jP_contentPanel.add(jB_black_Rook);
        jB_black_Rook.setBounds(100, 160, 40, 40);

        jB_black_Bishop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_black_Bishop.setFocusable(false);
        jB_black_Bishop.setName(""); // NOI18N
        jP_contentPanel.add(jB_black_Bishop);
        jB_black_Bishop.setBounds(140, 160, 40, 40);

        jB_black_Pawn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_black_Pawn.setFocusable(false);
        jB_black_Pawn.setName(""); // NOI18N
        jP_contentPanel.add(jB_black_Pawn);
        jB_black_Pawn.setBounds(220, 160, 40, 40);

        jB_black_Knight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_black_Knight.setFocusable(false);
        jB_black_Knight.setName(""); // NOI18N
        jP_contentPanel.add(jB_black_Knight);
        jB_black_Knight.setBounds(180, 160, 40, 40);

        jB_white_Pawn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_white_Pawn.setFocusable(false);
        jB_white_Pawn.setName(""); // NOI18N
        jP_contentPanel.add(jB_white_Pawn);
        jB_white_Pawn.setBounds(220, 110, 40, 40);

        jB_white_Knight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_white_Knight.setFocusable(false);
        jB_white_Knight.setName(""); // NOI18N
        jP_contentPanel.add(jB_white_Knight);
        jB_white_Knight.setBounds(180, 110, 40, 40);

        jB_white_Bishop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_white_Bishop.setFocusable(false);
        jB_white_Bishop.setName(""); // NOI18N
        jP_contentPanel.add(jB_white_Bishop);
        jB_white_Bishop.setBounds(140, 110, 40, 40);

        jB_white_Rook.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_white_Rook.setFocusable(false);
        jB_white_Rook.setName(""); // NOI18N
        jP_contentPanel.add(jB_white_Rook);
        jB_white_Rook.setBounds(100, 110, 40, 40);

        jB_white_Queen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_white_Queen.setFocusable(false);
        jB_white_Queen.setName(""); // NOI18N
        jP_contentPanel.add(jB_white_Queen);
        jB_white_Queen.setBounds(60, 110, 40, 40);

        jCB_whitePlaysFromBottom.setSelected(true);
        jCB_whitePlaysFromBottom.setText("white plays from bottom");
        jCB_whitePlaysFromBottom.setName("jCB_whitePlaysFromBottom"); // NOI18N
        jCB_whitePlaysFromBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_whi_castle_king_sideActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jCB_whitePlaysFromBottom);
        jCB_whitePlaysFromBottom.setBounds(300, 10, 210, 24);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(null);

        buttonGroup1.add(jRB_whiteToPlay);
        jRB_whiteToPlay.setSelected(true);
        jRB_whiteToPlay.setText("white to play");
        jRB_whiteToPlay.setName("jRB_whiteToPlay"); // NOI18N
        jRB_whiteToPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_whi_castle_king_sideActionPerformed(evt);
            }
        });
        jPanel1.add(jRB_whiteToPlay);
        jRB_whiteToPlay.setBounds(10, 10, 150, 28);

        buttonGroup1.add(jRB_blackToPlay);
        jRB_blackToPlay.setText("black to play");
        jRB_blackToPlay.setName("jRB_blackToPlay"); // NOI18N
        jRB_blackToPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_whi_castle_king_sideActionPerformed(evt);
            }
        });
        jPanel1.add(jRB_blackToPlay);
        jRB_blackToPlay.setBounds(10, 30, 150, 28);

        jP_contentPanel.add(jPanel1);
        jPanel1.setBounds(520, 0, 170, 60);

        jcb_whi_castle_king_side.setSelected(true);
        jcb_whi_castle_king_side.setText("white can castle king side");
        jcb_whi_castle_king_side.setName("jcb_whi_castle_king_side"); // NOI18N
        jcb_whi_castle_king_side.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_whi_castle_king_sideActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jcb_whi_castle_king_side);
        jcb_whi_castle_king_side.setBounds(290, 120, 230, 24);

        jcb_whi_castle_queen_side.setSelected(true);
        jcb_whi_castle_queen_side.setText("white can castle queen side");
        jcb_whi_castle_queen_side.setName("jcb_whi_castle_queen_side"); // NOI18N
        jcb_whi_castle_queen_side.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_whi_castle_king_sideActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jcb_whi_castle_queen_side);
        jcb_whi_castle_queen_side.setBounds(290, 140, 230, 24);

        jcb_bla_castle_king_side.setSelected(true);
        jcb_bla_castle_king_side.setText("black can castle king side");
        jcb_bla_castle_king_side.setName("jcb_bla_castle_king_side"); // NOI18N
        jcb_bla_castle_king_side.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_whi_castle_king_sideActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jcb_bla_castle_king_side);
        jcb_bla_castle_king_side.setBounds(290, 160, 230, 24);

        jcb_bla_castle_queen_side.setSelected(true);
        jcb_bla_castle_queen_side.setText("black can castle queen side");
        jcb_bla_castle_queen_side.setName("jcb_bla_castle_queen_side"); // NOI18N
        jcb_bla_castle_queen_side.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcb_whi_castle_king_sideActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jcb_bla_castle_queen_side);
        jcb_bla_castle_queen_side.setBounds(290, 180, 230, 24);

        jTF_movesWithoutProgress.setText("0");
        jTF_movesWithoutProgress.setMinimumSize(new java.awt.Dimension(14, 20));
        jTF_movesWithoutProgress.setPreferredSize(new java.awt.Dimension(21, 20));
        jTF_movesWithoutProgress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTF_enPassantMoveKeyReleased(evt);
            }
        });
        jP_contentPanel.add(jTF_movesWithoutProgress);
        jTF_movesWithoutProgress.setBounds(290, 70, 20, 20);

        jL_pliesWIthoutProgress.setText("plies without progress");
        jL_pliesWIthoutProgress.setName("jL_pliesWIthoutProgress"); // NOI18N
        jL_pliesWIthoutProgress.setRequestFocusEnabled(false);
        jP_contentPanel.add(jL_pliesWIthoutProgress);
        jL_pliesWIthoutProgress.setBounds(320, 70, 190, 20);

        jB_initialPosition.setText("Set initial position");
        jB_initialPosition.setMaximumSize(null);
        jB_initialPosition.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_initialPosition.setName("jB_initialPosition"); // NOI18N
        jB_initialPosition.setPreferredSize(new java.awt.Dimension(260, 20));
        jB_initialPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_initialPositionActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jB_initialPosition);
        jB_initialPosition.setBounds(10, 10, 260, 20);

        jB_onlyKings.setText("Leave only kings");
        jB_onlyKings.setMaximumSize(null);
        jB_onlyKings.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_onlyKings.setName("jB_onlyKings"); // NOI18N
        jB_onlyKings.setPreferredSize(new java.awt.Dimension(260, 20));
        jB_onlyKings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_onlyKingsActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jB_onlyKings);
        jB_onlyKings.setBounds(10, 40, 260, 20);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jP_contentPanel.add(jSeparator1);
        jSeparator1.setBounds(280, 10, 10, 190);

        jTF_enPassantMove.setMinimumSize(new java.awt.Dimension(14, 20));
        jTF_enPassantMove.setPreferredSize(new java.awt.Dimension(14, 20));
        jTF_enPassantMove.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTF_enPassantMoveKeyReleased(evt);
            }
        });
        jP_contentPanel.add(jTF_enPassantMove);
        jTF_enPassantMove.setBounds(290, 100, 20, 20);

        jL_enPassantSquare.setText("En passant square");
        jL_enPassantSquare.setName("jL_enPassantSquare"); // NOI18N
        jP_contentPanel.add(jL_enPassantSquare);
        jL_enPassantSquare.setBounds(320, 100, 200, 20);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 204)));
        jPanel5.setPreferredSize(new java.awt.Dimension(130, 50));
        jPanel5.setLayout(null);
        jP_contentPanel.add(jPanel5);
        jPanel5.setBounds(520, 130, 170, 65);

        jB_pasteFENposition.setText("Paste FEN position");
        jB_pasteFENposition.setMaximumSize(null);
        jB_pasteFENposition.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_pasteFENposition.setName("jB_pasteFENposition"); // NOI18N
        jB_pasteFENposition.setPreferredSize(new java.awt.Dimension(260, 20));
        jB_pasteFENposition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_pasteFENpositionActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jB_pasteFENposition);
        jB_pasteFENposition.setBounds(10, 70, 260, 20);

        jTF_FENstring.setEditable(false);
        jTF_FENstring.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTF_FENstring.setForeground(new java.awt.Color(51, 102, 255));
        jTF_FENstring.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_FENstringActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jTF_FENstring);
        jTF_FENstring.setBounds(130, 210, 540, 21);

        jL_FENstring.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_FENstring.setText("FEN string :");
        jL_FENstring.setName("jL_FENstring"); // NOI18N
        jP_contentPanel.add(jL_FENstring);
        jL_FENstring.setBounds(0, 210, 120, 16);

        jTF_moveNumber.setMinimumSize(new java.awt.Dimension(14, 20));
        jTF_moveNumber.setPreferredSize(new java.awt.Dimension(14, 20));
        jP_contentPanel.add(jTF_moveNumber);
        jTF_moveNumber.setBounds(290, 40, 30, 20);

        jL_moveNumber.setText("Move number");
        jL_moveNumber.setName("jL_moveNumber"); // NOI18N
        jP_contentPanel.add(jL_moveNumber);
        jL_moveNumber.setBounds(330, 40, 190, 20);

        jB_white_King.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_white_King.setFocusable(false);
        jB_white_King.setName("jB_white_King"); // NOI18N
        jP_contentPanel.add(jB_white_King);
        jB_white_King.setBounds(20, 110, 40, 40);

        jB_black_King.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jB_black_King.setFocusable(false);
        jB_black_King.setName(""); // NOI18N
        jP_contentPanel.add(jB_black_King);
        jB_black_King.setBounds(20, 160, 40, 40);

        jB_flipBoard.setText("Flip board");
        jB_flipBoard.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_flipBoard.setName("jB_flipBoard"); // NOI18N
        jB_flipBoard.setPreferredSize(new java.awt.Dimension(170, 20));
        jB_flipBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_flipBoardActionPerformed(evt);
            }
        });
        jP_contentPanel.add(jB_flipBoard);
        jB_flipBoard.setBounds(520, 75, 170, 35);

        add(jP_contentPanel);
        jP_contentPanel.setBounds(0, 0, 700, 240);
    }// </editor-fold>//GEN-END:initComponents

    private void jB_initialPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_initialPositionActionPerformed
        // TODO add your handling code here:

		_globalCoordinatesInDrag.setPosition( ChessGamePosition.getInitialPosition() );

    }//GEN-LAST:event_jB_initialPositionActionPerformed

    private void jB_onlyKingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_onlyKingsActionPerformed
        // TODO add your handling code here:

		_globalCoordinatesInDrag.setPosition( ChessGamePosition.getOnlyKingsPosition() );

    }//GEN-LAST:event_jB_onlyKingsActionPerformed

    private void jB_pasteFENpositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_pasteFENpositionActionPerformed
        // TODO add your handling code here:

		_globalCoordinatesInDrag.pasteFenPosition();

    }//GEN-LAST:event_jB_pasteFENpositionActionPerformed

    private void jcb_whi_castle_king_sideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcb_whi_castle_king_sideActionPerformed
        // TODO add your handling code here:

		_globalCoordinatesInDrag.newChangeInPosition();

    }//GEN-LAST:event_jcb_whi_castle_king_sideActionPerformed

    private void jTF_enPassantMoveKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTF_enPassantMoveKeyReleased
        // TODO add your handling code here:

		_globalCoordinatesInDrag.newChangeInPosition();
		
    }//GEN-LAST:event_jTF_enPassantMoveKeyReleased

    private void jTF_FENstringActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_FENstringActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTF_FENstringActionPerformed

    private void jB_flipBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_flipBoardActionPerformed
        // TODO add your handling code here:

		_globalCoordinatesInDrag.flipBoard();

    }//GEN-LAST:event_jB_flipBoardActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jB_black_Bishop;
    private javax.swing.JButton jB_black_King;
    private javax.swing.JButton jB_black_Knight;
    private javax.swing.JButton jB_black_Pawn;
    private javax.swing.JButton jB_black_Queen;
    private javax.swing.JButton jB_black_Rook;
    private javax.swing.JButton jB_flipBoard;
    private javax.swing.JButton jB_initialPosition;
    private javax.swing.JButton jB_onlyKings;
    private javax.swing.JButton jB_pasteFENposition;
    private javax.swing.JButton jB_white_Bishop;
    private javax.swing.JButton jB_white_King;
    private javax.swing.JButton jB_white_Knight;
    private javax.swing.JButton jB_white_Pawn;
    private javax.swing.JButton jB_white_Queen;
    private javax.swing.JButton jB_white_Rook;
    private javax.swing.JCheckBox jCB_whitePlaysFromBottom;
    private javax.swing.JLabel jL_FENstring;
    private javax.swing.JLabel jL_enPassantSquare;
    private javax.swing.JLabel jL_moveNumber;
    private javax.swing.JLabel jL_pliesWIthoutProgress;
    private javax.swing.JPanel jP_contentPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRB_blackToPlay;
    private javax.swing.JRadioButton jRB_whiteToPlay;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTF_FENstring;
    private javax.swing.JTextField jTF_enPassantMove;
    private javax.swing.JTextField jTF_moveNumber;
    private javax.swing.JTextField jTF_movesWithoutProgress;
    private javax.swing.JCheckBox jcb_bla_castle_king_side;
    private javax.swing.JCheckBox jcb_bla_castle_queen_side;
    private javax.swing.JCheckBox jcb_whi_castle_king_side;
    private javax.swing.JCheckBox jcb_whi_castle_queen_side;
    // End of variables declaration//GEN-END:variables


	protected void recursiveSetCursor( Cursor cursor )
	{
		if( ! getCursor().getName().equals( cursor.getName() ) )
		{
			recursiveSetCursor( this, cursor );
		}
	}
	
	protected void recursiveSetCursor( Component comp, Cursor cursor )
	{
		comp.setCursor( cursor );
		if( comp instanceof Container )
		{
			Container cont = (Container) comp;
			
			for( int ii=0; ii<cont.getComponentCount(); ii++ )
				recursiveSetCursor( cont.getComponent(ii), cursor );
		}
	}

	protected void recursiveAddListeners( Component comp )
	{
		comp.addMouseMotionListener( this );
		comp.addMouseListener( this );
		if( comp instanceof Container )
		{
			Container cont = (Container) comp;
			
			for( int ii=0; ii<cont.getComponentCount(); ii++ )
				recursiveAddListeners( cont.getComponent(ii) );
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent me)
	{
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		if( me.getButton() == MouseEvent.BUTTON1 ) 
		{
			Component component = me.getComponent();

			PieceData pd = _mapToPieceData.get( component );
			if( pd != null )
			{
				ChessPiece cp = null;
				try
				{
					cp = ChessPiece.createPiece( _globalCoordinatesInDrag.getChessBoard(), pd.getPieceType(), pd.getColor() );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}

				if( cp != null )
				{
					_globalCoordinatesInDrag.setDraggedPiece(cp);
					_globalCoordinatesInDrag.setIsInDrag(true);
					_globalCoordinatesInDrag.setNewDragPosition( me.getLocationOnScreen() );
					recursiveSetCursor(CursorFunctions._handCursor );

					repaint();
	//				_globalCoordinatesInDrag.repaint( false );
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent me)
	{
		if( me.getButton() == MouseEvent.BUTTON1 ) 
		{
			_globalCoordinatesInDrag.setNewDragPosition( me.getLocationOnScreen() );

			recursiveSetCursor(CursorFunctions._defaultCursor );
			_globalCoordinatesInDrag.mouseReleased();
		}
	}

	@Override
	public void mouseEntered(MouseEvent me)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent me)
	{
	}

	@Override
	public void mouseMoved( MouseEvent evt )
	{
		if( !_globalCoordinatesInDrag.isInDrag() )
		{
			Component component = evt.getComponent();

			PieceData pd = _mapToPieceData.get( component );
			if( pd != null )
			{
				recursiveSetCursor(CursorFunctions._handCursor );
			}
			else
			{
				recursiveSetCursor(CursorFunctions._defaultCursor );
			}
		}
		else
		{
			recursiveSetCursor(CursorFunctions._defaultCursor );
		}
	}

	@Override
	public void mouseDragged( MouseEvent evt )
	{
		if( _globalCoordinatesInDrag.isInDrag() )
		{
			_globalCoordinatesInDrag.setNewDragPosition( evt.getLocationOnScreen() );
			_globalCoordinatesInDrag.repaint( false );
		}
	}

	protected void paintPieceInDrag( Graphics gc )
	{
		if( _globalCoordinatesInDrag.isInDrag() )
		{
			try
			{
				ChessPiece cp = _globalCoordinatesInDrag.getDraggedPiece();

				if( cp != null )
				{
					int currentPieceWidth = _globalCoordinatesInDrag.getPieceWidth();
					Point dragPoint = _globalCoordinatesInDrag.getDragPosition( this );
					Point upperLeftCorner = new Point(	(int) ( dragPoint.getX() - currentPieceWidth/2 ),
														(int) ( dragPoint.getY() - currentPieceWidth/2 ) );
					BufferedImage bi = ChessBoardImages.instance().getSemiTransparentPieceImage( cp.getPieceCode(), currentPieceWidth );
					ImageFunctions.instance().paintClippedImage(this, gc, bi, upperLeftCorner);
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	@Override
	public void paintComponent(Graphics gc)
	{
		paintPanel( gc );
	}

	@Override
	public void paint(Graphics gc)
	{
		paintPanel( gc );
	}

	public void paintPanel( Graphics gc )
	{
		BufferedImage bi = ScreenImage.createImage( jP_contentPanel );
		Graphics gc1 = bi.getGraphics();

		paintPieceInDrag( gc1 );

		gc.drawImage( bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null );
	}
	
	@Override
	public void setSize( Dimension dim )
	{
		super.setSize( dim );

		jP_contentPanel.setSize( dim );
	}

	@Override
	public void setSize( int width, int height )
	{
		super.setSize( width, height );

		jP_contentPanel.setSize( width, height );
	}
	
	@Override
	public void setBounds( Rectangle rect )
	{
		super.setBounds( rect );

		jP_contentPanel.setSize( rect.getSize() );
	}
	
	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
		super.setBounds( xx, yy, width, height );

		jP_contentPanel.setSize( width, height );
	}

	public void setFenString( String fenString )
	{
		jTF_FENstring.setText( fenString );
	}

	public boolean getWhitePlaysFromBottom()
	{
		return( jCB_whitePlaysFromBottom.isSelected() );
	}

	@Override
	public void doTasksAfterResizingComponent(double zoomFactor)
	{
		SwingUtilities.invokeLater( () -> {
			if( this.hasBeenAlreadyMapped() &&
				( _currentZoomFactorForComponentResized != zoomFactor ) )
			{
				initPieceButtons();
				_currentZoomFactorForComponentResized = zoomFactor;
			}
		} );
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper)
	{
		buttonGroup1 = compMapper.mapComponent( buttonGroup1 );
		jB_black_Bishop = compMapper.mapComponent( jB_black_Bishop );
		jB_black_Knight = compMapper.mapComponent( jB_black_Knight );
		jB_black_Pawn = compMapper.mapComponent( jB_black_Pawn );
		jB_black_Queen = compMapper.mapComponent( jB_black_Queen );
		jB_black_King = compMapper.mapComponent( jB_black_King );
		jB_black_Rook = compMapper.mapComponent( jB_black_Rook );
		jB_initialPosition = compMapper.mapComponent( jB_initialPosition );
		jB_onlyKings = compMapper.mapComponent( jB_onlyKings );
		jB_pasteFENposition = compMapper.mapComponent( jB_pasteFENposition );
		jB_white_Bishop = compMapper.mapComponent( jB_white_Bishop );
		jB_white_Knight = compMapper.mapComponent( jB_white_Knight );
		jB_white_Pawn = compMapper.mapComponent( jB_white_Pawn );
		jB_white_King = compMapper.mapComponent( jB_white_King );
		jB_white_Queen = compMapper.mapComponent( jB_white_Queen );
		jB_white_Rook = compMapper.mapComponent( jB_white_Rook );
		jCB_whitePlaysFromBottom = compMapper.mapComponent( jCB_whitePlaysFromBottom );
		jL_FENstring = compMapper.mapComponent( jL_FENstring );
		jL_enPassantSquare = compMapper.mapComponent( jL_enPassantSquare );
		jL_moveNumber = compMapper.mapComponent( jL_moveNumber );
		jL_pliesWIthoutProgress = compMapper.mapComponent( jL_pliesWIthoutProgress );
		jP_contentPanel = compMapper.mapComponent( jP_contentPanel );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel5 = compMapper.mapComponent( jPanel5 );
		jRB_blackToPlay = compMapper.mapComponent( jRB_blackToPlay );
		jRB_whiteToPlay = compMapper.mapComponent( jRB_whiteToPlay );
		jSeparator1 = compMapper.mapComponent( jSeparator1 );
		jTF_FENstring = compMapper.mapComponent( jTF_FENstring );
		jTF_enPassantMove = compMapper.mapComponent( jTF_enPassantMove );
		jTF_moveNumber = compMapper.mapComponent( jTF_moveNumber );
		jTF_movesWithoutProgress = compMapper.mapComponent( jTF_movesWithoutProgress );
		jcb_bla_castle_king_side = compMapper.mapComponent( jcb_bla_castle_king_side );
		jcb_bla_castle_queen_side = compMapper.mapComponent( jcb_bla_castle_queen_side );
		jcb_whi_castle_king_side = compMapper.mapComponent( jcb_whi_castle_king_side );
		jcb_whi_castle_queen_side = compMapper.mapComponent( jcb_whi_castle_queen_side );
		jB_flipBoard = compMapper.mapComponent( jB_flipBoard );

		makeAcceptPanelFillWholeParent();
		if( !hasBeenAlreadyMapped() )
			initPieceButtons();

		super.setComponentMapper(compMapper);
	}

	@Override
	public void invertColorsChild( ColorInversor colorInversor )
	{
		super.invertColorsChild( colorInversor );
		SwingUtilities.invokeLater( this::initPieceButtons );
	}

	public void setFigureSetChangedObserved( FigureSetChangedObserved figureSetChangedObserved )
	{
		registerToFigureSetChangedObserved( figureSetChangedObserved );
	}

	protected void registerToFigureSetChangedObserved( FigureSetChangedObserved figureSetChangedObserved )
	{
		unregisterToFigureSetChangedObserved();
		_figureSetChangedObserved = figureSetChangedObserved;
		_figureSetChangedObserved.addFigureSetChangedListener(this);
	}

	public void unregisterToFigureSetChangedObserved()
	{
		if( _figureSetChangedObserved != null )
			_figureSetChangedObserved.removeFigureSetChangedListener(this);
	}

	@Override
	public void figureSetChanged(FigureSetChangedObserved observed, FigureSet oldValue, FigureSet newFigureSet) {
		SwingUtilities.invokeLater( () -> {
			initPieceButtons();
			repaint();
		});
	}

	@Override
	public void releaseResources()
	{
		unregisterToFigureSetChangedObserved();
	}

	public static class PieceData
	{
		protected int _color = -1;
		protected String _pieceType = null;
		
		public PieceData( int color, String pieceType )
		{
			_color = color;
			_pieceType = pieceType;
		}

		public int getColor()
		{
			return( _color );
		}

		public String getPieceType()
		{
			return( _pieceType );
		}
	}

}
