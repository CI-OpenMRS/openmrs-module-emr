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

package org.openmrs.module.emr.domain;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;

import java.util.Date;

public class PaperRecordRequest extends BaseOpenmrsObject {

    public enum Status{ OPEN, ASSIGNED, FULFILLED, CANCELLED }

    private Integer requestId;

    private Patient patient;

    private String identifier;

    private Location medicalRecordLocation;

    private Location requestLocation;

    private User assignee;

    private Status status = Status.OPEN;

    private User creator;

    private Date dateCreated;

    // TODO: we could add a type here at some point if need be

    public PaperRecordRequest() {
    }

    @Override
    public Integer getId() {
        return requestId;
    }

    @Override
    public void setId(Integer requestId) {
       this.requestId = requestId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Location getMedicalRecordLocation() {
        return medicalRecordLocation;
    }

    public void setMedicalRecordLocation(Location medicalRecordLocation) {
        this.medicalRecordLocation = medicalRecordLocation;
    }

    public Location getRequestLocation() {
        return requestLocation;
    }

    public void setRequestLocation(Location requestLocation) {
        this.requestLocation = requestLocation;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}