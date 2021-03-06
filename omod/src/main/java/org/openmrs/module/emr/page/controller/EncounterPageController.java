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

package org.openmrs.module.emr.page.controller;

import org.openmrs.Encounter;
import org.openmrs.module.emr.EmrContext;
import org.openmrs.module.htmlformentry.extension.html.FormEntryHandlerExtension;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
public class EncounterPageController {

    public void get(EmrContext emrContext,
                    @RequestParam("encounterId") Encounter encounter,
                    PageModel pageModel) {

        String displayWith = null;
        if (encounter.getForm() != null) {
            if (new FormEntryHandlerExtension().getFormsModuleCanView().contains(encounter.getForm())) {
                displayWith = "htmlformentry";
            }
        }

        pageModel.addAttribute("patient", emrContext.getCurrentPatient());
        pageModel.addAttribute("encounter", encounter);
        pageModel.addAttribute("displayWith", displayWith);
    }
}
