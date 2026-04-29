package com.farmacy.api.service;

import com.farmacy.api.model.Sale;
import com.farmacy.api.model.SalePatch;
import com.farmacy.api.model.SaleResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SaleService {

    // Lista estática que actúa como base de datos temporal mientras el servidor esté vivo
    private static final List<SaleResponse> memoriaTemporal = new ArrayList<>();

    /**
     * Guarda la venta recibida en la lista y devuelve la respuesta
     */
    public SaleResponse processSale(Sale saleRequest) {
        String id = saleRequest.getSaleId();
        boolean exists = memoriaTemporal.stream().anyMatch(s -> s.getSaleId().equals(id));
        if (exists) {
            return null;
        }

        SaleResponse response = new SaleResponse();
        response.setSaleId(id);
        response.setEmployeeId(saleRequest.getEmployeeId());
        response.setClientId(saleRequest.getClientId());
        response.setQuantity(saleRequest.getQuantity());
        response.setSaleDate(saleRequest.getSaleDate());
        response.setType(saleRequest.getType());

        if (saleRequest.getPaymentMethod() != null) {
            response.setPaymentMethod(
                SaleResponse.PaymentMethodEnum.fromValue(saleRequest.getPaymentMethod().getValue())
            );
        }

        response.setCreatedAt(OffsetDateTime.now());

        // GUARDAR EN MEMORIA
        memoriaTemporal.add(response);
        
        System.out.println(">>> Venta guardada con éxito ID: " + response.getSaleId());
        return response;
    }

    public List<SaleResponse> getAllSales(String empleadoId) {
        return memoriaTemporal.stream()
                .filter(s -> empleadoId == null || empleadoId.equals(s.getEmployeeId()))
                .collect(Collectors.toList());
    }

    public SaleResponse getSaleById(String saleId) {
        return memoriaTemporal.stream()
                .filter(s -> s.getSaleId().equals(saleId))
                .findFirst()
                .orElse(null);
    }

    public SaleResponse updateSale(String saleId, SalePatch patch) {
        Optional<SaleResponse> found = memoriaTemporal.stream()
                .filter(s -> s.getSaleId().equals(saleId))
                .findFirst();

        if (found.isEmpty()) {
            return null;
        }

        SaleResponse sale = found.get();
        if (patch.getPaymentMethod() != null) {
            sale.setPaymentMethod(SaleResponse.PaymentMethodEnum.fromValue(patch.getPaymentMethod()));
        }
        if (patch.getQuantity() != null) {
            sale.setQuantity(patch.getQuantity());
        }
        return sale;
    }

    public boolean deleteSale(String saleId) {
        return memoriaTemporal.removeIf(s -> s.getSaleId().equals(saleId));
    }
}