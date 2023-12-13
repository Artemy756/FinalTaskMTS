package org.roombooking;

import org.roombooking.controller.Controller;

import java.util.List;

public class Aplication {
  private final List<Controller> controllers;

  public Aplication(List<Controller> controllers) {
    this.controllers = controllers;
  }

  public void start() {
    for (Controller controller : controllers) {
      controller.initializeEndpoints();
    }
  }
}
