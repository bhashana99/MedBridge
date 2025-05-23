package com.medbridge.patientservice.service;

import com.medbridge.patientservice.dto.PatientRequestDTO;
import com.medbridge.patientservice.dto.PatientResponseDTO;
import com.medbridge.patientservice.exception.EmailAlreadyExistsException;
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

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("This email already exits "+patientRequestDTO.getEmail());
        }

        Patient newPatient = patientRepository.save(PatientMapper.toPatientModel(patientRequestDTO));

        return PatientMapper.toPatientResponseDTO(newPatient);
    }
}
