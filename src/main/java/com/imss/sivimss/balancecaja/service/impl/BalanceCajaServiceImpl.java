package com.imss.sivimss.balancecaja.service.impl;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.imss.sivimss.balancecaja.beans.FolioConvenio;
import com.imss.sivimss.balancecaja.beans.FolioOrdenServicio;
import com.imss.sivimss.balancecaja.service.BalanceCajaService;
import com.imss.sivimss.balancecaja.util.DatosRequest;
import com.imss.sivimss.balancecaja.util.LogUtil;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<Object> consultarFolioConvenio(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<Object> consultarFolioRenovacionConvenio(DatosRequest request, Authentication authentication)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
