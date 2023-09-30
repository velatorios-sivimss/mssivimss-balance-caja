package com.imss.sivimss.balancecaja.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.balancecaja.beans.ModificarPago;
import com.imss.sivimss.balancecaja.beans.RealizarCierre;
import com.imss.sivimss.balancecaja.model.request.PagoRequest;
import com.imss.sivimss.balancecaja.model.request.ReporteRequest;
import com.imss.sivimss.balancecaja.model.request.UsuarioDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.imss.sivimss.balancecaja.beans.ConsultaGeneral;

@Service
public class BalanceCajaServiceImpl implements BalanceCajaService{

	@Value("${endpoints.mod-catalogos}")
	private String urlDominio;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Value("${reporte.balance-caja}")
	private String reporteBalanceCaja;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	private final LogUtil logUtil;
	
	
	private static final Logger log = LoggerFactory.getLogger(BalanceCajaServiceImpl.class);

	private final ProviderServiceRestTemplate providerServiceRestTemplate;
	
	private final ModelMapper modelMapper;
	
	private FolioOrdenServicio folioOrdenServicio=FolioOrdenServicio.obtenerFolioOrdenInstance();

	private FolioConvenio folioConvenio=FolioConvenio.obtenerFolioConvenioInstance();

	ModificarPago modificarPago = new ModificarPago();

	private static final String ERROR_QUERY = "Error al ejecutar el query: ";
	private static final String NO_SE_ENCONTRO_INFORMACION = "45"; // No se encontr� informaci�n relacionada a tu
	private static final String ERROR_AL_DESCARGAR_DOCUMENTO= "64"; // Error en la descarga del documento.Intenta nuevamente.
	// b�squeda.

	private static final String CU069_NOMBRE = "BalanceCaja: ";
	private static final String CATALOGO_CONSULTAR= "/consulta";
	private static final String CONSULTAR_PAGINADO = "/paginado";
	private static final String CONSULTAR_FILTRO_PAGINADO = "consultar-balance-caja: " ;
	private static final String CONSULTA = "consulta";
	private static final String GENERAR_DOCUMENTO = "Generar Reporte: " ;
	private static final String GENERA_DOCUMENTO = "Genera_Documento";
	
	private static final String CU69_NAME= "Realizar cierre : ";
	private static final String MODIFICADO_CORRECTAMENTE = "18"; // Modificado correctamente.
	private static final String TIPO_REPORTE = "tipoReporte";
	private static final String RUTA_NOMBRE_REPORTE = "rutaNombreReporte";
	
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
			ReporteRequest reporteRequest  = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ReporteRequest.class);
			UsuarioDto usuarioDto = new Gson().fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			logUtil.crearArchivoLog(Level.INFO.toString(), CU69_NAME +  this.getClass().getSimpleName(),	this.getClass().getPackage().toString(), "actualiza realizar cierre", AppConstantes.MODIFICACION, authentication);
			ActualizarMultiRequest actualizarMultiRequest = new RealizarCierre().actualizaEstatusCierre(reporteRequest, usuarioDto, formatoFecha);
			consulta = actualizarMultiRequest.toString();
			Response<Object>response = providerServiceRestTemplate.consumirServicio(actualizarMultiRequest, urlDominio.concat("/actualizar/multiples") , authentication);
			return MensajeResponseUtil.mensajeResponseObject(response, MODIFICADO_CORRECTAMENTE);
		} catch (Exception e) {
			e.fillInStackTrace();
			 log.error(AppConstantes.ERROR_QUERY.concat(consulta));
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + consulta, AppConstantes.CONSULTA,
					authentication);
			throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
	}
	@Override
	public Response<Object> consultarFiltroPaginado(DatosRequest request, Authentication authentication)throws IOException {
		ConsultaGeneral consultaGral = new ConsultaGeneral();
		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteRequest reporteRequest = gson.fromJson(datosJson, ReporteRequest.class);

		String query = consultaGral.consultarGralFiltros(reporteRequest,formatoFecha);
		DatosRequest datosRequest= encodeQuery(query, request);
			try {
				log.info(CU069_NOMBRE);
				log.info(query);
				logUtil.crearArchivoLog(Level.INFO.toString(), CU069_NOMBRE + CONSULTAR_FILTRO_PAGINADO + this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "consultarDonados", CONSULTA, authentication);
				MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(consultaGral.cerrarEstatusCaja(), urlDominio + AppConstantes.CATALOGO_ACTUALIZAR, authentication),"5");
				logUtil.crearArchivoLog(Level.INFO.toString(), CU069_NOMBRE + this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "ESTATUS ACTUALIZADOS CORRECTAMENTE", "ACTUALIZACION", authentication);
				Response<Object>  response = providerRestTemplate.consumirServicio(datosRequest.getDatos(), urlDominio + CONSULTAR_PAGINADO , authentication);
				return MensajeResponseUtil.mensajeConsultaResponse(response, NO_SE_ENCONTRO_INFORMACION);
			} catch (Exception e) {
				log.error( CU069_NOMBRE );
				log.error(CONSULTAR_FILTRO_PAGINADO );
				log.error(ERROR_QUERY );
				log.error( query);
				logUtil.crearArchivoLog(Level.WARNING.toString(), CU069_NOMBRE + CONSULTAR_FILTRO_PAGINADO + this.getClass().getSimpleName(),
						this.getClass().getPackage().toString(), ERROR_QUERY +  query, CONSULTA, authentication);
				throw new IOException("52", e.getCause());
			}
	}

	@Override
	public Response<Object> generarReporteBalanceCaja(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteRequest reporteRequest = gson.fromJson(datosJson, ReporteRequest.class);
		ConsultaGeneral consultaGral = new ConsultaGeneral();
	//	String query = consultaGral.consultarTotalesGralFiltros(reporteRequest);
		 String query = consultaGral.consultarGralFiltros(reporteRequest, formatoFecha);
		try {
			log.info( CU069_NOMBRE );
			log.info( GENERAR_DOCUMENTO );
		/*	DatosRequest datosRequest= encodeQuery(query, request);
			Response<Object> response = providerRestTemplate.consumirServicio(datosRequest, urlDominio + CATALOGO_CONSULTAR, authentication);

			String json = responseToJson(response.getDatos().toString());

			ReporteRequest reporteTotales = gson.fromJson(json, ReporteRequest.class); */
			
			Map<String, Object> envioDatos = new HashMap<>();
			
			log.info( query);
			envioDatos.put("query", query);
		/*	envioDatos.put("totalImporte", reporteTotales.getTotalImporte());
			envioDatos.put("totalIngreso", reporteTotales.getTotalIngreso());
			envioDatos.put("totalRegistros", reporteTotales.getTotalRegistros());
			envioDatos.put(TIPO_REPORTE, reporteRequest.getTipoReporte()); */
			envioDatos.put(RUTA_NOMBRE_REPORTE, reporteBalanceCaja);
			logUtil.crearArchivoLog(Level.INFO.toString(), CU069_NOMBRE + GENERAR_DOCUMENTO + this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), "generarDocumento", GENERA_DOCUMENTO, authentication);
			Response<Object>response = providerServiceRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
			return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_AL_DESCARGAR_DOCUMENTO);
		} catch (Exception e) {
			log.error( CU069_NOMBRE );
			log.error(GENERAR_DOCUMENTO );
			log.error(ERROR_QUERY );
			log.error( query);
			logUtil.crearArchivoLog(Level.WARNING.toString(), CU069_NOMBRE + GENERAR_DOCUMENTO + this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), ERROR_QUERY + query, GENERA_DOCUMENTO,
					authentication);
			throw new IOException("52", e.getCause());
		}

	}
	private String responseToJson(String str){
		str = str.replace("=", "\":\"");
		str = str.replace(", ", "\",\"");
		str = str.replace("}]", "\"}");
		str = str.replace("[{", "{\"");
		return str;
	}
	private DatosRequest encodeQuery(String query, DatosRequest request) {
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
}
