// #Sireum

import org.sireum._
import org.sireum.U7._
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

  // END USER CODE: Members

  object Value {

    val maxSize: Z = z"8"

    def empty: MValue = {
      return MValue(F, u7"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Value] = {
      val r = empty
      r.decode(input.toMS, context)
      return if (context.hasError) None[Value]() else Some(r.toImmutable)
    }

  }

  @datatype class Value(
    val cont: B,
    val value: U7
  ) {

    @strictpure def toMutable: MValue = MValue(cont, value)

    def encode(output: MSZ[B], context: Context): Unit = {
      toMutable.encode(output, context)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MValue(
    var cont: B,
    var value: U7
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Value = Value(cont, value)

    def wellFormed: Z = {


      // BEGIN USER CODE: Value.wellFormed
      if (!cont) {
        return ERROR_Value
      }
      // END USER CODE: Value.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      cont = Reader.MS.bleB(input, context)
      value = Reader.MS.beU7(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleB(output, context, cont)
      Writer.beU7(output, context, value)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Value)
      }
    }

  }

  object Foo {

    val maxSize: Z = z"81"

    def empty: MFoo = {
      return MFoo(MSZ[MValue](), F)
    }

    def decode(input: ISZ[B], context: Context): Option[Foo] = {
      val r = empty
      r.decode(input.toMS, context)
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
    val end: B
  ) {

    @strictpure def toMutable: MFoo = MFoo(Foo.toMutableElements(elements), end)

    def encode(output: MSZ[B], context: Context): Unit = {
      toMutable.encode(output, context)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MFoo(
    var elements: MSZ[MValue],
    var end: B
  ) extends Runtime.Composite {

    @strictpure def toImmutable: Foo = Foo(Foo.toImmutableElements(elements), end)

    def wellFormed: Z = {

      if (elements.size > 10) {
        return ERROR_Foo_elements
      }

      // BEGIN USER CODE: Foo.wellFormed
      if (end) {
        return ERROR_Foo
      }
      // END USER CODE: Foo.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      elements = MSZ()
      while (matchElements(input, context)) {
        elements = elements :+ Value.empty
        elements(elements.size - 1).decode(input, context)
      }
      end = Reader.MS.bleB(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      for (i <- 0 until elements.size) {
        elements(i).encode(output, context)
      }
      Writer.bleB(output, context, end)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Foo)
      }
    }

    def matchElements(input: MSZ[B], context: Context): B = {
      var ctx = context
      var hasError = F
      if(!hasError) {
        hasError = !Reader.MS.bleB(input, ctx)
      }
      return !hasError
    }
  }

}

// BEGIN USER CODE: Test
import BitCodec._

val fooExample = MFoo(MSZ(MValue(T, u7"3"), MValue(T, u7"4"), MValue(T, u7"5")), F)
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