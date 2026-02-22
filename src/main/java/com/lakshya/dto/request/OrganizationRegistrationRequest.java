package com.lakshya.dto.request;

import lombok.Data;

@Data
public class OrganizationRegistrationRequest {
    private String companyName;
    private String domain;
    private String email;
    private String contactNo;
    private String address;
    private String firstName;
    private String lastName;
    private String password;
}
