Mirror
=====

### Opus #4

A smart, simple and fast alternative to Java's clunky reflection API.
This is potentially the fastest access API possible, avoiding all slow security checks.

Version 5 of Mirror abandoned trying to shortcut the JDK's reflection API and instead compiles direct access routes, using the most efficient method possible.

## Maven Information
```xml
<repository>
    <id>kenzie</id>
    <name>Kenzie's Repository</name>
    <url>https://repo.kenzie.mx/releases</url>
</repository>
``` 

```xml
<dependency>
    <groupId>mx.kenzie</groupId>
    <artifactId>mirror</artifactId>
    <version>5.0.3</version>
    <scope>compile</scope>
</dependency>
```

## Introduction

Mirror provides an alternative to Java's reflection and MethodHandles API with better efficiency and less boilerplate.

Rather than breaking security and injecting the code into the class's namespace to see private members, Mirror uses Java's `invokedynamic` opcode to access the member directly. This involves writing a miniature accessing class at runtime, but these class stubs are automatically cached and reused whenever an accessor is created to reduce overhead.

Although it uses a fairly similar process to Java's internal accessor system, Mirror gains some speed advantages through minor time-saves. Every `invoke` call to a reflection object involves:
1. More than seven recursive security checks.
2. A tree-lookup to find the internal accessor.
3. At least two delegated `invoke` calls to internal accessors.

Mirror avoids all of this by:
- Performing the invocation in-situ.
- Not performing any pointless security checks.
- Using the fastest instruction possible in the situation.

A lot of reflection API use is to access members not available at compile-time, especially if the class name or version isn't known, or if the class is unavailable as a dependency. In these cases, the actual member isn't private and so a lot of the overhead is wasted, making reflection significantly slower than normal invocation.
Mirror avoids this by writing a normal invocation call where possible, meaning the only difference between using the accessor and calling the method directly is a tiny method call and `checkcast` instructions for mismatched parameters.

The timings below are based on running the speed check in `BasicTest` on my laptop.

| Access Type                         | Average Speed |
|-------------------------------------|---------------|
| Normal Invocation                   | ~4ns          |
| Access Public Member                | ~6ns          |
| Access Hidden Member                | ~40ns         |
| Access Member via `Mirror#unsafe()` | ~80ns         |
| Magic Mirror Invocation             | ~40ns         |
| ~~Reflection~~ (JDK)                | ~220ns        |
| ~~Proxy Invocation~~ (JDK)          | ~1300ns       |

## Migrating from v4.0.0

Version 5 removes all alternative mirror types, and in return provides a much safer implementation of v4's 'fast mirror'.

An object or class is first targeted with `Mirror.of(thing)`, from which special 'accessors' can be created to use particular fields, methods and constructors.

## Magic Mirrors

Magic mirrors are a feature borrowed from Mirror v4, allowing the smart binding of user-defined interface methods to hidden methods. These are slower than normal method accessors, though still faster than using Java reflection.

Magic mirrors originally used Java's proxy system internally (which has a lot of needless bloat) but in Mirror v5 they were switched over to [Mimic](https://github.com/Moderocky/Mimic) which is a significantly faster and more adaptive replacement for proxies.

The magic mirror implementation in v4 used a proxy, performed a table lookup and then called the method invoker, costing around `2000` nanoseconds.
When switching to the v5 MethodAccessors and improving the table lookup, this was reduced to `1200` nanoseconds.
After switching from Java's proxies to [Mimic](https://github.com/Moderocky/Mimic), this process was reduced to `200` nanoseconds, almost six times faster than proxies.
By hardcoding the method calls to remove the table lookup and bridge call entirely, this averages at `33` nanoseconds and is comparable to using the MethodAccessor directly, meaning it takes a little more than 1.5% of the time that v4 took.

This means that for the first time ever, magic mirrors are a viable alternative in performance-dependent projects and implementations, especially those requiring a lot of repetitive calls which can benefit from JIT.

Another benefit of using Mimic is that the magic mirrors can use non-final default classes as templates, increasing developer freedom.

```java 
interface Test {
    int thing(int i); // mapped to a thing(I)I method on the target object
}
final Test test = Mirror.of(object)
    .magic(Test.class);
assert test.thing(3) == 5;
```

## Intrinsic Magic Mirrors

Intrinsic magic mirrors are the fastest possible way to repetitively call a member, consistently outperforming everything other than directly accessing the member itself.

This speed is achieved by removing as much as possible from the call, making it an almost-direct invocation of the object itself. The overhead for accessing a non-private member is under five nanoseconds, requiring only an `xload` bytecode instruction and a method call. 
The target handle is baked directly into the method implementation, removing the need to retrieve an accessor by index and call it.

As a result these have a lot less security and will throw critical errors if parameters or other type information is incorrect. As they use a provided class erasure, they cannot be calculated dynamically like MethodAccessors can, so they will not be suitable in cases where target erasure is unknown at compile-time. Boxing and type conversion is also unsupported.

Intrinsic magic mirrors also support field access through the `$` prefix before a method name.

```java 
interface Magic {
    int $number(); // retrieves the 'number' field - static or dynamic
    
    void $number(int i); // sets the 'number' field - static or dynamic
}

final Magic magic = Mirror.of(object).magicIntrinsic(Magic.class);
magic.$number(5);
assert magic.$number() == 5;
```

## Accessing Named Modules

Java 17+ has tried to make it impossible to access private members in named modules (such as `jdk.internal` resources.)
As some libraries depend on these, Mirror v5 has a way of accessing them. This is a particularly **dangerous** method, so it is advised not to be used unless absolutely necessary.

By default, Mirror will attempt to inject the accessor into whatever class-loader the target member is provided by, and then exports the accessor to the caller class.
This will allow access to named modules and even JDK internals, which reflection would not.

If this is not sufficient (for example, if a module has some way of blocking the export process) an accessor-chain can be used. This creates two separate accessors: an out-facing one is placed within some accessible namespace of the target module (the module must have *some* open-facing attachment point in order to be running within the same JVM as your code) and a bridge is placed in an intermediary namespace, relaying the access calls.

Calling the `unsafe` method on a mirror will replace the code-writer with one capable of building these chain-calls, but is only accessible in an environment where Java's `Unsafe` is accessible, since this is needed to access the native bootstrap classloader and inject the accessors.

Generally speaking, this chain version will not be necessary - even internal JDK modules are accessible via the smart export system.

```java 
final MethodAccessor<Class<?>[]> accessor = Mirror.of(Class.class)
    .unsafe()
    .method("getInterfaces0");
final Class<?>[] interfaces = accessor.invoke();
assert interfaces[0] == Serializable.class;
```

Using this unsafe behaviour is not recommended, but may be necessary for applications that previously depended on reflection to access secret JDK internals.

## Examples

Getting and using a method accessor:
```java 
final MethodAccessor<?> method = Mirror.of(System.out).method("println", String.class);
method.invoke("hello");
```

Using a method accessor in-line without a variable:
```java 
Mirror.of(System.out)
    .method("println", String.class)
    .invoke("hello");
```

Using the return value of a method accessor:
```java 
long value = (long) Mirror.of(System.class)
    .method("nanoTime")
    .invoke(); // the long primitive will be wrapped as a Long
assert value > -1;
```

Getting a field accessor and using its value:
```java 
final FieldAccessor<PrintStream> field = Mirror.of(System.class).field("out");
field.get().println("hello");
// the explicit <PrintStream> type allows us to directly call .println
```

Fast-mirroring a field value:
```java 
Mirror.of(System.class)
    .field("out")
    .mirror() // Calls Mirror.of on the field value
    .method("println", int.class)
    .invoke(2); // Boxed Integer is automatically unboxed 
```

Setting a field value:
```java 
Mirror.of(this)
    .field("word")
    .set("bean");
```

Using a constructor mirror:
```java 
Mirror.of(ConstructorAccessorTest.TestConstructor.class)
    .constructor(int.class, int.class)
    .newInstance(0, 0);
```

Creating and using a simple magic mirror:
```java 
interface MyTemplate {
    void myMethod(int i, int j);
    void myMethod(int i);
}
final MyTemplate template = Mirror.of(object)
    .magic(MyTemplate.class);
template.myMethod(6, 6);
template.myMethod(3);
```

Creating an advanced intrinsic magic mirror:
```java 
interface Magic {
    int $number(); // retrieves the 'number' field
    void $number(int i); // sets the 'number' field
    
    String getName();  // invokes the 'getName' method on the target
}

final Magic magic = Mirror.of(object).magicIntrinsic(Magic.class);
magic.$number(5);
assert magic.$number() == 5;
assert magic.getName().equals("Henry");
```
