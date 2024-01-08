/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.time;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TimeFunctions {
	protected static TimeFunctions _instance = new TimeFunctions();
	
	public static TimeFunctions instance()
	{
		return( _instance );
	}

	public String getTimeStr(int ms)
	{
		int hh = ms / 3600000;
		int mm = ( ms / 60000 ) % 60;
		int ss = ( ms / 1000 ) % 60;

		return( String.format( "%d:%02d:%02d", hh, mm, ss ) );
	}
}
