package utils;

public class FhirHttpHeader {
  private String name, value;

  private FhirHttpHeader(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public static FhirHttpHeader of(String name, String value) {
    return new FhirHttpHeader(name, value);
  }

  public String name() {
    return name;
  }

  public String value() {
    return value;
  }
}
