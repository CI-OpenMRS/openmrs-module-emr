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

package org.openmrs.module.emr.printer;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.LocationService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.openmrs.module.emr.EmrConstants.LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER;

public class PrinterServiceComponentTest extends BaseModuleContextSensitiveTest {


   @Autowired
   private PrinterService printerService;

   @Autowired
   private LocationService locationService;

   @Before
   public void beforeAllTests() throws Exception {
       executeDataSet("printerServiceComponentTestDataset.xml");
   }

   @Test
   public void testSavePrinter() {

       Printer printer = new Printer();
       printer.setName("Another Test Printer");
       printer.setIpAddress("192.1.1.8");
       printer.setType(Printer.Type.ID_CARD);

       printerService.savePrinter(printer);

       List<Printer> printers = printerService.getAllPrinters();

       // there is already a test printer in the dataset, so there should be two printers now
       Assert.assertEquals(2, printers.size());

       // make sure the audit fields have been set
       Assert.assertNotNull(printer.getDateCreated());
       Assert.assertNotNull(printer.getCreator());
       Assert.assertNotNull(printer.getUuid());
   }

    @Test
    public void testShouldReturnTrueIfAnotherPrinterAlreadyHasIpAddressAssigned() {

        Printer differentPrinter = new Printer();
        differentPrinter.setName("Another printer");
        differentPrinter.setIpAddress("192.1.1.2");   // printer in test dataset has this ip
        differentPrinter.setType(Printer.Type.LABEL);

        Assert.assertTrue(printerService.isIpAddressAllocatedToAnotherPrinter(differentPrinter));

    }

    @Test
    public void testShouldReturnFalseIfAnotherPrinterDoesNotHaveIpAddressAssigned() {

        Printer differentPrinter = new Printer();
        differentPrinter.setName("Another printer");
        differentPrinter.setIpAddress("192.1.1.8");
        differentPrinter.setType(Printer.Type.LABEL);

        Assert.assertFalse(printerService.isIpAddressAllocatedToAnotherPrinter(differentPrinter));

    }

    @Test
    public void testGetPrinterByName() {

        Printer printer = printerService.getPrinterByName("Test Printer");
        Assert.assertEquals(new Integer(1), printer.getId());
        Assert.assertEquals(new Integer(2), printer.getPhysicalLocation().getId());
        Assert.assertEquals("192.1.1.2", printer.getIpAddress());
        Assert.assertEquals("2", printer.getPort());
        Assert.assertEquals("LABEL", printer.getType().name());

    }

    @Test
    public void testShouldReturnTrueIfAnotherPrinterAlreadyHasSameName() {

        Printer differentPrinter = new Printer();
        differentPrinter.setName("Test Printer");
        differentPrinter.setIpAddress("192.1.1.9");
        differentPrinter.setType(Printer.Type.LABEL);

        Assert.assertTrue(printerService.isNameAllocatedToAnotherPrinter(differentPrinter));
    }

    @Test
    public void testShouldReturnFalseIfAnotherPrinterDoesNotHaveSameName() {

        Printer differentPrinter = new Printer();
        differentPrinter.setName("Test Printer With Different Name");
        differentPrinter.setIpAddress("192.1.1.9");
        differentPrinter.setType(Printer.Type.LABEL);

        Assert.assertFalse(printerService.isNameAllocatedToAnotherPrinter(differentPrinter));
    }

    @Test
    public void shouldSetDefaultLabelPrinterForLocation() {

        Location location = locationService.getLocation(2);
        Printer printer = printerService.getPrinterById(1);

        printerService.setDefaultPrinter(location, printer);

        Printer fetchedPrinter = printerService.getDefaultPrinter(location, Printer.Type.LABEL);
        Assert.assertEquals(printer, fetchedPrinter);
    }

    @Test
    public void shouldGetDefaultLabelPrinterForLocation() {

        Location location = locationService.getLocation(3);
        Printer printer = printerService.getPrinterById(1);  // this has been set as the default printer for location 3 in dataset

        Printer fetchedPrinter = printerService.getDefaultPrinter(location, Printer.Type.LABEL);
        Assert.assertEquals(printer, fetchedPrinter);

    }

    @Test(expected = APIException.class)
    public void shouldNotAllowMismatchedLocationAttributeTypeandPrinterType() {

        Location location = locationService.getLocation(2);

        Printer printer = new Printer();
        printer.setName("Test Label Printer");
        printer.setIpAddress("192.1.1.9");
        printer.setType(Printer.Type.ID_CARD);

        LocationAttributeType defaultIdCardPrinter = locationService.getLocationAttributeTypeByUuid(LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER.get("LABEL"));

        LocationAttribute attribute = new LocationAttribute();
        attribute.setAttributeType(defaultIdCardPrinter);
        attribute.setValue(printer);

        location.addAttribute(attribute);

        locationService.saveLocation(location);
    }
}
