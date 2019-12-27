package com.oracolo.fhir.model.t4c.event;

import com.oracolo.fhir.model.t4c.EventT4C;

import java.text.ParseException;
import java.util.Date;

/**
 * Custom object that mirrors T4C event when type is a procedure
 */
public class EventT4CProcedure extends EventT4C {

  public EventT4CProcedure() {
    super.type = "procedure";
  }

  private EventT4CProcedureContent content;

  public EventT4CProcedureContent getContent() {
    return content;
  }

  public EventT4CProcedure setContent(EventT4CProcedureContent content) {
    this.content = content;
    return this;
  }

  @Override
  public EventT4CProcedure setPlace(String place) {
    super.setPlace(place);
    return this;
  }

  @Override
  public EventT4CProcedure setEventId(int eventId) {
    super.setEventId(eventId);
    return this;
  }


  @Override
  public EventT4CProcedure setDate(Date date) throws ParseException {
    super.setDate(date);
    return this;
  }

  @Override
  public EventT4CProcedure setTime(Date time) throws ParseException {
    super.setTime(time);
    return this;
  }

  @Override
  public EventT4CProcedure setType(String type) {
    super.setType(type);
    return this;
  }


}
