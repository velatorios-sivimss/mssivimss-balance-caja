package com.imss.sivimss.balancecaja.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealizarCierreRequest {
	
	private Integer idPagoDetalle;
	private Integer indEstatusCaja;

}
