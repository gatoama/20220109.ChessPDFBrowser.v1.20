/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.generic.dialogs.helper;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindow;
import com.frojasg1.applications.common.components.zoom.SwitchToZoomComponents;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.classes.Classes;
import com.frojasg1.general.desktop.generic.dialogs.impl.DesktopFileChooserParameters;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.jtable.JTableFunctions;
import com.frojasg1.general.desktop.view.zoom.filechooser.ConvertDialogComponents;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.reflection.ReflectionFunctions;
import com.frojasg1.general.zoom.ZoomInterface;
import com.frojasg1.generic.GenericFunctions;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboPopup;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopDialogsWrapperHelper
{
	protected static DesktopDialogsWrapperHelper INSTANCE = new DesktopDialogsWrapperHelper();

	public static DesktopDialogsWrapperHelper instance()
	{
		return( INSTANCE );
	}

	public void zoomScrollBar( JScrollBar sb, double zoomFactor )
	{
		if( sb != null )
		{
			if( sb.getOrientation() == JScrollBar.HORIZONTAL )
				zoomHorizontalScrollBar( sb, zoomFactor );
			else if( sb.getOrientation() == JScrollBar.VERTICAL )
				zoomVerticalScrollBar( sb, zoomFactor );
		}
	}

	protected void zoomHorizontalScrollBar( JScrollBar hsb, double zoomFactor )
	{
		if( hsb != null )
		{
			Dimension size = hsb.getPreferredSize();
			hsb.setPreferredSize( new Dimension( size.width,
									IntegerFunctions.zoomValueCeil(hsb.getPreferredSize().height, zoomFactor)
													)
									);
		}
	}
	
	protected void zoomVerticalScrollBar( JScrollBar vsb, double zoomFactor )
	{
		if( vsb != null )
		{
			Dimension size = vsb.getPreferredSize();
			vsb.setPreferredSize( new Dimension(
										IntegerFunctions.zoomValueCeil( vsb.getPreferredSize().width, zoomFactor),
										size.width )
									);
		}
	}

	public void zoomFont( Component comp, double zoomFactor )
	{
		Font newFont = M_getNewFontForComponentFromApplicationFontSize(comp, zoomFactor );
		if( newFont != null )
			comp.setFont( newFont );
	}

	protected Font M_getNewFontForComponentFromApplicationFontSize( Component comp, double factor )
	{
		Font result = null;

		Font oldFont = comp.getFont();
		if( oldFont != null )
		{
			if( factor > 0.0F )
			{
				result = oldFont.deriveFont( (float) ( factor * oldFont.getSize2D() ) );
			}
		}

		return( result );
	}

	protected void M_changeFontToApplicationFontSize( Component comp,
														double zoomFactor,
														int level,
														int fromLevelToZoomFont )
	{
//		System.out.println( StringFunctions.instance().buildStringFromRepeatedChar( ' ', level * 4) +
//							comp.getClass().getName() );

		if( !(comp instanceof JTable) && ( fromLevelToZoomFont < level ) )
			zoomFont(comp, zoomFactor );

		Class<?> filePaneClass = Classes.getFilePaneClass();	// JRE 7, 8, ...

		if( ( comp instanceof JComboBox ) && !isZoomed( (JComponent) comp) )
		{
			JComboBox combo = (JComboBox) comp;
			BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

			M_changeFontToApplicationFontSize( popup, zoomFactor, level + 1, level + 4 );
		}
//		else if( comp instanceof FilePane )
		else if( ( filePaneClass != null ) && filePaneClass.isInstance( comp ) )	// JRE 7, 8, ...
		{
//			JPopupMenu popupMenu = fp.getComponentPopupMenu();
			JPopupMenu popupMenu = (JPopupMenu) ReflectionFunctions.instance().invokeMethod( "getComponentPopupMenu",
																							comp.getClass(), comp );
			M_changeFontToApplicationFontSize( popupMenu, zoomFactor, level + 1, -1 );
/*
			JTable detailsTable = (JTable) ReflectionFunctions.instance().getAttribute("detailsTable", JTable.class, comp);
			if( detailsTable != null )
				M_changeFontToApplicationFontSize( detailsTable, zoomFactor, level + 1, -1 );

			JList list = (JList) ReflectionFunctions.instance().getAttribute("list", JList.class, comp);
			if( list != null )
				M_changeFontToApplicationFontSize( list, zoomFactor, level + 1, -1 );
*/
		}
		else if( ( comp instanceof JScrollBar ) && !isZoomed( (JComponent) comp) )
		{
			zoomScrollBar( (JScrollBar) comp, zoomFactor );
		}
		else if( comp instanceof JTable )
		{
//			JTableFunctions.instance().zoomCellRenderer( ( (JTable) comp ).getTableHeader().getDefaultRenderer(), zoomFactor );
			JTableFunctions.instance().switchCellEditors( (JTable) comp );

			M_changeFontToApplicationFontSize( ( (JTable) comp ).getTableHeader(), zoomFactor, level + 1, -1 );
		}
		else if( comp instanceof JLabel )
		{
			JLabel jlabel = (JLabel) comp;
			Icon icon = jlabel.getIcon();
			if( icon != null )
			{
				boolean centerSmaller = true;
				ZoomIconImp zi = new ZoomIconImp( icon, new DoubleReference( zoomFactor ), centerSmaller );
				jlabel.setIcon( zi );

				Dimension newDimension = ViewFunctions.instance().getNewDimension(jlabel.getSize(),
																				null, zoomFactor );
				jlabel.setSize(newDimension);
			}
		}

		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				M_changeFontToApplicationFontSize(contnr.getComponent(ii), zoomFactor, level + 1, fromLevelToZoomFont );
			}

			if( contnr instanceof JMenu )
			{
				JMenu jmnu = (JMenu) contnr;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_changeFontToApplicationFontSize(jmnu.getMenuComponent( ii ), zoomFactor, level + 1, fromLevelToZoomFont );
			}
		}
	}

	protected boolean isZoomed( JComponent comp )
	{
		boolean result = false;
		ComponentUI ui = ComponentFunctions.instance().getUI(comp);
		
		if( ( ui != null ) && ( ui.getClass().getName().contains("frojasg1") ) )
			result = true;

		return( result );
	}

	public Set<Component> M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( double zoomFactor,
																								Component comp,
																								Component parent)
	{
		return( M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( zoomFactor, comp,
																						parent, null ) );
	}

	public Set<Component> M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( double zoomFactor,
																								Component comp,
																								Component parent,
																								Predicate<Component> compFilter)
	{
		boolean isDarkMode = isDarkMode();
//		double factor = 0.0D;
		double factor = zoomFactor;
		Set<Component> invertedComponentColors = null;

		if( factor > 0.0F )
		{
//			System.out.println( String.format( "%n%n%nAntes%n=====" ) +
//								ViewFunctions.instance().traceComponentTree(comp, (component) -> "preferredSize : " + component.getPreferredSize() + "     size : " + component.getSize() ));


//			System.out.println( ViewFunctions.instance().traceComponentTree(comp));

			SwitchToZoomComponents stzc = new SwitchToZoomComponents( null, null );
//			stzc.changeToColorInvertibleComponents( comp, isDarkMode );
			ConvertDialogComponents.instance().changeToColorInvertibleComponents( comp, isDarkMode );

			invertedComponentColors = invertColorsIfNecessary(comp, parent, compFilter, null);

			M_changeFontToApplicationFontSize( comp, factor );

			stzc.setZoomFactor(comp, zoomFactor, (comp_) -> {
				boolean result = ( comp_ != null ) &&
					(
						( comp_.getClass().getName().endsWith( "AlignedLabel" ) ) ||
						( comp_ instanceof ZoomInterface )
					);
				return( result );
			});

			System.out.println( String.format( "%n%n%n" ) +
								ViewFunctions.instance().traceComponentTree(comp, (component) -> stzc.getComponentResizingResult().get(component) ));

//			FileChooserUI ui;
//			if( ( comp instanceof JFileChooser ) ||
//				( comp instanceof JOptionPane ) )
			if( comp instanceof JFileChooser )
			{
				Dimension dim = comp.getPreferredSize();
				int width = (int) ( ( (float) dim.getWidth()) * factor );
				int height = (int) ( ( (float) dim.getHeight()) * factor );
				Dimension newDim = new Dimension( width, height);

//				comp.setPreferredSize( newDim );
			}

//			System.out.println( ViewFunctions.instance().traceComponentTree(comp, (comp_) -> getInfoString(comp_) ) );

//			System.out.println( String.format( "%n%n%nDespues%n=======" ) +
//								ViewFunctions.instance().traceComponentTree(comp, (component) -> "preferredSize : " + component.getPreferredSize() + "     size : " + component.getSize() ));
		}
		
		return( invertedComponentColors );
	}

	protected String getInfoString( Component comp )
	{
		String result = "";
		if( comp instanceof Container )
		{
			Container cont = (Container) comp;
			Insets insets = cont.getInsets();
			if( insets != null )
			{
				result = " insets - " + insets;
			}

			LayoutManager lm = cont.getLayout();
			if( lm != null )
			{
				result += "        " + lm.getClass().getName();
				if( lm instanceof BorderLayout )
				{
					result += " ( " + lm + " )";
				}
			}
		}
		return( result );
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( GenericFunctions.instance().getAppliConf() );
	}

	public void setDefaultLocale( DesktopFileChooserParameters dfcp ) {
		Locale locale = dfcp.getLocale();
		if( locale == null )
		{
			BaseApplicationConfigurationInterface appliConf = getAppliConf();
			if( appliConf != null )
			{
				String language = appliConf.getLanguage();
				locale = GenericFunctions.instance().getObtainAvailableLanguages().getLocaleOfLanguage( language );
				dfcp.setLocale( locale );
			}
		}
	}

	public boolean isDarkMode()
	{
		boolean result = false;

		BaseApplicationConfigurationInterface appliConf = getAppliConf();
		if( appliConf != null )
			result = appliConf.isDarkModeActivated();

		return( result );
	}

	protected Set<Component> invertColorsIfNecessary( Component target, Component parent,
														Predicate<Component> compFilter,
														Set<Component> alreadyDone )
	{
		if( ( target != null ) && ( parent != null ) )
		{
			InternationalizedWindow iw = FrameworkComponentFunctions.instance().getInternationalizedWindow(parent);
			if( ( iw != null ) && ( iw.isDarkMode() ) )
				alreadyDone = iw.getColorInversor().setOneShotInconditionalDarkMode(target, compFilter, alreadyDone);
		}
		return( alreadyDone );
	}

	public Point getCenteredLocationForComponent( Component comp )
	{
		int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
		Point result = new Point( width/2 - comp.getWidth()/2, height/2 - comp.getHeight()/2 );

		return( result );
	}
	
	protected void M_changeFontToApplicationFontSize( Component comp, double factor )
	{
		M_changeFontToApplicationFontSize( comp, factor, 0, -1 );
	}

}
