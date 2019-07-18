# BitCodec Examples

This repository holds bit-codec spec examples in Slang (`*-spec.sc`)
along with their corresponding generated codec in Slang 
scripts (`*-gen.sc`) by Sireum's `bcgen` tool. 
In addition, the Slang codecs can be further translated
to C to produce native ones.

## Requirements

* [Sireum Kekinian](https://github.com/sireum/kekinian)
* C compiler toolchain (e.g., gcc, clang, make)
* CMake 3.6.2 or above


## Running on the JVM

* macOS/Linux:

  ```bash
  bin/build.cmd run
  ```

* Windows:

  ```bash
  bin\build.cmd run
  ```
  
## Running Natively (via C translation)

* macOS/Linux:

  ```bash
  bin/build.cmd run-native
  ```

* Windows:

  ```bash
  bin\build.cmd run-native
  ```
  
## Regenerating Slang Codecs

The repository already holds the generated codecs, however,
they can be regenerated as follows:

* macOS/Linux:

  ```bash
  bin/build.cmd gen
  ```

* Windows:

  ```bash
  bin\build.cmd gen
  ```
  
## Regenerating JSON

The repository already holds the generated specs in JSON, however,
they can be regenerated as follows:

* macOS/Linux:

  ```bash
  bin/build.cmd json
  ```

* Windows:

  ```bash
  bin\build.cmd json
  ```
  
## Regenerating GraphViz's .dot

The repository already holds the generated .dot file for each example spec, 
however, they can be regenerated as follows:

* macOS/Linux:

  ```bash
  bin/build.cmd dot
  ```

* Windows:

  ```bash
  bin\build.cmd dot
  ```

## Running All Tasks

To run all the tasks above in one go:

* macOS/Linux:

  ```bash
  bin/build.cmd all
  ```

* Windows:

  ```bash
  bin\build.cmd all
  ```


  
