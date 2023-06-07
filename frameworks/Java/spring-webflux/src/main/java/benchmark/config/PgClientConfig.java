package benchmark.config;

import benchmark.PgClients;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("pgclient")
@ConfigurationProperties(prefix = "database")
public class PgClientConfig {
    private String name;
    private String host;
    private int port;
    private String username;
    private String password;

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public PgClients pgClients(Vertx vertx) {
        List<PgPool> clients = new ArrayList<>();

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            clients.add(pgClient(vertx));
        }

        return new PgClients(clients);
    }


    public PgPool pgClient(Vertx vertx) {
        SqlConnectOptions options = new SqlConnectOptions();
        options.setDatabase(name);
        options.setHost(host);
        options.setPort(port);
        options.setUser(username);
        options.setPassword(password);
        options.setCachePreparedStatements(true);
        return PgPool.pool(vertx, PgConnectOptions.wrap(options), new PoolOptions().setMaxSize(1));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
