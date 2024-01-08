package com.frojasg1.general.desktop.view.javadesktopmodule;

import com.frojasg1.general.desktop.classes.Classes;
import com.frojasg1.general.reflection.ReflectionFunctions;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.text.AttributedCharacterIterator;
import java.util.Locale;

public class ReflectionToJavaDesktop {

    protected static ReflectionToJavaDesktop INSTANCE = new ReflectionToJavaDesktop();

    public static ReflectionToJavaDesktop instance()
    {
        return INSTANCE;
    }

    public void MenuItemLayoutHelperClass_clearUsedClientProperties(JComponent jcomp )
    {
        Class<?> clazz = Classes.getMenuItemLayoutHelperClass();
        ReflectionFunctions.instance().invokeClassMethod( "clearUsedClientProperties",clazz, null, jcomp);
    }

    public int SwingUtilities2_getUIDefaultsInt(Object key, Locale loc )
    {
        Class<?> clazz = Classes.getSwingUtilities2Class();
        return (Integer) ReflectionFunctions.instance().invokeClassMethod( "getUIDefaultsInt", clazz, key, loc );
    }

    public String SwingUtilities2_displayPropertiesToCSS(Font font, Color fg)
    {
        Class<?> clazz = Classes.getSwingUtilities2Class();
        return (String) ReflectionFunctions.instance().invokeClassMethod( "displayPropertiesToCSS", clazz, font, fg );
    }

    public Object DefaultLookup_get(JComponent jcomp, ComponentUI ui, String var2)
    {
        Class<?> clazz = Classes.getDefaultLookupClass();
        return( ReflectionFunctions.instance().invokeClassMethod( "get", clazz, null, jcomp, var2) );
    }

    public int DefaultLookup_getInt(JComponent jcomp, ComponentUI ui, String var2, int defaultValue)
    {
        Class<?> clazz = Classes.getDefaultLookupClass();
        return( (Integer) ReflectionFunctions.instance().invokeClassMethod( "getInt", clazz, null, jcomp, var2, defaultValue) );
    }

    public Object AppContext_getAppContext()
    {
        Class<?> clazz = Classes.getAppContextClass();
        return( ReflectionFunctions.instance().invokeClassMethod( "getAppContext", clazz, null ) );
    }

    public Object AppContext_getAppContext_get(Object key )
    {
        Object result = null;
        if( key != null )
            result = ReflectionFunctions.instance().invokeMethod( "get", AppContext_getAppContext(), key );

        return( result );
    }

    public void AppContext_getAppContext_put(Object key, Object value)
    {
        ReflectionFunctions.instance().invokeMethod( "put", AppContext_getAppContext(), key, value );
    }

    public Color DefaultLookUp_getColor(JComponent jcomp, ComponentUI ui, String var2, Color color)
    {
        Class<?> clazz = Classes.getDefaultLookupClass();
        return( (Color) ReflectionFunctions.instance().invokeClassMethod( "getColor",clazz, null, jcomp, ui, var2, color) );
    }

    public float SwingUtilities2_drawString(JComponent jcomp, Graphics grp, AttributedCharacterIterator var2, int var3, int var4)
    {
        Class<?> clazz = Classes.getSwingUtilities2Class();
        return( (Float) ReflectionFunctions.instance().invokeClassMethod( "drawString", clazz, null, jcomp, grp, var2, var3, var4) );
    }

    public boolean SwingUtilities2_useSelectedTextColor(Highlighter.Highlight var0, JTextComponent var1)
    {
        Class<?> clazz = Classes.getSwingUtilities2Class();
        return( (Boolean) ReflectionFunctions.instance().invokeClassMethod( "useSelectedTextColor", clazz, null, var0, var1) );
    }

    public int SunDragSourceContextPeer_convertModifiersToDropAction(int var0, int var1)
    {
        Class<?> clazz = Classes.getSunDragSourceContextPeerClass();
        return( (Integer) ReflectionFunctions.instance().invokeClassMethod( "convertModifiersToDropAction",clazz, null, var0, var1) );
    }

}
