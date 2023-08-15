package com.imss.sivimss.balancecaja.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PagoRequest {
    @JsonProperty
    private Integer idPagoDetalle;
    @JsonProperty
    private String motivoModifica;
}
