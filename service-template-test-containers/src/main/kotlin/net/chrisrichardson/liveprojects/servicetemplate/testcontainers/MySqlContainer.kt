package net.chrisrichardson.liveprojects.servicetemplate.testcontainers

import org.testcontainers.containers.MySQLContainer

object MySqlContainer : DefaultPropertyProvidingContainer() {

    fun getContainerAlias() = "mysql"

    override val container: MySQLContainer<Nothing> = MySQLContainer<Nothing>("mysql:5.7.28").apply {
        withDatabaseName("dbname")
        withReuse(true)
        ContainerNetwork.withNetwork(this)
        withNetworkAliases(getContainerAlias())
    }



    override fun consumeProperties(registry: PropertyConsumer) {
        registry.forNameAndPorts(getContainerAlias(), hostPort= getPort(), servicePort=3306)

        registry.add("spring.datasource.url", container.getJdbcUrl())
        registry.add("spring.datasource.password", container.getPassword())
        registry.add("spring.datasource.username", container.getUsername())
    }

    fun getPort() = container.getMappedPort(3306)


}