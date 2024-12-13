/*
 * Copyright (c) 2009--2010 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.redhat.rhn.domain.kickstart.crypto;


import com.redhat.rhn.domain.BaseDomainHelper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * CryptoKeyType - Class representation of the table rhnCryptoKeyType.
 */
@Entity
@Table(name = "rhnCryptoKeyType")
public class CryptoKeyType extends BaseDomainHelper {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cryptoKeyTypeSeq")
    @SequenceGenerator(name = "cryptoKeyTypeSeq", sequenceName = "RHN_CRYPTOKEY_TYPE_ID_SEQ", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "label", nullable = false, length = 32)
    private String label;

    @Column(name = "description", nullable = false, length = 256)
    private String description;

    /**
     * Getter for id
     * @return Long to get
    */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for id
     * @param idIn to set
    */
    public void setId(Long idIn) {
        this.id = idIn;
    }

    /**
     * Getter for label
     * @return String to get
    */
    public String getLabel() {
        return this.label;
    }

    /**
     * Setter for label
     * @param labelIn to set
    */
    public void setLabel(String labelIn) {
        this.label = labelIn;
    }

    /**
     * Getter for description
     * @return String to get
    */
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter for description
     * @param descriptionIn to set
    */
    public void setDescription(String descriptionIn) {
        this.description = descriptionIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof CryptoKeyType castOther)) {
            return false;
        }
        return new EqualsBuilder().append(this.getLabel(), castOther.getLabel()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getLabel()).toHashCode();
    }

}
