package com.farmacy.api.service;

import com.farmacy.api.model.Medication;
import com.farmacy.api.model.MedicationPatch;
import com.farmacy.api.model.MedicationResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class MedicationService {

    private final List<MedicationResponse> memoriaTemporal = new ArrayList<>();

    public MedicationResponse createMedication(Medication medicationRequest) {
        String id = medicationRequest.getMedicationID();
        boolean exists = memoriaTemporal.stream().anyMatch(m -> m.getMedicationID().equals(id));
        if (exists) {
            return null;
        }

        MedicationResponse response = new MedicationResponse();
        response.setMedicationID(id);
        response.setMedicationName(medicationRequest.getMedicationName());
        response.setContent(medicationRequest.getContent());
        response.setPrice(medicationRequest.getPrice());
        response.setExpirationDate(medicationRequest.getExpirationDate());
        response.setInStock(medicationRequest.getInStock());
        response.setCreatedAt(OffsetDateTime.now());
        response.setUpdatedAt(OffsetDateTime.now());

        memoriaTemporal.add(response);
        return response;
    }

    public List<MedicationResponse> getAllMedications(String nombre, Boolean caducados) {
        return memoriaTemporal.stream()
                .filter(m -> nombre == null || (m.getMedicationName() != null &&
                        m.getMedicationName().toLowerCase().contains(nombre.toLowerCase())))
                .filter(m -> {
                    if (caducados == null) return true;
                    if (m.getExpirationDate() == null) return false;
                    boolean expirado = m.getExpirationDate().isBefore(LocalDate.now());
                    return caducados ? expirado : !expirado;
                })
                .collect(Collectors.toList());
    }

    public MedicationResponse getMedicationById(String medicationId) {
        return memoriaTemporal.stream()
                .filter(m -> m.getMedicationID().equals(medicationId))
                .findFirst()
                .orElse(null);
    }

    public MedicationResponse updateMedication(String id, MedicationPatch patchRequest) {
        Optional<MedicationResponse> existente = memoriaTemporal.stream()
                .filter(m -> m.getMedicationID().equals(id))
                .findFirst();

        if (existente.isEmpty()) {
            return null;
        }

        MedicationResponse med = existente.get();
        if (patchRequest.getMedicationName() != null) {
            med.setMedicationName(patchRequest.getMedicationName());
        }
        if (patchRequest.getContent() != null) {
            med.setContent(patchRequest.getContent());
        }
        if (patchRequest.getPrice() != null) {
            med.setPrice(patchRequest.getPrice());
        }
        if (patchRequest.getExpirationDate() != null) {
            med.setExpirationDate(patchRequest.getExpirationDate());
        }
        if (patchRequest.getInStock() != null) {
            med.setInStock(patchRequest.getInStock());
        }
        med.setUpdatedAt(OffsetDateTime.now());
        return med;
    }

    public boolean deleteMedication(String id) {
        return memoriaTemporal.removeIf(m -> m.getMedicationID().equals(id));
    }
}