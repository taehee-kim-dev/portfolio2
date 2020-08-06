package portfolio2.infra;


import org.testcontainers.containers.MariaDBContainer;

public abstract class ContainerBaseTest {

    static final MariaDBContainer MARIA_DB_CONTAINER;

    static {
        MARIA_DB_CONTAINER = new MariaDBContainer();
        MARIA_DB_CONTAINER.start();
    }

}
