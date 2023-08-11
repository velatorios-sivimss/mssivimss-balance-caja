package com.imss.sivimss.balancecaja.beans;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.balancecaja.model.request.ActualizarMultiRequest;
import com.imss.sivimss.balancecaja.model.request.RealizarCierreRequest;
import com.imss.sivimss.balancecaja.model.request.UsuarioDto;
import com.imss.sivimss.balancecaja.util.QueryHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealizarCierre {

	private static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP()";

	public ActualizarMultiRequest actualizaEstatusCierre(RealizarCierreRequest realizarCierreRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - actualizaEstatusCierre");
		ActualizarMultiRequest actualizarMultiRequest = new ActualizarMultiRequest();
		List<String> updates = new ArrayList<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_PAGO_DETALLE ");
		q.agregarParametroValues("IND_ESTATUS_CAJA", String.valueOf(realizarCierreRequest.getIndEstatusCaja()));
		q.agregarParametroValues("ID_USUARIO_MODIFICA", String.valueOf(usuarioDto.getIdUsuario()));
		q.agregarParametroValues("FEC_ACTUALIZACION", CURRENT_TIMESTAMP);
		q.agregarParametroValues("FEC_CIERRE_CAJA", CURRENT_TIMESTAMP);
		q.addWhere("ID_PAGO_DETALLE = " + realizarCierreRequest.getIdPagoDetalle());
		final String query = q.obtenerQueryActualizar();
		log.info(" actualizarOrdenEntrada: " + query);
		updates.add(DatatypeConverter.printBase64Binary(q.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		log.info(" TERMINO - actualizaEstatusCierre");
		
		return actualizarMultiRequest;
	}

}