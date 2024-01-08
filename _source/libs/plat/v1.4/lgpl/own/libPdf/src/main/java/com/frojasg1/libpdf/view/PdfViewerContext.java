/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.libpdf.view;

import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.ImageWrapper;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PdfViewerContext {
	protected List<GlyphWrapper> _glyphsOfPage = null;
	protected List<ImageWrapper> _imagesOfPage = null;

	protected GlyphWrapper _selectedGlyph = null;
	protected ImageWrapper _selectedImage = null;

	protected ImageWrapper _hoverImage = null;
	protected GlyphWrapper _hoverGlyph = null;

	public List<GlyphWrapper> getGlyphsOfPage() {
		return _glyphsOfPage;
	}

	public void setGlyphsOfPage(List<GlyphWrapper> _glyphsOfPage) {
		this._glyphsOfPage = _glyphsOfPage;
	}

	public List<ImageWrapper> getImagesOfPage() {
		return _imagesOfPage;
	}

	public void setImagesOfPage(List<ImageWrapper> _imagesOfPage) {
		this._imagesOfPage = _imagesOfPage;
	}

	public GlyphWrapper getSelectedGlyph() {
		return _selectedGlyph;
	}

	public void setSelectedGlyph(GlyphWrapper _selectedGlyph) {
		this._selectedGlyph = _selectedGlyph;
	}

	public ImageWrapper getSelectedImage() {
		return _selectedImage;
	}

	public void setSelectedImage(ImageWrapper _selectedImage) {
		this._selectedImage = _selectedImage;
	}

	public ImageWrapper getHoverImage() {
		return _hoverImage;
	}

	public void setHoverImage(ImageWrapper _hoverImage) {
		this._hoverImage = _hoverImage;
	}

	public GlyphWrapper getHoverGlyph() {
		return _hoverGlyph;
	}

	public void setHoverGlyph(GlyphWrapper _hoverGlyph) {
		this._hoverGlyph = _hoverGlyph;
	}
}
