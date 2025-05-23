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
package com.redhat.rhn.frontend.action.configuration.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.redhat.rhn.domain.access.AccessGroupFactory;
import com.redhat.rhn.domain.config.ConfigChannel;
import com.redhat.rhn.domain.config.ConfigChannelType;
import com.redhat.rhn.domain.config.ConfigFile;
import com.redhat.rhn.domain.config.ConfigRevision;
import com.redhat.rhn.domain.server.Server;
import com.redhat.rhn.testing.ConfigTestUtils;
import com.redhat.rhn.testing.RhnMockStrutsTestCase;
import com.redhat.rhn.testing.UserTestUtils;

import org.junit.jupiter.api.Test;

/**
 * LocalRevisionDeployActionTest
 */
public class LocalRevisionDeployActionTest extends RhnMockStrutsTestCase {

    @Test
    public void testExecute() throws Exception {
        UserTestUtils.addAccessGroup(user, AccessGroupFactory.CONFIG_ADMIN);

        ConfigChannel cc = ConfigTestUtils.createConfigChannel(user.getOrg());
        cc.setConfigChannelType(ConfigChannelType.local());
        Server srv = ConfigTestUtils.giveUserChanAccess(user, cc);
        ConfigFile cf = ConfigTestUtils.createConfigFile(cc);
        ConfigRevision cr = ConfigTestUtils.createConfigRevision(cf);

        setRequestPathInfo("/configuration/file/RevisionDeploy");
        addRequestParameter("cfid", cr.getConfigFile().getId().toString());
        addRequestParameter("crid", cr.getId().toString());
        addRequestParameter("sid", srv.getId().toString());
        actionPerform();
        assertEquals(srv.getName(), request.getAttribute("system"));
    }
}
