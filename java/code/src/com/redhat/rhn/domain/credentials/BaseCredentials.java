/*
 * Copyright (c) 2023 SUSE LLC
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

package com.redhat.rhn.domain.credentials;

import com.redhat.rhn.domain.BaseDomainHelper;
import com.redhat.rhn.domain.user.User;
import com.redhat.rhn.domain.user.legacy.UserImpl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Credentials - Java representation of the table SUSECREDENTIALS.
 *
 * This table contains pairs of credentials used for communicating
 * with 3rd party systems, e.g. API usernames and keys.
 */
@Entity
@Table(name = "suseCredentials")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@NamedNativeQuery(
    name = "BaseCredentials.lookupByPaygSSHDataId",
    query = "SELECT sc.* FROM suseCredentials sc WHERE sc.payg_ssh_data_id = :sshDataId",
    resultClass = BaseCredentials.class
)
@NamedNativeQuery(
    name = "BaseCredentials.lookupByPaygSSHDataHostname",
    query = "SELECT sc.* " +
        "FROM suseCredentials sc INNER JOIN susepaygsshdata sd ON sc.payg_ssh_data_id = sd.id " +
        "WHERE sd.host = :hostname",
    resultClass = BaseCredentials.class
)
@NamedQuery(
    name = "BaseCredentials.getLastSCCRefreshDate",
    query = "SELECT MAX(modified) FROM BaseCredentials"
)
public abstract class BaseCredentials extends BaseDomainHelper implements Credentials {

    private Long id;
    private User user;

    /**
     * Get the ID of this object.
     * @return id
     */
    @Override
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "suse_credentials_seq")
    @SequenceGenerator(name = "suse_credentials_seq", sequenceName = "suse_credentials_id_seq", allocationSize = 1)
    public Long getId() {
        return this.id;
    }

    /**
     * Set the ID of this object.
     * @param idIn id
     */
    @Override
    public void setId(Long idIn) {
        this.id = idIn;
    }

    /**
     * Get the associated {@link User}.
     * @return user
     */
    @Override
    @ManyToOne(targetEntity = UserImpl.class)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return this.user;
    }

    /**
     * Set the associated {@link User}.
     * @param userIn user
     */
    @Override
    public void setUser(User userIn) {
        this.user = userIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BaseCredentials that)) {
            return false;
        }

        return new EqualsBuilder()
            .append(user, that.user)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(user)
            .toHashCode();
    }
}
