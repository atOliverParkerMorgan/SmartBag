// taken from https://github.com/vitSkalicky/lepsi-rozvrh/
package com.olivermorgan.ontime.main.BakalariAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    public String fullName;
    public String UserType;
}
