package me.kyunghwan.springrestapi.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcBuilderCustomizer;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@TestConfiguration
public class RestDocsConfigureation {

    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer () {
        return configurer -> configurer.operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());
    }
}
