## Mini Point of Sale System

### Prerequisite

This project is compiled with Scala 3. You need the following to run the codebase: 
- [sbt](https://www.scala-sbt.org/) (This script is tested with sbt v1.10.7)
- [A suitable version Java Development Kit](https://docs.scala-lang.org/overviews/jdk-compatibility/overview.html) (This script is tested with openjdk version "17.0.14")


### Running this program

You can run this program with `sbt run` or with `java -jar` after compilation.

#### sbt run

To run this program with sbt, Simply do the following:

``` sh
sbt "run Item1 Item2 Item3 ..."  # Items are optional
```

For example, 

``` sh
> sbt "run Apple Milk Bread"
[info] welcome to sbt 1.10.7 (Homebrew Java 23.0.2)
[info] loading settings for project adthena-tech-task-build from plugins.sbt...
[info] loading project definition from /path/to/Adthena-Tech-Task/project
[info] loading settings for project root from build.sbt...
[info] set current project to Adthena Tech Task (in build file:/path/to/Adthena-Tech-Task/)
[info] running shoppingCart Apple Milk Bread
Subtotal: £3.10
Apple 10% off: 10p
Total price: £3.00
[success] Total time: 0 s, completed 13 Feb 2025, 22:16:57

```

#### Compile and Run

This project is set up with the [sbt-assembly](https://github.com/sbt/sbt-assembly) plugin. 

To compile the project, simply run `sbt assembly`

You will be able to locate a jar file in the bottom of the compile message. 

```
...
[info] Built: /path/to/Adthena-Tech-Task/target/scala-3.6.3/Adthena Tech Task-assembly-0.1.0-SNAPSHOT.jar
[info] Jar hash: 588cb237ece606cea2fe8bfbcfa8a511d22dd32e
[success] Total time: 3 s, completed 13 Feb 2025, 22:02:25
```

To run the compiled program, you can use this command: 

``` sh
java -jar "target/scala-3.6.3/Adthena Tech Task-assembly-0.1.0-SNAPSHOT.jar" Item1 Item2 Item3 ...  # Items are optional
```

Currently, only four types of items are supported, `Soup`, `Bread`, `Milk` and `Apple`. You can see the list in `src/main/scala/Data.scala`.

### Development guide

#### Pre-commit hook

This repos is set up with a [pre-commit](https://pre-commit.com/) hook. It is recommended that you install it to run before commiting the code to the repos.

To install pre-commit, you can do so via pip with `pip install pre-commit`. I do so with uv with `uv tool install pre-commit`.

Once you have installed pre-commit, you can simply run `pre-commit install` in the project root once and it will run every time you do git commit.

#### Code testing

To run the tests, simply run `sbt test`.
