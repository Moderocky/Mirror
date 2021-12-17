Mirror
=====

### Opus #4

A smart, simple and fast alternative to Java's clunky reflection API.
This is potentially the fastest access API possible, avoiding all slow security checks.

Version 5 of Mirror abandoned trying to shortcut the JDK's reflection API and instead compiles direct access routes, using the most efficient method possible.

## Maven Information
```xml
<repository>
    <id>pan-repo</id>
    <name>Pandaemonium Repository</name>
    <url>https://gitlab.com/api/v4/projects/18568066/packages/maven</url>
</repository>
``` 

```xml
<dependency>
    <groupId>mx.kenzie</groupId>
    <artifactId>mirror</artifactId>
    <version>5.0.0</version>
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

| Access Type              | Average Speed |
|--------------------------|---------------|
| Normal Invocation        | ~4ns          |
| Access Public Member     | ~6ns          |
| Access Hidden Member     | ~40ns         |
| Access Restricted Member | ~80ns         |
| Reflection               | ~220ns        |
| Proxy Invocation         | ~1200ns       |

## Migrating from v4.0.0

Version 5 removes all alternative mirror types, and in return provides a much safer implementation of v4's 'fast mirror'.

An object or class is first targeted with `Mirror.of(thing)`, from which special 'accessors' can be created to use particular fields, methods and constructors.

## Magic Mirrors

Magic mirrors are a feature borrowed from Mirror v4, allowing the smart binding of user-defined interface methods to hidden methods. These are slower than normal method accessors, as they use Java's proxy system internally (which has a lot of needless boilerplate.)

In the future these will be changed to use [Glass](https://github.com/Moderocky/Glass) for a faster, more adaptive proxy creation system.

```java 
interface Test {
    int thing(int i); // mapped to a thing(I)I method on the target object
}
final Test test = Mirror.of(object)
    .magic(Test.class);
assert test.thing(3) == 5;
```

## Accessing Named Modules

Java 17+ has tried to make it impossible to access private members in named modules (such as `jdk.internal` resources.)
As some libraries depend on these, Mirror v5 has a way of accessing them. This is a particularly **dangerous** method, so it is advised not to be used unless absolutely necessary.

Calling the `unsafe` method on a mirror will replace the code-writer with one capable of using the native bootstrap classloader, which is traditionally inaccessible.

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
