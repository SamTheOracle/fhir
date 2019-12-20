package model.elements;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.Element;

/**
 * A human-readable summary of the resource conveying the essential clinical and business information for the resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Narrative extends Element {

  /**
   * The status of the narrative - whether it's entirely generated (from just the defined data or the extensions too),
   * or whether a human authored it and it may contain additional data.
   * <p>See http://hl7.org/fhir/valueset-narrative-status.html</p>
   * <p>Cardinality: 1..1</p>
   */
  private String status;
  /**
   * The actual narrative content, a stripped down version of XHTML.
   * <p>Cardinality: 1..1</p>
   */
  private String div;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDiv() {
    return div;
  }

  public void setDiv(String div) {
    this.div = div;
  }
}
