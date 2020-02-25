package com.oracolo.fhir.model.backboneelements;

import com.oracolo.fhir.model.BackboneElement;
import com.oracolo.fhir.model.datatypes.CodeableConcept;
import com.oracolo.fhir.model.datatypes.RepeatTiming;
import com.oracolo.fhir.model.elements.Extension;

import java.util.Date;
import java.util.List;

/**
 * Describes the occurrence of an event that may occur multiple times. Timing schedules are used for
 * specifying when events are expected or requested to occur and may also be used to represent the summary of a past
 * or ongoing event. For simplicity, the definitions of Timing components are expressed as 'future' events, but such components
 * can also be used to describe historic or ongoing events.
 */
public class Timing extends BackboneElement {
  /**
   * Identifies specific times when the event occurs.
   * <p>Cardinality: 0..*</p>
   */
  private List<Date> event;
  /**
   * Extension for event
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _event;
  /**
   * A set of rules that describe when the event is scheduled.
   * <p>See rules http://hl7.org/fhir/datatypes-definitions.html#Timing</p>
   * <p>Cardinality: 0..1</p>
   */
  private RepeatTiming repeat;
  /**
   * A code for the timing schedule (or just text in code.text). Some codes such as BID are ubiquitous, but many institutions
   * define their own additional codes. If a code is provided, the code is understood to be a complete statement of whatever
   * is specified in the structured timing data, and either the code or the data may be used to interpret the Timing, with the
   * exception that .repeat.bounds still applies over the code (and is not contained in the code).
   * <p>Cardinality: 0..1</p>
   */
  private CodeableConcept code;
}
