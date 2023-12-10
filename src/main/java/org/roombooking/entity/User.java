package org.roombooking.entity;

import org.roombooking.entity.id.UserId;

import java.util.Objects;

public class User {
  private final UserId userId;
  private final String name;
  private final String phoneNumber;
  private final String email;

  public User(UserId userId, String name, String phoneNumber, String email) {
    this.userId = userId;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }

  public UserId getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "User{" +
            "userId=" + userId +
            ", name='" + name + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", email='" + email + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(userId, user.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId);
  }
}
