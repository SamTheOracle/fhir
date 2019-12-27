package com.oracolo.fhir.model.elements;

import com.oracolo.fhir.model.Element;
import com.oracolo.fhir.model.datatypes.Period;

import java.util.Date;
import java.util.List;

/**
 * Either a duration for the length of the timing schedule, a range of possible length,
 * or outer bounds for start and/or end limits of the timing schedule.
 */
public class RepeatTiming extends Element {
  /**
   * Either a duration for the length of the timing schedule, a range of possible length, or outer bounds
   * for start and/or end limits of the timing schedule.
   * <p>Cardinality: 0..1</p>
   */
  private Duration boundDuration;

  /**
   * Either a duration for the length of the timing schedule, a range of possible length, or outer bounds
   * for start and/or end limits of the timing schedule.
   * <p>Cardinality: 0..1</p>
   */
  private Range boundRange;
  /**
   * Either a duration for the length of the timing schedule, a range of possible length, or outer bounds
   * for start and/or end limits of the timing schedule.
   * <p>Cardinality: 0..1</p>
   */
  private Period boundPeriod;
  /**
   * A total count of the desired number of repetitions across the duration of the entire timing specification.
   * If countMax is present, this element indicates the lower bound of the allowed range of count values.
   * <p>Must be positive</p>
   * <p>Cardinality: 0..1</p>
   */
  private int count;
  /**
   * Extension for count
   * <p>Cardinality: 0..1</p>
   */
  private Extension _count;
  /**
   * If present, indicates that the count is a range - so to perform the action between [count] and [countMax] times.
   * <p>Must be positive</p>
   * <p>Cardinality: 0..1</p>
   */
  private int countMax;
  /**
   * Extension for
   * <p>Cardinality: 0..1</p>
   */
  private Extension _countMax;
  /**
   * How long this thing happens for when it happens. If durationMax is present, this element indicates
   * the lower bound of the allowed range of the duration.
   * <p>Cardinality: 0..1</p>
   */
  private double duration;
  /**
   * Extension for duration
   * <p>Cardinality: 0..1</p>
   */
  private Extension _duration;
  /**
   * If present, indicates that the duration is a range - so to perform the action between [duration] and [durationMax] time length.
   * <p>Cardinality: 0..1</p>
   */
  private double durationMax;
  /**
   * Extension for durationMax
   * <p>Cardinality: 0..1</p>
   */
  private Extension _durationMax;
  /**
   * The units of time for the duration, in UCUM units.
   * <p>See http://hl7.org/fhir/valueset-units-of-time.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String durationUnit;

  /**
   * Extension for durationUnit
   * <p>Cardinality: 0..1</p>
   */
  private Extension _durationUnit;
  /**
   * The number of times to repeat the action within the specified period. If frequencyMax is present,
   * this element indicates the lower bound of the allowed range of the frequency.
   * <p>Must be positive</p>
   * <p>Cardinality: 0..1</p>
   */
  private int frequency;
  /**
   * Extension for frequency
   * <p>Cardinality: 0..1</p>
   */
  private Extension _frequency;
  /**
   * If present, indicates that the frequency is a range - so to repeat between [frequency] and [frequencyMax]
   * times within the period or period range.
   * <p>Must be positive</p>
   * <p>Cardinality: 0..1</p>
   */
  private int frequencyMax;
  /**
   * Extension for frequencyMax
   * <p>Cardinality: 0..1</p>
   */
  private Extension _frequencyMax;
  /**
   * Indicates the duration of time over which repetitions are to occur; e.g. to express "3 times per day", 3 would be
   * the frequency and "1 day" would be the period. If periodMax is present, this element indicates the lower bound of
   * the allowed range of the period length.
   * <p>Cardinality: 0..1</p>
   */
  private double period;
  /**
   * Extension for period
   * <p>Cardinality: 0..1</p>
   */
  private Extension _period;
  /**
   * If present, indicates that the period is a range from [period] to [periodMax],
   * allowing expressing concepts such as "do this once every 3-5 days.
   * <p>Cardinality: 0..1</p>
   */
  private double periodMax;
  /**
   * Extension for periodMax
   * <p>Cardinality: 0..1</p>
   */
  private Extension _periodMax;
  /**
   * The units of time for the period in UCUM units.
   * <p>See http://hl7.org/fhir/valueset-units-of-time.html</p>
   * <p>Cardinality: 0..1</p>
   */
  private String periodUnit;
  /**
   * Extension for periodUnit;
   * <p>Cardinality: 0..1</p>
   */
  private Extension _periodUnit;
  /**
   * The number of minutes from the event. If the event code does not indicate whether
   * the minutes is before or after the event, then the offset is assumed to be after the event.
   * <p>Cardinality: 0..1</p>
   */
  private int offset;
  /**
   * Extension for offset
   * <p>Cardinality: 0..1</p>
   */
  private Extension _offset;
  /**
   * If one or more days of week is provided, then the action happens only on the specified day(s).
   * <p>See http://hl7.org/fhir/valueset-days-of-week.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<String> dayOfWeek;
  /**
   * Extension for dayOfWeek
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _dayOfWeek;

  /**
   * Specified time of day for action to take place.
   * <p>Cardinality: 0..*</p>
   */
  private Date timeOfDay;
  /**
   * Extension for timeOfDay
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _timeOfDay;

  /**
   * An approximate time period during the day, potentially linked to an event of daily living that indicates when
   * the action should occur.
   * <p>See http://hl7.org/fhir/valueset-event-timing.html</p>
   * <p>Cardinality: 0..*</p>
   */
  private List<String> when;

  /**
   * Extension for when
   * <p>Cardinality: 0..*</p>
   */
  private List<Extension> _when;


  public Duration getBoundDuration() {
    return boundDuration;
  }

  public void setBoundDuration(Duration boundDuration) {
    this.boundDuration = boundDuration;
  }

  public Range getBoundRange() {
    return boundRange;
  }

  public void setBoundRange(Range boundRange) {
    this.boundRange = boundRange;
  }

  public Period getBoundPeriod() {
    return boundPeriod;
  }

  public void setBoundPeriod(Period boundPeriod) {
    this.boundPeriod = boundPeriod;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public Extension get_count() {
    return _count;
  }

  public void set_count(Extension _count) {
    this._count = _count;
  }

  public int getCountMax() {
    return countMax;
  }

  public void setCountMax(int countMax) {
    this.countMax = countMax;
  }

  public Extension get_countMax() {
    return _countMax;
  }

  public void set_countMax(Extension _countMax) {
    this._countMax = _countMax;
  }

  public double getDuration() {
    return duration;
  }

  public void setDuration(double duration) {
    this.duration = duration;
  }

  public Extension get_duration() {
    return _duration;
  }

  public void set_duration(Extension _duration) {
    this._duration = _duration;
  }

  public double getDurationMax() {
    return durationMax;
  }

  public void setDurationMax(double durationMax) {
    this.durationMax = durationMax;
  }

  public Extension get_durationMax() {
    return _durationMax;
  }

  public void set_durationMax(Extension _durationMax) {
    this._durationMax = _durationMax;
  }

  public String getDurationUnit() {
    return durationUnit;
  }

  public void setDurationUnit(String durationUnit) {
    this.durationUnit = durationUnit;
  }

  public Extension get_durationUnit() {
    return _durationUnit;
  }

  public void set_durationUnit(Extension _durationUnit) {
    this._durationUnit = _durationUnit;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public Extension get_frequency() {
    return _frequency;
  }

  public void set_frequency(Extension _frequency) {
    this._frequency = _frequency;
  }

  public int getFrequencyMax() {
    return frequencyMax;
  }

  public void setFrequencyMax(int frequencyMax) {
    this.frequencyMax = frequencyMax;
  }

  public Extension get_frequencyMax() {
    return _frequencyMax;
  }

  public void set_frequencyMax(Extension _frequencyMax) {
    this._frequencyMax = _frequencyMax;
  }

  public double getPeriod() {
    return period;
  }

  public void setPeriod(double period) {
    this.period = period;
  }

  public Extension get_period() {
    return _period;
  }

  public void set_period(Extension _period) {
    this._period = _period;
  }

  public double getPeriodMax() {
    return periodMax;
  }

  public void setPeriodMax(double periodMax) {
    this.periodMax = periodMax;
  }

  public Extension get_periodMax() {
    return _periodMax;
  }

  public void set_periodMax(Extension _periodMax) {
    this._periodMax = _periodMax;
  }

  public String getPeriodUnit() {
    return periodUnit;
  }

  public void setPeriodUnit(String periodUnit) {
    this.periodUnit = periodUnit;
  }

  public Extension get_periodUnit() {
    return _periodUnit;
  }

  public void set_periodUnit(Extension _periodUnit) {
    this._periodUnit = _periodUnit;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public Extension get_offset() {
    return _offset;
  }

  public void set_offset(Extension _offset) {
    this._offset = _offset;
  }

  public List<String> getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(List<String> dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public List<Extension> get_dayOfWeek() {
    return _dayOfWeek;
  }

  public void set_dayOfWeek(List<Extension> _dayOfWeek) {
    this._dayOfWeek = _dayOfWeek;
  }

  public Date getTimeOfDay() {
    return timeOfDay;
  }

  public void setTimeOfDay(Date timeOfDay) {
    this.timeOfDay = timeOfDay;
  }

  public List<Extension> get_timeOfDay() {
    return _timeOfDay;
  }

  public void set_timeOfDay(List<Extension> _timeOfDay) {
    this._timeOfDay = _timeOfDay;
  }

  public List<String> getWhen() {
    return when;
  }

  public void setWhen(List<String> when) {
    this.when = when;
  }

  public List<Extension> get_when() {
    return _when;
  }

  public void set_when(List<Extension> _when) {
    this._when = _when;
  }
}
