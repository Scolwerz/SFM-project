package hu.unideb.autoszerviz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import hu.unideb.autoszerviz.model.CatalogService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CatalogControllerTest {
    CatalogService service = mock(CatalogService.class);
    CatalogController controller = new CatalogController(service);

    @Test
    void catalogReturnsBrandsModelsAndServicesFromCatalogService() {
        Map<String, Object> expected = Map.of("brands", List.of());
        when(service.catalog()).thenReturn(expected);
        assertEquals(expected, controller.catalog());
    }
}