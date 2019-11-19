// #Sireum

import org.sireum._
import org.sireum.U8._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_Value: Z = 2

  val ERROR_Foo_elements: Z = 3

  val ERROR_Foo: Z = 4

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  object Value {

    val maxSize: Z = z"8"

    def empty: MValue = {
      return MValue(u8"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Value] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Value]() else Some(r.toImmutable)
    }

  }

  @datatype class Value(
    val value: U8
  ) {

    @strictpure def toMutable: MValue = MValue(value)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MValue(
    var value: U8
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Value = Value(value)

    def wellFormed: Z = {


      // BEGIN USER CODE: Value.wellFormed
      if (value == u8"0") {
        return ERROR_Value
      }
      // END USER CODE: Value.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      value = Reader.IS.beU8(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8(output, context, value)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Value)
      }
    }

  }

  object Foo {

    val maxSize: Z = z"-1"

    def empty: MFoo = {
      return MFoo(MSZ[MValue](), u8"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Foo] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Foo]() else Some(r.toImmutable)
    }

    def toMutableElements(s: ISZ[Value]): MSZ[MValue] = {
      var r = MSZ[MValue]()
      for (e <- s) {
        r = r :+ e.toMutable
      }
      return r
    }

    def toImmutableElements(s: MSZ[MValue]): ISZ[Value] = {
      var r = ISZ[Value]()
      for (e <- s) {
        r = r :+ e.toImmutable
      }
      return r
    }
  }

  @datatype class Foo(
    val elements: ISZ[Value],
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
    var elements: MSZ[MValue],
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
      while (elements.size < -1 && !matchElements(input, context)) {
        elements = elements :+ Value.empty
        elements(elements.size - 1).decode(input, context)
      }
      end = Reader.IS.beU8(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      for (i <- 0 until elements.size) {
        elements(i).encode(output, context)
      }
      Writer.beU8(output, context, end)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Foo)
      }
    }

    def matchElements(input: ISZ[B], context: Context): B = {
      var ctx = context
      var hasError = F
      if (!hasError) {
        val temp = Reader.IS.beU8(input, ctx)
        hasError = !(ctx.errorCode == 0 && temp == u8"0")
      }
      return !hasError
    }
  }

}

// BEGIN USER CODE: Test
import BitCodec._

val fooExample = MFoo(MSZ(MValue(u8"3"), MValue(u8"4"), MValue(u8"5")), u8"0")
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