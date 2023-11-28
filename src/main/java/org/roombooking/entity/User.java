package org.roombooking.entity;

import org.roombooking.entity.id.UserId;

public class User {
  private UserId userId;
  private String name;
  private String phoneNumber;
  private String email;

  public User(UserId userId, String name, String phoneNumber, String email) {
    this.userId = userId;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }
}
