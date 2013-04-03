package org.openmrs.module.emr.radiology;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.VisitSummary;
import org.openmrs.module.emr.order.EmrOrderService;
import org.openmrs.module.emr.radiology.db.RadiologyOrderDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public class RadiologyServiceImpl extends BaseOpenmrsService implements RadiologyService {

    private EmrProperties emrProperties;

    private EmrOrderService emrOrderService;

    private EncounterService encounterService;

    private RadiologyOrderDAO radiologyOrderDAO;

    @Transactional
    @Override
    public Encounter placeRadiologyRequisition(EmrContext emrContext, RadiologyRequisition requisition) {
        Encounter encounter = new Encounter();
        encounter.setEncounterType(emrProperties.getRadiologyOrderEncounterType());
        encounter.setProvider(emrProperties.getOrderingProviderEncounterRole(), requisition.getRequestedBy());
        encounter.setPatient(requisition.getPatient());
        encounter.setLocation(emrContext.getSessionLocation());
        VisitSummary activeVisitSummary = emrContext.getActiveVisitSummary();
        if (activeVisitSummary != null) {
            encounter.setVisit(activeVisitSummary.getVisit());
        }

        Date currentDatetime = new Date();
        encounter.setEncounterDatetime(currentDatetime);
        encounter.setDateCreated(currentDatetime);

        for (Concept study : requisition.getStudies()) {
            RadiologyOrder order = new RadiologyOrder();
            order.setExamLocation(requisition.getExamLocation());
            order.setClinicalHistory(requisition.getClinicalHistory());
            order.setConcept(study);
            order.setUrgency(requisition.getUrgency());
            order.setStartDate(new Date());
            order.setOrderType(emrProperties.getRadiologyTestOrderType());
            order.setPatient(requisition.getPatient());
            encounter.addOrder(order);
        }

        // since accession numbers are determined by primary key, we need to first save the encounter
        encounterService.saveEncounter(encounter);
        assignAccessionNumbersToOrders(encounter);
        return encounterService.saveEncounter(encounter);
    }

    private void assignAccessionNumbersToOrders(Encounter encounter) {
        for (Order order : encounter.getOrders()) {
            emrOrderService.ensureAccessionNumberAssignedTo(order);
        }
    }

    @Override
    public void saveRadiologyReport(RadiologyReport radiologyReport) {
        // TODO: implement
    }

    @Override
    public RadiologyOrder getRadiologyOrderByAccessionNumber(String accessionNumber) {
        return radiologyOrderDAO.getRadiologyOrderByAccessionNumber(accessionNumber);
    }

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void setEmrOrderService(EmrOrderService emrOrderService) {
        this.emrOrderService = emrOrderService;
    }

    public void setRadiologyOrderDAO(RadiologyOrderDAO radiologyOrderDAO) {
        this.radiologyOrderDAO = radiologyOrderDAO;
    }
}
