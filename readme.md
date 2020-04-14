# Bitcodec Examples

Bitcodec is a tool for specifying and generating Slang 
bit-precise (little/big-endian) encoder/decoder for processing raw byte arrays from/to structured data.
The specification input is currently a Slang script that construct
bitcodec specification objects. 
This allows one to use the full Slang language features (including IDE support) to specify data layouts. 

This repository holds bitcodec spec examples in Slang (`*-spec.sc`)
along with their corresponding generated codec in Slang 
scripts (`*-gen.sc`) and other artifacts (e.g., graph visualization of the specified
bit-level data layout -- `*-spec.dot`).
The generated Slang codecs can be further translated to C to produce native ones.

* [Running Examples](#running-examples)
* [Quick References](#quick-references)
  * [Scalars](#scalars)
  * [Concat](#concat)
  * [Unions](#unions)
  * [Repeats](#repeats)
  * [Raws](#raws)

## Running Examples

### Requirements

* [Sireum Kekinian](https://github.com/sireum/kekinian)
* [GraphViz](https://www.graphviz.org)'s `dot`
* C compiler toolchain (e.g., gcc, clang, make)
* CMake 3.6.2 or above

### Running on the JVM

* macOS/Linux:

  ```bash
  bin/build.cmd run
  ```

* Windows:

  ```cmd
  bin\build.cmd run
  ```
  
### Running Natively (via C translation)

* macOS/Linux:

  ```bash
  bin/build.cmd run-native
  ```

* Windows:

  ```cmd
  bin\build.cmd run-native
  ```
  
### Regenerating Slang Codecs

The repository already holds the generated codecs, however,
they can be regenerated as follows:

* macOS/Linux:

  ```bash
  bin/build.cmd gen
  ```

* Windows:

  ```cmd
  bin\build.cmd gen
  ```
  
### Regenerating JSON

The repository already holds the generated specs in JSON, however,
they can be regenerated as follows:

* macOS/Linux:

  ```bash
  bin/build.cmd json
  ```

* Windows:

  ```cmd
  bin\build.cmd json
  ```
  
### Regenerating GraphViz's .dot

The repository already holds the generated .dot file for each example spec, 
however, they can be regenerated as follows:

* macOS/Linux:

  ```bash
  bin/build.cmd dot
  ```

* Windows:

  ```cmd
  bin\build.cmd dot
  ```

### Running All Tasks

To run all the tasks above in one go:

* macOS/Linux:

  ```bash
  bin/build.cmd all
  ```

* Windows:

  ```cmd
  bin\build.cmd all
  ```

### Running Task(s) on a Specific Example

To run a specific example for any task (e.g., run):
  
* macOS/Linux:

  ```bash
  bin/build.cmd run basic-spec.sc
  ```

* Windows:

  ```cmd
  bin\build.cmd run basic-spec.sc
  ```

## Quick References

As mentioned previously, bitcodec accepts a Slang script as its input.
Essentially, one has to program the specification AST object directly in Slang, and
then the script has to print the JSON representation of the specification AST 
using the provided bitcodec API (i.e., `println(<spec>.toJSON(T)`):

https://github.com/sireum/runtime/blob/master/library/shared/src/main/scala/org/sireum/bitcodec/Spec.scala

The bitcodec tool runs the script and parses: (a) the JSON string, and (b) the original
input, to generate specified encoder/decoder and its corresponding AST; 
it can also generate other artifacts such as graph visualization of the data bit-level layout.

Below are some quick references of the API for constructing bitcodec `<spec>` objects.
Note that the top-level `<spec>` object has to be a [Concat](#concat).

* [Scalars](#scalars)
* [Concat](#concat)
* [Unions](#unions)
* [Repeats](#repeats)
* [Raws](#raws)

### Scalars

Example: [src/basic/basic-spec.sc](src/basic/basic-spec.sc)

Note: the field `<name>` of scalar `<spec>` has to start with a lower-case alphabet.

* `Boolean(<name>)`: 1-bit field `<name>` of true (1) or false (0)

* `Bits(<name>, <n>)`: `<n>` number of bits field `<name>`

* 8-bit integers
  
  * signed
  
    * `Byte(<name>)`: 8-bit signed integer field `<name>`
    * `ByteConst(<name>, <value>)`: 8-bit signed integer field `<name>` with an expected `<value>`
    * `ByteRange(<name>, <min>, <max>)`: 8-bit signed integer field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
    * `Bytes(<name>, <n>)`: `<n>` number of 8-bit signed integers field `<name>`
    * `BytesRange(<name>, <n>, <min>, <max>)`: `<n>` number of 8-bit signed integers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)
  
  * unsigned

    * `UByte(<name>, <value>)`: 8-bit unsigned integer field `<name>`
    * `UByteConst(<name>, <value>)`: 8-bit unsigned integer field `<name>` with an expected `<value>`
    * `UByteRange(<name>, <min>, <max>)`: 8-bit unsigned integer field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
    * `UBytes(<name>, <n>)`: `<n>` number of 8-bit unsigned integers field `<name>`
    * `UBytesRange(<name>, <n>, <min>, <max>)`: `<n>` number of 8-bit unsigned integers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)

* 16-bit integers
  
  * signed
  
    * `Short(<name>, <value>)`: 16-bit signed integer field `<name>`
    * `ShortConst(<name>, <value>)`: 16-bit signed integer field `<name>` with an expected `<value>`
    * `ShortRange(<name>, <min>, <max>)`: 16-bit signed integer field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
    * `Shorts(<name>, <n>)`: `<n>` number of 16-bit signed integers field `<name>`
    * `ShortsRange(<name>, <n>, <min>, <max>)`: `<n>` number of 16-bit signed integers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)
  
  * unsigned

    * `UShort(<name>, <value>)`: 16-bit unsigned integer field `<name>`
    * `UShortConst(<name>, <value>)`: 16-bit unsigned integer field `<name>` with an expected `<value>`
    * `UShortRange(<name>, <min>, <max>)`: 16-bit unsigned integer field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
    * `UShorts(<name>, <n>)`: `<n>` number of 16-bit unsigned integers field `<name>`
    * `UShortsRange(<name>, <n>, <min>, <max>)`: `<n>` number of 16-bit unsigned integers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)

* 32-bit integers
  
  * signed
  
    * `Int(<name>)`: 32-bit signed integer field `<name>`
    * `IntConst(<name>, <value>)`: 32-bit signed integer field `<name>` with an expected `<value>`
    * `IntRange(<name>, <min>, <max>)`: 32-bit signed integer field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
    * `Ints(<name>, <n>)`: `<n>` number of 32-bit signed integers field `<name>`
    * `IntsRange(<name>, <n>, <min>, <max>)`: `<n>` number of 32-bit signed integers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)
  
  * unsigned

    * `UInt(<name>)`: 32-bit unsigned integer field `<name>`
    * `UIntConst(<name>, <value>)`: 32-bit unsigned integer field `<name>` with an expected `<value>`
    * `UIntRange(<name>, <min>, <max>)`: 32-bit unsigned integer field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
    * `UInts(<name>, <n>)`: `<n>` number of 32-bit unsigned integers field `<name>`
    * `UIntsRange(<name>, <n>, <min>, <max>)`: `<n>` number of 32-bit unsigned integers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)

* 64-bit integers
  
  * signed
  
    * `Long(<name>)`: 64-bit signed integer field `<name>`
    * `LongConst(<name>, <value>)`: 64-bit signed integer field `<name>` with an expected `<value>`
    * `LongRange(<name>, <min>, <max>)`: 64-bit signed integer field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
    * `Longs(<name>, <n>)`: `<n>` number of 64-bit signed integers field `<name>`
    * `LongsRange(<name>, <n>, <min>, <max>)`: `<n>` number of 64-bit signed integers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)
  
  * unsigned

    * `ULong(<name>)`: 64-bit unsigned integer field `<name>`
    * `ULongConst(<name>, <value>)`: 64-bit unsigned integer field `<name>` with an expected `<value>`
    * `ULongRange(<name>, <min>, <max>)`: 64-bit unsigned integer field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
    * `ULongs(<name>, <n>)`: `<n>` number of 64-bit unsigned integers field `<name>`
    * `ULongsRange(<name>, <n>, <min>, <max>)`: `<n>` number of 64-bit unsigned integers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)

* 32-bit (single-precision) floating-point number

  * `Float(<name>)`: 32-bit (single-precision) floating-point number field `<name>`
  * `FloatRange(<name>)`: 32-bit (single-precision) floating-point number field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
  * `Floats(<name>, <n>)`: `<n>` number of 32-bit (single-precision) floating-point numbers field `<name>`
  * `FloatsRange(<name>, <n>, <min>, <max>)`: `<n>` number of 32-bit (single-precision) floating-point numbers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)

* 64-bit (single-precision) floating-point number

  * `Double(<name>)`: 64-bit (single-precision) floating-point number field `<name>`
  * `DoubleRange(<name>)`: 64-bit (single-precision) floating-point number field `<name>` with an expected value between `<min>` and `<max>` (inclusive)
  * `Doubles(<name>, <n>)`: `<n>` number of 64-bit (single-precision) floating-point numbers field `<name>`
  * `DoublesRange(<name>, <n>, <min>, <max>)`: `<n>` number of 64-bit (single-precision) floating-point numbers field `<name>`, each with an expected value between `<min>` and `<max>` (inclusive)

* Padding `Pads(<n>)`: skip `<n>` number of bits


### Concat

A concat specifies a sequence of the specified nested elements.

Example: [src/basic/basic-spec.sc](src/basic/basic-spec.sc)

`Concat(<name>, ISZ(<spec>, ..., <spec>))`

Note: the field `<name>` of a concat has to start with an upper-case alphabet.

### Unions

A union specifies a choice of the specified nested elements which can be distinguished by looking ahead/back.

Note: the field `<name>` of a union has to start with an upper-case alphabet.

#### Union

A union that chooses one of the specified elements based on previously decoded value(s).

Example: [src/union/union-spec.sc](src/union/union-spec.sc)

`Union[<T-1>, ..., <T-N>](<name>, ISZ(<access-1>, ... <access-N>), <var> => <exp>, ISZ(<spec>, ..., <spec>))`

where:

* `<access-x>` is the access expression of type `<T-x>` (as a string)
* `<var>` is of tuple type `(<T-1>, ..., <T-N>)`
* `<exp>` computes/chooses the index of the `<spec>` to use (based on `<var>`)
* each `<spec>` that is not a `Concat` will be auto-wrapped in one

#### PredUnion

A union that chooses one of the specified elements based on what comes next based on
predictive sequences of value(s).

Examples: 
* [src/predunion/simple/predunion-simple-spec.sc](src/predunion/simple/predunion-simple-spec.sc)
* [src/predunion/complex/predunion-complex-spec.sc](src/predunion/complex/predunion-complex-spec.sc)

`PredUnion(<name>, ISZ(<pred-spec>, ..., <pred-spec>))`

where `<pred-spec>` is:

`PredSpec(ISZ(<pred>, ..., <pred>), <spec>)`

Refer to [Predictive Value Matching Specifications](#predictive-value-matching-specifications) for `<pred>`.

#### GenUnion  

A union that chooses one of the specified elements by manually implementing 
the distinguishing logic in the generated encoder/decoder.

Example: [src/genunion/genunion-spec.sc](src/genunion/genunion-spec.sc)

`GenUnion(<name>, ISZ(<spec>, ..., <spec>))`

### Repeats

A repeat that specifies multiple occurrences of the specified nested element by looking ahead/back.
Each repeat kind has a bounded version and an unbounded version, where the former 
limits the maximum number of occurrences.

Note: the field `<name>` of a repeat has to start with a lower-case alphabet.

#### Repeat

A repeat that specifies multiple occurrences based on previously decoded value(s).

Example: [src/repeat/repeat-spec.sc](src/repeat/repeat-spec.sc)

`Repeat[<T-1>, ..., <T-N>](<name>, ISZ(<access-1>, ..., <access-N>), <var> => <exp>, <spec>)`

where:

* `<access-x>` is the access expression of type `<T-x>` (as a string)
* `<var>` is of tuple type `(<T-1>, ..., <T-N>)`
* `<exp>` computes the number of occurrences of `<spec>` (based on `<var>`)
* if the `<spec>` is not a `Concat`, it will be auto-wrapped in one

#### PredRepeatWhile

A repeat that accepts multiple occurrences as long as they satisfy some predictive sequences of value(s).

Example: [src/predrepeatwhile/predrepeatwhile-spec.sc](src/predrepeatwhile/predrepeatwhile-spec.sc)

* `BoundedPredRepeatWhile(<name>, <max>, ISZ(<pred>, ..., <pred>), <spec>)`
* `PredRepeatWhile(<name>, ISZ(<pred>, ..., <pred>), <spec>)`

Refer to [Predictive Value Matching Specifications](#predictive-value-matching-specifications) for `<pred>`.

#### PredRepeatUntil

A repeat that accepts multiple occurrences as long as they do not satisfy some predictive sequences of value(s).

Example: [src/predrepeatuntil/predrepeatuntil-spec.sc](src/predrepeatuntil/predrepeatuntil-spec.sc)

* `BoundedPredRepeatUntil(<name>, <max>, ISZ(<pred>, ..., <pred>), <spec>)`
* `PredRepeatUntil(<name>, ISZ(<pred>, ..., <pred>), <spec>)`

Refer to [Predictive Value Matching Specifications](#predictive-value-matching-specifications) for `<pred>`.

#### GenRepeat

A repeat that accepts multiple occurrences by manually implementing 
the repeating logic in the generated encoder/decoder.

Example: [src/genrepeat/genrepeat-spec.sc](src/genrepeat/genrepeat-spec.sc)

* `BoundedGenRepeat(<name>, <max>, <spec>)`
* `GenRepeat(<name>, <spec>)`

### Raws

A raw specifies a variable number of bits.

Note: the field `<name>` of a repeat has to start with a lower-case alphabet.

#### Raw

A raw that accepts a variable number of bits based on previously decoded value(s).

Example: [src/raw/raw-spec.sc](src/raw/raw-spec.sc)

* `BoundedRaw[T-1, ..., T-N](<name>, <max>, ISZ(<access-1>, ..., <access-N>), <var> => <exp>)`
* `Raw[T-1, ..., T-N](<name>, ISZ(<access-1>, ..., <access-N>), <var> => <exp>)`

where:

* `<access-x>` is the access expression of type `<T-x>` (as a string)
* `<var>` is of tuple type `(<T-1>, ..., <T-N>)`
* `<exp>` computes the number of bits (based on `<var>`)

#### GenRaw

A raw that accepts a variable number of bits by manually implementing 
the size function logic in the generated encoder/decoder.

Example: [src/genraw/genraw-spec.sc](src/genraw/genraw-spec.sc)

* `BoundedGenRaw(<name>, <max>)`
* `GenRaw(<name>)`
 
#### Predictive Value Matching Specifications

A `<pred>` can be one of the following:

* `boolean(<value>)`: matches a 1-bit `<value>` of either `true` or `false`

* `bits(<n>, <value>)`: matches a `<n>`-bit integer `<value>`

* `bytes(ISZ(<value>, ..., <value>))`: matches a sequence of (signed) 8-bit integer `<value>`

* `shorts(ISZ(<value>, ..., <value>))`: matches a sequence of (signed) 16-bit integer `<value>`

* `ints(ISZ(<value>, ..., <value>))`: matches a sequence of (signed) 32-bit integer `<value>`

* `longs(ISZ(<value>, ..., <value>))`: matches a sequence of (signed) 64-bit integer `<value>`

* `skip(<n>)`: matches skips `<n>` bits 

* `between(<n>, <min>, <max>)`: matches an `<n>`-bit (signed) integer between `<min>` and `<max>` (inclusive) 

* `not(<pred-spec>)`: matches if `<pred-spec>` does not match

* `or(ISZ(<pred-spec>), ..., <pred-spec>)`: matches if any of the `<pred-spec>`s matches

* ... more can be added