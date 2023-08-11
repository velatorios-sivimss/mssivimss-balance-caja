package com.imss.sivimss.balancecaja.model.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ActualizarMultiRequest {

	@JsonProperty
	private List<String> updates = new ArrayList<>();
}
