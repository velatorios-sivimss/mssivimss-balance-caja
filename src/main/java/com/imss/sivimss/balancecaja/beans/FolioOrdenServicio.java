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

public class FolioOrdenServicio {

	private static FolioOrdenServicio folioOrdenServicio;

	
	private static final Logger log = LoggerFactory.getLogger(FolioOrdenServicio.class);

	private String query;
	
	private FolioOrdenServicio() {
		
	}
	
	public static FolioOrdenServicio obtenerFolioOrdenInstance() {
		if (folioOrdenServicio==null) {
			folioOrdenServicio= new FolioOrdenServicio();
		}
		return folioOrdenServicio;
	}
	
	public DatosRequest obtenerFolios(String folio) {
		DatosRequest datosRequest= new DatosRequest();
		Map<String, Object>parametros= new HashMap<>();
		SelectQueryUtil selectQueryUtil= new SelectQueryUtil();
		selectQueryUtil.select("DISTINCT STO.CVE_FOLIO AS folio")
		.from("SVC_ORDEN_SERVICIO STO")
		.innerJoin("SVT_PAGO_BITACORA SPB", "SPB.ID_REGISTRO = STO.ID_ORDEN_SERVICIO ")
		.innerJoin("SVC_FLUJO_PAGOS SFP", "SFP.ID_FLUJO_PAGOS = SPB.ID_FLUJO_PAGOS")
		.innerJoin("SVT_PAGO_DETALLE SPD", "SPD.ID_PAGO_BITACORA = SPB.ID_PAGO_BITACORA")
		.innerJoin("SVC_METODO_PAGO SPA", "SPA.ID_METODO_PAGO = SPD.ID_METODO_PAGO ")
		.where("SPB.ID_FLUJO_PAGOS =1 ")
		.and("STO.ID_ESTATUS_ORDEN_SERVICIO IN (1,4)")
		.and("SPD.CVE_ESTATUS IN (4,5,8)")
		.and("STO.CVE_FOLIO LIKE '%"+folio+"%'")
		.orderBy("STO.CVE_FOLIO ASC");
		query=selectQueryUtil.build();
		log.info(query);
		String encoded=DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametros.put(AppConstantes.QUERY, encoded);
		datosRequest.setDatos(parametros);
		return datosRequest;
	}
}
