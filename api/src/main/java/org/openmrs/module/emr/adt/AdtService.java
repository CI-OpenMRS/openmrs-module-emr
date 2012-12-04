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

package org.openmrs.module.emr.adt;

import org.openmrs.*;
import org.openmrs.api.OpenmrsService;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 * API methods related to Admission, Discharge, and Transfer
 *
 * Since patients frequently leave the facility without having any formal electronic check-out process, we ensure that
 * old stale visits are automatically closed, even if they are never intentionally stopped. Our business logic is built
 * on the idea of <em>active</em> visits, per #isActive(Visit, Date). A visit with stopDatetime==null is not necessarily
 * active from our perspective. Non-active visits are liable to be stopped at any time.
 *
 * Visits are only allowed to happen at locations tagged with the EmrConstants.LOCATION_TAG_SUPPORTS_VISITS tag. When
 * you pass a location without that tag to a service method, we look from that location and above in the location
 * hierarchy until we find a location with this tag. (This allows you to configure the setup such that doing a check-in
 * at Outpatient Department creates a visit at its parent, with an Encounter at the location itself.)
 * </pre>
 */
public interface AdtService extends OpenmrsService {

    /**
     * Gets the patient's <em>active</em> visit at the given location, or null, if none exists.
     * If the patient has any non-stopped visits that are not active, they are stopped as a side-effect.
     * @param patient
     * @param department
     * @return
     */
    Visit getActiveVisit(Patient patient, Location department);

    /**
     * Like #getActiveVisit, but if the patient has no active visit, one is created (and persisted).
     * (This has the same side-effects as #getActiveVisit.)
     * The visit's location will be a valid visit location per our business logic.
     *
     * @param patient
     * @param department
     * @return
     */
    Visit ensureActiveVisit(Patient patient, Location department);

    /**
     * Our business logic is that a visit has ended if it has no recent encounter.
     * @see org.openmrs.module.emr.EmrProperties#getVisitExpireHours()
     * @return whether we think this visit has ended, according to our business logic
     */
    boolean isActive(Visit visit);

    /**
     * Creates a "check-in" encounter for the given patient, at the location where, and adds it to the active visit.
     * (This method calls ensureActiveVisit.)
     *
     * @param patient required
     * @param where required (must either support visits, or have an ancestor location that does)
     * @param checkInClerk optional (defaults to Provider for currently-authenticated user)
     * @param obsForCheckInEncounter optional
     * @param ordersForCheckInEncounter optional
     * @param newVisit says whether create a new visit or not
     * @return the encounter created (with EncounterService.saveEncounter already called on it)
     */
    Encounter checkInPatient(Patient patient, Location where, Provider checkInClerk, List<Obs> obsForCheckInEncounter,
                             List<Order> ordersForCheckInEncounter, boolean newVisit);

    /**
     * Looks at this location, then its ancestors in the location hierarchy, to find a location tagged with
     * @see org.openmrs.module.emr.EmrConstants#LOCATION_TAG_SUPPORTS_VISITS
     * @param location
     * @return location, or its closest ancestor that supports visits
     * @throws IllegalArgumentException if neither location nor its ancestors support visits
     */
    Location getLocationThatSupportsVisits(Location location);

    /**
     * @see org.openmrs.module.emr.EmrConstants#LOCATION_TAG_SUPPORTS_VISITS
     * @return all locations that are allowed to have visits assigned to them
     */
    List<Location> getAllLocationsThatSupportVisits();

    /**
     * @param visit
     * @param location
     * @param when
     * @return whether the given visit is suitable to store a patient interaction at the given location and date
     */
    boolean isSuitableVisit(Visit visit, Location location, Date when);
    
    /**
     * Gets all currently-active visits (per our business logic) at the given location or any of its children
	 * @param location
	 * @return
	 */
	List<VisitSummary> getActiveVisitSummaries(Location location);
	
	/**
	 * 
	 * Gets a summary of the active visit for a given patient at a given location
	 * 
	 * @param patient
	 * @param location
	 * @return
	 */
	VisitSummary getActiveVisitSummary(Patient patient, Location location);

    /**
     * If any currently-open visits are now inactive per our business logic, close them
     */
    void closeInactiveVisits();

    /**
     * @param patient
     * @return the most recent encounter for the given patient
     */
    Encounter getLastEncounter(Patient patient);

    /**
     * @param patient
     * @return the number of non-voided encounters this patient has had
     */
    int getCountOfEncounters(Patient patient);

    /**
     * @param patient
     * @return the number of non-voided visits this patient has had
     */
    int getCountOfVisits(Patient patient);

    /**
     * @param v1
     * @param v2
     * @return true if both visits are in overlapping locations, and have overlapping datetime ranges
     */
    boolean visitsOverlap(Visit v1, Visit v2);

    /**
     * Merges patients using the underlying OpenMRS core mechanism, but applying extra business logic:
     * <ul>
     *     <li>Any two visits that overlap will be joined together into one</li>
     * </ul>
     *
     * @see #visitsOverlap(org.openmrs.Visit, org.openmrs.Visit)
     * @see org.openmrs.api.PatientService#mergePatients(org.openmrs.Patient, org.openmrs.Patient)
     * @param preferred
     * @param notPreferred
     */
    void mergePatients(Patient preferred, Patient notPreferred);


    Encounter createCheckinInRetrospective(Patient patient, Location location, Provider clerk, Obs paymentReason, Obs paymentAmount, Date checkinDate);
}
