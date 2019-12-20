package model.backboneelements;

import model.datatypes.Period;
import model.elements.Extension;

/**
 * The history of statuses that the EpisodeOfCare has been through (without requiring processing the history of the resource).
 */
public class EpisodeOfCareStatusHistory {

  /**
   * planned | waitlist | active | onhold | finished | cancelled.
   * <p>Required</p>
   * <p>Cardinality: 1..1</p>
   */
  private String status;
  /**
   * Extension for status
   * <p>Cardinality: 0..1</p>
   */
  private Extension _status      ;
  /**
   * The period during this EpisodeOfCare that the specific status applied.
   * <p>Cardinality: 0..1</p>
   */
  private Period period;


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Extension get_status() {
    return _status;
  }

  public void set_status(Extension _status) {
    this._status = _status;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }


}
