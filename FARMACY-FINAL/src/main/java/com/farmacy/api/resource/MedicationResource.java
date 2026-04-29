package com.farmacy.api.resource;

import java.util.List;

import com.farmacy.api.model.DeleteConfirmation;
import com.farmacy.api.model.Medication;
import com.farmacy.api.model.MedicationPatch;
import com.farmacy.api.model.MedicationResponse;
import com.farmacy.api.service.MedicationService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/medications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicationResource {

    @Inject
    MedicationService medicationService;

    @POST
    public Response createMedication(@Valid Medication medicationRequest) {
        MedicationResponse response = medicationService.createMedication(medicationRequest);
        if (response == null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Ya existe un medicamento con el ID " + medicationRequest.getMedicationID() + "\"}")
                    .build();
        }
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public Response getAllMedications(
            @QueryParam("nombre") String nombre,
            @QueryParam("caducados") Boolean caducados) {
        List<MedicationResponse> lista = medicationService.getAllMedications(nombre, caducados);
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{medicationId}")
    public Response getMedicationById(@PathParam("medicationId") String medicationId) {
        if (medicationId == null || !medicationId.startsWith("med-")) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\": \"El formato del ID debe iniciar con 'med-'\"}")
                           .build();
        }

        MedicationResponse response = medicationService.getMedicationById(medicationId);
        if (response == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"El medicamento " + medicationId + " no está registrado\"}")
                    .build();
        }
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{medicationId}")
    public Response updateMedication(
            @PathParam("medicationId") String medicationId,
            @Valid MedicationPatch patchRequest) {
        MedicationResponse actualizado = medicationService.updateMedication(medicationId, patchRequest);
        if (actualizado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"No se pudo actualizar. El ID " + medicationId + " no existe.\"}")
                    .build();
        }
        return Response.ok(actualizado).build();
    }

    @DELETE
    @Path("/{medicationId}")
    public Response deleteMedication(@PathParam("medicationId") String medicationId) {
        boolean deleted = medicationService.deleteMedication(medicationId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"El medicamento " + medicationId + " no existe.\"}")
                    .build();
        }
        DeleteConfirmation confirmation = new DeleteConfirmation();
        confirmation.setMessage("Registro eliminado exitosamente");
        return Response.ok(confirmation).build();
    }
}