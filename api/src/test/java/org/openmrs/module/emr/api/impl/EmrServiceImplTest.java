package org.openmrs.module.emr.api.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.impl.PatientServiceImpl;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.api.EmrService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmrServiceImplTest {

    private EmrServiceImpl emrService;
    private EmrProperties emrProperties;
    private PatientService patientService;

    @Before
    public void setUp(){
        emrService = new EmrServiceImpl();

        emrProperties = mock(EmrProperties.class);
        emrService.setEmrProperties(emrProperties);

        patientService = mock(PatientService.class);
        emrService.setPatientService(patientService);
    }


    @Test
    public void shouldReturnPatientWhenSearchingByPrimaryId(){
        String identifierValue = "Y27X42";

        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();

        List<PatientIdentifierType> patientIdentifierTypeList = new ArrayList<PatientIdentifierType>();
        patientIdentifierTypeList.add(patientIdentifierType);

        when(emrProperties.getPrimaryIdentifierType()).thenReturn(patientIdentifierType);

        Patient patient = new Patient();

        List<Patient> patients = new ArrayList<Patient>();
        patients.add(patient);

        when(patientService.getPatients((String) isNull(), eq(identifierValue), eq(patientIdentifierTypeList), eq(true))).thenReturn(patients);

        Patient expectedPatient = emrService.findPatientByPrimaryId(identifierValue);

        assertThat(expectedPatient, is(patient));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldReturnExceptionWhenPrimaryIdIsNull(){
        String identifierValue = null;
        Patient expectedPatient = emrService.findPatientByPrimaryId(identifierValue);
    }

    @Test(expected = APIException.class)
    public void shouldReturnExceptionWhenThereIsNoPatientWithThisId(){
        String identifierValue = "Y27X42";

        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();

        List<PatientIdentifierType> patientIdentifierTypeList = new ArrayList<PatientIdentifierType>();
        patientIdentifierTypeList.add(patientIdentifierType);

        when(emrProperties.getPrimaryIdentifierType()).thenReturn(patientIdentifierType);

        when(patientService.getPatients((String) isNull(), eq(identifierValue), eq(patientIdentifierTypeList), eq(true))).thenReturn(Collections.<Patient>emptyList());

        Patient expectedPatient = emrService.findPatientByPrimaryId(identifierValue);
    }

    @Test(expected = RuntimeException.class)
    public void shouldReturnExceptionWhenPrimaryIdentifierIsNotConfigured(){
        String identifierValue = "Y27X42";

        when(emrProperties.getPrimaryIdentifierType()).thenReturn(null);
        Patient expectedPatient = emrService.findPatientByPrimaryId(identifierValue);
    }
}
