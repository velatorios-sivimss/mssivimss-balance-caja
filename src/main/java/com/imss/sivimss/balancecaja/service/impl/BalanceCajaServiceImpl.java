package com.imss.sivimss.balancecaja.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.balancecaja.beans.ModificarPago;
import com.imss.sivimss.balancecaja.beans.RealizarCierre;
import com.imss.sivimss.balancecaja.model.request.PagoRequest;
import com.imss.sivimss.balancecaja.model.request.RealizarCierreRequest;
import com.imss.sivimss.balancecaja.model.request.UsuarioDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.balancecaja.beans.FolioConvenio;
import com.imss.sivimss.balancecaja.beans.FolioOrdenServicio;
import com.imss.sivimss.balancecaja.model.request.ActualizarMultiRequest;
import com.imss.sivimss.balancecaja.model.request.FolioRequest;
import com.imss.sivimss.balancecaja.model.response.FolioResponse;
import com.imss.sivimss.balancecaja.service.BalanceCajaService;
import com.imss.sivimss.balancecaja.util.AppConstantes;
import com.imss.sivimss.balancecaja.util.DatosRequest;
import com.imss.sivimss.balancecaja.util.LogUtil;
import com.imss.sivimss.balancecaja.util.MensajeResponseUtil;
import com.imss.sivimss.balancecaja.util.ProviderServiceRestTemplate;
import com.imss.sivimss.balancecaja.util.Response;



@Service
public class BalanceCajaServiceImpl implements BalanceCajaService{

	@Value("${endpoints.mod-catalogos}")
	private String urlDominio;
	
	private final LogUtil logUtil;
	
	
	private static final Logger log = LoggerFactory.getLogger(BalanceCajaService.class);

	
	private final ProviderServiceRestTemplate providerServiceRestTemplate;
	
	private final ModelMapper modelMapper;
	
	private FolioOrdenServicio folioOrdenServicio=FolioOrdenServicio.obtenerFolioOrdenInstance();

	private FolioConvenio folioConvenio=FolioConvenio.obtenerFolioConvenioInstance();

	ModificarPago modificarPago = new ModificarPago();
	
	private static final String CU69_NAME= "Realizar cierre : ";
	private static final String MODIFICADO_CORRECTAMENTE = "18"; // Modificado correctamente.
	
	public BalanceCajaServiceImpl(ProviderServiceRestTemplate providerServiceRestTemplate, ModelMapper modelMapper, LogUtil logUtil) {
		this.providerServiceRestTemplate = providerServiceRestTemplate;
		this.modelMapper=modelMapper;
		this.logUtil=logUtil;
	}

	@Override
	public Response<Object> consultarFolioOrden(DatosRequest request, Authentication authentication) throws IOException {
		FolioRequest folioRequest= new FolioRequest();
		try {
            logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "consultarFolioOrden", AppConstantes.CONSULTA, authentication);
            List<FolioResponse>folioResponses;
			Gson gson= new Gson();
			String datosJson=request.getDatos().get(AppConstantes.DATOS).toString();
			folioRequest=gson.fromJson(datosJson,FolioRequest.class);
			Response<Object>response;
			response=providerServiceRestTemplate.consumirServicio(
					folioOrdenServicio.obtenerFolios(folioRequest.getFolio()).getDatos(), 
					urlDominio.concat(AppConstantes.CATALOGO_CONSULTAR), 
					authentication);
			if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
				folioResponses= Arrays.asList(modelMapper.map(response.getDatos(), FolioResponse[].class));
				response.setDatos(folioResponses);
			}

			return MensajeResponseUtil.mensajeConsultaResponseObject(response, AppConstantes.ERROR_CONSULTAR);
		
		} catch (Exception e) {
			String consulta = folioOrdenServicio.obtenerFolios(folioRequest.getFolio()).getDatos().get(AppConstantes.QUERY).toString();
	        String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
	        log.error(AppConstantes.ERROR_QUERY.concat(decoded));
	        log.error(e.getMessage());
	        logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + decoded, AppConstantes.CONSULTA, authentication);
	        throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarFolioConvenio(DatosRequest request, Authentication authentication) throws IOException {
		FolioRequest folioRequest= new FolioRequest();
		try {
            logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "consultarFolioOrden", AppConstantes.CONSULTA, authentication);
            List<FolioResponse>folioResponses;
			Gson gson= new Gson();
			String datosJson=request.getDatos().get(AppConstantes.DATOS).toString();
			folioRequest=gson.fromJson(datosJson,FolioRequest.class);
			Response<Object>response;
			response=providerServiceRestTemplate.consumirServicio(
					folioConvenio.obtenerFoliosConvenio(folioRequest.getFolio()).getDatos(), 
					urlDominio.concat(AppConstantes.CATALOGO_CONSULTAR), 
					authentication);
			if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
				folioResponses= Arrays.asList(modelMapper.map(response.getDatos(), FolioResponse[].class));
				response.setDatos(folioResponses);
			}

			return MensajeResponseUtil.mensajeConsultaResponseObject(response, AppConstantes.ERROR_CONSULTAR);
		
		} catch (Exception e) {
			String consulta = folioConvenio.obtenerFoliosConvenio(folioRequest.getFolio()).getDatos().get(AppConstantes.QUERY).toString();
	        String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
	        log.error(AppConstantes.ERROR_QUERY.concat(decoded));
	        log.error(e.getMessage());
	        logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + decoded, AppConstantes.CONSULTA, authentication);
	        throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarFolioRenovacionConvenio(DatosRequest request, Authentication authentication)
			throws IOException {
		FolioRequest folioRequest= new FolioRequest();
		try {
            logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "consultarFolioOrden", AppConstantes.CONSULTA, authentication);
            List<FolioResponse>folioResponses;
			Gson gson= new Gson();
			String datosJson=request.getDatos().get(AppConstantes.DATOS).toString();
			folioRequest=gson.fromJson(datosJson,FolioRequest.class);
			Response<Object>response;
			response=providerServiceRestTemplate.consumirServicio(
					folioConvenio.obtenerFoliosRenovacionConvenio(folioRequest.getFolio()).getDatos(), 
					urlDominio.concat(AppConstantes.CATALOGO_CONSULTAR), 
					authentication);
			if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
				folioResponses= Arrays.asList(modelMapper.map(response.getDatos(), FolioResponse[].class));
				response.setDatos(folioResponses);
			}

			return MensajeResponseUtil.mensajeConsultaResponseObject(response, AppConstantes.ERROR_CONSULTAR);
		
		} catch (Exception e) {
			String consulta = folioConvenio.obtenerFoliosRenovacionConvenio(folioRequest.getFolio()).getDatos().get(AppConstantes.QUERY).toString();
	        String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
	        log.error(AppConstantes.ERROR_QUERY.concat(decoded));
	        log.error(e.getMessage());
	        logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + decoded, AppConstantes.CONSULTA, authentication);
	        throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
	}

	@Override
	public Response<Object> modificarPago(DatosRequest request, Authentication authentication) throws IOException {
		Gson json = new Gson();
		UsuarioDto usuarioDto = json.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		PagoRequest actualizaPago = json.fromJson(datosJson, PagoRequest.class);
		Response<Object>response;
		response = providerServiceRestTemplate.consumirServicio(modificarPago.actualizaMotivo(actualizaPago,usuarioDto.getIdUsuario().toString()).getDatos(),
				urlDominio.concat(AppConstantes.CATALOGO_ACTUALIZAR),authentication);
		if(response.getCodigo()==200){
			return response;
		}
		response.setError(true);
		response.setCodigo(500);
		response.setMensaje("");
		response.setDatos(response.getDatos());
		return response;
	}

	@Override
	public Response<Object> realizarCierre(DatosRequest request, Authentication authentication) throws IOException {
		String consulta = "";
		try {
			RealizarCierreRequest realizarCierreRequest= new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), RealizarCierreRequest.class);
			UsuarioDto usuarioDto = new Gson().fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			logUtil.crearArchivoLog(Level.INFO.toString(), CU69_NAME +  this.getClass().getSimpleName(),	this.getClass().getPackage().toString(), "actualiza realizar cierre", AppConstantes.MODIFICACION, authentication);
			ActualizarMultiRequest actualizarMultiRequest = new RealizarCierre().actualizaEstatusCierre(realizarCierreRequest, usuarioDto);
			consulta = actualizarMultiRequest.toString();
			Response<Object>response = providerServiceRestTemplate.consumirServicio(actualizarMultiRequest, urlDominio.concat("/actualizar/multiples") , authentication);
			return MensajeResponseUtil.mensajeResponseObject(response, MODIFICADO_CORRECTAMENTE);
		} catch (Exception e) {
			e.printStackTrace();
			 log.error(AppConstantes.ERROR_QUERY.concat(consulta));
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + consulta, AppConstantes.CONSULTA,
					authentication);
			throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
	}
}
