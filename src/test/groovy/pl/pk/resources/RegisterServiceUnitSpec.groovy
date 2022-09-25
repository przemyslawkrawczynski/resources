package pl.pk.resources

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.util.concurrent.ListenableFuture
import pl.pk.resources.bussiness.RegisterStatus
import pl.pk.resources.bussiness.RegisteredRequest
import pl.pk.resources.repository.RegisteredRequestRepository
import pl.pk.resources.service.RegisterService
import spock.lang.Specification

class RegisterServiceUnitSpec extends Specification {

    private RegisterService registerService
    private KafkaTemplate<UUID, String> kafkaTemplate
    private RegisteredRequestRepository registeredRequestRepository;

    void setup() {
        kafkaTemplate = Mock(KafkaTemplate.class);
        registeredRequestRepository = Mock(RegisteredRequestRepository.class)

        registerService = new RegisterService(kafkaTemplate, registeredRequestRepository)
    }

    def "check testedClass"() {
        expect:
        registerService
        kafkaTemplate
        registeredRequestRepository
    }

    def "unit test - register"() {
        given:
        def site = 'http://onet.pl'
        def requestToSave = Mock(RegisteredRequest.class)
        requestToSave.registerStatus = RegisterStatus.REGISTERED
        def uuid = UUID.randomUUID()
        requestToSave.requestUUID = uuid
        requestToSave.requestedURL = site
        when:
        registerService.registerWebPageResourcesToSave(site)

        then:
        1 * registeredRequestRepository.findByRequestedURL(site) >> Optional.empty()
        1 * registeredRequestRepository.save(_) >> requestToSave
        1 * kafkaTemplate.send(RegisterService.TOPIC, _, site)
    }

    def "unit test - register - exist same request, should not send to kafka"() {
        given:
        def site = 'http://onet.pl'
        def existingRequest = Mock(RegisteredRequest.class)
        def uuid = UUID.randomUUID()
        def future = Mock(ListenableFuture.class)
        when:
        registerService.registerWebPageResourcesToSave(site)

        then:
        1 * registeredRequestRepository.findByRequestedURL(site) >> Optional.of(existingRequest)
        1 * existingRequest.getRegisterStatus() >> RegisterStatus.PROCESSED
        0 * kafkaTemplate.send(RegisterService.TOPIC, uuid, site) >> future
    }

    def "unit test - register - exist FAILED request, should send new request to kafka"() {
        given:
        def site = 'http://onet.pl'
        def existingRequest = Mock(RegisteredRequest.class)
        def newRequest = Mock(RegisteredRequest.class)

        when:
        registerService.registerWebPageResourcesToSave(site)

        then:
        1 * registeredRequestRepository.findByRequestedURL(site) >> Optional.of(existingRequest)
        1 * existingRequest.getRegisterStatus() >> RegisterStatus.FAIL
        1 * registeredRequestRepository.save(_) >> newRequest
        1 * kafkaTemplate.send(RegisterService.TOPIC, _, site)
    }

}
