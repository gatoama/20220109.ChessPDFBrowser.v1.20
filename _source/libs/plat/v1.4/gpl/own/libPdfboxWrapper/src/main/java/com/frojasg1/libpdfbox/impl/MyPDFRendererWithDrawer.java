/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.libpdfbox.impl;

import com.frojasg1.libpdf.api.GlyphWrapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MyPDFRendererWithDrawer extends MyPDFRenderer
{
	protected float _pageFactor;
	protected Map<MatrixWrapper, GlyphWrapper> _glyphMap;

	protected AtomicInteger _count = new AtomicInteger(0);

	public MyPDFRendererWithDrawer(PDDocument document,Map<MatrixWrapper, GlyphWrapper> glyphMap,
									float pageFactor)
	{
		super( document );
		_pageFactor = pageFactor;
		_glyphMap = glyphMap;
		setGetGlyphsAndImages(false);
		setShowText(true);
	}

	@Override
	protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
		MyPageDrawer result = new MyPageDrawer( parameters, _glyphMap, _pageFactor, _count );
		
		return( result );
	}
}
