package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping(VendorController.BASE_URL)
@RestController
public class VendorController {

    public static final String BASE_URL = "/api/v1/vendors/";

    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping()
    Flux<Vendor> list() {
        return vendorRepository.findAll();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Vendor> getById(@PathVariable String id) {
        return vendorRepository.findById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Void> create(@RequestBody Publisher<Vendor> vendorStream) {
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Vendor> update(@PathVariable String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Vendor> patch(@PathVariable String id, @RequestBody Vendor vendor) {

        Vendor current = vendorRepository.findById(id).block();

        if (current != null) {
            if (!vendor.getFirstName().equalsIgnoreCase(current.getFirstName())) {
                current.setFirstName(vendor.getFirstName());
            }
            if (!vendor.getLastName().equalsIgnoreCase(current.getLastName())) {
                current.setLastName(vendor.getLastName());
            }
            return vendorRepository.save(current);

        } else {
            return Mono.just(vendor);
        }
    }



}
