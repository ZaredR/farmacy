package com.farmacy.api.service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.farmacy.api.model.Employee;
import com.farmacy.api.model.EmployeePatch;
import com.farmacy.api.model.EmployeeResponse;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmployeeService {

    private final Map<String, EmployeeResponse> database = new HashMap<>();

    public EmployeeResponse createEmployee(Employee employee) {
        String id = employee.getEmployeeID();
        if (database.containsKey(id)) {
            return null;
        }

        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeID(id);
        response.setEmployeeName(employee.getEmployeeName());

        if (employee.getPosition() != null) {
            response.setPosition(EmployeeResponse.PositionEnum.valueOf(employee.getPosition().name()));
        }
        if (employee.getShift() != null) {
            response.setShift(EmployeeResponse.ShiftEnum.valueOf(employee.getShift().name()));
        }

        response.setPhone(employee.getPhone());
        response.setHireDate(employee.getHireDate());
        response.setCreatedAt(OffsetDateTime.now());

        database.put(id, response);
        return response;
    }

    public List<EmployeeResponse> getAllEmployees(String nombre) {
        return database.values().stream()
                .filter(e -> nombre == null || (e.getEmployeeName() != null &&
                        e.getEmployeeName().toLowerCase().contains(nombre.toLowerCase())))
                .collect(Collectors.toList());
    }

    public EmployeeResponse getEmployeeById(String id) {
        return database.get(id);
    }

    public EmployeeResponse updateEmployee(String id, EmployeePatch patch) {
        EmployeeResponse existing = database.get(id);
        if (existing == null) {
            return null;
        }
        if (patch.getEmployeeName() != null) {
            existing.setEmployeeName(patch.getEmployeeName());
        }
        if (patch.getShift() != null) {
            try {
                existing.setShift(EmployeeResponse.ShiftEnum.fromValue(patch.getShift()));
            } catch (IllegalArgumentException ignored) {}
        }
        return existing;
    }

    public boolean deleteEmployee(String id) {
        return database.remove(id) != null;
    }
}