package com.controlade.customer;

import com.controlade.clients.fraud.FraudCheckResponse;
import com.controlade.clients.fraud.FraudClient;
import com.controlade.clients.notification.NotificationClient;
import com.controlade.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
//public record CustomerService(CustomerRepository customerRepository) {   //first version, record is from Java17
public class CustomerService {

    private final CustomerRepository customerRepository;   //apply this when using class instead of record, and @AllArgsConstructor from Lombok
//    private final RestTemplate restTemplate;  //not in use after implementing Open Feign
    //inject FraudClient to use it instead of having the implementation here
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;

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
//        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
//                "http://FRAUD/api/v1/fraud-check/{customerId}",
//                FraudCheckResponse.class,
//                customer.getId()
//        );
        // above FraudCheckResponse is replace bellow:
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }
        // send notification
        // todo: make it async. i.e add to queue
        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome to controlade...",
                                customer.getFirstName())
                )
        );
    }
}
