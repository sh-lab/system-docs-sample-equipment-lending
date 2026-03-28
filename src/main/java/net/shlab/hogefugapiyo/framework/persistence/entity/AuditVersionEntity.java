package net.shlab.hogefugapiyo.framework.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import net.shlab.hogefugapiyo.framework.core.marker.Versioned;

@MappedSuperclass
public abstract class AuditVersionEntity extends AuditEntity implements Versioned {

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
