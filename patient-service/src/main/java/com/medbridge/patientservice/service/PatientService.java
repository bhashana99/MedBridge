package com.medbridge.patientservice.service;

import com.medbridge.patientservice.dto.PatientResponseDTO;
import com.medbridge.patientservice.mapper.PatientMapper;
import com.medbridge.patientservice.model.Patient;
import com.medbridge.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private PatientRepository patientRepository;


    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients (){
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOS = patients.stream().map(
                PatientMapper::toPatientResponseDTO).toList();

        return  patientResponseDTOS;
    }
}
