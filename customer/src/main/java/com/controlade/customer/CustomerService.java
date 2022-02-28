package com.controlade.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
//public record CustomerService(CustomerRepository customerRepository) {   //first version, record is from Java17
public class CustomerService {

    private final CustomerRepository customerRepository;   //apply this when using class instead of record, and @AllArgsConstructor from Lombok
    private final RestTemplate restTemplate;

    public void registerCustomer(CustomerRegistrationRequest request){
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        // todo: check if email valid
        // todo: check if email not taken
        customerRepository.saveAndFlush(customer);  // store customer in db
        // todo: check if fraudster
        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                "http://FRAUD/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()
        );

        if (fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }
        // todo: send notification
    }
}
