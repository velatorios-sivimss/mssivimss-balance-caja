package com.imss.sivimss.balancecaja.beans;

import com.imss.sivimss.balancecaja.model.request.PagoRequest;
import com.imss.sivimss.balancecaja.util.AppConstantes;
import com.imss.sivimss.balancecaja.util.DatosRequest;
import com.imss.sivimss.balancecaja.util.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModificarPago {
    private static final Logger log = LoggerFactory.getLogger(ModificarPago.class);
    public DatosRequest actualizaMotivo(PagoRequest request){
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        final QueryHelper q = new QueryHelper("UPDATE SVT_PAGO_DETALLE");
        String motivo = Objects.isNull(request.getMotivoModifica()) ? "" : "'" + request.getMotivoModifica() + "'";
        q.agregarParametroValues("DES_MOTIVO_MODIFICA", motivo);
        q.addWhere("ID_PAGO_DETALLE = " + request.getIdPagoDetalle());
        String query = q.obtenerQueryActualizar();
        String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }
}
