



plugins {
    id("TestReporter")
    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

apply<IntegrationTestsPlugin>()


apply<RestAssuredTestDependenciesPlugin>()



dependencies {

    implementation(project(":service-template-util"))
    implementation(project(":service-template-domain"))
    implementation(project(":service-template-config"))
    implementation(project(":service-template-persistence"))
    implementation(project(":service-template-web"))
    implementation(project(":service-template-distributed-tracing"))

    implementation(project(":service-template-health-check"))
    implementation(project(":service-template-metrics"))

    implementation("org.springframework.boot:spring-boot-devtools")

    implementation("io.springfox:springfox-swagger-ui:$springFoxVersion")


    testImplementation(project(":service-template-test-util"))
    testImplementation(project(":service-template-test-data"))
    testImplementation(project(":service-template-test-containers"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")

}



val checkTask = tasks.findByName("check")!!

checkTask.shouldRunAfter(":service-template-domain:check")
checkTask.shouldRunAfter(":service-template-web:check")
checkTask.shouldRunAfter(":service-template-persistence:check")
checkTask.shouldRunAfter(":service-template-metrics:check")
checkTask.shouldRunAfter(":service-template-health-check:check")
checkTask.shouldRunAfter(":service-template-distributed-tracing:check")


tasks.register("validateEnvironment", DefaultTask::class) {
    dependsOn("integrationTest")
}
