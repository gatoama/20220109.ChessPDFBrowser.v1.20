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
package com.frojasg1.chesspdfbrowser.view.chess.gamedata;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.structures.Pair;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GameDataJDialog extends InternationalizedJDialog<ApplicationInitContext>
	implements AcceptCancelRevertControllerInterface
{
	public static final String sa_configurationBaseFileName = "GameDataJDialog";

	protected Pair<String, JTextComponent>[] _tagMapper = null;

	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected ChessGameHeaderInfo _header = null;
	protected int _gameNumber = -1;

	/**
	 * Creates new form GameDataJDialog
	 */
	public GameDataJDialog(java.awt.Frame parent, boolean modal,
											ApplicationInitContext applicationContext,
											ChessGameHeaderInfo header,
											int gameNumber )
	{
		super(parent, modal, ApplicationConfiguration.instance(), applicationContext,
			true);

		_header = header;
		_gameNumber = gameNumber;
		initComponents();

		initOwnComponents();

		setWindowConfiguration();
	}

	protected AcceptCancelRevertPanel createAcceptCancelRevertPanel()
	{
		return( new AcceptCancelRevertPanel( this ) );
	}

	protected void initializeComponentContents()
	{
		for( Pair<String, JTextComponent> pair: getTagMapper() )
		{
			pair.getValue().setText( _header.get( pair.getKey() ) );
		}
		jTF_index.setText( String.valueOf( _gameNumber ) );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();
	}

	protected void initOwnComponents()
	{
		_acceptPanel = createAcceptCancelRevertPanel();

		jPanel4.add( _acceptPanel );
		_acceptPanel.setBounds( 0, 0, jPanel4.getWidth(), jPanel4.getHeight() );
	}

	protected void setWindowConfiguration( )
	{
/*
		a_intern = new JFrameInternationalization(	ApplicationConfiguration.sa_MAIN_FOLDER,
													ApplicationConfiguration.sa_APPLICATION_NAME,
													ApplicationConfiguration.sa_CONFIGURATION_GROUP,
													ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR,
													a_configurationBaseFileName,
													this,
													parent,
													a_vectorJpopupMenus,
													false,
													mapRRCI );
*/


		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_PARENT );

			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									sa_configurationBaseFileName,
									this,
									getParent(),
									null,
									true,
									mapRRCI );
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
        jL_whitePlayer = new javax.swing.JLabel();
        jTF_whitePlayer = new javax.swing.JTextField();
        jL_whiteElo = new javax.swing.JLabel();
        jTF_whiteElo = new javax.swing.JTextField();
        jL_blackPlayer = new javax.swing.JLabel();
        jTF_blackPlayer = new javax.swing.JTextField();
        jL_blackElo = new javax.swing.JLabel();
        jTF_blackElo = new javax.swing.JTextField();
        jL_site = new javax.swing.JLabel();
        jTF_site = new javax.swing.JTextField();
        jL_event = new javax.swing.JLabel();
        jTF_event = new javax.swing.JTextField();
        jL_round = new javax.swing.JLabel();
        jTF_round = new javax.swing.JTextField();
        jL_date = new javax.swing.JLabel();
        jTF_date = new javax.swing.JTextField();
        jL_eco = new javax.swing.JLabel();
        jTF_eco = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jTF_index = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel1.setLayout(null);

        jL_whitePlayer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_whitePlayer.setText("White player :");
        jL_whitePlayer.setName("jL_whitePlayer"); // NOI18N
        jPanel1.add(jL_whitePlayer);
        jL_whitePlayer.setBounds(10, 40, 100, 14);
        jPanel1.add(jTF_whitePlayer);
        jTF_whitePlayer.setBounds(110, 35, 195, 20);

        jL_whiteElo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_whiteElo.setText("ELO :");
        jL_whiteElo.setName("jL_whiteElo"); // NOI18N
        jPanel1.add(jL_whiteElo);
        jL_whiteElo.setBounds(311, 40, 50, 14);
        jPanel1.add(jTF_whiteElo);
        jTF_whiteElo.setBounds(365, 35, 45, 20);

        jL_blackPlayer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_blackPlayer.setText("Black player :");
        jL_blackPlayer.setName("jL_blackPlayer"); // NOI18N
        jPanel1.add(jL_blackPlayer);
        jL_blackPlayer.setBounds(10, 75, 100, 14);
        jPanel1.add(jTF_blackPlayer);
        jTF_blackPlayer.setBounds(110, 70, 195, 20);

        jL_blackElo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_blackElo.setText("ELO :");
        jL_blackElo.setName("jL_blackElo"); // NOI18N
        jPanel1.add(jL_blackElo);
        jL_blackElo.setBounds(311, 75, 50, 14);
        jPanel1.add(jTF_blackElo);
        jTF_blackElo.setBounds(365, 70, 45, 20);

        jL_site.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_site.setText("Site :");
        jL_site.setName("jL_site"); // NOI18N
        jPanel1.add(jL_site);
        jL_site.setBounds(5, 110, 105, 14);
        jPanel1.add(jTF_site);
        jTF_site.setBounds(110, 105, 195, 20);

        jL_event.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_event.setText("Event :");
        jL_event.setName("jL_event"); // NOI18N
        jPanel1.add(jL_event);
        jL_event.setBounds(10, 145, 100, 14);
        jPanel1.add(jTF_event);
        jTF_event.setBounds(110, 140, 195, 20);

        jL_round.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_round.setText("Round :");
        jL_round.setName("jL_round"); // NOI18N
        jPanel1.add(jL_round);
        jL_round.setBounds(310, 145, 50, 14);
        jPanel1.add(jTF_round);
        jTF_round.setBounds(365, 140, 45, 20);

        jL_date.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_date.setText("Date :");
        jL_date.setName("jL_date"); // NOI18N
        jPanel1.add(jL_date);
        jL_date.setBounds(5, 180, 100, 14);
        jPanel1.add(jTF_date);
        jTF_date.setBounds(110, 175, 90, 20);

        jL_eco.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_eco.setText("Eco :");
        jL_eco.setName("jL_eco"); // NOI18N
        jPanel1.add(jL_eco);
        jL_eco.setBounds(310, 180, 50, 14);
        jPanel1.add(jTF_eco);
        jTF_eco.setBounds(365, 175, 45, 20);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 204)));
        jPanel4.setLayout(null);
        jPanel1.add(jPanel4);
        jPanel4.setBounds(140, 210, 140, 50);

        jTF_index.setEditable(false);
        jTF_index.setBackground(new java.awt.Color(255, 255, 0));
        jPanel1.add(jTF_index);
        jTF_index.setBounds(10, 10, 59, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 420, 275);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jL_blackElo;
    private javax.swing.JLabel jL_blackPlayer;
    private javax.swing.JLabel jL_date;
    private javax.swing.JLabel jL_eco;
    private javax.swing.JLabel jL_event;
    private javax.swing.JLabel jL_round;
    private javax.swing.JLabel jL_site;
    private javax.swing.JLabel jL_whiteElo;
    private javax.swing.JLabel jL_whitePlayer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTF_blackElo;
    private javax.swing.JTextField jTF_blackPlayer;
    private javax.swing.JTextField jTF_date;
    private javax.swing.JTextField jTF_eco;
    private javax.swing.JTextField jTF_event;
    private javax.swing.JTextField jTF_index;
    private javax.swing.JTextField jTF_round;
    private javax.swing.JTextField jTF_site;
    private javax.swing.JTextField jTF_whiteElo;
    private javax.swing.JTextField jTF_whitePlayer;
    // End of variables declaration//GEN-END:variables

	@Override
	public void revert(InformerInterface panel)
	{
		initializeComponentContents();
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		// no validation at all
	}

	protected void applyChanges()
	{
		for( Pair<String, JTextComponent> pair: getTagMapper() )
			_header.put( pair.getKey(), pair.getValue().getText() );
	}

	@Override
	public void accept(InformerInterface panel)
	{
		validateForm();
		if( wasSuccessful() )
		{
			applyChanges();
			formWindowClosing(true);
		}
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		setWasSuccessful( false );

		formWindowClosing(true);
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jL_blackElo = compMapper.mapComponent( jL_blackElo );
		jL_blackPlayer = compMapper.mapComponent( jL_blackPlayer );
		jL_date = compMapper.mapComponent( jL_date );
		jL_eco = compMapper.mapComponent( jL_eco );
		jL_event = compMapper.mapComponent( jL_event );
		jL_round = compMapper.mapComponent( jL_round );
		jL_site = compMapper.mapComponent( jL_site );
		jL_whiteElo = compMapper.mapComponent( jL_whiteElo );
		jL_whitePlayer = compMapper.mapComponent( jL_whitePlayer );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel4 = compMapper.mapComponent( jPanel4 );
		jTF_blackElo = compMapper.mapComponent( jTF_blackElo );
		jTF_blackPlayer = compMapper.mapComponent( jTF_blackPlayer );
		jTF_date = compMapper.mapComponent( jTF_date );
		jTF_eco = compMapper.mapComponent( jTF_eco );
		jTF_event = compMapper.mapComponent( jTF_event );
		jTF_round = compMapper.mapComponent( jTF_round );
		jTF_site = compMapper.mapComponent( jTF_site );
		jTF_whiteElo = compMapper.mapComponent(jTF_whiteElo );
		jTF_whitePlayer = compMapper.mapComponent( jTF_whitePlayer );

		if( !hasBeenAlreadyMapped() )
		{
			_tagMapper = createTagMapper();
			initializeComponentContents();
		}
	}

	protected Pair<String, JTextComponent>[] getTagMapper()
	{
		return( _tagMapper );
	}

	protected Pair<String, JTextComponent>[] createTagMapper()
	{
		Pair<String, JTextComponent>[] result = ArrayFunctions.instance().createArray(
			new Pair<>( ChessGameHeaderInfo.WHITE_TAG, jTF_whitePlayer ),
			new Pair<>( ChessGameHeaderInfo.WHITEELO_TAG, jTF_whiteElo ),
			new Pair<>( ChessGameHeaderInfo.BLACK_TAG, jTF_blackPlayer ),
			new Pair<>( ChessGameHeaderInfo.BLACKELO_TAG, jTF_blackElo ),
			new Pair<>( ChessGameHeaderInfo.SITE_TAG, jTF_site ),
			new Pair<>( ChessGameHeaderInfo.EVENT_TAG, jTF_event ),
			new Pair<>( ChessGameHeaderInfo.ROUND_TAG, jTF_round ),
			new Pair<>( ChessGameHeaderInfo.DATE_TAG, jTF_date ),
			new Pair<>( ChessGameHeaderInfo.ECO_TAG, jTF_eco )
		);

		return( result );
	}
}