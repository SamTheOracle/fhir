package com.oracolo.fhir.model.backboneelements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.Reference;
import com.oracolo.fhir.model.elements.Extension;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagnosticReportMedia extends BackboneElement {
  private String comment;
  private Extension _comment;
  private Reference link;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Extension get_comment() {
    return _comment;
  }

  public void set_comment(Extension _comment) {
    this._comment = _comment;
  }

  public Reference getLink() {
    return link;
  }

  public void setLink(Reference link) {
    this.link = link;
  }
}
