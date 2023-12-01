package org.roombooking.entity.id;

import java.util.Objects;

public record UserId(long value) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserId id = (UserId) o;
    return value == id.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
