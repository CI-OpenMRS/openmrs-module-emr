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

package org.openmrs.module.emr.consult;

import org.openmrs.module.emr.EmrConstants;
import org.openmrs.util.OpenmrsUtil;

/**
 * Represents a recorded presumed/confirmed diagnosis, and whether it is primary/secondary.
 * (It is straightforward to extend this to include diagnosis certainty, date, and additional ordering.)
 */
public class Diagnosis {

    CodedOrFreeTextAnswer diagnosis;

    Order order;

    public Diagnosis(CodedOrFreeTextAnswer diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Diagnosis(CodedOrFreeTextAnswer diagnosis, Order order) {
        this.diagnosis = diagnosis;
        this.order = order;
    }

    public CodedOrFreeTextAnswer getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(CodedOrFreeTextAnswer diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Diagnosis)) {
            return false;
        }
        Diagnosis other = (Diagnosis) o;
        return OpenmrsUtil.nullSafeEquals(diagnosis, other.getDiagnosis()) &&
                OpenmrsUtil.nullSafeEquals(order, other.getOrder());
    }

    public enum Order {
        PRIMARY(EmrConstants.CONCEPT_CODE_DIAGNOSIS_ORDER_PRIMARY),
        SECONDARY(EmrConstants.CONCEPT_CODE_DIAGNOSIS_ORDER_SECONDARY);

        String codeInEmrConceptSource;

        Order(String codeInEmrConceptSource) {
            this.codeInEmrConceptSource = codeInEmrConceptSource;
        }

        String getCodeInEmrConceptSource() {
            return codeInEmrConceptSource;
        }

        public static Order parseConceptReferenceCode(String code) {
            for (Order order : values()) {
                if (order.getCodeInEmrConceptSource().equals(code)) {
                    return order;
                }
            }
            return null;
        }
    }

}
