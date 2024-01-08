/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.zoom.filechooser;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindow;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ConfigurationForFileChooserInterface;
import com.frojasg1.desktop.libtablecolumnadjuster.TableColumnAdjuster;
import com.frojasg1.general.desktop.classes.Classes;
import com.frojasg1.general.desktop.generic.dialogs.helper.DesktopDialogsWrapperHelper;
import com.frojasg1.general.desktop.language.DesktopSystemStringsConf;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.buttons.ButtonFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.jtable.JTableFunctions;
import com.frojasg1.general.desktop.view.zoom.filechooser.shell.ShellFolder;
import com.frojasg1.general.desktop.view.zoom.ui.ZoomMetalFileChooserUI;
import com.frojasg1.general.reflection.ReflectionFunctions;
import com.frojasg1.general.threads.ThreadFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.frojasg1.sun.awt.shell.ShellFolder;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJFileChooser extends JFileChooser
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoomJFileChooser.class);
	
		protected static final int INDEX_OF_ONLY_FILE_LIST_JTOGGLE_BUTTON = 0;
		protected static final int INDEX_OF_FILE_DETAILS_JTOGGLE_BUTTON = 1;

		protected static final int INDEX_OF_LIST_MODE_RADIOBUTTON = 0;
		protected static final int INDEX_OF_DETAILS_MODE_RADIOBUTTON = 1;

		protected JDialog _jDialog = null;

		protected Rectangle _initialBounds = null;

		protected ConfigurationForFileChooserInterface _conf;

		protected List<JToggleButton> _toggleButtons;

		protected Component _filePane;

		protected JTable _detailsTable;

		protected boolean _isDarkMode = false;

		protected MouseAdapter _detailsActionPerformedListener;

		protected Component _parent;

		protected List<JZoomFileChooserTableCellRendererColorInversor> _tableCellRendererColorInversorList;

		protected Set<Component> _alreadyInvertedComponentColors;

		protected JRadioButtonMenuItem[] _modeRadioButtons = new JRadioButtonMenuItem[2];

		public ZoomJFileChooser( Component parent, String path, ConfigurationForFileChooserInterface conf )
		{
			this( parent, path, conf, null, false );
		}

		public ZoomJFileChooser( Component parent, String path, ConfigurationForFileChooserInterface conf,
								Locale locale, boolean isDarkMode )
		{
			super( path );

			_parent = parent;
			_conf = conf;
			System.out.println( "Locale: " + locale );

			_isDarkMode = isDarkMode;

			if( locale == null )
				locale = Locale.US;
			setLocale( locale );

			System.out.println( "Locale: " + locale );

			ZoomMetalFileChooserUI ui = new ZoomMetalFileChooserUI( this, isDarkMode );
			ui.setZoomFactor(conf.getZoomFactor());

			setUI( ui );

			if(isAcceptAllFileFilterUsed()) {
				resetChoosableFileFilters();
				addChoosableFileFilter(getAcceptAllFileFilter());
			}
		}

		public void init( Component parent, Rectangle initialBounds )
		{
			_tableCellRendererColorInversorList = new ArrayList<>();

			zoomWidthOfAlignedLabels();

			_jDialog = createDialog_internal( parent );
			setDialogBounds( initialBounds );

			_toggleButtons = createListOfToggleButtonsAndFilePane(_jDialog);

			getFileDetailsToggleButton().addActionListener( evt ->
				SwingUtilities.invokeLater( () -> fileDetailsButtonPressed(evt) ) );

			getOnlyFileListToggleButton().addActionListener( evt ->
				SwingUtilities.invokeLater( () -> fileDetailsButtonPressed(evt) ) );

			getFileDetailsToggleButton().addActionListener( evt ->
						firePairedEvent( evt, getOnlyFileListToggleButton()) );

			getOnlyFileListToggleButton().addActionListener( evt ->
						firePairedEvent( evt, getFileDetailsToggleButton()) );

//			ComponentFunctions.instance().inspectHierarchy(_jDialog,
//				comp -> System.out.println( "Index:" + _toggleButtons.indexOf(comp) ) );
		}

		protected BaseApplicationConfigurationInterface getAppliConf()
		{
			return( GenericFunctions.instance().getAppliConf() );
		}

		protected double getZoomFactor()
		{
			return( getAppliConf().getZoomFactor() );
		}

		protected JLabel copyLabel( JLabel label )
		{
			JLabel result = new JLabel( label.getText() );
			result.setFont( label.getFont() );
			
			return( result );
		}

		protected void zoomWidthOfAlignedLabels()
		{
			double zoomFactor = getZoomFactor();
			ComponentFunctions.instance().browseComponentHierarchy(this,
				comp -> {
					if( comp.getClass().getName().equals( "javax.swing.plaf.metal.MetalFileChooserUI$AlignedLabel" ) )
					{
						JLabel label = ( (JLabel) comp );
						Dimension orig = copyLabel( label ).getPreferredSize();
						Dimension pref = ViewFunctions.instance().getNewDimension(orig,
										label.getInsets(),
										zoomFactor);
						comp.setPreferredSize(pref);
					}

					return( null );
				});
		}

		public void setAlreadyInvertedComponentColors( Set<Component> alreadyInvertedComponentColors )
		{
			_alreadyInvertedComponentColors = alreadyInvertedComponentColors;
		}

		@Override
		public void addChoosableFileFilter(FileFilter filter)
		{
			super.addChoosableFileFilter( createProxyFileFilter(filter) );
		}

		@Override
		public void setFileFilter(FileFilter filter)
		{
			super.setFileFilter( createProxyFileFilter(filter) );
		}

		protected FileFilter createProxyFileFilter( FileFilter filter )
		{
			return( ( ( filter == null ) || ( filter instanceof ProxyFileFilter ) ) ?
					filter :
					new ProxyFileFilter( filter ) );
		}

		protected JZoomFileChooserTableCellRendererColorInversor add( JZoomFileChooserTableCellRendererColorInversor tableCellRenderer )
		{
			if( tableCellRenderer != null )
				_tableCellRendererColorInversorList.add( tableCellRenderer );

			return( tableCellRenderer );
		}

		protected void firePairedEvent( ActionEvent evt, JToggleButton pairedButton )
		{
			if( ! evt.getActionCommand().equals( "paired" ) )
				ButtonFunctions.instance().fireActionEvent(pairedButton, "paired" );
		}

		protected JTable getDetailsTable()
		{
			AtomicReference<JTable> ref = new AtomicReference<>();

//			JTable detailsTable = (JTable) ReflectionFunctions.instance().getAttribute("detailsTable", JTable.class, _filePane);
			ComponentFunctions.instance().browseComponentHierarchy(_filePane,
				comp2 -> {
					if( comp2 instanceof JTable )
						ref.set((JTable) comp2 );

					return( null );
				});

			return( ref.get() );
		}

		protected JScrollPane getScrollPane( Component comp )
		{
			return( ComponentFunctions.instance().getFirstParentInstanceOf( JScrollPane.class, comp ) );
		}

		protected void setPopupModeTexts( JPopupMenu popupMenu )
		{
			Component[] radioButtons = ( ( JMenu ) popupMenu.getComponent(0)).getMenuComponents();
			String[] labels = new String[] { DesktopSystemStringsConf.CONF_LIST_MODE, DesktopSystemStringsConf.CONF_DETAILS_MODE };
			
			int index = 0;
			for( Component rb: radioButtons )
				if( rb instanceof JRadioButtonMenuItem )
				{
					_modeRadioButtons[index] = (JRadioButtonMenuItem) rb;
					_modeRadioButtons[index].setText( DesktopSystemStringsConf.instance().getInternationalString( labels[index++] ) );
				}
		}

		protected DesktopDialogsWrapperHelper getHelper()
		{
			return( DesktopDialogsWrapperHelper.instance() );
		}

		public void zoomFont( Component comp, double zoomFactor )
		{
			getHelper().zoomFont( comp, zoomFactor );
		}

		public void zoomScrollBar( JScrollBar sb, double zoomFactor )
		{
			getHelper().zoomScrollBar( sb, zoomFactor );
		}

		protected void convertJTable( JTable jTable )
		{
//			M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent(
//						_conf.getZoomFactor(), getScrollPane( _detailsTable ), _parent);
			JScrollPane sp = getScrollPane( jTable );

//			SwitchToZoomComponents stzc = new SwitchToZoomComponents( null, null );
//			stzc.convertScrollPane( sp, _isDarkMode );
			ConvertDialogComponents.instance().convertScrollPane( sp, _isDarkMode );

			double zoomFactor = _conf.getZoomFactor();
			Component tableHeader = jTable.getTableHeader();
			zoomFont( tableHeader, zoomFactor );
			JTableFunctions.instance().zoomTableRowHeight(jTable, zoomFactor);

			zoomScrollBar( sp.getHorizontalScrollBar(), zoomFactor );
			zoomScrollBar( sp.getVerticalScrollBar(), zoomFactor );

//			setPopupModeTexts( jTable );
			// time to update radioButtonSelection
//			updateModeRadioButtonSelection();

			if( _isDarkMode )
			{
				getColorInversor().invertSingleColorsGen( sp );
				getColorInversor().invertSingleColorsGen( sp.getViewport() );
				getColorInversor().invertSingleColorsGen( tableHeader );
//				getColorInversor().invertSingleColorsGen( jTable );
				getColorInversor().invertRendererColors(jTable,
					(ori, name) -> add( new JZoomFileChooserTableCellRendererColorInversor(ori, getColorInversor())
						.associateJTable(jTable) ) );
//				getColorInversor().invertEditorColors(jTable);
			}

			JTableFunctions.instance().switchCellEditors(jTable);

			_detailsTable.addMouseListener( getOrCreateMouseAdapter() );
			ActionListener listener = evt -> delayedInvokeAdjustColumns( );
			ComponentFunctions.instance().browseComponentHierarchy( this, comp -> {
				if( comp instanceof AbstractButton )
					((AbstractButton) comp).addActionListener(listener);
				else if( comp instanceof JComboBox )
					((JComboBox) comp).addActionListener(listener);
				else if( comp instanceof JTextField )
					((JTextField) comp).addActionListener(listener);

				return( null );
			});
		}

		protected void fileDetailsButtonPressed( ActionEvent evt )
		{
			try
			{
				applyFileDetailsSelectedIntoConf();
				if( getFileDetailsToggleButton().isSelected() )
				{
					JTable detailsTable = getDetailsTable();
					if( detailsTable != null )
					{
						if( _detailsTable != detailsTable )
						{
							_detailsTable = detailsTable;
//							ComponentFunctions.instance().inspectHierarchy(_detailsTable.getParent().getParent());
							convertJTable( _detailsTable );
						}
					}

					delayedInvokeAdjustColumns();
				}
			}
			catch( Exception ex )
			{
				LOGGER.error( "Error when preparing JTable", ex );
			}
		}

		protected MouseAdapter getOrCreateMouseAdapter()
		{
			if( _detailsActionPerformedListener == null )
				_detailsActionPerformedListener = new MouseAdapter() {
					@Override
					public void mousePressed( MouseEvent evt )
					{
						delayedInvokeAdjustColumns();

						SwingUtilities.invokeLater( () ->
										System.out.println( String.format( "%n%n%n" ) +
								ViewFunctions.instance().traceComponentTree(ZoomJFileChooser.this,
									(component) -> "font_size: " + ( component.getFont() == null ?
																	"null" :
																	component.getFont().getSize() ) ) )
						);
					}
				};

			return( _detailsActionPerformedListener );
		}

		protected void delayedInvokeAdjustColumns( )
		{
			if( getDetailsTable() != null )
			ThreadFunctions.instance().delayedInvokeEventDispatchThread(
				() -> adjustColumns( getDetailsTable() ),
				200 );
		}

		protected void adjustColumns(JTable detailsTable)
		{
			for( JZoomFileChooserTableCellRendererColorInversor tcr: _tableCellRendererColorInversorList )
				tcr.resetSelection();

			SwingUtilities.invokeLater( () ->
				new TableColumnAdjuster(detailsTable).adjustColumns()
			);
		}

		protected ColorInversor getColorInversor()
		{
			return( FrameworkComponentFunctions.instance().getColorInversor(this) );
		}

		protected JToggleButton getOnlyFileListToggleButton()
		{
			return( _toggleButtons.get(INDEX_OF_ONLY_FILE_LIST_JTOGGLE_BUTTON) );
		}

		protected JToggleButton getFileDetailsToggleButton()
		{
			return( _toggleButtons.get(INDEX_OF_FILE_DETAILS_JTOGGLE_BUTTON) );
		}

		protected List<JToggleButton> createListOfToggleButtonsAndFilePane( Component comp )
		{
			List<JToggleButton> result = new ArrayList<>();
//			Class<?> filePaneClass = Classes.getFilePaneClass();	// JRE 7, 8, ...

			ComponentFunctions.instance().browseComponentHierarchy(comp,
				comp2 -> {
					if( comp2 instanceof JToggleButton )
						result.add((JToggleButton) comp2 );

					if( ( comp2 != null ) && comp2.getClass().getSimpleName().equals( "FilePane" ) )
//					if( ( filePaneClass != null ) && ( filePaneClass.isInstance(comp2) ) )
						setFilePane( comp2 );

					return( null );
				});

			return( result );
		}

		protected void setFilePane( Component filePane )
		{
			this._filePane = filePane;

			// invoke later, in order to the menu be already switched to ZoomComponents before being set
			SwingUtilities.invokeLater( this::updatePopupMenu );
		}

		protected Set<Component> invertColorsIfNecessary( Component target, Component parent,
												Set<Component> alreadyDone )
		{
			if( ( target != null ) && ( parent != null ) )
			{
				InternationalizedWindow iw = FrameworkComponentFunctions.instance().getInternationalizedWindow(parent);
				if( ( iw != null ) && ( iw.isDarkMode() ) )
					alreadyDone = iw.getColorInversor().setOneShotInconditionalDarkMode(target, alreadyDone);
			}
			return( alreadyDone );
		}

		public void updatePopupMenu()
		{
			if( _filePane != null )
			{
				JPopupMenu newPopupMenu = getNewPopupMenu( _filePane );
				ReflectionFunctions.instance().setAttribute("contextMenu",
					_filePane, Classes.getFilePaneClass(),
					newPopupMenu);
				if( _filePane instanceof JPanel )
					( (JPanel) _filePane ).setComponentPopupMenu(newPopupMenu);

				_alreadyInvertedComponentColors = invertColorsIfNecessary( newPopupMenu, _parent, _alreadyInvertedComponentColors );

				setPopupModeTexts( newPopupMenu );
				// time to update radioButtonSelection
				updateModeRadioButtonSelection();

			}
		}

		protected JPopupMenu getNewPopupMenu( Component filePane )
		{
			AtomicReference<JPopupMenu> result = new AtomicReference<>();

			ComponentFunctions.instance().browseComponentHierarchy(filePane,
				comp2 -> {
					if( ( result.get() == null ) &&
						( comp2 instanceof JComponent ) &&
						!( comp2 instanceof JComboBox ) )
					{
						result.set( ( (JComponent) comp2 ).getComponentPopupMenu() );
					}

					return( null );
				});

			return( result.get() );
		}

		@Override
		public int showDialog(Component parent, String approveButtonText)
		{
			setFileDetailsSelectedToView();
//			SwingUtilities.invokeLater( this::setFileDetailsSelectedToView );
//			ThreadFunctions.instance().delayedInvokeEventDispatchThread(
//				this::setFileDetailsSelectedToView, 350 );
			int result = super.showDialog( parent, approveButtonText );
//			applyFileDetailsSelectedIntoConf();

			return( result );
		}

		protected void applyFileDetailsSelectedIntoConf()
		{
			_conf.setFileDetailsSelected( getFileDetailsToggleButton().isSelected() );
		}

		protected void updateModeRadioButtonSelection()
		{
			boolean fileDetailsActivated = _conf.isFileDetailsActivated();

			getDetailsRadioButton().setSelected(fileDetailsActivated);
			getListRadioButton().setSelected(!fileDetailsActivated);
		}

		protected void setFileDetailsSelectedToView()
		{
			boolean fileDetailsActivated = _conf.isFileDetailsActivated();
			
			getFileDetailsToggleButton().setSelected( fileDetailsActivated );
			getOnlyFileListToggleButton().setSelected( ! fileDetailsActivated );

//			updateModeRadioButtonSelection();

			JToggleButton button = fileDetailsActivated ?
									getFileDetailsToggleButton() :
									getOnlyFileListToggleButton();
			ButtonFunctions.instance().fireActionEvent(button, "Initialization" );
		}

		protected JRadioButtonMenuItem getListRadioButton()
		{
			return( _modeRadioButtons[INDEX_OF_LIST_MODE_RADIOBUTTON] );
		}

		protected JRadioButtonMenuItem getDetailsRadioButton()
		{
			return( _modeRadioButtons[INDEX_OF_DETAILS_MODE_RADIOBUTTON] );
		}

		protected JDialog createDialog_internal( Component parent ) throws HeadlessException
		{
			JDialog result = super.createDialog(parent);

			result.setAlwaysOnTop(true);

			return( result );
		}

		public JDialog getDialog()
		{
			return( _jDialog );
		}

		@Override
		public void setBounds( int xx, int yy, int width, int height )
		{
			super.setBounds( xx, yy, width, height );
		}

		// https://stackoverflow.com/questions/2270690/set-the-location-of-the-jfilechooser
		@Override
		protected JDialog createDialog(Component parent) throws HeadlessException
		{
			return( getDialog() );
		}

		public void setDialogBounds( Rectangle bounds )
		{
			_initialBounds = bounds;

			if( _initialBounds != null )
				_jDialog.setBounds( _initialBounds );
		}

		public Rectangle getDialogBounds()
		{
			Rectangle result = null;
			if( _jDialog != null )
			{
				result = _jDialog.getBounds();
			}
			return( result );
		}

		// https://www.google.com/search?q=jfilechooser+getComputerNodeFolder&rlz=1C1NCHA_enES646ES646&oq=jfilechooser+getComputerNodeFolder&aqs=chrome..69i57j33.4173j0j8&sourceid=chrome&ie=UTF-8
		@Override
		public void approveSelection() {
			File selectedFile = getSelectedFile();
			if (selectedFile != null && ShellFolder.isComputerNode(selectedFile)) {
				try {
					// Resolve path and try to navigate to it
					setCurrentDirectory(getComputerNodeFolder(selectedFile.getPath()));
				} catch (Exception ex) {
					// Alert user if given computer node cannot be accessed
					JOptionPane.showMessageDialog(this, "Cannot access " + selectedFile.getPath());
				}
			} else {
				super.approveSelection();
			}
		}

		protected File getComputerNodeFolder( String childFolderName )
		{
			File result = new File(childFolderName);
			FileSystemView fsv = FileSystemView.getFileSystemView();
			result = fsv.getParentDirectory(result);

			return( result );
		}
}
