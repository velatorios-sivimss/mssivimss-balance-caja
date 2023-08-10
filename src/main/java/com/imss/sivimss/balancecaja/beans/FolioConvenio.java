package com.imss.sivimss.balancecaja.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolioConvenio {

	private static FolioConvenio folioConvenio;
	
	
	private static final Logger log = LoggerFactory.getLogger(FolioConvenio.class);

	private FolioConvenio() {}
	
	public static FolioConvenio  obtenerFolioConvenioInstance() {
		if (folioConvenio==null) {
			folioConvenio= new FolioConvenio();
		}
		
		return folioConvenio;
	}
}
