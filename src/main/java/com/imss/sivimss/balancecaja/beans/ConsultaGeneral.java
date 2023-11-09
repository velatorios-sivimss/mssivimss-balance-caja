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
	
	private static final String AS_FECHA = "') AS fecha \r\n";
	private static final String AND_SPB_CVE_FOLIO_IN = " AND spb.CVE_FOLIO IN(";
	private static final String JOIN_SVC_ORDEN_SERVICIO = "JOIN SVC_ORDEN_SERVICIO sos ON sos.CVE_FOLIO = spb.CVE_FOLIO \r\n";
	private static final String JOIN_SVC_ESTATUS_ORDEN_SERVICIO = "JOIN SVC_ESTATUS_ORDEN_SERVICIO seos ON seos.ID_ESTATUS_ORDEN_SERVICIO = sos.ID_ESTATUS_ORDEN_SERVICIO \r\n";
	private static final String JOIN_SVT_CONVENIO_PF= "JOIN SVT_CONVENIO_PF scp ON  scp.DES_FOLIO = spb.CVE_FOLIO \r\n";
	private static final String JOIN_SVT_ESTATUS_CONVENIO_PF = "JOIN SVC_ESTATUS_CONVENIO_PF secp ON secp.ID_ESTATUS_CONVENIO_PF = scp.ID_ESTATUS_CONVENIO \r\n";
	
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
	public String consultarTotalesGralFiltros(ReporteRequest datos, String formatoFecha) {
	return "SELECT SUM(spb.IMP_VALOR) AS totalImporte "
	  		+ ", SUM(spd.IMP_PAGO) AS totalIngreso "
	  		+ ", COUNT(spb.ID_PAGO_BITACORA) AS totalRegistros "
	  		+ generaFromJoin()
	  		+ generaWhere(datos, datos.getIdTipoConvenio(), formatoFecha);
	}
	
	private StringBuilder generaEcabezados(ReporteRequest datos, String formatoFecha, int tipoConvenio) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT spd.ID_PAGO_DETALLE AS idPagoDetalle \r\n")
				.append(", IFNULL(sd.DES_DELEGACION,'') AS delegacion \r\n")
				.append(", IFNULL(sv.DES_VELATORIO,'') AS velatorio \r\n")
				.append(", IFNULL(spb.CVE_FOLIO,'') AS folio \r\n")
				.append(", IFNULL(sep.DES_ESTATUS,'') AS estatusPago \r\n")
				.append(", IFNULL(smp.DES_METODO_PAGO,'') AS metodoPago \r\n")
				.append(", IFNULL(spb.IMP_VALOR,'')  AS importe \r\n")
				.append(", IFNULL(spd.IMP_PAGO,'')  AS ingresoNeto \r\n")
				.append(", IFNULL(spd.REF_MOTIVO_MODIFICA,'')  AS modifPago \r\n")
				.append(", IFNULL(DATE_FORMAT(spd.FEC_PAGO,'" + formatoFecha + " %H:%i'),'')  AS fecHoraCierre \r\n")
				.append(", CASE WHEN spd.IND_ESTATUS_CAJA = 0 THEN 'Cerrado' ELSE 'Abierto' END  AS estatusCaja \r\n")
				.append(importeTotal(datos, tipoConvenio, formatoFecha))
				.append(totalIngreso(datos, tipoConvenio, formatoFecha))
				.append(totalRegistros(datos, tipoConvenio, formatoFecha));
		return query;
	}


	private StringBuilder importeTotal(ReporteRequest datos, int tipoConvenio, String formatoFecha) {
		StringBuilder query = new StringBuilder();
				query.append(", (SELECT SUM(spb.IMP_VALOR) ")
			.append( generaFromJoin());
		
		if(datos.getIdTipoConvenio() != null && datos.getIdTipoConvenio() != 0) {
			
			query.append("AND spb.ID_FLUJO_PAGOS = " + datos.getIdTipoConvenio());
			
		}
			query.append(generaWhere(datos, tipoConvenio, formatoFecha) + ") AS totalImporte ");
				return query;
	}
	private StringBuilder totalIngreso (ReporteRequest datos, int tipoConvenio, String formatoFecha) {
		StringBuilder query = new StringBuilder();
		query.append(", (SELECT SUM(spd.IMP_PAGO)")
		.append( generaFromJoin());
		
		if(datos.getIdTipoConvenio() != null && datos.getIdTipoConvenio() != 0) {
			
			query.append("AND spb.ID_FLUJO_PAGOS = " + datos.getIdTipoConvenio());
			
		}

		query.append(generaWhere(datos,tipoConvenio,formatoFecha) +  ") AS totalIngreso ");
		return query;
	}
	private StringBuilder totalRegistros (ReporteRequest datos, int tipoConvenio, String formatoFecha) {
		StringBuilder query = new StringBuilder();
		query.append(", (SELECT COUNT(spb.ID_PAGO_BITACORA) ")
		.append( generaFromJoin());
		
		if(datos.getIdTipoConvenio() != null && datos.getIdTipoConvenio() != 0) {
			
			query.append("AND spb.ID_FLUJO_PAGOS = " + datos.getIdTipoConvenio());
			
		}
		
		query.append(generaWhere(datos, tipoConvenio,formatoFecha) + ") AS totalRegistros");
		return query;
	}

	private StringBuilder generaFromJoin() {
		StringBuilder query = new StringBuilder();	
		query.append(" FROM SVT_PAGO_BITACORA spb  ")
		.append(" JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = spb.ID_VELATORIO \r\n")
		.append(" JOIN SVC_DELEGACION sd ON sd.ID_DELEGACION = sv.ID_DELEGACION \r\n")
		.append(" JOIN SVT_PAGO_DETALLE spd ON spd.ID_PAGO_BITACORA = spb.ID_PAGO_BITACORA \r\n")
		.append(" JOIN SVC_METODO_PAGO smp ON smp.ID_METODO_PAGO = spd.ID_METODO_PAGO \r\n")
		.append(" JOIN SVC_ESTATUS_PAGO sep ON sep.ID_ESTATUS_PAGO = spb.CVE_ESTATUS_PAGO \r\n");	
		return query;
	}
	private String queryODS (ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(datos, formatoFecha, 1).append(", IFNULL(seos.DES_ESTATUS,'')  AS estatus \r\n")
				.append(",'Pago de Orden de servicio' AS tipoIngreso \r\n")
				.append(", DATE_FORMAT(sos.FEC_ALTA,'" + formatoFecha + AS_FECHA)
				.append(generaFromJoin())
				.append(JOIN_SVC_ORDEN_SERVICIO)
				.append(JOIN_SVC_ESTATUS_ORDEN_SERVICIO)
		.append(generaWhere(datos, 1,formatoFecha)).toString();
	}
	
	private String queryConvenios(ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(datos, formatoFecha, 2).append(", IFNULL(secp.DES_ESTATUS,'') AS estatus ")
				.append(", 'Pago de Nuevos convenios de previsión funeraria' AS tipoIngreso ")
				.append(", DATE_FORMAT(scp.FEC_ALTA,'" + formatoFecha + AS_FECHA)
				.append(generaFromJoin())
				.append(JOIN_SVT_CONVENIO_PF)
				.append(JOIN_SVT_ESTATUS_CONVENIO_PF)
				.append(generaWhere(datos, 2, formatoFecha)).toString();
	}
	
	private String queryRenovacionConvenios(ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(datos, formatoFecha, 3).append(", IFNULL(secp.DES_ESTATUS,'') AS estatus ")
				.append(",'Pago de Renovación de convenios de previsión funeraria' AS tipoIngreso ")
				.append(", DATE_FORMAT(scp.FEC_ALTA,'" + formatoFecha + AS_FECHA)
				.append(generaFromJoin())
				.append(JOIN_SVT_CONVENIO_PF)
				.append(JOIN_SVT_ESTATUS_CONVENIO_PF)
				.append(generaWhere(datos, 3, formatoFecha)).toString();
	}
	
	private String generaWhere(ReporteRequest datos, int tipoConvenio, String formatoFecha) {
		StringBuilder where = new StringBuilder();
		
		where.append(" WHERE spd.CVE_ESTATUS = 4 \r\n");
		
		if(datos.getFolioODS()!=null) {
			where.append(AND_SPB_CVE_FOLIO_IN);
			where.append("'" + datos.getFolioODS() + "'\r\n"); 
			if(datos.getFolioNuevoConvenio()!=null)
				where.append(",'" + datos.getFolioNuevoConvenio() + "'\r\n"); 
			if(datos.getFolioRenovacionConvenio()!=null)
				where.append(",'" + datos.getFolioRenovacionConvenio() + "'\r\n"); 
			where.append(")");
		}else if(datos.getFolioNuevoConvenio()!=null) {
			where.append(AND_SPB_CVE_FOLIO_IN);
			where.append("'" + datos.getFolioNuevoConvenio() + "'\r\n"); 
			if(datos.getFolioRenovacionConvenio()!=null)
				where.append(",'" + datos.getFolioRenovacionConvenio() + "'\r\n"); 
			where.append(")");
		}else if(datos.getFolioRenovacionConvenio()!=null) {
			where.append(AND_SPB_CVE_FOLIO_IN);
			where.append("'" + datos.getFolioRenovacionConvenio() + "'\r\n"); 
			where.append(")");
		}

		if(datos.getIdDelegacion()!= null)
			where.append(" AND sd.ID_DELEGACION = " + datos.getIdDelegacion() + "\r\n" );
		if(datos.getIdVelatorio()!= null)
			where.append(" AND sv.ID_VELATORIO = " + datos.getIdVelatorio() + "\r\n" );
		if(datos.getIdMetodoPago()!= null)
			where.append(" AND smp.ID_METODO_PAGO = " + datos.getIdMetodoPago() + "\r\n");
		if(datos.getFecha()!= null)
			where.append(" AND DATE_FORMAT(spb.FEC_ODS,'" + formatoFecha +"') = "
					+ "DATE_FORMAT('" + datos.getFecha() +"','" + formatoFecha +"')\r\n");
		
		if(datos.getIdTipoConvenio() != null && datos.getIdTipoConvenio() != 0) {
			where.append(" AND spb.ID_FLUJO_PAGOS = " + tipoConvenio + " \r\n");
		}
		
		return where.toString();
	}
}
