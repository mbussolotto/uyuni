/*
 * Copyright (c) 2009--2014 Red Hat, Inc.
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
package com.redhat.rhn.domain.server;

import com.redhat.rhn.common.localization.LocalizationService;
import com.redhat.rhn.domain.BaseDomainHelper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * Class representation of the rhnserverhistory table
 * ServerHistoryEvent
 */
@Entity
@Table(name = "rhnserverhistory")
public class ServerHistoryEvent extends BaseDomainHelper {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rhn_event_id_seq")
    @SequenceGenerator(name = "rhn_event_id_seq", sequenceName = "rhn_event_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(name = "summary")
    private String summary = "";

    @Column(name = "details")
    private String details = "";

    /**
     * Constructor for ServerHistoryEvent
     */
    public ServerHistoryEvent() {

    }

    /**
     * Getter for details
     * @return the details of the event
     */
    public String getDetails() {
        return details;
    }

    /**
     * Set the details of an event
     * @param detailsIn the details to set
     */
    public void setDetails(String detailsIn) {
        this.details = detailsIn;
    }

    /**
     * Getter for id
     * @return the id of the event
     */
    public Long getId() {
        return id;
    }


    /**
     * Set the id of an event
     * @param idIn the id to set
     */
    public void setId(Long idIn) {
        this.id = idIn;
    }

    /**
     * Getter for server
     * @return the server associated with the event
     */
    public Server getServer() {
        return server;
    }

    /**
     * associate a server with the event
     * @param serverIn the Server to associate
     */
    public void setServer(Server serverIn) {
        this.server = serverIn;
    }

    /**
     * Getter for summary
     * @return the summary of the event
     */
    public String getSummary() {
        return summary;
    }

    /**
     * sets the summary of the event
     * @param summaryIn the summary to set
     */
    public void setSummary(String summaryIn) {
        this.summary = summaryIn;
    }

    /**
     * @return Returns date of creating of the event
     */
    @Override
    public Date getCreated() {
        return super.getCreated();
    }

    /**
     * @param createdIn Date of creation of the event
     */
    @Override
    public void setCreated(Date createdIn) {
        super.setCreated(createdIn);
    }

    /**
     * @param createdIn Date of creation of the event
     */
    public void setCreated(String createdIn) {
        if (createdIn == null) {
            super.setCreated(null);
        }
        else {
            try {
                super.setCreated(new SimpleDateFormat(
                        LocalizationService.RHN_DB_DATEFORMAT).parse(createdIn));
            }
            catch (ParseException e) {
                throw new IllegalArgumentException("lastCheckin must be of the: [" +
                        LocalizationService.RHN_DB_DATEFORMAT + "] it was: " +
                        createdIn);
            }
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        ServerHistoryEvent that = (ServerHistoryEvent) obj;
        return new EqualsBuilder().
                append(this.getId(), that.getId()).
                append(this.getServer(), that.getServer()).
                isEquals();
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

       HashCodeBuilder builder = new HashCodeBuilder();
       builder.append(this.getId());
       builder.append(this.getServer());
       return builder.toHashCode();
    }
}

