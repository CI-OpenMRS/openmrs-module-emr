package org.openmrs.module.emr.fragment.controller.visit;

import org.apache.commons.lang.time.DateFormatUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.visit.ParsedObs;
import org.openmrs.module.emr.visit.ParserEncounterIntoSimpleObjects;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.encounter.EncounterDomainWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitDetailsFragmentController {

    public SimpleObject getVisitDetails(
            @SpringBean("featureToggles") FeatureToggleProperties featureToggleProperties,
            @SpringBean("adminService") AdministrationService administrationService,
            @RequestParam("visitId") Visit visit,
            UiUtils uiUtils,
            EmrContext emrContext) throws ParseException {

        SimpleObject simpleObject = SimpleObject.fromObject(visit, uiUtils, "id", "location", "patient.patientId");

        User authenticatedUser = emrContext.getUserContext().getAuthenticatedUser();

        boolean deleteEncounter = featureToggleProperties.isFeatureEnabled("deleteEncounter");
        boolean canDelete = authenticatedUser.hasPrivilege(EmrConstants.PRIVILEGE_DELETE_ENCOUNTER);

        Date startDatetime = visit.getStartDatetime();
        Date stopDatetime = visit.getStopDatetime();

        simpleObject.put("startDatetime", DateFormatUtils.format(startDatetime, "dd MMM yyyy hh:mm a", emrContext.getUserContext().getLocale()));

        if (stopDatetime!=null){
            simpleObject.put("stopDatetime", DateFormatUtils.format(stopDatetime, "dd MMM yyyy hh:mm a", emrContext.getUserContext().getLocale()));
        } else {
            simpleObject.put("stopDatetime", null);
        }

        List<SimpleObject> encounters = new ArrayList<SimpleObject>();
        simpleObject.put("encounters", encounters);

        String[] datePatterns = { administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT) };
        for (Encounter encounter : visit.getEncounters()) {
            if (!encounter.getVoided()) {
                SimpleObject simpleEncounter = SimpleObject.fromObject(encounter, uiUtils,  "encounterId", "location", "encounterDatetime", "encounterProviders.provider", "voided", "form");

                // manually set the date and time components so we can control how we format them
                simpleEncounter.put("encounterDate", DateFormatUtils.format(encounter.getEncounterDatetime(), "dd MMM yyyy", emrContext.getUserContext().getLocale()));
                simpleEncounter.put("encounterTime", DateFormatUtils.format(encounter.getEncounterDatetime(), "hh:mm a", emrContext.getUserContext().getLocale()));

                EncounterType encounterType = encounter.getEncounterType();
                simpleEncounter.put("encounterType", SimpleObject.create("uuid", encounterType.getUuid(), "name", uiUtils.format(encounterType)));

                if(verifyIfUserHasPermissionToDeleteAnEncounter(encounter, authenticatedUser, canDelete, deleteEncounter)){
                    simpleEncounter.put("canDelete", true);
                }
                encounters.add(simpleEncounter);
            }
        }

        return simpleObject;
    }


    public SimpleObject getEncounterDetails(@RequestParam("encounterId") Encounter encounter,
                                            @SpringBean("emrApiProperties") EmrApiProperties emrApiProperties,
                                            UiUtils uiUtils) {

        ParserEncounterIntoSimpleObjects parserEncounter = new ParserEncounterIntoSimpleObjects(encounter, uiUtils, emrApiProperties);

        ParsedObs parsedObs = parserEncounter.parseObservations(uiUtils.getLocale());
        List<SimpleObject> orders = parserEncounter.parseOrders();

        return SimpleObject.create("patientId", encounter.getPatient().getPatientId(), "observations", parsedObs.getObs(), "orders", orders, "diagnoses", parsedObs.getDiagnoses(), "dispositions", parsedObs.getDispositions());
    }



    public FragmentActionResult deleteEncounter(UiUtils ui,
                                        @SpringBean("featureToggles") FeatureToggleProperties featureToggleProperties,
                                        @RequestParam("encounterId")Encounter encounter,
                                        @SpringBean("encounterService")EncounterService encounterService,
                                        EmrContext emrContext){

       if(encounter!=null){
           User authenticatedUser = emrContext.getUserContext().getAuthenticatedUser();
           boolean deleteEncounter = featureToggleProperties.isFeatureEnabled("deleteEncounter");
           boolean canDelete = authenticatedUser.hasPrivilege(EmrConstants.PRIVILEGE_DELETE_ENCOUNTER);
           if(verifyIfUserHasPermissionToDeleteAnEncounter(encounter, authenticatedUser, canDelete, deleteEncounter)){
               encounterService.voidEncounter(encounter, "delete encounter");
               encounterService.saveEncounter(encounter);
           }else{
               return new FailureResult(ui.message("emr.patientDashBoard.deleteEncounter.notAllowed"));
           }
       }
       return new SuccessResult(ui.message("emr.patientDashBoard.deleteEncounter.successMessage"));
    }

    private boolean verifyIfUserHasPermissionToDeleteAnEncounter(Encounter encounter, User authenticatedUser, boolean canDelete, boolean deleteEncounter) {
        EncounterDomainWrapper encounterDomainWrapper = new EncounterDomainWrapper(encounter);
        boolean userParticipatedInEncounter = encounterDomainWrapper.participatedInEncounter(authenticatedUser);
        return canDelete || (deleteEncounter && userParticipatedInEncounter);
    }
}

