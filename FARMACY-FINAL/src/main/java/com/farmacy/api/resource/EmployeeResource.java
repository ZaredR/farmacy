package com.farmacy.api.resource;

import com.farmacy.api.model.DeleteConfirmation;
import com.farmacy.api.model.Employee;
import com.farmacy.api.model.EmployeePatch;
import com.farmacy.api.model.EmployeeResponse;
import com.farmacy.api.service.EmployeeService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {

    @Inject
    EmployeeService employeeService;

    @POST
    public Response createEmployee(@Valid Employee employee) {
        EmployeeResponse response = employeeService.createEmployee(employee);
        if (response == null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Ya existe un empleado con el ID " + employee.getEmployeeID() + "\"}")
                    .build();
        }
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    public Response getAllEmployees(@QueryParam("nombre") String nombre) {
        List<EmployeeResponse> lista = employeeService.getAllEmployees(nombre);
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{employeeId}")
    public Response getEmployee(@PathParam("employeeId") String employeeId) {
        EmployeeResponse response = employeeService.getEmployeeById(employeeId);
        if (response == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"El empleado " + employeeId + " no está registrado\"}")
                    .build();
        }
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{employeeId}")
    public Response updateEmployee(@PathParam("employeeId") String employeeId, @Valid EmployeePatch patch) {
        EmployeeResponse updated = employeeService.updateEmployee(employeeId, patch);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"No se pudo actualizar. El ID " + employeeId + " no existe.\"}")
                    .build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{employeeId}")
    public Response deleteEmployee(@PathParam("employeeId") String employeeId) {
        boolean deleted = employeeService.deleteEmployee(employeeId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"El empleado " + employeeId + " no existe.\"}")
                    .build();
        }
        DeleteConfirmation confirmation = new DeleteConfirmation();
        confirmation.setMessage("Registro eliminado exitosamente");
        return Response.ok(confirmation).build();
    }
}