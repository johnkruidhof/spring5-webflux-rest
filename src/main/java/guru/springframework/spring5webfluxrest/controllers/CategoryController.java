package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(CategoryController.BASE_URL)
public class CategoryController {

    public static final String BASE_URL = "/api/v1/categories/";


    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    Flux<Category> list() {
        return categoryRepository.findAll();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Category> getById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Void> create(@RequestBody Publisher<Category> categoryStream) {
        return categoryRepository.saveAll(categoryStream).then();
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Category> update(@PathVariable String id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    Mono<Category> patch(@PathVariable String id, @RequestBody Category category) {

        Category current = categoryRepository.findById(id).block();

        if (current != null) {
            if (!category.getDescription().equalsIgnoreCase(current.getDescription())) {
                current.setDescription(category.getDescription());
                return categoryRepository.save(current);
            }
        } else {
            return Mono.just(category);
        }

        return Mono.just(current);
    }

}
