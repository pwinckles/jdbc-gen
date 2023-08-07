# jdbc-gen

## Overview

`jdbc-gen` uses compile-time annotation processing to generate simple JDBC database access classes. It only supports
basic CRUD operations on single-table entities. It does not model entity relationships or operate on aggregates.

Its niche is executing simple DB operations without the weight and baggage of a proper ORM or the tediousness of
JDBC. It does not use runtime reflection, and has no dependencies.

## Dependencies

Add a dependency on the API:

```xml
<dependency>
    <groupId>com.pwinckles.jdbcgen</groupId>
    <artifactId>jdbc-gen-api</artifactId>
    <version>1.0.1</version>
</dependency>
```

And the annotation processor:

```xml
<dependency>
    <groupId>com.pwinckles.jdbcgen</groupId>
    <artifactId>jdbc-gen-processor</artifactId>
    <version>1.0.1</version>
    <scope>provided</scope>
</dependency>
```

Note, that the annotation processor should be scoped as `provided` as it is only used at compile time for code
generation.

## Usage

`jdbc-gen` generates database access classes for entities that are annotated with `@JdbcGen`. Entity classes should
be simple POJOs (records are supported!), and have the following restrictions:

1. They may not be `private`
2. They may not extend another class (implementing interfaces is fine)
3. They may not be non-static inner classes
4. The fields may only be types supported by the JDBC driver implementation

Enum fields are only currently supported as strings mapped to their canonical name.

For example:

```java

// This annotation is required to denote an entity to generate a class for
@JdbcGen
// This annotation is required to map the entity to its table
@JdbcTable(name = "example")
public class Example {

    // These annotations map the entity's fields to their columns
    @JdbcGenColumn(name = "example_id", identity = true)
    private Long id;

    @JdbcGenColumn(name = "example_value")
    private String value;

    @JdbcGenColumn(name = "example_timestamp")
    private Instant timestamp;

    public Long getId() {
        return this.id;
    }

    public Example setId(Long id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public Example setValue(String value) {
        this.value = value;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Example setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
```

After compilation, a class named `ExampleDb` is generated in the same package as its corresponding entity. The
`ExampleDb` object implements `JdbcGenDb` and is able to select, select all, insert, update, patch, delete, and execute
batch operations. For example:

```java
// In these examples, "conn" is a JDBC connection created elsewhere

var exampleDb = new ExampleDb();

// insert and return a generated id
var id = exampleDb.insert(new Example()
        .setValue("test")
        .setTimestamp(Instant.now()), conn);

// update a single field on an existing entity
exampleDb.update(id, new ExambleDb.Patch().setValue("updated"), conn);

// select all entities, ordered by a specific column
var examples = exampleDb.select(select -> select.sort(orderBy -> orderBy.valueAsc()), conn);

// select all entities that match a filter
var filtered = exambleDb.select(select -> select.filter(entity -> entity
        .value().isEqualTo("test")
        .and()
        .timestamp().isGreaterThan(Instant.now().minusWeeks(1))), conn);

// select all entities that match a filter with sorting and paging
var filtered = exambleDb.select(select -> select
        .filter(entity -> entity.value().isEqualTo("test"))
        .sort(orderBy -> orderBy.timestampAsc())
        .paginate(0, 100), conn);

// delete an entity
exampleDb.delete(id, conn);
```

`jdbc-gen` supports setting fields using a canonical constructor, setters, or direct field access, and getting values
using getters or direct access. It does not use reflection. It determines which method to use at compile time based on
what is available on the entity. A canonical constructor is given highest precedence and direct field access lowest.

The name of the generated class can be controlled by setting the `name` attribute in the `@JdbcGen` annotation. For
example, `@JdbcGen(name = "MyExampleDao")` would produce a class named `MyExampleDao` in the same package as the entity.

## Roadmap

In the future, support may be added for the following features:

1. Joins
2. Inheritance
