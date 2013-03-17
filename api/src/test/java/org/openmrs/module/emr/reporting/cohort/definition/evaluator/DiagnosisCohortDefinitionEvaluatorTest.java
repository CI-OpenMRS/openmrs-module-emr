/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.emr.reporting.cohort.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.TestUtils;
import org.openmrs.module.emr.consult.Diagnosis;
import org.openmrs.module.emr.consult.DiagnosisMetadata;
import org.openmrs.module.emr.reporting.cohort.definition.DiagnosisCohortDefinition;
import org.openmrs.module.emr.test.builder.ObsBuilder;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.emr.test.ReportingMatchers.hasExactlyIds;

/**
 *
 */
public class DiagnosisCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    EmrProperties emrProperties;

    @Autowired
    ConceptService conceptService;

    @Autowired
    PatientService patientService;

    @Autowired
    DiagnosisCohortDefinitionEvaluator evaluator;

    DiagnosisMetadata dmd;

    @Before
    public void setUp() throws Exception {
        dmd = TestUtils.setupDiagnosisMetadata(conceptService, emrProperties);
    }

    @Test
    public void testEvaluateSimple() throws Exception {
        DiagnosisCohortDefinition cd = new DiagnosisCohortDefinition();
        EvaluatedCohort cohort = evaluator.evaluate(cd, new EvaluationContext());
        assertThat(cohort.size(), is(0));
        assertThat((DiagnosisCohortDefinition) cohort.getDefinition(), is(cd));

        createDiagnosisObs();

        cd = new DiagnosisCohortDefinition();
        cohort = evaluator.evaluate(cd, new EvaluationContext());
        assertThat(cohort, hasExactlyIds(2, 6, 7));

        cd.setOnOrAfter(DateUtil.parseDate("2013-01-02", "yyyy-MM-dd"));
        cd.setOnOrBefore(DateUtil.parseDate("2013-01-02", "yyyy-MM-dd"));

        cohort = evaluator.evaluate(cd, new EvaluationContext());
        assertThat(cohort, hasExactlyIds(2));
    }

    @Test
    public void testEvaluateByOrder() throws Exception {
        createDiagnosisObs();

        DiagnosisCohortDefinition cd = new DiagnosisCohortDefinition();
        cd.setDiagnosisOrder(Diagnosis.Order.SECONDARY);
        EvaluatedCohort cohort = evaluator.evaluate(cd, new EvaluationContext());
        assertThat(cohort, hasExactlyIds(2));
    }

    @Test
    public void testEvaluateByCoded() throws Exception {
        createDiagnosisObs();

        DiagnosisCohortDefinition cd = new DiagnosisCohortDefinition();
        cd.setCodedDiagnoses(Arrays.asList(conceptService.getConcept(9)));
        EvaluatedCohort cohort = evaluator.evaluate(cd, new EvaluationContext());
        assertThat(cohort, hasExactlyIds(6));
    }

    @Test
    public void testEvaluateByAnyCoded() throws Exception {
        createDiagnosisObs();

        DiagnosisCohortDefinition cd = new DiagnosisCohortDefinition();
        cd.setIncludeAllCodedDiagnoses(true);
        EvaluatedCohort cohort = evaluator.evaluate(cd, new EvaluationContext());
        assertThat(cohort, hasExactlyIds(2, 6));
    }

    @Test
    public void testEvaluateByAnyNonCoded() throws Exception {
        createDiagnosisObs();

        DiagnosisCohortDefinition cd = new DiagnosisCohortDefinition();
        cd.setIncludeAllNonCodedDiagnoses(true);
        EvaluatedCohort cohort = evaluator.evaluate(cd, new EvaluationContext());
        assertThat(cohort, hasExactlyIds(2, 7));
    }

    private void createDiagnosisObs() {
        Patient patient = patientService.getPatient(2);
        buildDiagnosis(patient, "2013-01-02", Diagnosis.Order.PRIMARY, conceptService.getConcept(11)).save();
        buildDiagnosis(patient, "2013-01-02", Diagnosis.Order.SECONDARY, "Headache").save();

        Patient otherPatient = patientService.getPatient(7);
        buildDiagnosis(otherPatient, "2013-03-11", Diagnosis.Order.PRIMARY, "Cough").save();

        Patient thirdPatient = patientService.getPatient(6);
        buildDiagnosis(thirdPatient, "2013-03-11", Diagnosis.Order.PRIMARY, conceptService.getConcept(9)).save();
    }

    private ObsBuilder buildDiagnosis(Patient patient, String dateYmd, Diagnosis.Order order, Object diagnosis) {
        ObsBuilder builder = new ObsBuilder()
            .setPerson(patient)
                .setObsDatetime(DateUtil.parseDate(dateYmd, "yyyy-MM-dd"))
                .setConcept(dmd.getDiagnosisSetConcept())
                .addMember(dmd.getDiagnosisOrderConcept(), dmd.getConceptFor(order));
        if (diagnosis instanceof Concept) {
            builder.addMember(dmd.getCodedDiagnosisConcept(), (Concept) diagnosis);
        } else if (diagnosis instanceof String) {
            builder.addMember(dmd.getNonCodedDiagnosisConcept(), (String) diagnosis);
        } else {
            throw new IllegalArgumentException("Diagnosis value must be a Concept or String");
        }
        return builder;
    }

}
