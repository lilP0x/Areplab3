package org.example;

import org.example.server.FileReader;
import org.example.controller.LabController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {

    private FileReader fileReader;

    @BeforeEach
    void setUp() {
        fileReader = new FileReader();
    }

    @Test
    void testRouteRegistration() {
        Method method = fileReader.getRouteMappings().get("/app/add");
        assertNotNull(method);
        assertEquals("add", method.getName());
    }
}
