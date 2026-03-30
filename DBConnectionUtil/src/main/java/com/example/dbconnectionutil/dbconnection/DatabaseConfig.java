package com.example.dbconnectionutil.dbconnection;

import com.fasterxml.jackson.annotation.JsonProperty;

class DatabaseConfig {
    // Note:
    // - Fields need to be public for successful mapping
    // - JsonProperty can be used to map customField name in .json to POJO. If not present, then field with same name is searched in json.
    // - You can't have extra fields in .json (By Default). It should have same number of fields as in POJO unless ObjectMapper is configured to not fail on unknown property discovery.
    @JsonProperty("jdbcConnection")
    public String url;
    public String username;
    public String password;
}
