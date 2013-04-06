package org.openmrs.module.emr.patient;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openmrs.module.emr.EmrConstants.TEST_PATIENT_ATTRIBUTE_UUID;

public class PatientDomainWrapperTest {

    private PatientDomainWrapper patientDomainWrapper;
    private EmrApiProperties emrApiProperties;
    private Patient patient;
    private VisitService visitService;

    @Before
    public void setUp() throws Exception {
        patient = new Patient();
        emrApiProperties = mock(EmrApiProperties.class);
        visitService = mock(VisitService.class);
        patientDomainWrapper = new PatientDomainWrapper(patient, emrApiProperties, mock(AdtService.class),
                visitService, mock(EncounterService.class) );
    }

    @Test
    public void shouldVerifyIfPatientIsUnknown(){

        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setPersonAttributeTypeId(10);
        personAttributeType.setName(EmrConstants.UNKNOWN_PATIENT_PERSON_ATTRIBUTE_TYPE_NAME);
        personAttributeType.setFormat("java.lang.String");

        PersonAttribute newAttribute = new PersonAttribute(personAttributeType, "true");
        patient.addAttribute(newAttribute);

        when(emrApiProperties.getUnknownPatientPersonAttributeType()).thenReturn(personAttributeType);

        assertTrue(patientDomainWrapper.isUnknownPatient());

    }

    @Test
    public void shouldVerifyIfPatientIsATest(){

        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setPersonAttributeTypeId(11);
        personAttributeType.setName("Test Patient");
        personAttributeType.setFormat("java.lang.Boolean");
        personAttributeType.setUuid(TEST_PATIENT_ATTRIBUTE_UUID);

        PersonAttribute newAttribute = new PersonAttribute(personAttributeType, "true");

        patient.addAttribute(newAttribute);

        when(emrApiProperties.getTestPatientPersonAttributeType()).thenReturn(personAttributeType);

        assertTrue(patientDomainWrapper.isTestPatient());

    }

    @Test
    public void shouldVerifyIfPatientIsNotATest(){

        PersonAttributeType personAttributeType = new PersonAttributeType();
        personAttributeType.setPersonAttributeTypeId(11);
        personAttributeType.setName("Test Patient");
        personAttributeType.setFormat("java.lang.Boolean");
        personAttributeType.setUuid(TEST_PATIENT_ATTRIBUTE_UUID);

        PersonAttribute newAttribute = new PersonAttribute(personAttributeType, "false");

        patient.addAttribute(newAttribute);

        when(emrApiProperties.getTestPatientPersonAttributeType()).thenReturn(personAttributeType);

        assertFalse(patientDomainWrapper.isTestPatient());

    }

    @Test
    public void shouldCreateAListOfVisitDomainWrappersBasedOnVisitListFromVisitService(){
        when(visitService.getVisitsByPatient(patient, true, false)).thenReturn(asList(new Visit(), new Visit(), new Visit()));

        List<VisitDomainWrapper> visitDomainWrappers =  patientDomainWrapper.getAllVisitsUsingWrappers();

        assertThat(visitDomainWrappers.size(), is(3));
    }

    @Test
    public void shouldReturnFormattedName(){
        patient = mock(Patient.class);

        patientDomainWrapper =  new PatientDomainWrapper(patient, emrApiProperties, mock(AdtService.class),
                visitService, mock(EncounterService.class) );

        Set<PersonName> personNames = new HashSet<PersonName>();

        PersonName personNamePreferred = createPreferredPersonName("John", "Dover");
        personNames.add(personNamePreferred);

        when(patient.getNames()).thenReturn(personNames);

        String formattedName = patientDomainWrapper.getFormattedName();

        assertThat(formattedName, is("Dover, John"));
    }


    @Test
    public void shouldReturnPersonNameWhenThereAreTwoNamesAndOneOfThemIsPreferred(){
        patient = mock(Patient.class);

        patientDomainWrapper =  new PatientDomainWrapper(patient, emrApiProperties, mock(AdtService.class),
                visitService, mock(EncounterService.class) );

        Set<PersonName> personNames = new HashSet<PersonName>();

        PersonName personNamePreferred = createPreferredPersonName("mario", "neissi");
        personNames.add(personNamePreferred);

        PersonName personNameNonPreferred = createNonPreferredPersonName("Ana", "emerson");
        personNames.add(personNameNonPreferred);

        when(patient.getNames()).thenReturn(personNames);
        PersonName returnedName = patientDomainWrapper.getPersonName();

        assertSame(personNamePreferred, returnedName);

    }

    @Test
    public void shouldReturnPersonNameWhenThereAreTwoNamesAndNoneOfThemIsPreferred(){
        patient = mock(Patient.class);

        patientDomainWrapper =  new PatientDomainWrapper(patient, emrApiProperties, mock(AdtService.class),
                visitService, mock(EncounterService.class) );

        Set<PersonName> personNames = new HashSet<PersonName>();

        PersonName personNamePreferred = createNonPreferredPersonName("mario", "neissi");
        personNames.add(personNamePreferred);

        PersonName personNameNonPreferred = createNonPreferredPersonName("Ana", "emerson");
        personNames.add(personNameNonPreferred);

        when(patient.getNames()).thenReturn(personNames);
        PersonName returnedName = patientDomainWrapper.getPersonName();

        assertNotNull(returnedName);

    }

    @Test
    public void shouldCalculateCorrectAgeInMonthsForDeceasedPatient() {
        patient.setDead(true);

        Calendar cal = Calendar.getInstance();
        cal.set(2012, 11, 4);
        patient.setBirthdate(cal.getTime());

        cal.set(2013, 2, 1);
        patient.setDeathDate(cal.getTime());

        assertThat(patientDomainWrapper.getAgeInMonths(), is(2));
    }

    @Test
    public void shouldCalculateCorrectAgeInDaysForDeceasedPatient() {
        patient.setDead(true);

        Calendar cal = Calendar.getInstance();
        cal.set(2013, 1, 26);
        patient.setBirthdate(cal.getTime());

        cal.set(2013, 2, 1);
        patient.setDeathDate(cal.getTime());

        assertThat(patientDomainWrapper.getAgeInDays(), is(3));
    }

    private PersonName createPreferredPersonName(String givenName, String familyName) {
        PersonName personNamePreferred = createPersonName(givenName, familyName, true);
        return personNamePreferred;
    }

    private PersonName createNonPreferredPersonName(String givenName, String familyName) {
        PersonName personNameNonPreferred = createPersonName(givenName, familyName, false);
        return personNameNonPreferred;
    }

    private PersonName createPersonName(String givenName, String familyName, boolean preferred) {
        PersonName personNameNonPreferred = new PersonName();
        personNameNonPreferred.setGivenName(givenName);
        personNameNonPreferred.setFamilyName(familyName);
        personNameNonPreferred.setPreferred(preferred);
        return personNameNonPreferred;
    }


}
