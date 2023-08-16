package com.imss.sivimss.balancecaja.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.balancecaja.util.DatosRequest;
import com.imss.sivimss.balancecaja.util.Response;



public interface BalanceCajaService {

	Response<Object>consultarFolioOrden(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object>consultarFolioConvenio(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object>consultarFolioRenovacionConvenio(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object>modificarPago(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object> realizarCierre(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object>consultarFiltroPaginado(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object>generarReporteBalanceCaja(DatosRequest request, Authentication authentication) throws IOException;
}
