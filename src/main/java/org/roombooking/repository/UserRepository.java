package org.roombooking.repository;

import org.roombooking.entity.User;
import org.roombooking.entity.id.UserId;


public interface UserRepository {
  UserId generateId();
  void addUser(User user);
  User getUserById(int id);
  User getUserByPhoneNumber(String phoneNumber);
  User getUserByEmail(String email);

}
