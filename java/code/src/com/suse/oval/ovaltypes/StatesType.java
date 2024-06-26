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

package com.suse.oval.ovaltypes;

import com.suse.oval.ovaltypes.linux.DpkginfoState;
import com.suse.oval.ovaltypes.linux.RpminfoState;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * The StatesType is a container for one or more state child elements.
 * Each state provides details about specific characteristics that can be used during an evaluation of an object.
 * <p>
 * Please refer to the description of the state element for more information about an individual state.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatesType", namespace = "http://oval.mitre.org/XMLSchema/oval-definitions-5")
public class StatesType {

    @XmlElements({
        @XmlElement(name = "rpminfo_state",
                namespace = "http://oval.mitre.org/XMLSchema/oval-definitions-5#linux", type = RpminfoState.class),
        @XmlElement(name = "dpkginfo_state",
                namespace = "http://oval.mitre.org/XMLSchema/oval-definitions-5#linux", type = DpkginfoState.class),
        @XmlElement(name = "state",
                namespace = "http://oval.mitre.org/XMLSchema/oval-definitions-5", type = StateType.class)
    })
    protected List<StateType> states;

    /**
     * Gets the contained states.
     * @return the states
     */
    public List<StateType> getStates() {
        if (states == null) {
            states = new ArrayList<>();
        }
        return this.states;
    }

}
