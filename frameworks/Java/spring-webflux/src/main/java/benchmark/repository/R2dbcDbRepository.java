package benchmark.repository;

import benchmark.model.Fortune;
import benchmark.model.World;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Profile("r2dbc")
@RequiredArgsConstructor
public class R2dbcDbRepository implements DbRepository {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @PostConstruct
    void initWorld() {
        r2dbcEntityTemplate.getDatabaseClient()
                .sql("select * from information_schema.tables t where t.table_name = 'world' and t.table_catalog = 'hello_world'")
                .fetch().first()
                .map(e -> false)
                .switchIfEmpty(r2dbcEntityTemplate.getDatabaseClient()
                        .sql("create table public.world (id serial primary key, randomnumber integer);")
                        .then()
                        .then(Flux.range(1, 10_000)
                                .map(id -> new World(id, (int) (Math.random() * 10_000)))
                                .flatMap(r2dbcEntityTemplate::insert).parallel().then())
                        .thenReturn(true))
                .then()
                .subscribe();
    }

    @Override
    public Mono<World> getWorld(int id) {
        return r2dbcEntityTemplate.select(World.class)
                .matching(Query.query(Criteria.where("id").is(id)))
                .first();

    }

    public Mono<World> updateWorld(World world) {
        return r2dbcEntityTemplate.update(World.class)
                .matching(Query.query(Criteria.where("id").is(world.id)))
                .apply(Update.update("randomnumber", world.randomnumber))
                .thenReturn(world);
    }

    public Mono<World> findAndUpdateWorld(int id, int randomNumber) {
        return getWorld(id).flatMap(world -> {
            world.randomnumber = randomNumber;
            return updateWorld(world);
        });
    }

    @PostConstruct
    void initFortune() {
        r2dbcEntityTemplate.getDatabaseClient()
                .sql("select * from information_schema.tables t where t.table_name = 'fortune' and t.table_catalog = 'hello_world'")
                .fetch().first().switchIfEmpty(r2dbcEntityTemplate.getDatabaseClient()
                        .sql("create table fortune(id serial primary key, message varchar(100))").then()
                        .then(Flux.just(
                                        "A bad random number generator: 1, 1, 1, 1, 1, 4.33e+67, 1, 1, 1",
                                        "A computer program does what you tell it to do, not what you want it to do.",
                                        "A computer scientist is someone who fixes things that aren&apos;t broken.",
                                        "A list is only as strong as its weakest link. — Donald Knuth",
                                        "Additional fortune added at request time.",
                                        "After enough decimal places, nobody gives a damn.",
                                        "Any program that runs right is obsolete.",
                                        "Computers make very fast, very accurate mistakes.",
                                        "Emacs is a nice operating system, but I prefer UNIX. — Tom Christaensen",
                                        "Feature: A bug with seniority.",
                                        "fortune: No such file or directory",
                                        "??????????????"
                                )
                                .map(m -> new Fortune(0, m))
                                .flatMapSequential(r2dbcEntityTemplate::insert).then())
                        .thenReturn(Map.of()))
                .subscribe();
    }

    @Override
    public Flux<Fortune> fortunes() {
        return r2dbcEntityTemplate.select(Fortune.class).all();
    }
}
