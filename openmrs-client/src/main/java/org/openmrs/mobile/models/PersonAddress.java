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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openmrs.mobile.utilities.StringUtils;

import java.io.Serializable;

public class PersonAddress extends BaseOpenmrsEntity implements Serializable {

    @SerializedName("preferred")
    @Expose
    private Boolean preferred;
    @SerializedName("subCounty")
    @Expose
    private String subCounty;
    @SerializedName("county")
    @Expose
    private String county;

    /**
     * 
     * @return
     *     The preferred
     */
    public Boolean getPreferred() {
        return preferred;
    }

    /**
     * 
     * @param preferred
     *     The preferred
     */
    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }



    /**
     * 
     * @return
     *     The cityVillage
     */
    public String getAddressString()
    {
        String addr="";
        if(StringUtils.notNull(county))
            addr+=county+"\n";
        if(StringUtils.notNull(subCounty))
            addr+=subCounty;
        return addr;
    }

    /**
     * 
     * @return
     *     The subCounty
     */
    public String getSubCounty() {
        return subCounty;
    }

    /**
     * 
     * @param subCounty
     *     The subCounty
     */
    public void setSubCounty(String subCounty) {
        this.subCounty = subCounty;
    }

    /**
     * 
     * @return
     *     The county
     */
    public String getCounty() {
        return county;
    }

    /**
     * 
     * @param county
     *     The county
     */
    public void setCounty(String county) {
        this.county = county;
    }
}
