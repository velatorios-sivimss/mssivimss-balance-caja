package com.imss.sivimss.balancecaja.service.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.imss.sivimss.balancecaja.service.BalanceCajaService;
import com.imss.sivimss.balancecaja.util.LogUtil;
import com.imss.sivimss.balancecaja.util.ProviderServiceRestTemplate;


@Service
public class BalanceCajaServiceImpl implements BalanceCajaService{

	@Value("${endpoints.mod-catalogos}")
	private String urlDominio;
	
	private final LogUtil logUtil;
	
	
	private static final Logger log = LoggerFactory.getLogger(BalanceCajaService.class);

	
	private final ProviderServiceRestTemplate providerServiceRestTemplate;
	
	private final ModelMapper modelMapper;
	
	public BalanceCajaServiceImpl(ProviderServiceRestTemplate providerServiceRestTemplate, ModelMapper modelMapper, LogUtil logUtil) {
		this.providerServiceRestTemplate = providerServiceRestTemplate;
		this.modelMapper=modelMapper;
		this.logUtil=logUtil;
	}
}
