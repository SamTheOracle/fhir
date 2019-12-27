package com.oracolo.fhir.model.t4c;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@JsonFormat
public abstract class EventT4C {
  protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  protected SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
  /**
   * Progressive integer number, unique for each medical operation
   */
  protected int eventId;
  /**
   * yyyy-mm-dd
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  protected String date;
  /**
   * hh:mm:ss
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")

  protected String time;
  /**
   * <p>“procedure” - manovra/procedura</p>
   * <p>“diagnostic” - esame clinico</p>
   * <p>“drug” - somministrazione di un farmaco</p>
   * <p>“blood-product” - somministrazione di un emoderivato</p>
   * <p>“vital-signs-mon” - parametri vitali da monitor</p>
   * <p>“clinical-variation” - variazione del quadro clinico del paziente</p>
   * <p>“photo” - foto</p>
   * <p>“video” - video</p>
   * <p>“vocal-note” - nota vocale</p>
   * <p>“text-note” - nota testuale</p>
   * <p>“trauma-leader” - scelta del trauma leader</p>
   * <p>“room-in” - evento che rappresenta l’ingresso in una stanza</p>
   * <p>“room-out” - evento che rappresenta l’uscita da una stanza</p>
   * <p>“patient-accepted” - evento che rappresenta l’ingresso del paziente in Shock Room</p>
   * <p>“report-reactivation” - evento che rappresenta il ripristino di un report interrotto precedentemente senza terminazione</p>
   */
  protected String type;

  /**
   * Where the event took place
   */
  protected String place;

  public String getPlace() {
    return place;
  }

  public EventT4C setPlace(String place) {
    this.place = place;
    return this;
  }

  public int getEventId() {
    return eventId;
  }

  public EventT4C setEventId(int eventId) {
    this.eventId = eventId;
    return this;
  }

  public String getDate() {

    return date;
  }

  public EventT4C setDate(Date date) throws ParseException {
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    this.date = dateFormat.format(Date.from(zonedDateTime.toInstant()));

    return this;
  }

  public String getTime() {
    return time;
  }

  public EventT4C setTime(Date time) throws ParseException {
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
    this.time = timeFormat.format(Date.from(zonedDateTime.toInstant()));
    return this;
  }

  public String getType() {
    return type;
  }

  public EventT4C setType(String type) {
    this.type = type;
    return this;
  }


}
