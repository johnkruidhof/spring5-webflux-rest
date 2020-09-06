package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class VendorControllerTest {

    WebTestClient webTestClient;

    @Mock
    VendorRepository vendorRepository;

    @InjectMocks
    VendorController vendorController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        given(vendorRepository.findAll()).willReturn(Flux.just(
                Vendor.builder().firstName("Joe").lastName("Buck").build(),
                Vendor.builder().firstName("Jimmy").lastName("The Weed").build()
        ));

        webTestClient.get()
                .uri(VendorController.BASE_URL)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        Vendor vendor = Vendor.builder().firstName("Joe").lastName("Buck").build();

        given(vendorRepository.findById(anyString())).willReturn(Mono.just(vendor));

        webTestClient.get()
                .uri(VendorController.BASE_URL+"1")
                .exchange()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);
    }

    @Test
    public void createVendor() {
        given(vendorRepository.saveAll(any(Publisher.class))).willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().firstName("John").build());

        webTestClient.post()
                .uri(VendorController.BASE_URL)
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateVendor() {
        Vendor vendor = Vendor.builder().id("1").firstName("Jimmy").build();

        given(vendorRepository.save(any(Vendor.class))).willReturn(Mono.just(vendor));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().firstName("Jimmy").build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.put()
                .uri(VendorController.BASE_URL+"1")
                .body(vendorToSave, Vendor.class)
                .exchange();

        responseSpec.expectBody(Vendor.class).isEqualTo(vendor);
        responseSpec.expectStatus().isOk();
    }

    @Test
    public void patchVendor() {
        Vendor vendorCurrent = Vendor.builder().id("1").firstName("John").lastName("Kruidhof").build();
        Vendor vendorSaved = Vendor.builder().id("1").firstName("Jimmy").lastName("Kruidhof").build();

        given(vendorRepository.findById(anyString())).willReturn(Mono.just(vendorCurrent));
        given(vendorRepository.save(any(Vendor.class))).willReturn(Mono.just(vendorSaved));

        Mono<Vendor> catToSave = Mono.just(Vendor.builder().firstName("Jimmy").lastName("Kruidhof").build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.patch()
                .uri(VendorController.BASE_URL+"1")
                .body(catToSave, Vendor.class)
                .exchange();

        responseSpec.expectBody(Vendor.class).isEqualTo(vendorSaved);
        responseSpec.expectStatus().isOk();
    }


}