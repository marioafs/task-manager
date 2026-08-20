package com.mariosilva.task_manager.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class IntegrationTestWithTestcontainer {
    
}
