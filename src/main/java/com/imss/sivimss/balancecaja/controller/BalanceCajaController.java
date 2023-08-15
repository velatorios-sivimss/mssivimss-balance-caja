package com.imss.sivimss.balancecaja.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imss.sivimss.balancecaja.service.BalanceCajaService;
import com.imss.sivimss.balancecaja.util.DatosRequest;
import com.imss.sivimss.balancecaja.util.LogUtil;
import com.imss.sivimss.balancecaja.util.ProviderServiceRestTemplate;
import com.imss.sivimss.balancecaja.util.Response;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;


@RestController
@RequestMapping("/balance-caja")
public class BalanceCajaController {

	@Autowired
	private BalanceCajaService balanceCajaService;
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	private static final String RESILENCE="Resiliencia";
	
	@PostMapping("/consultar/folio-orden")
	public CompletableFuture<Object>consultarFolioOrden(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response=balanceCajaService.consultarFolioOrden(request, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@PostMapping("/consultar/folio-convenio")
	public CompletableFuture<Object>consultarFolioConvenio(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response=balanceCajaService.consultarFolioConvenio(request, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@PostMapping("/consultar/folio-renovacion-convenio")
	public CompletableFuture<Object>consultarFolioRenovacionConvenio(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response=balanceCajaService.consultarFolioRenovacionConvenio(request, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	@PostMapping("/modificar-pago")
	public CompletableFuture<Object>modificarPAgo(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response=balanceCajaService.modificarPago(request, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@PostMapping("/realiza/cierre")
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	public CompletableFuture<Object> actualizaEstatusCierre(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
		Response<Object> response =  balanceCajaService.realizarCierre(request, authentication);
		return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	@PostMapping("/consultar/balance-caja")
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	public CompletableFuture<Object>consultarFiltroPaginado(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response=balanceCajaService.consultarFiltroPaginado(request, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	@PostMapping("/consultar/total-balance-caja")
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	public CompletableFuture<Object>consultarTotalFiltroPaginado(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response=balanceCajaService.consultarTotalesFiltroPaginado(request, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	@PostMapping("/generar/reporte-balance-caja")
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	public CompletableFuture<Object>generarReporteBalanceCaja(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response=balanceCajaService.generarReporteBalanceCaja(request, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	/**
	 * fallbacks generico
	 * 
	 * @return respuestas
	 * @throws IOException 
	 */
	private CompletableFuture<Object> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			CallNotPermittedException e) throws IOException {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
	    logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), RESILENCE, RESILENCE, authentication);
	  
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	private CompletableFuture<Object> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			RuntimeException e) throws IOException {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		
	    logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), RESILENCE,RESILENCE, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	private CompletableFuture<Object> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			NumberFormatException e) throws IOException {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());

	    logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(),RESILENCE, RESILENCE, authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
}
