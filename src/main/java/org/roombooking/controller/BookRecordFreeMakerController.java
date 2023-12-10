package org.roombooking.controller;

import org.roombooking.service.BookService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;

import java.awt.print.Book;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookRecordFreeMakerController implements Controller {
  private final Service service;
  private final BookService bookService;
  private final FreeMarkerEngine freeMarkerEngine;

  public BookRecordFreeMakerController(Service service, BookService bookService, FreeMarkerEngine freeMarkerEngine) {
    this.service = service;
    this.bookService = bookService;
    this.freeMarkerEngine = freeMarkerEngine;
  }

  @Override
  public void initializeEndpoints() {
    getAllBooks();
  }
  private void getAllBooks(){
    service.get(
            "/",
            (Request request, Response response) -> {
              response.type("text/html; charset=utf-8");
              Map<Book,Book> bookMap=new ConcurrentHashMap<>();
              Map<String,Object> model =new ConcurrentHashMap<>();
              model.put("articles",bookMap);
              return freeMarkerEngine.render(new ModelAndView(model,"index.ftl"));
            }
    );
  }
}
