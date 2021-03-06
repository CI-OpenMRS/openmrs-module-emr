package org.openmrs.module.emr.page.controller.printer;

import org.openmrs.module.emrapi.printer.PrinterService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class ManagePrintersPageController {

    public void get(PageModel model, @SpringBean("printerService") PrinterService printerService) {
        model.addAttribute("printers", printerService.getAllPrinters());
    }

}
