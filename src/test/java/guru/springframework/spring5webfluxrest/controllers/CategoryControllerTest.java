package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
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

public class CategoryControllerTest {

    WebTestClient webTestClient;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryController categoryController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    public void list() {
        given(categoryRepository.findAll()).willReturn(Flux.just(
                Category.builder().description("Cat1").build(),
                Category.builder().description("Cat1").build())
        );

        webTestClient.get()
                .uri(CategoryController.BASE_URL)
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        Category category = Category.builder().description("Cat1").build();

        given(categoryRepository.findById(anyString())).willReturn(Mono.just(category));

        webTestClient.get()
                .uri(CategoryController.BASE_URL+"1")
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(category);
    }

    @Test
    public void createCategory() {
        given(categoryRepository.saveAll(any(Publisher.class))).willReturn(Flux.just(Category.builder().build()));

        Mono<Category> catToSave = Mono.just(Category.builder().description("Cat1").build());

        webTestClient.post()
                .uri(CategoryController.BASE_URL)
                .body(catToSave, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateCategory() {
        Category category = Category.builder().id("1").description("Cat1").build();

        given(categoryRepository.save(any(Category.class))).willReturn(Mono.just(category));

        Mono<Category> catToSave = Mono.just(Category.builder().description("Cat1").build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.put()
                .uri(CategoryController.BASE_URL+"1")
                .body(catToSave, Category.class)
                .exchange();

        responseSpec.expectBody(Category.class).isEqualTo(category);
        responseSpec.expectStatus().isOk();
    }

    @Test
    public void patchCategory() {
        Category categoryCurrent = Category.builder().id("1").description("Cat1").build();
        Category categorySaved = Category.builder().id("1").description("Cat2").build();

        given(categoryRepository.findById(anyString())).willReturn(Mono.just(categoryCurrent));
        given(categoryRepository.save(any(Category.class))).willReturn(Mono.just(categorySaved));

        Mono<Category> catToSave = Mono.just(Category.builder().description("Cat2").build());

        WebTestClient.ResponseSpec responseSpec = webTestClient.patch()
                .uri(CategoryController.BASE_URL+"1")
                .body(catToSave, Category.class)
                .exchange();

        responseSpec.expectBody(Category.class).isEqualTo(categorySaved);
        responseSpec.expectStatus().isOk();
    }
}