package org.roombooking.repository;

import org.roombooking.entity.User;


public interface UserRepository {
  long generateId();
  void addUser(User user);
  User getUserById(int id);
  User getUserByPhoneNumber(String phoneNumber);
  User getUserByEmail(String email);

}
