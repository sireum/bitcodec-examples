// #Sireum

import org.sireum._
import org.sireum.U8._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_SixBytes: Z = 2

  val ERROR_Foo_elements: Z = 2

  val ERROR_Foo: Z = 4

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  object SixBytes {
    def empty: SixBytes = {
      return SixBytes(MSZ.create(6, u8"0"))
    }
  }

  @record class SixBytes(
    var sixBytes: MSZ[U8]
  ) extends Runtime.Composite {

    def wellFormed: Z = {

      if (sixBytes.size != 6) {
        return ERROR_SixBytes
      }

      // BEGIN USER CODE: SixBytes.wellFormed
      // ... empty
      // END USER CODE: SixBytes.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      Reader.MS.beU8S(input, context, sixBytes, 6)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8S(output, context, sixBytes)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_SixBytes)
      }
    }

  }

  object Foo {
    def empty: Foo = {
      return Foo(u8"0", MSZ[SixBytes]())
    }
  }

  @record class Foo(
    var size: U8,
    var elements: MSZ[SixBytes]
  ) extends Runtime.Composite {

    def wellFormed: Z = {

      val elementsSize = sizeOfElements(size)
      if (elements.size != elementsSize) {
        return ERROR_Foo_elements
      }

      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      size = Reader.MS.beU8(input, context)
      val elementsSize = sizeOfElements(size)
      if (elementsSize >= 0) {
        elements = MSZ.create(elementsSize, SixBytes.empty)
        for (i <- 0 until elementsSize) {
          elements(i).decode(input, context)
        }
      } else {
        context.signalError(ERROR_Foo_elements)
      }

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8(output, context, size)
      val elementsSize = sizeOfElements(size)
      if (elementsSize >= 0) {
        for (i <- 0 until elementsSize) {
          elements(i).encode(output, context)
        }
      } else {
        context.signalError(ERROR_Foo_elements)
      }

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Foo)
      }
    }

    def sizeOfElements(p: U8): Z = {
      val r: Z = {
        conversions.U8.toZ(p)
      }
      return r
    }
  }

}

// BEGIN USER CODE: Test
import BitCodec._
val fooExample = Foo(u8"3", MSZ(
  SixBytes(MSZ.create(6, u8"1")),
  SixBytes(MSZ.create(6, u8"2")),
  SixBytes(MSZ.create(6, u8"3"))
))
println(s"fooExample = $fooExample")

assert(fooExample.wellFormed == 0, "fooExample is not well-formed!")

val fooExampleOutput = MSZ.create(1000, F)
val fooExampleOutputContext = Context.create
fooExample.encode(fooExampleOutput, fooExampleOutputContext)
val fooExampleEncoded = Writer.resultMS(fooExampleOutput, fooExampleOutputContext)
println(s"encode(fooExample) = $fooExampleEncoded")
println(s"encode(fooExample).offset = ${fooExampleOutputContext.offset}")
println(s"encode(fooExample).errorCode = ${fooExampleOutputContext.errorCode}")
println(s"encode(fooExample).errorOffset = ${fooExampleOutputContext.errorOffset}")

assert(fooExampleOutputContext.errorCode == 0 && fooExampleOutputContext.errorOffset == 0, "Encoding error!")

val fooExampleInputContext = Context.create
val fooExampleDecoded = Foo.empty
fooExampleDecoded.decode(fooExampleEncoded, fooExampleInputContext)
println(s"decode(encode(fooExample)) = $fooExampleDecoded")
println(s"decode(encode(fooExample)).offset = ${fooExampleInputContext.offset}")
println(s"decode(encode(fooExample)).errorCode = ${fooExampleInputContext.errorCode}")
println(s"decode(encode(fooExample)).errorOffset = ${fooExampleInputContext.errorOffset}")

assert(fooExampleInputContext.errorCode == 0 && fooExampleInputContext.errorOffset == 0, "Decoding error!")
assert(fooExampleOutputContext.offset == fooExampleInputContext.offset, "The decoder does not consume the same number of bits produced by the encoder!")
assert(fooExample == fooExampleDecoded, s"$fooExample != $fooExampleDecoded")
// END USER CODE: Test