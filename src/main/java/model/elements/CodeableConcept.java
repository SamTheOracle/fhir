package model.elements;

import com.fasterxml.jackson.annotation.JsonInclude;
import model.Element;
import model.datatypes.Coding;

import java.util.ArrayList;
import java.util.List;

/**
 * A concept that may be defined by a formal reference to a terminology or ontology or may be provided by text.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeableConcept extends Element {
  /**
   * A reference to a code defined by a terminology system.
   * <p>Cardinality: 0..*</p>
   */
  private List<Coding> coding;
  /**
   * A human language representation of the concept as seen/selected/uttered by the user who entered
   * the data and/or which represents the intended meaning of the user.
   * <p>Cardinality: 0..1</p>
   */
  private String text;
  /**
   * Extension for text
   * <p>Cardinality: 0..1</p>
   */
  private String _text;

  public List<Coding> getCoding() {
    return coding;
  }

  public CodeableConcept setText(String text) {
    this.text = text;
    return this;
  }

  public String get_text() {
    return _text;
  }

  public void set_text(String _text) {
    this._text = _text;
  }

  public CodeableConcept setCoding(List<Coding> coding) {
    this.coding = coding;
    return this;
  }

  public String getText() {
    return text;
  }

  public CodeableConcept addNewCoding(Coding coding) {
    if (this.coding == null) {
      this.coding = new ArrayList<>();
    }
    this.coding.add(coding);
    return this;
  }
}