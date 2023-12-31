package org.roombooking.entity.id;

import java.util.Objects;

public record AuditoryId(long value) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuditoryId id = (AuditoryId) o;
    return value == id.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

}
