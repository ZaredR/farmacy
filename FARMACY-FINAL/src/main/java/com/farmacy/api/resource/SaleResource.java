package com.farmacy.api.resource;

import com.farmacy.api.model.DeleteConfirmation;
import com.farmacy.api.model.Sale;
import com.farmacy.api.model.SalePatch;
import com.farmacy.api.model.SaleResponse;
import com.farmacy.api.service.SaleService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SaleResource {

    @Inject
    SaleService saleService;

    @POST
    public Response createSale(@Valid Sale saleRequest) {
        SaleResponse response = saleService.processSale(saleRequest);
        if (response == null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Ya existe una venta con el ID " + saleRequest.getSaleId() + "\"}")
                    .build();
        }
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public Response getAllSales(@QueryParam("empleado") String empleadoId) {
        List<SaleResponse> lista = saleService.getAllSales(empleadoId);
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{saleId}")
    public Response getSaleById(@PathParam("saleId") String saleId) {
        SaleResponse response = saleService.getSaleById(saleId);
        if (response == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"La venta " + saleId + " no está registrada\"}")
                    .build();
        }
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{saleId}")
    public Response updateSale(@PathParam("saleId") String saleId, @Valid SalePatch patch) {
        SaleResponse updated = saleService.updateSale(saleId, patch);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"No se pudo actualizar. El ID " + saleId + " no existe.\"}")
                    .build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{saleId}")
    public Response deleteSale(@PathParam("saleId") String saleId) {
        boolean deleted = saleService.deleteSale(saleId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"La venta " + saleId + " no existe.\"}")
                    .build();
        }
        DeleteConfirmation confirmation = new DeleteConfirmation();
        confirmation.setMessage("Registro eliminado exitosamente");
        return Response.ok(confirmation).build();
    }
}