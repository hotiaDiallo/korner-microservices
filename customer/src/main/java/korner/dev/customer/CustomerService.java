package korner.dev.customer;

import korner.dev.clients.fraud.FraudCheckResponse;
import korner.dev.clients.fraud.FraudClient;
import korner.dev.clients.notification.NotificationClient;
import korner.dev.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final NotificationClient notificationClient;
    private final FraudClient fraudClient;
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        // todo: check email is valid
        // todo : check email not taken
        customerRepository.saveAndFlush(customer);
        // todo : check if fraudster
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());
        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("Fraudster");
        }
        // todo: make it async. i.e add to queue
        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome to korner service...",
                                customer.getFirstName())
                )
        );
    }
}
