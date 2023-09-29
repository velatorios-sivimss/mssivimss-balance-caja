package com.imss.sivimss.balancecaja.beans;

import com.imss.sivimss.balancecaja.model.request.ReporteRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ConsultaGeneral {
	private String folioConvenio;
	private String folioODS;
	private String folioRenovacionConvenio;
	private String fecha;
	private Integer idNivel;
	private Integer idDelegacion;
	private Integer idVelatorio;
	
	private static final String AS_FECHA = "') AS fecha";
	private static final String WHERE_SPB_CVE_FOLIO_IN = " WHERE spb.CVE_FOLIO IN(";
	private static final String JOIN_SVC_ORDEN_SERVICIO = "JOIN SVC_ORDEN_SERVICIO sos ON sos.CVE_FOLIO = spb.CVE_FOLIO ";
	private static final String JOIN_SVC_ESTATUS_ORDEN_SERVICIO = "JOIN SVC_ESTATUS_ORDEN_SERVICIO seos ON seos.ID_ESTATUS_ORDEN_SERVICIO = sos.ID_ESTATUS_ORDEN_SERVICIO ";
	private static final String JOIN_SVT_CONVENIO_PF= "JOIN SVT_CONVENIO_PF scp ON  scp.DES_FOLIO = spb.CVE_FOLIO ";
	private static final String JOIN_SVT_ESTATUS_CONVENIO_PF = "JOIN SVC_ESTATUS_CONVENIO_PF secp ON secp.ID_ESTATUS_CONVENIO_PF = scp.ID_ESTATUS_CONVENIO ";
	
	public ConsultaGeneral(Object obj) {}
	
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
	//
	public String consultarTotalesGralFiltros(ReporteRequest datos) {
		return "SELECT SUM(spb.DESC_VALOR) AS totalImporte "
	  		+ ", SUM (spd.IMP_PAGO) AS totalIngreso "
	  		+ ", COUNT(spb.ID_PAGO_BITACORA) AS totalRegistros "
	  		+ generaFromJoin()
	  		+ generaWhere(datos);
	}
	
	private StringBuilder generaEcabezados(ReporteRequest datos, String formatoFecha, int tipoConvenio) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT spd.ID_PAGO_DETALLE AS idPagoDetalle ")
				.append(", IFNULL(sd.DES_DELEGACION,'') AS delegacion")
				.append(", IFNULL(sv.DES_VELATORIO,'') AS velatorio")
				.append(", IFNULL(spb.CVE_FOLIO,'') AS folio ")
				.append(", IFNULL(sep.DES_ESTATUS,'') AS estatusPago")
				.append(", IFNULL(smp.DESC_METODO_PAGO,'') AS metodoPago ")
				.append(", IFNULL(spb.DESC_VALOR,'')  AS importe ")
				.append(", IFNULL(spd.IMP_PAGO,'')  AS ingresoNeto ")
				.append(", IFNULL(spd.DES_MOTIVO_MODIFICA,'')  AS modifPago ")
				.append(", IFNULL(DATE_FORMAT(spd.FEC_PAGO,'" + formatoFecha + " %H:%i'),'')  AS fecHoraCierre ")
				.append(", CASE WHEN spd.CVE_ESTATUS = 0 THEN 'Cerrado' ELSE 'Abierto' END  AS estatusCaja ")
				.append(importeTotal(datos, tipoConvenio))
				.append(totalIngreso(datos, tipoConvenio))
				.append(totalRegistros(datos, tipoConvenio));
		return query;
	}


	private StringBuilder importeTotal(ReporteRequest datos, int tipoConvenio) {
		StringBuilder query = new StringBuilder();
				query.append(", (SELECT SUM(spb.DESC_VALOR) ")
			.append( generaFromJoin());
		if(tipoConvenio == 1) {
			query.append(JOIN_SVC_ORDEN_SERVICIO)
			.append(JOIN_SVC_ESTATUS_ORDEN_SERVICIO);
		}
		if(tipoConvenio == 2) {
			query.append(JOIN_SVT_CONVENIO_PF)
			.append(JOIN_SVT_ESTATUS_CONVENIO_PF);
		}
		if (tipoConvenio == 3) {
			query.append(JOIN_SVT_ESTATUS_CONVENIO_PF);
		}
			query.append(generaWhere(datos) + ") AS totalImporte ");
				return query;
	}
	private StringBuilder totalIngreso (ReporteRequest datos, int tipoConvenio) {
		StringBuilder query = new StringBuilder();
		query.append(", (SELECT SUM (spd.IMP_PAGO)")
		.append( generaFromJoin());
		if(tipoConvenio == 1) {
			query.append(JOIN_SVC_ORDEN_SERVICIO)
			.append(JOIN_SVC_ESTATUS_ORDEN_SERVICIO);
		}
		if(tipoConvenio == 2) {
			query.append(JOIN_SVT_CONVENIO_PF)
			.append(JOIN_SVT_ESTATUS_CONVENIO_PF);
		}
		if (tipoConvenio == 3) {
			query.append(JOIN_SVT_ESTATUS_CONVENIO_PF);
		}
		query.append(generaWhere(datos) +  ") AS totalIngreso ");
		return query;
	}
	private StringBuilder totalRegistros (ReporteRequest datos, int tipoConvenio) {
		StringBuilder query = new StringBuilder();
		query.append(", (SELECT COUNT(spb.ID_PAGO_BITACORA) ")
		.append( generaFromJoin());
		if(tipoConvenio == 1) {
			query.append(JOIN_SVC_ORDEN_SERVICIO)
			.append(JOIN_SVC_ESTATUS_ORDEN_SERVICIO);
		}
		if(tipoConvenio == 2) {
			query.append(JOIN_SVT_CONVENIO_PF)
			.append(JOIN_SVT_ESTATUS_CONVENIO_PF);
		}
		if (tipoConvenio == 3) {
			query.append(JOIN_SVT_ESTATUS_CONVENIO_PF);
		}
		query.append(generaWhere(datos) + ") AS totalRegistros");
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
		return generaEcabezados(datos, formatoFecha, 1).append(", IFNULL(seos.DES_ESTATUS,'')  AS estatus ")
				.append(",'Pago de Orden de servicio' AS tipoIngreso ")
				.append(", DATE_FORMAT(sos.FEC_ALTA,'" + formatoFecha + AS_FECHA)
				.append(generaFromJoin())
				.append(JOIN_SVC_ORDEN_SERVICIO)
				.append(JOIN_SVC_ESTATUS_ORDEN_SERVICIO)
		.append(generaWhere(datos)).toString();
	}
	
	private String queryConvenios(ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(datos, formatoFecha, 2).append(", IFNULL(secp.DES_ESTATUS,'') AS estatus ")
				.append(", 'Pago de Nuevos convenios de previsión funeraria' AS tipoIngreso ")
				.append(", DATE_FORMAT(scp.FEC_ALTA,'" + formatoFecha + AS_FECHA)
				.append(generaFromJoin())
				.append(JOIN_SVT_CONVENIO_PF)
				.append(JOIN_SVT_ESTATUS_CONVENIO_PF)
				.append(generaWhere(datos))
				.append(" AND spb.ID_FLUJO_PAGOS = 2 ").toString();
	}
	
	private String queryRenovacionConvenios(ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(datos, formatoFecha, 3).append(", IFNULL(secp.DES_ESTATUS,'') AS estatus ")
				.append(",'Pago de Renovación de convenios de previsión funeraria' AS tipoIngreso ")
				.append(", DATE_FORMAT(scp.FEC_ALTA,'" + formatoFecha + AS_FECHA)
				.append(generaFromJoin())
				.append(JOIN_SVT_CONVENIO_PF)
				.append(JOIN_SVT_ESTATUS_CONVENIO_PF)
				.append(generaWhere(datos))
				.append(" AND spb.ID_FLUJO_PAGOS =3 ").toString();
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
		if(datos.getFecha()!= null) {
			where.append(" AND DATE_FORMAT(spb.FEC_ODS, '%Y-%m-%d') = DATE_FORMAT('" + datos.getFecha() +"', '%Y-%m-%d')");
		}else {
			where.append(" AND DATE_FORMAT(spb.FEC_ODS, '%Y-%m-%d') = DATE_FORMAT(CURDATE(), '%Y-%m-%d')");
		}
			
		
		return where.toString();
	}
}
