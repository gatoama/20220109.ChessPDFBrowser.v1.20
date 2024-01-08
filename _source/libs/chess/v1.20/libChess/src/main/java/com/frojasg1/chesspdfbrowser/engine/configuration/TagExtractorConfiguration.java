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
package com.frojasg1.chesspdfbrowser.engine.configuration;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface TagExtractorConfiguration
{
	public void setDefaultRegexForTagExtraction();
	public void setWhitePlayerExtractionRegex( String regex );
	public void setBlackPlayerExtractionRegex( String regex );
	public void setWhiteEloExtractionRegex( String regex );
	public void setBlackEloExtractionRegex( String regex );
	public void setVariantExtractionRegex( String regex );
	public void setEventExtractionRegex( String regex );
	public void setSiteExtractionRegex( String regex );
	public void setRoundExtractionRegex( String regex );
	public void setDateExtractionRegex( String regex );
	
	public String getWhitePlayerExtractionRegex();
	public String getBlackPlayerExtractionRegex();
	public String getWhiteEloExtractionRegex();
	public String getBlackEloExtractionRegex();
	public String getVariantExtractionRegex();
	public String getEventExtractionRegex();
	public String getSiteExtractionRegex();
	public String getRoundExtractionRegex();
	public String getDateExtractionRegex();
}
