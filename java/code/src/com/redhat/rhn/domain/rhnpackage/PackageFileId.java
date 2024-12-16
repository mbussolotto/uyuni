package com.redhat.rhn.domain.rhnpackage;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Embeddable
public class PackageFileId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "package_id", insertable = false, updatable = false)
    private Package pack; // The package_id field
    @ManyToOne
    @JoinColumn(name = "capability_id", insertable = false, updatable = false)
    private PackageCapability capability; // The capability_id field

    public PackageFileId() {}

    public PackageFileId(Package packIn, PackageCapability capabilityIn) {
        pack = packIn;
        capability = capabilityIn;
    }

    // Getters and setters
    public Package getPack() {
        return pack;
    }

    public void setPack(Package pack) {
        this.pack = pack;
    }

    public PackageCapability getCapability() {
        return capability;
    }

    public void setCapability(PackageCapability capability) {
        this.capability = capability;
    }

    // Override equals and hashCode for composite keys (essential for Hibernate)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageFileId that = (PackageFileId) o;
        return Objects.equals(pack, that.pack) && Objects.equals(capability, that.capability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pack, capability);
    }
}