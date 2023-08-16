package com.imss.sivimss.balancecaja.model.request;


import com.fasterxml.jackson.annotation.JsonIgnoreType;

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
@JsonIgnoreType(value = true)
public class ReporteRequest {
	private Integer idDelegacion;
	private Integer idVelatorio;
	private Integer idMetodoPago;
	private String folioODS;
	private String folioNuevoConvenio;
	private String folioRenovacionConvenio;
	private String fecha;
	private String tipoReporte;
	private String totalImporte;
	private String totalIngreso;
	private String totalRegistros;
	private Integer idTipoConvenio;
}
