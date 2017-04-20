/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.models;

import java.io.Serializable;

public class IdentifierType extends BaseOpenmrsObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String format;

    private Boolean required = Boolean.FALSE;

    private String formatDescription;

    private Boolean checkDigit = Boolean.FALSE;

    private String validator;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getFormatDescription() {
        return formatDescription;
    }

    public void setFormatDescription(String formatDescription) {
        this.formatDescription = formatDescription;
    }

    public Boolean getCheckDigit() {
        return checkDigit;
    }

    public void setCheckDigit(Boolean checkDigit) {
        this.checkDigit = checkDigit;
    }

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }
}
