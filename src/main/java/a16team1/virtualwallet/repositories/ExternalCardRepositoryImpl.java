package a16team1.virtualwallet.repositories;

import a16team1.virtualwallet.models.Card;
import a16team1.virtualwallet.models.dtos.CardWithdrawalDto;
import a16team1.virtualwallet.repositories.contracts.ExternalCardRepository;
import a16team1.virtualwallet.utilities.mappers.PaymentInstrumentDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Repository
@PropertySource({"classpath:application.properties", "classpath:messages.properties"})
public class ExternalCardRepositoryImpl implements ExternalCardRepository {

    private final RestTemplate restTemplate;
    private final PaymentInstrumentDtoMapper paymentInstrumentDtoMapper;

    private final String postUrl;
    private final String apiKey;

    @Autowired
    public ExternalCardRepositoryImpl(RestTemplate restTemplate, PaymentInstrumentDtoMapper paymentInstrumentDtoMapper, Environment env) {
        this.restTemplate = restTemplate;
        this.paymentInstrumentDtoMapper = paymentInstrumentDtoMapper;
        postUrl = env.getProperty("postUrl");
        apiKey = env.getProperty("apiKey");
    }

    @Override
    public boolean withdraw(BigDecimal amount, String description, Card card, String csv, String idempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        CardWithdrawalDto paymentRequest = paymentInstrumentDtoMapper.fromCard(amount, description, idempotencyKey, card);
        HttpEntity<String> request = new HttpEntity<>(paymentRequest.toString(), headers);
        try {
            restTemplate.postForEntity(postUrl, request, String.class);
            return true;
        } catch (HttpClientErrorException.Forbidden e) {
            return false;
        }
    }
}
