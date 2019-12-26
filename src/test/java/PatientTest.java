import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import model.datatypes.Identifier;
import model.datatypes.Period;
import model.domain.Patient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FhirUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientTest {

  private Patient fhirPatient;
 private String correctPatientJsonString="{\n" +
   "  \"resourceType\": \"Patient\",\n" +
   "  \"id\": \"example\",\n" +
   "  \"text\": {\n" +
   "    \"status\": \"generated\",\n" +
   "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n\\t\\t\\t<table>\\n\\t\\t\\t\\t<tbody>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Name</td>\\n\\t\\t\\t\\t\\t\\t<td>Peter James \\n              <b>Chalmers</b> (&quot;Jim&quot;)\\n            </td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Address</td>\\n\\t\\t\\t\\t\\t\\t<td>534 Erewhon, Pleasantville, Vic, 3999</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Contacts</td>\\n\\t\\t\\t\\t\\t\\t<td>Home: unknown. Work: (03) 5555 6473</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Id</td>\\n\\t\\t\\t\\t\\t\\t<td>MRN: 12345 (Acme Healthcare)</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t</tbody>\\n\\t\\t\\t</table>\\n\\t\\t</div>\"\n" +
   "  },\n" +
   "  \"identifier\": [\n" +
   "    {\n" +
   "      \"use\": \"usual\",\n" +
   "      \"type\": {\n" +
   "        \"coding\": [\n" +
   "          {\n" +
   "            \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n" +
   "            \"code\": \"MR\"\n" +
   "          }\n" +
   "        ]\n" +
   "      },\n" +
   "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
   "      \"value\": \"12345\",\n" +
   "      \"period\": {\n" +
   "        \"start\": \"2001-05-06\"\n" +
   "      },\n" +
   "      \"assigner\": {\n" +
   "        \"display\": \"Acme Healthcare\"\n" +
   "      }\n" +
   "    }\n" +
   "  ],\n" +
   "  \"active\": true,\n" +
   "  \"name\": [\n" +
   "    {\n" +
   "      \"use\": \"official\",\n" +
   "      \"family\": \"Zanotti\",\n" +
   "      \"given\": [\n" +
   "        \"Giacomo\"\n" +
   "      ]\n" +
   "    }\n" +
   "  ],\n" +
   "  \"telecom\": [\n" +
   "    {\n" +
   "      \"use\": \"home\"\n" +
   "    },\n" +
   "    {\n" +
   "      \"system\": \"phone\",\n" +
   "      \"value\": \"(03) 5555 6473\",\n" +
   "      \"use\": \"work\",\n" +
   "      \"rank\": 1\n" +
   "    },\n" +
   "    {\n" +
   "      \"system\": \"phone\",\n" +
   "      \"value\": \"(03) 3410 5613\",\n" +
   "      \"use\": \"mobile\",\n" +
   "      \"rank\": 2\n" +
   "    },\n" +
   "    {\n" +
   "      \"system\": \"phone\",\n" +
   "      \"value\": \"(03) 5555 8834\",\n" +
   "      \"use\": \"old\",\n" +
   "      \"period\": {\n" +
   "        \"end\": \"2014\"\n" +
   "      }\n" +
   "    }\n" +
   "  ],\n" +
   "  \"gender\": \"male\",\n" +
   "  \"birthDate\": \"1905-08-23\",\n" +
   "  \"_birthDate\": {\n" +
   "    \"extension\": [\n" +
   "      {\n" +
   "        \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-birthTime\",\n" +
   "        \"valueDateTime\": \"1974-12-25T14:35:45-05:00\"\n" +
   "      }\n" +
   "    ]\n" +
   "  },\n" +
   "  \"deceasedBoolean\": false,\n" +
   "  \"address\": [\n" +
   "    {\n" +
   "      \"use\": \"home\",\n" +
   "      \"type\": \"both\",\n" +
   "      \"text\": \"534 Erewhon St PeasantVille, Rainbow, Vic  3999\",\n" +
   "      \"line\": [\n" +
   "        \"534 Erewhon St\"\n" +
   "      ],\n" +
   "      \"city\": \"PleasantVille\",\n" +
   "      \"district\": \"Rainbow\",\n" +
   "      \"state\": \"Vic\",\n" +
   "      \"postalCode\": \"3999\",\n" +
   "      \"period\": {\n" +
   "        \"start\": \"1974-12-25\"\n" +
   "      }\n" +
   "    }\n" +
   "  ],\n" +
   "  \"contact\": [\n" +
   "    {\n" +
   "      \"relationship\": [\n" +
   "        {\n" +
   "          \"coding\": [\n" +
   "            {\n" +
   "              \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0131\",\n" +
   "              \"code\": \"N\"\n" +
   "            }\n" +
   "          ]\n" +
   "        }\n" +
   "      ],\n" +
   "      \"name\": {\n" +
   "        \"family\": \"du Marché\",\n" +
   "        \"_family\": {\n" +
   "          \"extension\": [\n" +
   "            {\n" +
   "              \"url\": \"http://hl7.org/fhir/StructureDefinition/humanname-own-prefix\",\n" +
   "              \"valueString\": \"VV\"\n" +
   "            }\n" +
   "          ]\n" +
   "        },\n" +
   "        \"given\": [\n" +
   "          \"Bénédicte\"\n" +
   "        ]\n" +
   "      },\n" +
   "      \"telecom\": [\n" +
   "        {\n" +
   "          \"system\": \"phone\",\n" +
   "          \"value\": \"+33 (237) 998327\"\n" +
   "        }\n" +
   "      ],\n" +
   "      \"address\": {\n" +
   "        \"use\": \"home\",\n" +
   "        \"type\": \"both\",\n" +
   "        \"line\": [\n" +
   "          \"534 Erewhon St\"\n" +
   "        ],\n" +
   "        \"city\": \"PleasantVille\",\n" +
   "        \"district\": \"Rainbow\",\n" +
   "        \"state\": \"Vic\",\n" +
   "        \"postalCode\": \"3999\",\n" +
   "        \"period\": {\n" +
   "          \"start\": \"1974-12-25\"\n" +
   "        }\n" +
   "      },\n" +
   "      \"gender\": \"female\",\n" +
   "      \"period\": {\n" +
   "        \"start\": \"2012\"\n" +
   "      }\n" +
   "    }\n" +
   "  ],\n" +
   "  \"managingOrganization\": {\n" +
   "    \"reference\": \"Organization/1\"\n" +
   "  }\n" +
   "}\n";

  @BeforeEach
  void setUp() {
    fhirPatient = new Patient();
  }

  @Test
  void getIdentifier() {

  }

  @Test
  public void testDecode() {


    Patient correctPatient = Json.decodeValue(correctPatientJsonString, Patient.class);
    Assertions.assertDoesNotThrow(() -> FhirUtils.validateJsonAgainstSchema(JsonObject.mapFrom(correctPatient)));

  }

  @Test
  void setIdentifier() {
    Identifier identifier = new Identifier();
    identifier.setPeriod(new Period().setEnd(new Date().toString())
      .setStart(new Date().toString()));
    List<Identifier> identifiers = new ArrayList<>();
    identifiers.add(identifier);
    fhirPatient.setIdentifier(identifiers);
    String encoded = Json.encodePrettily(fhirPatient);
    System.out.println(encoded);
    Patient decodedPatient = Json.decodeValue(encoded, Patient.class);
    assertEquals(decodedPatient.getIdentifier().get(0).getPeriod().getStart(),
      fhirPatient.getIdentifier().get(0).getPeriod().getStart());
  }

  @Test
  void isActive() {
  }

  @Test
  void setActive() {
  }

  @Test
  void getName() {
  }

  @Test
  void setName() {
  }

  @Test
  void getContactPoints() {
  }

  @Test
  void setContactPoints() {
  }

  @Test
  void getGender() {
  }

  @Test
  void setGender() {
  }

  @Test
  void getBirthDate() {
  }

  @Test
  void setBirthDate() {
  }

  @Test
  void isDeceasedBoolean() {
  }

  @Test
  void setDeceasedBoolean() {
  }

  @Test
  void getDeceasedDateTime() {
  }

  @Test
  void setDeceasedDateTime() {
  }

  @Test
  void getAddress() {
  }

  @Test
  void setAddress() {
  }

  @Test
  void getMaritalStatus() {
  }

  @Test
  void setMaritalStatus() {
  }

  @Test
  void isMultipleBirthBoolean() {
  }

  @Test
  void setMultipleBirthBoolean() {
  }

  @Test
  void getMultipleBirthInteger() {
  }

  @Test
  void setMultipleBirthInteger() {
  }

  @Test
  void getPhoto() {
  }

  @Test
  void setPhoto() {
  }

  @Test
  void getContact() {
  }

  @Test
  void setContact() {
  }

  @Test
  void getCommunication() {
  }

  @Test
  void setCommunication() {
  }

  @Test
  void getGeneralPractitioner() {
  }

  @Test
  void setGeneralPractitioner() {
  }

  @Test
  void getManagingOrganization() {
  }

  @Test
  void setManagingOrganization() {
  }

  @Test
  void getLink() {
  }

  @Test
  void setLink() {
  }
}
