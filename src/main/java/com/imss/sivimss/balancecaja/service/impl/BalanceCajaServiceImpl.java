package com.imss.sivimss.balancecaja.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.balancecaja.beans.FolioConvenio;
import com.imss.sivimss.balancecaja.beans.FolioOrdenServicio;
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
		return null;
	}
}
