/*
 * Copyright (c) 2025 SUSE LLC
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 */

package com.suse.coco.module.snpguest.execution;

import com.suse.coco.module.snpguest.model.EpycGeneration;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class SNPGuestWrapperVer07Below extends AbstractSNPGuestWrapper {
    /**
     * Default constructor.
     */
    public SNPGuestWrapperVer07Below() {
        super();
    }

    /**
     * Constructor to specify a runtime. For unit testing.
     *
     * @param runtimeIn the runtime used to execute processes
     */
    public SNPGuestWrapperVer07Below(Runtime runtimeIn) {
        super(runtimeIn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessOutput fetchVCEK(EpycGeneration generation, Path certsDir, Path report) throws ExecutionException {
        return executeProcess(
                SNPGUEST.toString(),
                "fetch",
                "vcek",
                "DER",
                generation.name().toLowerCase(),
                certsDir.toString(),
                report.toString()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessOutput verifyAttestation(EpycGeneration generation, Path certsDir, Path report)
            throws ExecutionException {
        return executeProcess(
                SNPGUEST.toString(),
                "verify",
                "attestation",
                certsDir.toString(),
                report.toString()
        );
    }
}
