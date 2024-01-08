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
package com.frojasg1.general.desktop.view.zoom.ui;

import com.frojasg1.general.desktop.classes.Classes;
import com.frojasg1.general.desktop.view.IconFunctions;
import com.frojasg1.general.desktop.view.javadesktopmodule.ReflectionToJavaDesktop;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.general.reflection.ReflectionFunctions;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.io.File;
import java.util.Locale;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.metal.MetalFileChooserUI;
//import com.frojasg1.sun.swing.SwingUtilities2;


/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomMetalFileChooserUI extends MetalFileChooserUI
{
	protected DoubleReference _zoomFactor = new DoubleReference( 1.0D );

    protected AcceptAllFileFilterLocale acceptAllFileFilterLocale = new AcceptAllFileFilterLocale();

	protected JFileChooser _jFileChooser = null;

	protected boolean _isDarkMode = false;

//	protected JComboBox directoryComboBox = null;

	public ZoomMetalFileChooserUI( JFileChooser jfc, boolean isDarkMode )
	{
		super( jfc );

		_isDarkMode = isDarkMode;
		_jFileChooser = jfc;
	}

	@Override
    public FileFilter getAcceptAllFileFilter(JFileChooser fc)
	{
		return( acceptAllFileFilterLocale );
	}

	protected boolean isDarkMode()
	{
		return( _isDarkMode );
	}

	@Override
	protected void installIcons(JFileChooser fc)
	{
		super.installIcons(fc);

        directoryIcon    = createZoomIcon( directoryIcon, true );
        fileIcon    = createZoomIcon( fileIcon, true );
        computerIcon    = createZoomIcon( computerIcon, true );
        hardDriveIcon    = createZoomIcon( hardDriveIcon, true );
        floppyDriveIcon    = createZoomIcon( floppyDriveIcon, true );
        newFolderIcon    = createZoomIcon( newFolderIcon, false );
        upFolderIcon    = createZoomIcon( upFolderIcon, false );
        homeFolderIcon    = createZoomIcon( homeFolderIcon, false );
        detailsViewIcon    = createZoomIcon( detailsViewIcon, false );
        listViewIcon    = createZoomIcon( listViewIcon, false );
        viewMenuIcon    = createZoomIcon( viewMenuIcon, false );
	}

	protected ZoomIconImp createZoomIcon( Icon icon, boolean canInvert )
	{
		ZoomIconImp result = null;
		if( icon != null )
		{
			if( canInvert )
				icon = invertIfNecessary( icon );
			result = new ZoomIconImp( icon, _zoomFactor );
//			result.setAdditionalFactor( 1.33D );
		}
		return( result );
	}

	protected Icon invertIfNecessary( Icon icon )
	{
		Icon result = icon;
		if( isDarkMode() )
			result = IconFunctions.instance().invertIconColors(icon);

		return( result );
	}

	public void setZoomFactor( double zoomFactor )
	{
		_zoomFactor._value = zoomFactor;
	}

    // *****************************************
    // ***** default AcceptAll file filter *****
    // *****************************************
    protected class AcceptAllFileFilterLocale extends FileFilter {

        public AcceptAllFileFilterLocale() {
        }

        public boolean accept(File f) {
            return true;
        }

        public String getDescription() {
            return UIManager.getString("FileChooser.acceptAllFileFilterText", _jFileChooser.getLocale() );
        }
    }

	protected JPanel getTopPanel(JFileChooser fc)
	{
		JPanel result = null;

		LayoutManager lm = fc.getLayout();
		if( lm instanceof BorderLayout )
		{
			Component resultComp = ( (BorderLayout) lm ).getLayoutComponent( BorderLayout.NORTH );
			if( resultComp instanceof JPanel )
				result = (JPanel) resultComp;
		}

		return( result );
	}

	private Integer getMnemonic(String key, Locale l) {
		return ReflectionToJavaDesktop.instance().SwingUtilities2_getUIDefaultsInt(key, l);
    }
/*
	protected void changeDefaultCellEditorJTextField()
	{
		try
		{
			FilePane fp = ReflectionFunctions.instance().getAttribute( "filePane", com.frojasg1.sun.swing.FilePane.class, this, javax.swing.plaf.metal.MetalFileChooserUI.class);

			DefaultCellEditor dce = (DefaultCellEditor) ReflectionFunctions.instance().invokeMethod( "getDetailsTableCellEditor", FilePane.class, fp);

			CtClass origClazz = ClassPool.getDefault().get("sun.swing.FilePane$DetailsTableCellEditor");
			
			String subClassName = "sun.swing.FilePane$DetailsTableCellEditor_New";
			CtClass subClass = ClassPool.getDefault().makeClass(subClassName, origClazz);
			CtMethod m = CtNewMethod.make(
						 "		public java.awt.Component getTableCellEditorComponent(javax.swing.JTable jtable, Object o, boolean bln, int i, int i1) {" +
						"			java.awt.Component result = super.getTableCellEditorComponent( jtable, o, bln, i, i1 );\n" +
						"			result.setFont( result.getFont().deriveFont( (int) ( result.getFont().getSize() * " + _zoomFactor._value + " ) ) );\n" +
						"			return( result );\n" +
						"		}\n" +
						"",
						 subClass );
			subClass.addMethod(m);
			javassist.CtConstructor cons = javassist.CtNewConstructor.make( " public " + subClass.getSimpleName() + "( com.frojasg1.sun.swing.FilePane fp, javax.swing.JTextField tf ) \n" +
																			"{ super(fp, tf); }", subClass );
//			subClass.addConstructor(CtNewConstructor.defaultConstructor(subClass));
			subClass.addConstructor(cons);

			Class clazz = subClass.toClass();

			JTextField tf = ReflectionFunctions.instance().getAttribute( "tf", javax.swing.JTextField.class, dce);
			Object obj = clazz.getConstructor( FilePane.class, JTextField.class ).newInstance( fp, tf );

			ReflectionFunctions.instance().setAttribute( "tf", obj, origClazz.toClass(), tf);

			ReflectionFunctions.instance().setAttribute( "tableCellEditor", fp, FilePane.class, obj);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected void changeCellEditorFont()
	{
		changeDefaultCellEditorJTextField();

//		JTextField tf = getDefaultCellEditorJTextField();
//		if( tf != null )
//		{
//			tf.setFont( FontFunctions.instance().getZoomedFont( tf.getFont(), _zoomFactor._value ) );
//		}
	}

	public void installComponents(JFileChooser fc)
	{
		super.installComponents(fc);

		changeCellEditorFont();
	}
*/
/*
	public void installComponents(JFileChooser fc)
	{
		super.installComponents(fc);


		JPanel topPanel = getTopPanel(fc);
        // CurrentDir ComboBox

		Locale l = fc.getLocale();
		String lookInLabelText = UIManager.getString("FileChooser.lookInLabelText",l);

		int lookInLabelMnemonic = getMnemonic("FileChooser.lookInLabelMnemonic", l);

        // ComboBox Label
        JLabel lookInLabel = new JLabel(lookInLabelText);
        lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
        topPanel.add(lookInLabel, BorderLayout.BEFORE_LINE_BEGINS);

		directoryComboBox = new JComboBox() {
			@Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                // Must be small enough to not affect total width.
                d.width = IntegerFunctions.zoomValueFloor(150, _zoomFactor._value );
                return d;
            }
		};
        directoryComboBox.putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY,
                                            lookInLabelText);
        directoryComboBox.putClientProperty( "JComboBox.isTableCellEditor", Boolean.TRUE );
        lookInLabel.setLabelFor(directoryComboBox);
        DirectoryComboBoxModel directoryComboBoxModel = createDirectoryComboBoxModel(fc);
        directoryComboBox.setModel(directoryComboBoxModel);
        directoryComboBox.addActionListener(new DirectoryComboBoxAction());
        directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(fc));
        directoryComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        directoryComboBox.setAlignmentY(JComponent.TOP_ALIGNMENT);
        directoryComboBox.setMaximumRowCount(8);

		topPanel.add(directoryComboBox, BorderLayout.CENTER);
	}

	protected class DirectoryComboBoxAction extends AbstractAction {
        protected DirectoryComboBoxAction() {
            super("DirectoryComboBoxAction");
        }

        public void actionPerformed(ActionEvent e) {
            directoryComboBox.hidePopup();
            File f = (File)directoryComboBox.getSelectedItem();
            if (!getFileChooser().getCurrentDirectory().equals(f)) {
                getFileChooser().setCurrentDirectory(f);
            }
        }
    }
*/
}
