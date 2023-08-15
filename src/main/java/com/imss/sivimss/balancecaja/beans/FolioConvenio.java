package com.imss.sivimss.balancecaja.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.balancecaja.util.AppConstantes;
import com.imss.sivimss.balancecaja.util.DatosRequest;
import com.imss.sivimss.balancecaja.util.SelectQueryUtil;

public class FolioConvenio {

	private static FolioConvenio folioConvenio;
	
	private String query;
	
	private static final Logger log = LoggerFactory.getLogger(FolioConvenio.class);

	private FolioConvenio() {}
	
	public static FolioConvenio  obtenerFolioConvenioInstance() {
		if (folioConvenio==null) {
			folioConvenio= new FolioConvenio();
		}
		
		return folioConvenio;
	}
	
	public DatosRequest obtenerFoliosConvenio(String folio) {
		DatosRequest datosRequest= new DatosRequest();
		Map<String, Object>parametros= new HashMap<>();
		SelectQueryUtil selectQueryUtil= new SelectQueryUtil();
		selectQueryUtil.select("DISTINCT STO.DES_FOLIO  AS folio")
		.from("SVT_CONVENIO_PF STO")
		.innerJoin("SVT_PAGO_BITACORA SPB", "SPB.ID_REGISTRO = STO.ID_CONVENIO_PF ")
		.innerJoin("SVC_FLUJO_PAGOS SFP", "SFP.ID_FLUJO_PAGOS = SPB.ID_FLUJO_PAGOS")
		.innerJoin("SVT_PAGO_DETALLE SPD", "SPD.ID_PAGO_BITACORA = SPB.ID_PAGO_BITACORA")
		.innerJoin("SVC_METODO_PAGO SPA", "SPA.ID_METODO_PAGO = SPD.ID_METODO_PAGO ")
		.where("SPB.ID_FLUJO_PAGOS = 2 ")
		.and("STO.ID_ESTATUS_CONVENIO IN (2)")
		.and("SPD.CVE_ESTATUS IN (4,5)")
		.and("STO.DES_FOLIO LIKE '%"+folio+"%'")
		.orderBy("STO.DES_FOLIO ASC");
		query=selectQueryUtil.build();
		log.info(query);
		String encoded=DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametros.put(AppConstantes.QUERY, encoded);
		datosRequest.setDatos(parametros);
		return datosRequest;
	}
	
	public DatosRequest obtenerFoliosRenovacionConvenio(String folio) {
		DatosRequest datosRequest= new DatosRequest();
		Map<String, Object>parametros= new HashMap<>();
		SelectQueryUtil selectQueryUtil= new SelectQueryUtil();
		selectQueryUtil.select("DISTINCT STO.DES_FOLIO  AS folio")
		.from("SVT_CONVENIO_PF STO")
		.innerJoin("SVT_PAGO_BITACORA SPB", "SPB.ID_REGISTRO = STO.ID_CONVENIO_PF ")
		.innerJoin("SVC_FLUJO_PAGOS SFP", "SFP.ID_FLUJO_PAGOS = SPB.ID_FLUJO_PAGOS")
		.innerJoin("SVT_PAGO_DETALLE SPD", "SPD.ID_PAGO_BITACORA = SPB.ID_PAGO_BITACORA")
		.innerJoin("SVC_METODO_PAGO SPA", "SPA.ID_METODO_PAGO = SPD.ID_METODO_PAGO ")
		.where("SPB.ID_FLUJO_PAGOS = 3 ")
		.and("STO.ID_ESTATUS_CONVENIO IN (2)")
		.and("SPD.CVE_ESTATUS IN (4,5)")
		.and("STO.DES_FOLIO LIKE '%"+folio+"%'")
		.orderBy("STO.DES_FOLIO ASC");
		query=selectQueryUtil.build();
		log.info(query);
		String encoded=DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametros.put(AppConstantes.QUERY, encoded);
		datosRequest.setDatos(parametros);
		return datosRequest;
	}
}
