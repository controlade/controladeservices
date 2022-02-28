package com.controlade.customer;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email) {

}
