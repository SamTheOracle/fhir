package model.datatypes;

import model.Element;

import java.util.Date;

/**
 * This type is for containing or referencing attachments - additional data content defined in other formats.
 * The most common use of this type is to include images or reports in some report format such as PDF. However, it can be used for any data that has a MIME type.
 */
public class Attachment extends Element {
  /**
   * Identifies the type of the data in the attachment and allows a method to be chosen to interpret or render the data.
   * Includes mime type parameters such as charset where appropriate.
   * <p>Cardinality: 0..1</p>
   */
  private String contentType;
  /**
   * The human language of the content. The value can be any valid value according to BCP 47.
   * <p>Cardinality: 0..1</p>
   */
  private String language;
  /**
   * The actual data of the attachment - a sequence of bytes, base64 encoded.
   * <p>Cardinality: 0..*</p>
   */
  private String data;
  /**
   * A location url where the data can be accessed.
   * <p>Cardinality: 0..1</p>
   */
  private String url;
  /**
   * The number of bytes of data that make up this attachment (before base64 encoding, if that is done).
   * <p>Cardinality: 0..1</p>
   */
  private int size;
  /**
   * The calculated hash of the data using SHA-1. Represented using base64.
   * <p>Cardinality: 0..1</p>
   */
  private String hash;
  /**
   * A label or set of text to display in place of the data.
   * <p>Cardinality: 0..1</p>
   */
  private String title;
  /**
   * The date that the attachment was first created.
   * <p>Cardinality: 0..1</p>
   */
  private Date creation;

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }
}
