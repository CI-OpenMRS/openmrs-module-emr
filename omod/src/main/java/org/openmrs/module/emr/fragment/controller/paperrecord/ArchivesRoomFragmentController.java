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

package org.openmrs.module.emr.fragment.controller.paperrecord;

import org.openmrs.module.emr.paperrecord.PaperRecordRequest;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

public class ArchivesRoomFragmentController {

    public FragmentActionResult markPaperRecordRequestAsSent(@RequestParam(value = "identifier", required = true) String identifier,
                                                             @SpringBean("paperRecordService") PaperRecordService paperRecordService,
                                                             UiUtils ui) {

        PaperRecordRequest paperRecordRequest = paperRecordService.getActivePaperRecordRequestByIdentifier(identifier);

        if (paperRecordRequest == null) {
            return new FailureResult("This paper record has not currently been requested");
        }


        return new SuccessResult(identifier);

    }


}
