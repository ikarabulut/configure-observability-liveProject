plugins {
    id("TestReporter")
}

apply<IntegrationTestsPlugin>()
apply<RestAssuredTestDependenciesPlugin>()

dependencies {






    testImplementation(project(":service-template-domain"))
    testImplementation(project(":service-template-web"))
    testImplementation(project(":service-template-util"))
    testImplementation(project(":service-template-test-util"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":service-template-test-containers"))

}
