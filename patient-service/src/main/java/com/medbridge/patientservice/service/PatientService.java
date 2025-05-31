package com.medbridge.patientservice.service;

import com.medbridge.patientservice.dto.PatientRequestDTO;
import com.medbridge.patientservice.dto.PatientResponseDTO;
import com.medbridge.patientservice.exception.EmailAlreadyExistsException;
import com.medbridge.patientservice.exception.PatientNotFoundException;
import com.medbridge.patientservice.grpc.BillingServiceGrpcClient;
import com.medbridge.patientservice.kafka.KafkaProducer;
import com.medbridge.patientservice.mapper.PatientMapper;
import com.medbridge.patientservice.model.Patient;
import com.medbridge.patientservice.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOS = patients.stream().map(PatientMapper::toPatientResponseDTO).toList();

        return patientResponseDTOS;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("This email already exits " + patientRequestDTO.getEmail());
        }

        Patient newPatient = patientRepository.save(PatientMapper.toPatientModel(patientRequestDTO));


            billingServiceGrpcClient.createBillingAccount(
                    newPatient.getId().toString(),
                    newPatient.getName(),
                    newPatient.getEmail()
            );

            kafkaProducer.sendEvent(newPatient);

        return PatientMapper.toPatientResponseDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)) {
            throw new EmailAlreadyExistsException("This email already exits " + patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toPatientResponseDTO(updatedPatient);
    }

    public void deletePatient(UUID id){

    }
}
