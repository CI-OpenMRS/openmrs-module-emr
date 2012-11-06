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

package org.openmrs.module.emr.task;

import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emr.EmrContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Simple implementation of TaskDescriptor. Typically you'd instantiate one or more of these as Spring beans in your
 * moduleApplicationContext.xml file.
 */
public class SimpleTaskDescriptor extends BaseTaskDescriptor implements TaskDescriptor {

    private String url;

    private String iconUrl;

    private String tinyIconUrl;

    private double priority = 0d;

    @Override
    public String getUrl(EmrContext context) {
        if (context.getCurrentPatient() != null) {
            return url + (url.indexOf("?") >= 0 ? "&" : "?") + "patientId=" + context.getCurrentPatient().getId();
        } else {
            return url;
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getIconUrl(EmrContext context) {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public String getTinyIconUrl(EmrContext context) {
        return tinyIconUrl;
    }

    public void setTinyIconUrl(String tinyIconUrl) {
        this.tinyIconUrl = tinyIconUrl;
    }

    @Override
    public double getPriority(EmrContext context) {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

}
