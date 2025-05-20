package com.medbridge.patientservice.mapper;

import com.medbridge.patientservice.dto.PatientResponseDTO;
import com.medbridge.patientservice.model.Patient;

public class PatientMapper {
    public static PatientResponseDTO toPatientResponseDTO(Patient patient){
        PatientResponseDTO patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setId(patient.getId().toString());
        patientResponseDTO.setAddress(patient.getAddress());
        patientResponseDTO.setName(patient.getName());
        patientResponseDTO.setEmail(patient.getEmail());
        patientResponseDTO.setDateOfBirth(patient.getDateOfBirth().toString());

        return patientResponseDTO;


    }
}
