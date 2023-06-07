package benchmark;

import io.vertx.pgclient.PgPool;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public class PgClients {
    private final Iterator<PgPool> iterator;

    public PgClients(Collection<PgPool> clients) {
        iterator = Stream.generate(() -> clients).flatMap(Collection::stream).iterator();
    }

    public synchronized PgPool getOne() {
        return iterator.next();
    }
}
