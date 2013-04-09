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

package org.openmrs.module.emr.fragment.controller;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Ideally you pass in a PatientDomainWrapper as the "patient" config parameter. But if you pass in a Patient, then
 * this controller will wrap that for you.
 */
public class PatientHeaderFragmentController {

    public void controller(FragmentConfiguration config,
                           @SpringBean("emrApiProperties") EmrApiProperties emrApiProperties,
                           @SpringBean("baseIdentifierSourceService") IdentifierSourceService identifierSourceService,
                           @FragmentParam("patient") Object patient,
                           @InjectBeans PatientDomainWrapper wrapper) {
        if (patient instanceof Patient) {
            wrapper.setPatient((Patient) patient);
            config.addAttribute("patient", wrapper);
        }

        List<ExtraPatientIdentifierType> extraPatientIdentifierTypes = new ArrayList<ExtraPatientIdentifierType>();

        for (PatientIdentifierType type : emrApiProperties.getExtraPatientIdentifierTypes()) {
            AutoGenerationOption option = identifierSourceService.getAutoGenerationOption(type);
            extraPatientIdentifierTypes.add(new ExtraPatientIdentifierType(type, option != null ? option.isManualEntryEnabled() : true));
        }

        config.addAttribute("extraPatientIdentifierTypes", extraPatientIdentifierTypes);
    }

    public class ExtraPatientIdentifierType {

        private PatientIdentifierType patientIdentifierType;

        private boolean editable = false;

        public ExtraPatientIdentifierType(PatientIdentifierType type, boolean editable) {
            this.patientIdentifierType = type;
            this.editable = editable;
        }

        public PatientIdentifierType getPatientIdentifierType() {
            return patientIdentifierType;
        }

        public void setPatientIdentifierType(PatientIdentifierType patientIdentifierType) {
            this.patientIdentifierType = patientIdentifierType;
        }

        public boolean isEditable() {
            return editable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }
    }

}
