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
      return MFoo(MSZ[U8](), u8"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Foo] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Foo]() else Some(r.toImmutable)
    }

    def toMutableElements(s: ISZ[U8]): MSZ[U8] = {
      var r = MSZ[U8]()
      for (e <- s) {
        r = r :+ e
      }
      return r
    }

    def toImmutableElements(s: MSZ[U8]): ISZ[U8] = {
      var r = ISZ[U8]()
      for (e <- s) {
        r = r :+ e
      }
      return r
    }
  }

  @datatype class Foo(
    val elements: ISZ[U8],
    val end: U8
  ) {

    @strictpure def toMutable: MFoo = MFoo(Foo.toMutableElements(elements), end)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MFoo(
    var elements: MSZ[U8],
    var end: U8
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Foo = Foo(Foo.toImmutableElements(elements), end)

    def wellFormed: Z = {


      // BEGIN USER CODE: Foo.wellFormed
      // ... empty
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      elements = MSZ()
      while (!matchElements(input, context)) {
        val value = Reader.IS.bleU8(input, context)
        elements = elements :+ value
      }
      end = Reader.IS.bleU8(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      for (i <- 0 until elements.size) {
        val value = elements(i)
        Writer.bleU8(output, context, value)
      }
      Writer.bleU8(output, context, end)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Foo)
      }
    }

    def matchElements(input: ISZ[B], context: Context): B = {
      var ctx = context
      var hasError = F
      if (!hasError) {
        val temp = Reader.IS.bleU8(input, ctx)
        hasError = !(ctx.errorCode == 0 && temp == u8"0")
      }
      return !hasError
    }
  }

}

// BEGIN USER CODE: Test
import BitCodec._

val fooExample = MFoo(MSZ(u8"3", u8"4", u8"5"), u8"0")
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
fooExampleDecoded.decode(fooExampleEncoded.toIS, fooExampleInputContext)
println(s"decode(encode(fooExample)) = $fooExampleDecoded")
println(s"decode(encode(fooExample)).offset = ${fooExampleInputContext.offset}")
println(s"decode(encode(fooExample)).errorCode = ${fooExampleInputContext.errorCode}")
println(s"decode(encode(fooExample)).errorOffset = ${fooExampleInputContext.errorOffset}")

assert(fooExampleInputContext.errorCode == 0 && fooExampleInputContext.errorOffset == 0, "Decoding error!")
assert(fooExampleOutputContext.offset == fooExampleInputContext.offset, "The decoder does not consume the same number of bits produced by the encoder!")
assert(fooExample == fooExampleDecoded, s"$fooExample != $fooExampleDecoded")
// END USER CODE: Test