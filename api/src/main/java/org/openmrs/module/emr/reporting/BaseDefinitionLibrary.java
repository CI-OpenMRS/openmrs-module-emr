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

package org.openmrs.module.emr.reporting;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.definition.persister.DefinitionPersister;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementations of this class can conveniently implement on-the-fly-created reporting definitions, with inline
 * documentation on how they'll behave.
 * <br/>
 * Remember to annotate implementations as @Handler(supports = T.class)
 */
public abstract class BaseDefinitionLibrary<T extends Definition> implements DefinitionPersister<T> {

    public abstract String getUuidPrefix();

    @Override
    public T getDefinitionByUuid(String uuid) {
        if (uuid.startsWith(getUuidPrefix())) {
            String lookFor = uuid.substring(getUuidPrefix().length());
            return findAndInvokeMethod(lookFor);
        } else {
            return null;
        }
    }

    private T findAndInvokeMethod(String annotationValue) {
        Method method = findMethod(annotationValue);
        if (method == null) {
            return null;
        }
        return buildDefinition(method);
    }

    private T buildDefinition(Method method) {
        try {
            @SuppressWarnings("unchecked")
            T definition = (T) method.invoke(this);

            DocumentedDefinition documented = method.getAnnotation(DocumentedDefinition.class);
            definition.setUuid(documented.value());
            definition.setName(documented.name());
            definition.setDescription(documented.definition());
            return definition;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Method findMethod(String annotationValue) {
        for (Method candidate : this.getClass().getMethods()) {
            DocumentedDefinition annotation = candidate.getAnnotation(DocumentedDefinition.class);
            if (annotation != null && annotation.value().equals(annotationValue)) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public List<T> getAllDefinitions(boolean includeRetired) {
        List<T> definitions = new ArrayList<T>();
        for (Method candidate : this.getClass().getMethods()) {
            DocumentedDefinition annotation = candidate.getAnnotation(DocumentedDefinition.class);
            if (annotation != null) {
                definitions.add(buildDefinition(candidate));
            }
        }
        return definitions;
    }

    @Override
    public int getNumberOfDefinitions(boolean includeRetired) {
        int count = 0;
        for (Method candidate : this.getClass().getMethods()) {
            DocumentedDefinition annotation = candidate.getAnnotation(DocumentedDefinition.class);
            if (annotation != null) {
                ++count;
            }
        }
        return count;
    }

    @Override
    public T getDefinition(Integer id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> getDefinitions(String name, boolean exactMatchOnly) throws APIException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T saveDefinition(T definition) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void purgeDefinition(T definition) {
        throw new UnsupportedOperationException();
    }

    protected <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
        if (parameterizable == null) {
            throw new NullPointerException("Programming error: missing parameterizable");
        }
        if (mappings == null) {
            mappings = ""; // probably not necessary, just to be safe
        }
        return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
    }

    protected <T extends Parameterizable> Mapped<T> noMappings(T parameterizable) {
        if (parameterizable == null) {
            throw new NullPointerException("Programming error: missing parameterizable");
        }
        return new Mapped<T>(parameterizable, Collections.<String, Object>emptyMap());
    }

}
