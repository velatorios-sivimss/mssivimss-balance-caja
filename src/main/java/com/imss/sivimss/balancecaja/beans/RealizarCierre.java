package com.imss.sivimss.balancecaja.beans;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.balancecaja.model.request.ActualizarMultiRequest;
import com.imss.sivimss.balancecaja.model.request.ReporteRequest;
import com.imss.sivimss.balancecaja.model.request.UsuarioDto;
import com.imss.sivimss.balancecaja.util.QueryHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealizarCierre {

	public static final String CURRENT_DATE = "CURRENT_DATE()";
	private static final String WHERE_SPB_CVE_FOLIO_IN = " WHERE spb.CVE_FOLIO IN(";
	private static final String JOIN_SVC_ORDEN_SERVICIO = "JOIN SVC_ORDEN_SERVICIO sos ON sos.CVE_FOLIO = spb.CVE_FOLIO ";
	private static final String JOIN_SVC_ESTATUS_ORDEN_SERVICIO = "JOIN SVC_ESTATUS_ORDEN_SERVICIO seos ON seos.ID_ESTATUS_ORDEN_SERVICIO = sos.ID_ESTATUS_ORDEN_SERVICIO ";
	private static final String JOIN_SVT_CONVENIO_PF= "JOIN SVT_CONVENIO_PF scp ON  scp.DES_FOLIO = spb.CVE_FOLIO ";
	private static final String JOIN_SVT_ESTATUS_CONVENIO_PF = "JOIN SVC_ESTATUS_CONVENIO_PF secp ON secp.ID_ESTATUS_CONVENIO_PF = scp.ID_ESTATUS_CONVENIO ";
	

	public ActualizarMultiRequest actualizaEstatusCierre(ReporteRequest reporteRequest , UsuarioDto usuarioDto, String formatoFecha) {
		log.info(" INICIO - actualizaEstatusCierre");
		ActualizarMultiRequest actualizarMultiRequest = new ActualizarMultiRequest();
		List<String> updates = new ArrayList<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_PAGO_DETALLE ");
		q.agregarParametroValues("IND_ESTATUS_CAJA", String.valueOf(0));
		q.agregarParametroValues("ID_USUARIO_MODIFICA", String.valueOf(usuarioDto.getIdUsuario()));
		q.agregarParametroValues("FEC_ACTUALIZACION", CURRENT_DATE);
		q.agregarParametroValues("FEC_CIERRE_CAJA", CURRENT_DATE);
		q.addWhere("ID_PAGO_DETALLE IN ( " + consultarGralFiltros(reporteRequest,formatoFecha) +")");
		final String query = q.obtenerQueryActualizar();
		log.info(" actualizarOrdenEntrada: " + query);
		updates.add(DatatypeConverter.printBase64Binary(q.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		log.info(" TERMINO - actualizaEstatusCierre");
		
		return actualizarMultiRequest;
	}
	
	public String consultarGralFiltros(ReporteRequest datos, String formatoFecha) {
		StringBuilder query  = new StringBuilder();
		if(datos.getIdTipoConvenio() == null || datos.getIdTipoConvenio() == 0) {
			query.append(queryODS(datos, formatoFecha))
			.append(" UNION ")
			.append(queryConvenios(datos, formatoFecha))
			.append(" UNION ")
			.append(queryRenovacionConvenios(datos, formatoFecha));
		}else if(datos.getIdTipoConvenio() == 1) {
			query.append(queryODS(datos, formatoFecha));
		}else if (datos.getIdTipoConvenio() == 2) {
			query.append(queryConvenios(datos, formatoFecha));
		}else if(datos.getIdTipoConvenio() == 3) {
			query.append(queryRenovacionConvenios(datos, formatoFecha));
		}
		  return query.toString();
	}
	
	private StringBuilder generaEcabezados(String formatoFecha, int tipoConvenio) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT spd.ID_PAGO_DETALLE ");
		return query;
	}

	private StringBuilder generaFromJoin() {
		StringBuilder query = new StringBuilder();	
		query.append(" FROM SVT_PAGO_BITACORA spb  ")
		.append(" JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = spb.ID_VELATORIO  ")
		.append(" JOIN SVC_DELEGACION sd ON sd.ID_DELEGACION = sv.ID_DELEGACION ")
		.append(" JOIN SVT_PAGO_DETALLE spd ON spd.ID_PAGO_BITACORA = spb.ID_PAGO_BITACORA  ")
		.append(" JOIN SVC_METODO_PAGO smp ON smp.ID_METODO_PAGO = spd.ID_METODO_PAGO  ")
		.append(" JOIN SVC_ESTATUS_PAGO sep ON sep.ID_ESTATUS_PAGO = spb.CVE_ESTATUS_PAGO ");	
		return query;
	}
	private String queryODS (ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(formatoFecha, 1)
				.append(generaFromJoin())
				.append(JOIN_SVC_ORDEN_SERVICIO)
				.append(JOIN_SVC_ESTATUS_ORDEN_SERVICIO)
		.append(generaWhere(datos)).toString();
	}
	
	private String queryConvenios(ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(formatoFecha, 2)
				.append(generaFromJoin())
				.append(JOIN_SVT_CONVENIO_PF)
				.append(JOIN_SVT_ESTATUS_CONVENIO_PF)
				.append(generaWhere(datos))
				.append("AND spb.ID_FLUJO_PAGOS = 2 ").toString();
	}
	
	private String queryRenovacionConvenios(ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(formatoFecha, 3)
				.append(generaFromJoin())
				.append(JOIN_SVT_CONVENIO_PF)
				.append(JOIN_SVT_ESTATUS_CONVENIO_PF)
				.append(generaWhere(datos))
				.append("AND spb.ID_FLUJO_PAGOS =3 ").toString();
	}
	
	private String generaWhere(ReporteRequest datos) {
		StringBuilder where = new StringBuilder();
		if(datos.getFolioODS()!=null) {
			where.append(WHERE_SPB_CVE_FOLIO_IN);
			where.append("'" + datos.getFolioODS() + "'"); 
			if(datos.getFolioNuevoConvenio()!=null)
				where.append(",'" + datos.getFolioNuevoConvenio() + "'"); 
			if(datos.getFolioRenovacionConvenio()!=null)
				where.append(",'" + datos.getFolioRenovacionConvenio() + "'"); 
			where.append(")");
		}else if(datos.getFolioNuevoConvenio()!=null) {
			where.append(WHERE_SPB_CVE_FOLIO_IN);
			where.append("'" + datos.getFolioNuevoConvenio() + "'"); 
			if(datos.getFolioRenovacionConvenio()!=null)
				where.append(",'" + datos.getFolioRenovacionConvenio() + "'"); 
			where.append(")");
		}else if(datos.getFolioRenovacionConvenio()!=null) {
			where.append(WHERE_SPB_CVE_FOLIO_IN);
			where.append("'" + datos.getFolioRenovacionConvenio() + "'"); 
			where.append(")");
		}

		if(datos.getIdDelegacion()!= null)
			where.append(" AND sd.ID_DELEGACION = " + datos.getIdDelegacion());
		if(datos.getIdVelatorio()!= null)
			where.append(" AND sv.ID_VELATORIO = " + datos.getIdVelatorio());
		if(datos.getIdMetodoPago()!= null)
			where.append(" AND smp.ID_METODO_PAGO = " + datos.getIdMetodoPago());
		if(datos.getFecha()!= null)
			where.append(" AND DATE_FORMAT(spb.FEC_ODS,'YY-MM-DD') = DATE_FORMAT('" + datos.getFecha() +"','YY-MM-DD')");
		
		return where.toString();
	}

}