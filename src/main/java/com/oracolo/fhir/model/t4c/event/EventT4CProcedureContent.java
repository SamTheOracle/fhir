package com.oracolo.fhir.model.t4c.event;

/**
 * Represents the content information about a EventT4cProcedure
 */
public class EventT4CProcedureContent {
  /**
   * <p>l’identificatore univoco della procedura.</p>
   * <p>Possibili valori: [“intubation”, “supraglottic-presidium”, “fibroscopy”,
   * “tracheostomy”, “drainage”, “chest-tube”, “infuser”, “intraosseous”, “arterial-catheter”, “pelvic-binder”,
   * “fixator”, “tourniquet”, “reboa”, “thoracotomy”, “gastric-probe”, “bladder-foley”, “als”]</p>
   */
  private String procedureId;
  /**
   * Descrizione della manovra (da utilizzare nella voce del report!!!)
   */
  private String procedureDescription;
  /**
   * <p>Discrimina tra procedure one-shot e procedure tempo dipendenti</p>
   * <p>Possibili valori: [“one-shot”, “time-dependent”]</p>
   */
  private String procedureType;
  /**
   * <p>NOTA BENE: presente solo nel caso in cui al campo precedente (procedureType) sia stato specificato
   * il valore  “time-dependent”.</p>
   * <p>Può assumere solo due valori diversi [“start”, “end”] per indicare rispettivamente che si
   * tratta dell’evento di inizio della procedura o dell’evento di fine della procedura</p>
   */
  private String event;
  /**
   * There might be these parameters depending on the procedureId;
   */
  private boolean difficultAirway, inhalation, videolaringo, frova, right, left;

  public String getProcedureId() {
    return procedureId;
  }

  public EventT4CProcedureContent setProcedureId(String procedureId) {
    this.procedureId = procedureId;
    return this;
  }

  public String getProcedureDescription() {
    return procedureDescription;
  }

  public EventT4CProcedureContent setProcedureDescription(String procedureDescription) {
    this.procedureDescription = procedureDescription;
    return this;
  }

  public String getProcedureType() {
    return procedureType;
  }

  public EventT4CProcedureContent setProcedureType(String procedureType) {
    this.procedureType = procedureType;
    return this;
  }

  public String getEvent() {
    return event;
  }

  public EventT4CProcedureContent setEvent(String event) {
    this.event = event;
    return this;
  }

  public boolean isDifficultAirway() {
    return difficultAirway;
  }

  public EventT4CProcedureContent setDifficultAirway(boolean difficultAirway) {
    this.difficultAirway = difficultAirway;
    return this;
  }

  public boolean isInhalation() {
    return inhalation;
  }

  public EventT4CProcedureContent setInhalation(boolean inhalation) {
    this.inhalation = inhalation;
    return this;
  }

  public boolean isVideolaringo() {
    return videolaringo;
  }

  public EventT4CProcedureContent setVideolaringo(boolean videolaringo) {
    this.videolaringo = videolaringo;
    return this;

  }

  public boolean isFrova() {
    return frova;
  }

  public EventT4CProcedureContent setFrova(boolean frova) {
    this.frova = frova;
    return this;

  }

  public boolean isRight() {
    return right;
  }

  public EventT4CProcedureContent setRight(boolean right) {
    this.right = right;
    return this;

  }

  public boolean isLeft() {
    return left;
  }

  public EventT4CProcedureContent setLeft(boolean left) {
    this.left = left;
    return this;

  }
}
