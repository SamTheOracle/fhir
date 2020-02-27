package com.oracolo.fhir.model.aggregations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oracolo.fhir.model.Resource;
import com.oracolo.fhir.model.domain.*;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregationEncounter implements AggregationResource {

  private String resourceType = "AggregationEncounter";

  private List<String> ids;

  private Encounter mainEncounter;

  private List<Encounter> subEncounters;

  private List<Condition> conditions;

  private List<Practitioner> practitioners;

  private List<Observation> observations;

  private List<Procedure> procedures;

  private List<Location> locations;

  public String getResourceType() {
    return resourceType;
  }


  public Encounter getMainEncounter() {
    return mainEncounter;
  }

  public AggregationEncounter setMainEncounter(Encounter mainEncounter) {
    this.mainEncounter = mainEncounter;
    return this;
  }

  public List<Encounter> getSubEncounters() {
    return subEncounters;
  }

  public AggregationEncounter setSubEncounters(List<Encounter> subEncounters) {
    this.subEncounters = subEncounters;
    return this;
  }

  public List<Condition> getConditions() {
    return conditions;
  }

  public AggregationEncounter setConditions(List<Condition> conditions) {
    this.conditions = conditions;
    return this;
  }

  public List<Practitioner> getPractitioners() {
    return practitioners;
  }

  public AggregationEncounter setPractitioners(List<Practitioner> practitioners) {
    this.practitioners = practitioners;
    return this;
  }

  public List<Observation> getObservations() {
    return observations;
  }

  public AggregationEncounter setObservations(List<Observation> observations) {
    this.observations = observations;
    return this;
  }

  public List<Procedure> getProcedures() {
    return procedures;
  }

  public AggregationEncounter setProcedures(List<Procedure> procedures) {
    this.procedures = procedures;
    return this;
  }

  public List<Location> getLocations() {
    return locations;
  }

  public AggregationEncounter setLocations(List<Location> locations) {
    this.locations = locations;
    return this;
  }

  public AggregationEncounter addNewSubEncounter(Encounter encounter) {
    if (subEncounters == null) {
      subEncounters = new ArrayList<>();
    }
    subEncounters.add(encounter);
    return this;
  }

  public List<Resource> resources() {

    List<Resource> resources = new ArrayList<>();
    if (subEncounters != null) {
      resources.addAll(subEncounters);
    }
    if (conditions != null) {
      resources.addAll(conditions);
    }
    if (observations != null) {
      resources.addAll(observations);
    }
    if (practitioners != null) {
      resources.addAll(practitioners);
    }
    if (procedures != null) {
      resources.addAll(procedures);
    }
    if (locations != null) {
      resources.addAll(locations);
    }
    return resources;
  }

  public AggregationEncounter addNewId(String id) {
    if (this.ids == null) {
      ids = new ArrayList<>();
    }
    ids.add(id);
    return this;
  }

  public List<String> getIds() {
    return ids;
  }

  public AggregationEncounter setIds(List<String> ids) {
    this.ids = ids;
    return this;
  }

}
