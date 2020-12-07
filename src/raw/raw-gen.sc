// #Sireum

import org.sireum._
import org.sireum.U8._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_Foo_elements: Z = 2

  val ERROR_Foo: Z = 3

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  object Foo {

    val maxSize: Z = z"-1"

    def empty: MFoo = {
      return MFoo(u8"0", MSZ[B]())
    }

    def decode(input: MSZ[B], context: Context): Option[Foo] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Foo]() else Some(r.toImmutable)
    }

  }

  @datatype class Foo(
    val size: U8,
    val elements: ISZ[B]
  ) {

    @strictpure def toMutable: MFoo = MFoo(size, elements.toMS)

    def encode(buffSize: Z, context: Context): MOption[MSZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) MNone[MSZ[B]]() else MSome(buffer)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MFoo(
    var size: U8,
    var elements: MSZ[B]
  ) extends Runtime.MComposite {

    @strictpure def toImmutable: Foo = Foo(size, elements.toIS)

    def wellFormed: Z = {

      val elementsSz = sizeOfElements(size)
      if (elements.size != elementsSz) {
        return ERROR_Foo_elements
      }

      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      size = Reader.MS.bleU8(input, context)
      val elementsSz = sizeOfElements(size)
      if (elementsSz >= 0) {
        elements = MSZ.create(elementsSz, F)
        Reader.MS.bleRaw(input, context, elements, elementsSz)
      } else {
        context.signalError(ERROR_Foo_elements)
      }

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleU8(output, context, size)
      val elementsSz = sizeOfElements(size)
      if (elementsSz >= 0) {
        Writer.bleRaw(output, context, elements, elementsSz)
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
val fooExample = MFoo(u8"3", MSZ(T, F, T))
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