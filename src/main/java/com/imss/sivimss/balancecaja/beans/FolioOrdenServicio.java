package com.imss.sivimss.balancecaja.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolioOrdenServicio {

	private static FolioOrdenServicio folioOrdenServicio;

	
	private static final Logger log = LoggerFactory.getLogger(FolioOrdenServicio.class);

	
	private FolioOrdenServicio() {
		
	}
	
	public static FolioOrdenServicio obtenerFolioOrdenInstance() {
		if (folioOrdenServicio==null) {
			folioOrdenServicio= new FolioOrdenServicio();
		}
		return folioOrdenServicio;
	}
}
