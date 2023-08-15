package com.imss.sivimss.balancecaja.beans;

import com.imss.sivimss.balancecaja.model.request.ReporteRequest;
import com.imss.sivimss.balancecaja.util.DatosRequest;

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
	
	public ConsultaGeneral(Object obj) {}
	
	public String consultarGralFiltros(DatosRequest request, ReporteRequest datos, String formatoFecha) {
		  return queryODS(datos, formatoFecha) + " UNION " + queryConvenios(datos, formatoFecha) + " UNION " + queryRenovacionConvenios(datos, formatoFecha);
	}
	
	public String consultarTotalesGralFiltros(DatosRequest request, ReporteRequest datos) {
	return "SELECT SUM(spb.DESC_VALOR) AS totalImporte "
	  		+ ", SUM (spd.IMP_IMPORTE) AS totalIngreso "
	  		+ ", COUNT(spb.ID_PAGO_BITACORA) AS totalRegistros "
	  		+ generaFromJoin()
	  		+ generaWhere(datos);
	}
	
	private StringBuilder generaEcabezados(ReporteRequest datos, String formatoFecha) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT spd.ID_PAGO_DETALLE AS idPagoDetalle ")
				.append(", IFNULL(sd.DES_DELEGACION,'') AS delegacion")
				.append(", IFNULL(sv.DES_VELATORIO,'') AS velatorio")
				.append(", IFNULL(spb.CVE_FOLIO,'') AS folio ")
				.append(", IFNULL(sep.DES_ESTATUS,'') AS estatusPago")
				.append(", IFNULL(smp.DESC_METODO_PAGO,'') AS metodoPago ")
				.append(", IFNULL(spb.DESC_VALOR,'')  AS importe ")
				.append(", IFNULL(spd.IMP_IMPORTE,'')  AS ingresoNeto ")
				.append(", IFNULL(spd.IND_ESTATUS_CAJA,'')  AS modifPago ")
				.append(", IFNULL(DATE_FORMAT(spd.FEC_CIERRE_CAJA,'" + formatoFecha + " %H:%i'),'')  AS fecHoraCierre ")
				.append(", CASE WHEN spd.IND_ESTATUS_CAJA = 0 THEN 'Cerrado' ELSE 'Abierto' END  AS estatusCaja ")
				.append(", (SELECT SUM(spb.DESC_VALOR) FROM SVT_PAGO_BITACORA spb "
						+ "JOIN SVC_VELATORIO sv ON	sv.ID_VELATORIO = spb.ID_VELATORIO "
						+ "JOIN SVC_DELEGACION sd ON sd.ID_DELEGACION = sv.ID_DELEGACION "
						+ "JOIN SVT_PAGO_DETALLE spd ON spd.ID_PAGO_BITACORA = spb.ID_PAGO_BITACORA "
						+ "JOIN SVC_METODO_PAGO smp ON smp.ID_METODO_PAGO = spd.ID_METODO_PAGO "
						+ "JOIN SVC_ESTATUS_PAGO sep ON sep.ID_ESTATUS_PAGO = spb.CVE_ESTATUS_PAGO "
						+ generaWhere(datos) + ") AS totalImporte "
						+ "	,(SELECT SUM (spd.IMP_IMPORTE) FROM SVT_PAGO_BITACORA spb "
						+ "JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = spb.ID_VELATORIO "
						+ "JOIN SVC_DELEGACION sd ON sd.ID_DELEGACION = sv.ID_DELEGACION "
						+ "JOIN SVT_PAGO_DETALLE spd ON spd.ID_PAGO_BITACORA = spb.ID_PAGO_BITACORA "
						+ "JOIN SVC_METODO_PAGO smp ON smp.ID_METODO_PAGO = spd.ID_METODO_PAGO "
						+ "JOIN SVC_ESTATUS_PAGO sep ON sep.ID_ESTATUS_PAGO = spb.CVE_ESTATUS_PAGO "
						+ generaWhere(datos) +  ") AS totalIngreso "
						+ "	,(SELECT COUNT(spb.ID_PAGO_BITACORA) FROM SVT_PAGO_BITACORA spb "
						+ "JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = spb.ID_VELATORIO "
						+ "JOIN SVC_DELEGACION sd ON sd.ID_DELEGACION = sv.ID_DELEGACION "
						+ "JOIN SVT_PAGO_DETALLE spd ON spd.ID_PAGO_BITACORA = spb.ID_PAGO_BITACORA "
						+ "JOIN SVC_METODO_PAGO smp ON smp.ID_METODO_PAGO = spd.ID_METODO_PAGO "
						+ "JOIN SVC_ESTATUS_PAGO sep ON sep.ID_ESTATUS_PAGO = spb.CVE_ESTATUS_PAGO "
						+ generaWhere(datos) + ") AS totalRegistros");
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
		return generaEcabezados(datos, formatoFecha).append(", IFNULL(seos.DES_ESTATUS,'')  AS estatus ")
				.append(",'Pago de Orden de servicio' AS tipoIngreso ")
				.append(", DATE_FORMAT(sos.FEC_ALTA,'" + formatoFecha + "') AS fecha")
				.append(generaFromJoin())
				.append("JOIN SVC_ORDEN_SERVICIO sos ON sos.CVE_FOLIO = spb.CVE_FOLIO ")
				.append("JOIN SVC_ESTATUS_ORDEN_SERVICIO seos ON seos.ID_ESTATUS_ORDEN_SERVICIO = sos.ID_ESTATUS_ORDEN_SERVICIO ")
		.append(generaWhere(datos)).toString();
	}
	
	private String queryConvenios(ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(datos, formatoFecha).append(", IFNULL(secp.DES_ESTATUS,'') AS estatus ")
				.append(", 'Pago de Nuevos convenios de previsión funeraria' AS tipoIngreso ")
				.append(", DATE_FORMAT(scp.FEC_ALTA,'" + formatoFecha + "') AS fecha")
				.append(generaFromJoin())
				.append("JOIN SVT_CONVENIO_PF scp ON  scp.DES_FOLIO = spb.CVE_FOLIO ")
				.append("JOIN SVC_ESTATUS_CONVENIO_PF secp ON secp.ID_ESTATUS_CONVENIO_PF = scp.ID_ESTATUS_CONVENIO ")
				.append(generaWhere(datos))
				.append("AND spb.ID_FLUJO_PAGOS = 2 ").toString();
	}
	
	private String queryRenovacionConvenios(ReporteRequest datos, String formatoFecha) {
		return generaEcabezados(datos, formatoFecha).append(", IFNULL(secp.DES_ESTATUS,'') AS estatus ")
				.append(",'Pago de Renovación de convenios de previsión funeraria' AS tipoIngreso ")
				.append(", DATE_FORMAT(scp.FEC_ALTA,'" + formatoFecha + "') AS fecha")
				.append(generaFromJoin())
				.append("JOIN SVT_CONVENIO_PF scp ON  scp.DES_FOLIO = spb.CVE_FOLIO ")
				.append("JOIN SVC_ESTATUS_CONVENIO_PF secp ON secp.ID_ESTATUS_CONVENIO_PF = scp.ID_ESTATUS_CONVENIO ")
				.append(generaWhere(datos))
				.append("AND spb.ID_FLUJO_PAGOS =3 ").toString();
	}
	
	private String generaWhere(ReporteRequest datos) {
		StringBuilder where = new StringBuilder();
		where.append(" WHERE spb.CVE_FOLIO IN('");
		if(datos.getFolioODS()!=null) {
			where.append(datos.getFolioODS() + "'"); 
		if(datos.getFolioNuevoConvenio()!=null)
			where.append(",'" + datos.getFolioNuevoConvenio() + "'"); 
		if(datos.getFolioRenovacionConvenio()!=null)
			where.append(",'" + datos.getFolioRenovacionConvenio() + "'"); 
		where.append(")");
		}else 
			where.append("')");
		if(datos.getIdDelegacion()!= null)
			where.append(" AND sd.ID_DELEGACION = " + datos.getIdDelegacion());
		if(datos.getIdVelatorio()!= null)
			where.append(" AND sv.ID_VELATORIO = " + datos.getIdVelatorio());
		if(datos.getIdMetodoPago()!= null)
			where.append(" AND smp.ID_METODO_PAGO = " + datos.getIdMetodoPago());
		if(datos.getFecha()!= null)
			where.append(" AND DATE_FORMAT(smp.ID_METODO_PAGO,'YY-MM-DD') = DATE_FORMAT(" + datos.getFecha() +",'YY-MM-DD')");
		
		return where.toString();
	}
}
