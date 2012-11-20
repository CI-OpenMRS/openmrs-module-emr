package org.openmrs.module.emr.integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.module.emr.paperrecord.PaperRecordServiceImpl;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.NotTransactional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@SkipBaseSetup
public class PaperRecordServiceIntegrationTest extends BaseModuleContextSensitiveTest {

    private AdministrationService administrationService;

    @Autowired
    private IdentifierSourceService identifierSourceService;

    @Autowired
    private PatientService patientService;

    private PaperRecordService paperRecordService;


    @Before
    public void setUp(){
        paperRecordService = new PaperRecordServiceImpl();
        administrationService = mock(AdministrationService.class);
        ((PaperRecordServiceImpl) paperRecordService).setAdministrationService(administrationService);
        ((PaperRecordServiceImpl) paperRecordService).setIdentifierSourceService(identifierSourceService);
        ((PaperRecordServiceImpl) paperRecordService).setPatientService(patientService);
    }

    @Override
    public Boolean useInMemoryDatabase() {
        return false;
    }

    @Override
    public String getWebappName() {
        return "mirebalais";
    }


    @Test
    @DirtiesContext
    @NotTransactional
    public void shouldCreateTwoDifferentDossierNumbers() throws Exception {
        authenticate();
        when(administrationService.getGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE)).thenReturn("e66645eb-03a8-4991-b4ce-e87318e37566");

        String paperMedicalRecordNumberFor = paperRecordService.createPaperMedicalRecordNumberFor(mock(Patient.class), new Location(15));
     }

}
