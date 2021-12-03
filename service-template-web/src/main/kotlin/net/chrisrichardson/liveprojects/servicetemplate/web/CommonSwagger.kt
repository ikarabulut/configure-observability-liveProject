package net.chrisrichardson.liveprojects.servicetemplate.web

import com.fasterxml.classmate.TypeResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class CommonSwaggerConfiguration {
    @Bean
    fun api(typeResolver: TypeResolver?): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .securityContexts(listOf(securityContext()))
                .securitySchemes(listOf(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.chrisrichardson.liveprojects.servicetemplate"))
                .build()
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity::class.java)
                .useDefaultResponseMessages(false)
    }

    private fun apiKey() = ApiKey("JWT", "Authorization", "header")

    private fun securityContext(): SecurityContext? {
        return SecurityContext.builder().securityReferences(defaultAuth()).build()
    }

    private fun defaultAuth(): List<SecurityReference?> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        return listOf(SecurityReference("JWT", arrayOf(authorizationScope)))
    }
}