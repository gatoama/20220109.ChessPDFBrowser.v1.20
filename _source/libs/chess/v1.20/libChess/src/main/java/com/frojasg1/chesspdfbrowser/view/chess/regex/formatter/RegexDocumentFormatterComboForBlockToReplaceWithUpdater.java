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
package com.frojasg1.chesspdfbrowser.view.chess.regex.formatter;

import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexLexicalAnalyser;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexTokenId;
import com.frojasg1.chesspdfbrowser.model.regex.utils.BlockRegexUtils;
import com.frojasg1.general.desktop.view.combobox.renderer.ComboCellRendererBase;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.colors.Colors;
import com.frojasg1.general.desktop.view.combobox.utils.ComboBoxFunctions;
import com.frojasg1.general.desktop.view.document.formatter.FormatterListener;
import com.frojasg1.general.desktop.view.document.formatter.ZoomDocumentFormatAppender;
import com.frojasg1.general.desktop.view.document.formatter.ZoomDocumentFormatterOnTheFly_markingBrackets;
import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.StringFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexDocumentFormatterComboForBlockToReplaceWithUpdater extends ZoomDocumentFormatAppender<String>
{
	protected static final int INVERTIBLE_GREEN_COLOR_INDEX = 0;
//	protected static final int INVERTIBLE_RED_COLOR_INDEX = 1;

	protected static Color[] _originalPutOutableColorModeColors = new Color[] {
		Color.GREEN.darker()//,
//		Color.RED
	};

	protected JComboBox _comboForBlockToReplaceWith = null;
	protected RegexExternalFormatter _externalFormatter = null;
/*
	protected String _regexStr = null;
	protected List<RegexToken> _listOfRegexTokensCache = null;
*/
	protected ActionListener _comboActionListener = null;

	protected RegexLexicalAnalyser _lex = null;

	protected String _initialComboSelectedItem = null;
	protected boolean _isFirstTime = true;

	protected boolean _isFirstFormat = true;

	protected ListCellRenderer _comboCellRenderer = null;

	protected BlockRegexBuilder _regexBuilder = null;

	protected FormatForText _selectedBlockFormatForText = null;

	protected List<RegexToken> _lastTokenList = null;

	public RegexDocumentFormatterComboForBlockToReplaceWithUpdater(
		JTextPane pane, ChangeZoomFactorServerInterface changeZoomFactorServer,
		BlockRegexBuilder regexBuilder )
	{
		super( pane, changeZoomFactorServer, String.class );

		setFormatterListener( createFormatterListener() );

		_regexBuilder = regexBuilder;
	}

	protected FormatterListener createFormatterListener()
	{
		return( new RegexFormatterListener( this, getJTextPane() ) );
	}

	protected void init( )
	{
		if( _lex == null )
			_lex = createLex();
	}

	@Override
	public void setNewJTextPane( JTextPane jtp )
	{
		_isFirstFormat = true;

		super.setNewJTextPane(jtp);

		_externalFormatter.setTextPane( jtp );
	}

	// comboSelectedItem can be either a blockNameToReplaceWith (RegexEditionJPanel) or a tagName (LineOfTagsJPanel)
	public void init( JTextPane pane, JComboBox comboForBlockToReplaceWith,
					String comboSelectedItem )
	{
		init();

		_initialComboSelectedItem = comboSelectedItem;

		if( _externalFormatter == null )
			_externalFormatter = createRegexExternalFormatter();
		setExternalTextFormatter(_externalFormatter);

		setNewJTextPane(pane);
		setCombo( comboForBlockToReplaceWith );

		comboActionPerformed(null);
	}

	protected ListCellRenderer createComboCellRenderer( JComboBox combo )
	{
		return( new ComboCellRenderer(combo) );
	}

	protected ListCellRenderer getComboCellRenderer( JComboBox combo )
	{
/*
		if( _comboCellRenderer == null )
			_comboCellRenderer = createComboCellRenderer(combo);
//			_comboCellRenderer = combo.getRenderer();
*/
		return( createComboCellRenderer(combo) );
	}

	protected RegexLexicalAnalyser createLex()
	{
		RegexLexicalAnalyser result = new RegexLexicalAnalyser();
		result.setOnErrorThrowException( false );

		return( result );
	}

	public ActionListener createComboActionListener()
	{
		return( ( evt ) -> comboActionPerformed( evt ) );
	}

	public ActionListener getComboActionListener()
	{
		if( _comboActionListener == null )
			_comboActionListener = createComboActionListener();

		return( _comboActionListener );
	}

	protected void comboActionPerformed( ActionEvent evt )
	{
		SwingUtilities.invokeLater( () -> {
			_externalFormatter.setIsNew( true );
			formatDocument();
			SwingUtilities.invokeLater( () ->visualizeSelectedBlock() );
		});
	}

	@Override
	public synchronized void formatDocument( Document doc, int initialPosition ) throws ZoomDocumentFormatterOnTheFly_markingBrackets.CharacterAnalyserException, BadLocationException
	{
		super.formatDocument(doc, initialPosition );

		if( _isFirstFormat )
		{
			shrinkTextJPane();
			_isFirstFormat = false;
		}
	}

	@Override
	protected void formatText( Collection<FormatForText> collecTextFormat )
	{
		_selectedBlockFormatForText = null;
		super.formatText( collecTextFormat );

		for( FormatForText fft: collecTextFormat )
			if( fft.getStyleName().equals( RegexExternalFormatter.STYLE_FOR_BLOCK_TO_REPLACE_WITH ) )
			{
				_selectedBlockFormatForText = fft;
				break;
			}
	}

	public void visualizeSelectedBlock()
	{
//		setCaret( _selectedBlockFormatForText );
		visualizeBlock( _selectedBlockFormatForText );
	}

	protected void visualizeBlock( FormatForText fft )
	{
		if( fft != null )
		{
			SwingUtilities.invokeLater( () -> {
				getJTextPane().setSelectionStart( fft.getStart() );
				getJTextPane().setSelectionEnd( fft.getStart() + fft.getLength() );
				SwingUtilities.invokeLater( () -> getJTextPane().setSelectionEnd( fft.getStart() ) );
			});
		}
	}

	protected void setCaret( FormatForText fft )
	{
		if( fft != null )
		{
			SwingUtilities.invokeLater( () -> {
				getJTextPane().setCaretPosition( fft.getStart() );
				SwingUtilities.invokeLater( () -> getJTextPane().setCaretPosition( fft.getStart() + fft.getLength() ) );
			});
		}
	}

	public void setCombo( JComboBox comboForBlockToReplaceWith )
	{
		removeComboListeners();
		_comboForBlockToReplaceWith = comboForBlockToReplaceWith;
		addComboListeners();
	}

	protected RegexExternalFormatter createRegexExternalFormatter()
	{
		RegexExternalFormatter result = new RegexExternalFormatter(_regexBuilder,
																getJTextPane() );

		return( result );
	}

	protected String getComboSelection()
	{
		String result = null;

		if( _isFirstTime )
		{
			result = _initialComboSelectedItem;
			_isFirstTime = false;
		}
		else
		{
			if( _comboForBlockToReplaceWith != null )
			{
				result = (String) _comboForBlockToReplaceWith.getSelectedItem();
			}
		}

		return( result );
	}

	protected String getRegexExpression()
	{
		String result = null;

		if( getJTextPane() != null )
			result = getJTextPane().getText();

		return( result );
	}

	protected void removeComboListeners()
	{
		if( _comboForBlockToReplaceWith != null )
		{
			_comboForBlockToReplaceWith.removeActionListener( getComboActionListener() );
		}
	}

	protected void addComboListeners()
	{
		if( _comboForBlockToReplaceWith != null )
		{
			_comboForBlockToReplaceWith.setRenderer( getComboCellRenderer(_comboForBlockToReplaceWith) );
			_comboForBlockToReplaceWith.addActionListener( getComboActionListener() );
		}
	}

	protected List<RegexToken> getRegexTokenList( String expression )
	{
		boolean permisive = true;
		return( BlockRegexUtils.instance().getRegexTokenList(expression, permisive) );
	}

	protected List<RegexToken> getRegexTokenList() throws BadLocationException
	{
		return( getRegexTokenList( getPaneText() ) );
	}

	@Override
	protected void addParticularStyles(Integer defaultFontSize)
	{
		final Style defaultStyle = newFormattedStyleToBeModified(RegexExternalFormatter.DEFAULT_STYLE);

		final Style styleForBlockToReplaceWith = newFormattedStyleToBeModified(RegexExternalFormatter.STYLE_FOR_BLOCK_TO_REPLACE_WITH);
		StyleConstants.setForeground(styleForBlockToReplaceWith, getInvertibleColor( INVERTIBLE_GREEN_COLOR_INDEX ) );
		StyleConstants.setBold(styleForBlockToReplaceWith, true);

		final Style styleForErrors = newFormattedStyleToBeModified(RegexExternalFormatter.STYLE_FOR_ERROR);
		StyleConstants.setForeground(styleForErrors, Color.RED );//getInvertibleColor( INVERTIBLE_RED_COLOR_INDEX ) );
		StyleConstants.setBold(styleForErrors, true);
	}

	@Override
	protected void invokeExternalFormatterToFormatText( String text ) throws BadLocationException
	{
		_externalFormatter.setBlockToReplaceWith( getBlockToReplaceWith( getComboSelection() ) );

		List<RegexToken> list = getOrCalculateTokenList( text );
		_externalFormatter.setRegexTokenList( list );

		super.invokeExternalFormatterToFormatText(text);
	}

	protected List<RegexToken> getOrCalculateTokenList( String text )
	{
		if( _lastTokenList == null )
			_lastTokenList = ExecutionFunctions.instance().safeFunctionExecution( () -> getRegexTokenList(text) );

		return( _lastTokenList );
	}

	protected void invalidateTokens()
	{
		_lastTokenList = null;
		_externalFormatter.setIsNew( true );
	}

	@Override
	public void dispose()
	{
		super.dispose();

		removeComboListeners();
	}

	public void fillInComboBox()
	{
		if( _comboForBlockToReplaceWith != null )
		{
			List<RegexToken> tokenList = ExecutionFunctions.instance().safeFunctionExecution( () -> getRegexTokenList() );

			String selectedBlockToReplaceWith = getBlockToReplaceWith( getComboSelection() );
			_isFirstTime = false;
			String[] elements = getElementsForCombo( tokenList, selectedBlockToReplaceWith );

			ComboBoxFunctions.instance().fillComboBox(_comboForBlockToReplaceWith, elements, selectedBlockToReplaceWith);
		}
	}

	protected int incAndGetIndex( Map<String, AtomicInteger> mapOfIndexes, String blockName )
	{
		int result = 0;

		if( blockName != null )
		{
			AtomicInteger ai = mapOfIndexes.get( blockName );
			if( ai == null )
			{
				ai = new AtomicInteger(0);
				mapOfIndexes.put( blockName, ai );
			}
			result = ai.addAndGet(1);
		}

		return( result );
	}

	protected String[] getElementsForCombo( List<RegexToken> tokenList,
											String selectedBlockToReplaceWith )
	{
		List<String> resultList = new ArrayList<>();

		Map<String, AtomicInteger> mapOfIndexes = new HashMap<>();
		for( RegexToken token: tokenList )
		{
			if( token.getTokenId().equals( RegexTokenId.BLOCK_NAME ) )
			{
				String blockName = StringFunctions.instance().removeAtEnd( token.getString(), "?" );

				int index = incAndGetIndex( mapOfIndexes, blockName );

				String element = String.format( "%s[%d]", blockName, index );

				resultList.add( element );
			}
		}

		if( ( selectedBlockToReplaceWith != null ) &&
			! resultList.contains( selectedBlockToReplaceWith ) )
		{
			resultList.add( selectedBlockToReplaceWith );
		}

		return( resultList.toArray( new String[resultList.size()] ) );
	}

	public boolean blockIsPresent( String blockToFind ) throws BadLocationException
	{
		String expression = getPaneText();
		boolean result = BlockRegexUtils.instance().blockIsPresent(expression, blockToFind );

		return( result );
	}

	public void revert()
	{
		_isFirstTime = true;

		updateCombo();
	}

	public void updateCombo()
	{
		fillInComboBox();
	}

	protected String getBlockToReplaceWith( String comboItem )
	{
		return( comboItem );
	}

	protected int getXdeltaForTotalSize()
	{
		return( IntegerFunctions.zoomValueRound( getJTextPane().getFont().getSize() , 0.5D ) );
	}

	public Dimension estimateTotalDimension( int maxWidth )
	{
		Dimension result = super.estimateTotalDimension( maxWidth );
		
		if( result != null )
			result.width += getXdeltaForTotalSize();

		return( result );
	}

	@Override
	protected Color[] createOriginalInvertibleColors() {
		return( _originalPutOutableColorModeColors );
	}

	protected class RegexFormatterListener extends RegexForBlockToReplaceFormatterListener
	{
		public RegexFormatterListener( RegexDocumentFormatterComboForBlockToReplaceWithUpdater documentFormatter,
										JTextPane textPane )
		{
			super( documentFormatter, textPane );
		}

		@Override
		public void caretUpdate(CaretEvent e)
		{
			boolean isNew = ( _lastTokenList == null );

			super.caretUpdate(e);

			formatDocument();

			_externalFormatter.setIsNew( isNew );
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			invalidateTokens();

			super.insertUpdate( e );
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			invalidateTokens();

			super.insertUpdate( e );
		}
	}


	class ComboCellRenderer extends ComboCellRendererBase
	{
		protected Font _nonExistingBlockFont = null;

		public ComboCellRenderer( JComboBox combo )
		{
			super( combo );
		}

		protected Font getNonExistingBlockFont( Font font )
		{
			if( ( _nonExistingBlockFont == null ) || ( _nonExistingBlockFont.getSize() != font.getSize() ) )
			{
				_nonExistingBlockFont = FontFunctions.instance().getStyledFont( font, Font.BOLD );
			}

			return( _nonExistingBlockFont );
		}

		public Component getListCellRendererComponent(JList list, Object value, int index,
														boolean isSelected, boolean cellHasFocus) {
			Font font = getCombo().getFont();
//			Font font = list.getFont();
			Color fgColor = list.getForeground();

			String blockToFind = getBlockToReplaceWith( (String) value );
			Boolean blockIsPresent = ExecutionFunctions.instance().safeFunctionExecution( () -> blockIsPresent( blockToFind ) );
			if( ( blockIsPresent == null ) || !blockIsPresent  )
			{
				font = getNonExistingBlockFont( font );
				fgColor = Color.RED;
			}

			JLabel renderer = (JLabel) getDefaultCellRenderer().getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

			setOriginalColors( renderer );

			renderer.setForeground(fgColor);
			renderer.setText( (String) value );
			renderer.setFont( font );

			return renderer;
		}
	}
}
