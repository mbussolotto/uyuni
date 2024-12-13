package com.redhat.rhn.domain.rhnpackage;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

@Embeddable
public class PackageFileId implements Serializable {

    @JoinColumn(name = "package_id")
    private Long pack; // The package_id field
    @JoinColumn(name = "capability_id")
    private Long capability; // The capability_id field

    // Default constructor
    public PackageFileId() {}

    // Constructor with parameters to initialize the composite key fields
    public PackageFileId(Long pack, Long capability) {
        this.pack = pack;
        this.capability = capability;
    }

    // Getters and setters
    public Long getPack() {
        return pack;
    }

    public void setPack(Long pack) {
        this.pack = pack;
    }

    public Long getCapability() {
        return capability;
    }

    public void setCapability(Long capability) {
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