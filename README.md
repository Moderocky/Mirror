Mirror
=====

### Opus #4

A smart, simple and fast reflection API.
This is potentially the fastest reflection API possible, using internal hacks and tricks to cut out slow security checks.

Mirror is designed to have alternative reflection options that may benefit particular situations - allowing you to prioritise efficiency where it is needed, while balancing it with ease-of-use.


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
    <version>4.0.0</version>
    <scope>compile</scope>
</dependency>
```

## Introduction

A long description and examples for each mirror type are given below. Other examples can be found in the comprehensive unit tests, which cover most use-cases, and the assumptions on which this library depends.

Can't be bothered to read the readme? A mini-comparison can be found below.

Note - speed comparisons were done on the second invocation (as the first is unusually slow.)
Given how small the times are, it's difficult to measure these accurately and give a useful result.

In the time it takes you to read the table, you could have done them all 100000000 times.

It's up to you whether you think the performance differences are necessary.

> These comparisons are based on a number of repeat tests running on my laptop in a unit test with a dozen programs open, to simulate the worst case scenario. :)

|Type|Init Speed|Use Speed|Advantages|Disadvantages|
|----|----------|---------|----------|-------------|
|Direct Use|None|~300ns|(This is normal Java code.)| - |
|Normal Reflection|< 15000ns|< 10000ns|Reliable, well-known, easy to cache.|Lots of boilerplate, lots of checked exceptions to handle.|
|Basic Mirror|< 20000ns|< 10000ns|Reliable, easy to cache, no checked exceptions, helper methods.|Wrapper, can be slightly slower than normal reflection to start with.|
|Magic Mirror|> 100000ns|< 10000ns|Much easier to use, quite intuitive, auto-caching.|Always slower than normal reflection.|
|Fast Mirror|~25000ns|~1500ns|As fast as possible using reflection.|Involves precise JVM manipulation.|

## Basic Mirrors

Basic mirrors create a simple wrapper for objects, classes, `Method` and `Field` reflection classes.

They are designed to simplify the process and deal with unnecessary exceptions, access and module checks, as well as using generics to avoid ugly casting.

Basic mirrors are simple, reliable and reusable - it is generally best to cache them (and their field/method mirrors) rather than re-creating them to reduce overhead.

The more you use the same field/method mirror, the more efficient it will become! (Up to a point...)

### Examples
Alter fields, including final and effective constants.
```java 
// Alter fields, including final and effective constants
Mirror.of(object)
    .field("hello")
    .set("there");
```

Find and access fields, including inherited ones.
```java 
final String string = Mirror.of(object)
    .field("hello")
    .get();
final Blob blob = Mirror.of(object)
    .<Type>field("something") // Specify the field type if you want
    .<Blob>get(); // Auto-cast using type arguments
```
Find and call methods.
```java 
final Object result = Mirror.of(object)
    .method("myMethod") // Find the method
    .invoke();
    
// Direct smart invocation
Mirror.ofClass(clazz).invoke("methodName", param1, param2);
```

Create new instances from an object.
```java 
final MyClass obj = Mirror.of(object)
    .newParallelInstance();
```

Or from its class...
```java 
final Object b = Mirror.ofClass(cls)
    .newInstance(arg1, arg2, ...);
```

Even an array class...
```java 
final String[] strings = Mirror.ofClass(String[].class)
    .newInstance(10);
assert strings.length == 10;
```
Even if it has no nullary constructor!

```java 
final Constructor<?> thing = Mirror.ofClass(Constructor.class)
    .newInstance();
```

(Note that this may cause issues - all fields will be empty.)

## Fast Mirrors

Fast mirrors are designed to sacrifice some overhead time in their creation to make their immediate execution faster.

They are at least **twice as fast** as method invocation, and take on average between 25% and 35% of the time that a normal invocation does.
This speed increase is **significantly** higher on the first few calls.

To guarantee this time-save, the library's unit tests require it to be faster in order to build and deploy. :)

Normal method invocation will eventually catch up to our faster speed - after a few million calls!

How is this speed-increase achieved?

The JDK's standard `method.invoke(...)` has to go through several steps before actually invoking the method.
1. Check the caller has permission.
2. Check the class that called the method can access this module. (Particularly unhelpful for `jdk.internal` classes!)
3. Obtain the 'root' template method.
4. Obtain the MethodAccessor.
5. The MethodAccessor is a delegator - it has another MethodAccessor inside it, so obtain that.
6. Actually invoke the method!

Fast mirrors aim to skip straight to step #5 or #6 (depending on the availability.)

The JIT can eventually optimise repeat calls, but this takes either hundreds of thousands of invocations or use of the AOT compiler.

#### Warning!
Fast mirrors are not entirely without their risk.

Part of the preparation process involves temporarily re-writing the accessor object's header in memory. While this is only in effect for a few nano-seconds, during that time the object could potentially cause an error if something else tried to access it. This should be impossible - the JDK doesn't like giving out Accessor instances, and very little actually uses it, but it is theoretically possible. 

You can prevent an error by using a synchronisation lock on the object, but this could slow down your program - so the choice is yours.

### Examples

This is exactly the same as basic mirrors, except the method names start with `fast`! :)

```java 
final Mirror<Something> mirror = ...;
mirror.fastField("hello");
mirror.fastMethod("myMethod", String.class, int.class);
```

To get the best performance, caching the fast field/method mirrors is advised.

## Magic Mirrors

Magic mirrors sacrifice some efficiency and speed for ease-of-use.

They are designed to make it easier to manipulate so-called 'blind' objects - where the type cannot be used at compile-time.

To create a magic mirror, you first make an interface with the methods (and fields - more on this later) that you'll want to access.
This interface is then proxied at runtime, and the method invocations are passed to the real object.

The methods are matched by name/erasure, but they also try and allow sub/superclasses of objects - in case you can't see a parameter type at compile-time either. :)

This is slower than normal reflection, but quite a bit easier to use.

Fields may also be accessed, using `get$<name>` and `set$<name>` methods. See examples below.

### Examples

This can be done using a local interface...
(My personal favourite!)


Note that you don't need *all* the methods of the original class - only the ones you want!

```java 
final Sealed sealed = new Sealed();
interface Thing { // a local interface - practical for this :)
    int method(); // these match the methods present in Sealed.class
    String blob();
    Object getOriginal();
    Class<?> getOriginalClass();
}
final Thing unsealed = new Mirror<>(sealed).magic(Thing.class);
assert unsealed.method() == 1;
assert unsealed.blob().equals("hello");
```

Or using an interface defined elsewhere.
```java 
final Sealed sealed = new Sealed();
final Thing unsealed = new Mirror<>(sealed).magic(Thing.class);
assert unsealed.method() == 1;
assert unsealed.blob().equals("hello");
```

You can use multiple interfaces if you want. The result-proxy will inherit them both!

```java 
Foo unsealed = new Mirror<>(sealed).magic(Foo.class, Bar.class);
unsealed.method();
```

Using type parameters can help with that, but it can be a little cumbersome.

```java 
public <Generic extends Foo & Bar> // Type parameter declared here
void generic() { // We make a 'type' that extends Foo and Bar
        final Sealed sealed = new Sealed();
        final Generic unsealed = new Mirror<>(sealed)
            .magic(Foo.class, Bar.class);
        // Our class inherits both!
        unsealed.fooMethod();
        unsealed.barMethod();
        // Methods from both interfaces are present :)
    }
```

You can also access fields, using special `get$` and `set$` methods.

```java 
final Secret secret = new Secret();
assert secret != null;
interface Keeper {
    int get$number(); // gets the 'number' int field
    String get$string(); // gets the 'string' String field
    void set$string(String value); // sets the 'string' field
}
final Keeper keeper = Mirror.of(secret).magic(Keeper.class);
assert keeper.get$number() == 6;
assert keeper.get$string().equals("hello");
keeper.set$string("hi");
assert secret.string.equals("hi"); // the original field was changed
```

You can even use annotation types as a proxy.
Why you'd ever do this I don't know - but it's possible!
```java 
@interface Access {
    int method();
    String blob();
}

final Sealed sealed = new Sealed();
final Access unsealed = new Mirror<>(sealed).magic(Access.class);
assert unsealed.method() == 1;
assert unsealed.blob() == null;
```
