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

package org.openmrs.module.emr.order;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Order;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.idgen.validator.LuhnMod10IdentifierValidator;

public class EmrOrderServiceImpl extends BaseOpenmrsService implements EmrOrderService {
	
	@Override
	public void ensureAccessionNumberAssignedTo(Order order) {
		if (order.getAccessionNumber() == null) {
            String accessionNumber = new LuhnMod10IdentifierValidator().getValidIdentifier(order.getOrderId().toString());
            accessionNumber = StringUtils.leftPad(accessionNumber, 10, "0"); // pad the accession number to 10 digits
			order.setAccessionNumber(accessionNumber);
		}
	}
	
}
