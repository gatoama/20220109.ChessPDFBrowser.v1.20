/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable;

import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEventType;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperEventType extends JComponentWrapperEventType
{
	public static final int NEW_CURRENT_ROW = 1024;
	public static final long RECORDS_MOVED = 2048;
	public static final long RECORDS_DELETED = 4096;
	public static final long RECORDS_SORTED = 8192;
	public static final long FILTER_HAS_CHANGED = 16384;
	public static final long SELECTION_HAS_CHANGED = 32768;
}
