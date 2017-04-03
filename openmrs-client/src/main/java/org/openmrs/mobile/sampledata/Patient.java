package org.openmrs.mobile.sampledata;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class Patient implements Serializable {

    public String given_name;
    public String middle_name;
    public String family_name;
    public int age;
    public String id;
    public char gender;
    public int active_visit;
    private JSONArray patients = new JSONArray();

    private String patientsString = "[\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Nasim\",\n" +
            "\t\t\"end_date\": \"Walker\",\n" +
            "\t\t\"visit_tag\": \"Lydia\",\n" +
            "\t\t\"age\": 41,\n" +
            "\t\t\"id\": \"J4H-2U9-K7X\",\n" +
            "\t\t\"gender\": \"F\",\n" +
            "\t\t\"active_visit\": \"1\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Lance\",\n" +
            "\t\t\"end_date\": \"Yael\",\n" +
            "\t\t\"visit_tag\": \"Darryl\",\n" +
            "\t\t\"age\": 87,\n" +
            "\t\t\"id\": \"D9W-6R0-C1Q\",\n" +
            "\t\t\"gender\": \"M\",\n" +
            "\t\t\"active_visit\": \"0\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Zane\",\n" +
            "\t\t\"end_date\": \"Maggie\",\n" +
            "\t\t\"visit_tag\": \"Hanna\",\n" +
            "\t\t\"age\": 85,\n" +
            "\t\t\"id\": \"R4V-9O4-C0H\",\n" +
            "\t\t\"gender\": \"M\",\n" +
            "\t\t\"active_visit\": \"1\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Judith\",\n" +
            "\t\t\"end_date\": \"Emi\",\n" +
            "\t\t\"visit_tag\": \"Marcia\",\n" +
            "\t\t\"age\": 98,\n" +
            "\t\t\"id\": \"F3V-3O3-C5U\",\n" +
            "\t\t\"gender\": \"M\",\n" +
            "\t\t\"active_visit\": \"0\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Austin\",\n" +
            "\t\t\"end_date\": \"Wallace\",\n" +
            "\t\t\"visit_tag\": \"Adria\",\n" +
            "\t\t\"age\": 59,\n" +
            "\t\t\"id\": \"A0H-5C1-D5F\",\n" +
            "\t\t\"gender\": \"F\",\n" +
            "\t\t\"active_visit\": \"1\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Jennifer\",\n" +
            "\t\t\"end_date\": \"Elliott\",\n" +
            "\t\t\"visit_tag\": \"Fay\",\n" +
            "\t\t\"age\": 1,\n" +
            "\t\t\"id\": \"X6X-6E6-T9B\",\n" +
            "\t\t\"gender\": \"M\",\n" +
            "\t\t\"active_visit\": \"0\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Shelby\",\n" +
            "\t\t\"end_date\": \"Nayda\",\n" +
            "\t\t\"visit_tag\": \"Gannon\",\n" +
            "\t\t\"age\": 82,\n" +
            "\t\t\"id\": \"V4S-9B6-M4I\",\n" +
            "\t\t\"gender\": \"F\",\n" +
            "\t\t\"active_visit\": \"1\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Ivory\",\n" +
            "\t\t\"end_date\": \"Megan\",\n" +
            "\t\t\"visit_tag\": \"Julian\",\n" +
            "\t\t\"age\": 89,\n" +
            "\t\t\"id\": \"Q7G-3Z0-X1F\",\n" +
            "\t\t\"gender\": \"F\",\n" +
            "\t\t\"active_visit\": \"1\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Hu\",\n" +
            "\t\t\"end_date\": \"Fallon\",\n" +
            "\t\t\"visit_tag\": \"Leandra\",\n" +
            "\t\t\"age\": 18,\n" +
            "\t\t\"id\": \"I1P-2B1-R8Q\",\n" +
            "\t\t\"gender\": \"F\",\n" +
            "\t\t\"active_visit\": \"0\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"visit_date\": \"Tara\",\n" +
            "\t\t\"end_date\": \"Desirae\",\n" +
            "\t\t\"visit_tag\": \"Zephr\",\n" +
            "\t\t\"age\": 61,\n" +
            "\t\t\"id\": \"N7W-3J0-I3K\",\n" +
            "\t\t\"gender\": \"M\",\n" +
            "\t\t\"active_visit\": \"0\"\n" +
            "\t}\n" +
            "]";

    public Patient() {
        try {
            patients = new JSONArray(patientsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Patient(String given_name, String middle_name, String family_name, String id, int age, char gender, int active_visit) {
        this.given_name = given_name;
        this.middle_name = middle_name;
        this.family_name = family_name;
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.active_visit = active_visit;
    }

    public Patient newPatient(String given_name, String middle_name, String family_name, String id, int age, char gender, int active_visit) {
        return new Patient(given_name, middle_name, family_name, id, age, gender, active_visit);
    }


    public JSONArray getPatients() {
        return patients;
    }
}

