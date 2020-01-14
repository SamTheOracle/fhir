package com.oracolo.fhir.model.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleQuantity {
  private double decimal;
  private String currency;

  public double getDecimal() {
    return decimal;
  }

  public SimpleQuantity setDecimal(double decimal) {
    this.decimal = decimal;
    return this;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
