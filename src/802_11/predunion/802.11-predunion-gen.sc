// #Sireum

import org.sireum._
import org.sireum.U1._
import org.sireum.U2._
import org.sireum.U4._
import org.sireum.U8._
import org.sireum.U12._
import org.sireum.U32._
import org.sireum.ops.Bits.{Context, Reader, Writer}
import org.sireum.bitcodec.Runtime

// BEGIN USER CODE: Imports
// ... empty
// END USER CODE: Imports

object BitCodec {

  val ERROR_Frame: Z = 2

  val ERROR_FrameControl: Z = 3

  val ERROR_Cts: Z = 4

  val ERROR_Rts: Z = 7

  val ERROR_SeqControl: Z = 10

  val ERROR_Data_body: Z = 11

  val ERROR_Data: Z = 12

  val ERROR_MacFrame: Z = 13

  // BEGIN USER CODE: Members
  // ... empty
  // END USER CODE: Members

  @enum object Frame {
    'Management
    'Control
    'Data
    'Reserved
  }

  def decodeFrame(input: MSZ[B], context: Context): Frame.Type = {
    if (context.offset + 2 > input.size) {
      context.signalError(ERROR_Frame)
    }
    if (context.hasError) {
      return Frame.Management
    }
    val r: Frame.Type = Reader.MS.beU2(input, context) match {
      case u2"0" => Frame.Management
      case u2"1" => Frame.Control
      case u2"2" => Frame.Data
      case u2"3" => Frame.Reserved
      case _ =>
        context.signalError(ERROR_Frame)
        Frame.Management
    }
    return r
  }

  def encodeFrame(output: MSZ[B], context: Context, tpe: Frame.Type): Unit = {
    if (context.offset + 2 > output.size) {
      context.signalError(ERROR_Frame)
    }
    if (context.hasError) {
      return
    }
    tpe match {
      case Frame.Management => Writer.beU2(output, context, u2"0")
      case Frame.Control => Writer.beU2(output, context, u2"1")
      case Frame.Data => Writer.beU2(output, context, u2"2")
      case Frame.Reserved => Writer.beU2(output, context, u2"3")
    }
  }

  object FrameControl {

    val maxSize: Z = z"16"

    def empty: FrameControl = {
      return FrameControl(u2"0", Frame.Management, u4"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0")
    }
  }

  @record class FrameControl(
    var protocol: U2,
    var tpe: Frame.Type,
    var subType: U4,
    var toDS: U1,
    var fromDS: U1,
    var moreFrag: U1,
    var retry: U1,
    var powerMgmt: U1,
    var moreData: U1,
    var wep: U1,
    var order: U1
  ) extends Runtime.Composite {

    def wellFormed: Z = {


      // BEGIN USER CODE: FrameControl.wellFormed
      tpe match {
        case Frame.Management if (u4"0" <= subType && subType <= u4"5") || (u4"8" <= subType && subType <= u4"12") =>
        case Frame.Control if u4"10" <= subType && subType <= u4"15" =>
        case Frame.Data if (u4"0" <= subType && subType <= u4"2") || (u4"5" <= subType && subType <= u4"7") =>
        case Frame.Reserved =>
        case _ => return ERROR_FrameControl
      }
      // ... put more checks here
      // END USER CODE: FrameControl.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      protocol = Reader.MS.beU2(input, context)
      tpe = decodeFrame(input, context)
      subType = Reader.MS.beU4(input, context)
      toDS = Reader.MS.bleU1(input, context)
      fromDS = Reader.MS.bleU1(input, context)
      moreFrag = Reader.MS.bleU1(input, context)
      retry = Reader.MS.bleU1(input, context)
      powerMgmt = Reader.MS.bleU1(input, context)
      moreData = Reader.MS.bleU1(input, context)
      wep = Reader.MS.bleU1(input, context)
      order = Reader.MS.bleU1(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU2(output, context, protocol)
      encodeFrame(output, context, tpe)
      Writer.beU4(output, context, subType)
      Writer.bleU1(output, context, toDS)
      Writer.bleU1(output, context, fromDS)
      Writer.bleU1(output, context, moreFrag)
      Writer.bleU1(output, context, retry)
      Writer.bleU1(output, context, powerMgmt)
      Writer.bleU1(output, context, moreData)
      Writer.bleU1(output, context, wep)
      Writer.bleU1(output, context, order)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_FrameControl)
      }
    }

  }

  object Cts {

    val maxSize: Z = z"112"

    def empty: Cts = {
      return Cts(FrameControl.empty, MSZ.create(2, u8"0"), MSZ.create(6, u8"0"), u32"0")
    }
  }

  @record class Cts(
    var frameControl: FrameControl,
    var duration: MSZ[U8],
    var receiver: MSZ[U8],
    var fcs: U32
  ) extends MacFrame {

    def wellFormed: Z = {

      val wfFrameControl = frameControl.wellFormed
      if (wfFrameControl != 0) {
        return wfFrameControl
      }

      if (duration.size != 2) {
        return ERROR_Cts
      }

      if (receiver.size != 6) {
        return ERROR_Cts
      }

      // BEGIN USER CODE: Cts.wellFormed

      // END USER CODE: Cts.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      frameControl.decode(input, context)
      Reader.MS.beU8S(input, context, duration, 2)
      Reader.MS.beU8S(input, context, receiver, 6)
      fcs = Reader.MS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      frameControl.encode(output, context)
      Writer.beU8S(output, context, duration)
      Writer.beU8S(output, context, receiver)
      Writer.beU32(output, context, fcs)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Cts)
      }
    }

  }

  object Rts {

    val maxSize: Z = z"160"

    def empty: Rts = {
      return Rts(FrameControl.empty, MSZ.create(2, u8"0"), MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), u32"0")
    }
  }

  @record class Rts(
    var frameControl: FrameControl,
    var duration: MSZ[U8],
    var receiver: MSZ[U8],
    var transmitter: MSZ[U8],
    var fcs: U32
  ) extends MacFrame {

    def wellFormed: Z = {

      val wfFrameControl = frameControl.wellFormed
      if (wfFrameControl != 0) {
        return wfFrameControl
      }

      if (duration.size != 2) {
        return ERROR_Rts
      }

      if (receiver.size != 6) {
        return ERROR_Rts
      }

      if (transmitter.size != 6) {
        return ERROR_Rts
      }

      // BEGIN USER CODE: Rts.wellFormed

      // END USER CODE: Rts.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      frameControl.decode(input, context)
      Reader.MS.beU8S(input, context, duration, 2)
      Reader.MS.beU8S(input, context, receiver, 6)
      Reader.MS.beU8S(input, context, transmitter, 6)
      fcs = Reader.MS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      frameControl.encode(output, context)
      Writer.beU8S(output, context, duration)
      Writer.beU8S(output, context, receiver)
      Writer.beU8S(output, context, transmitter)
      Writer.beU32(output, context, fcs)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Rts)
      }
    }

  }

  object SeqControl {

    val maxSize: Z = z"16"

    def empty: SeqControl = {
      return SeqControl(u4"0", u12"0")
    }
  }

  @record class SeqControl(
    var fragNumber: U4,
    var seqNumber: U12
  ) extends Runtime.Composite {

    def wellFormed: Z = {


      // BEGIN USER CODE: SeqControl.wellFormed
      // ... empty
      // END USER CODE: SeqControl.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      fragNumber = Reader.MS.beU4(input, context)
      seqNumber = Reader.MS.beU12(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU4(output, context, fragNumber)
      Writer.beU12(output, context, seqNumber)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_SeqControl)
      }
    }

  }

  object Data {

    val maxSize: Z = z"-1"

    def empty: Data = {
      return Data(FrameControl.empty, MSZ.create(2, u8"0"), MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), SeqControl.empty, MSZ.create(6, u8"0"), MSZ[B](), u32"0")
    }
  }

  @record class Data(
    var frameControl: FrameControl,
    var duration: MSZ[U8],
    var address1: MSZ[U8],
    var address2: MSZ[U8],
    var address3: MSZ[U8],
    var seqControl: SeqControl,
    var address4: MSZ[U8],
    var body: MSZ[B],
    var fcs: U32
  ) extends MacFrame {

    def wellFormed: Z = {

      val wfFrameControl = frameControl.wellFormed
      if (wfFrameControl != 0) {
        return wfFrameControl
      }

      if (duration.size != 2) {
        return ERROR_Data
      }

      if (address1.size != 6) {
        return ERROR_Data
      }

      if (address2.size != 6) {
        return ERROR_Data
      }

      if (address3.size != 6) {
        return ERROR_Data
      }

      val wfSeqControl = seqControl.wellFormed
      if (wfSeqControl != 0) {
        return wfSeqControl
      }

      if (address4.size != 6) {
        return ERROR_Data
      }

      val bodySize = sizeOfBody((frameControl.tpe, frameControl.subType))
      if (body.size != bodySize) {
        return ERROR_Data_body
      }

      // BEGIN USER CODE: Data.wellFormed
      // ... empty
      // END USER CODE: Data.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      frameControl.decode(input, context)
      Reader.MS.beU8S(input, context, duration, 2)
      Reader.MS.beU8S(input, context, address1, 6)
      Reader.MS.beU8S(input, context, address2, 6)
      Reader.MS.beU8S(input, context, address3, 6)
      seqControl.decode(input, context)
      Reader.MS.beU8S(input, context, address4, 6)
      val bodySize = sizeOfBody((frameControl.tpe, frameControl.subType))
      if (bodySize >= 0) {
        body = MSZ.create(bodySize, F)
        Reader.MS.bleRaw(input, context, body, bodySize)
      } else {
        context.signalError(ERROR_Data_body)
      }
      fcs = Reader.MS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      frameControl.encode(output, context)
      Writer.beU8S(output, context, duration)
      Writer.beU8S(output, context, address1)
      Writer.beU8S(output, context, address2)
      Writer.beU8S(output, context, address3)
      seqControl.encode(output, context)
      Writer.beU8S(output, context, address4)
      val bodySize = sizeOfBody((frameControl.tpe, frameControl.subType))
      if (bodySize >= 0) {
        Writer.bleRaw(output, context, body, bodySize)
      } else {
        context.signalError(ERROR_Data_body)
      }
      Writer.beU32(output, context, fcs)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Data)
      }
    }

    def sizeOfBody(p: (Frame.Type, U4)): Z = {
      val r: Z = {
        p match {
          case /* CTS */ (Frame.Control, u4"0xC") => 0
          case /* RTS */ (Frame.Control, u4"0xB") => 0
          case _ => -1
        }
      }
      return r
    }
  }

  @record trait MacFrame extends Runtime.Composite

  object MacFrame {

    val maxSize: Z = z"-1"

    def empty: MacFrame = {
      return Cts.empty
    }

    @enum object Choice {
       'Cts
       'Rts
       'Data
       'Error
    }

    def choose(input: MSZ[B], context: Context): Choice.Type = {
      {
        var ctx = context
        var hasError = F
        if (!hasError) {
          ctx.skip(input.size, 2, -1)
          hasError = !(ctx.errorCode == 0)
        }
        if (!hasError) {
          val temp = Reader.MS.beU2(input, ctx)
          hasError = !(ctx.errorCode == 0 && temp == u2"1")
        }
        if (!hasError) {
          val temp = Reader.MS.beU4(input, ctx)
          hasError = !(ctx.errorCode == 0 && temp == u4"12")
        }
        if (!hasError && ctx.errorCode == 0) {
          return Choice.Cts
        }
      }
      ;{
        var ctx = context
        var hasError = F
        if (!hasError) {
          ctx.skip(input.size, 2, -1)
          hasError = !(ctx.errorCode == 0)
        }
        if (!hasError) {
          val temp = Reader.MS.beU2(input, ctx)
          hasError = !(ctx.errorCode == 0 && temp == u2"1")
        }
        if (!hasError) {
          val temp = Reader.MS.beU4(input, ctx)
          hasError = !(ctx.errorCode == 0 && temp == u4"11")
        }
        if (!hasError && ctx.errorCode == 0) {
          return Choice.Rts
        }
      }
      ;{
        var ctx = context
        var hasError = F
        if (!hasError) {
          ctx.skip(input.size, 2, -1)
          hasError = !(ctx.errorCode == 0)
        }
        if (!hasError) {
          val temp = Reader.MS.beU2(input, ctx)
          hasError = !(ctx.errorCode == 0 && temp == u2"2")
        }
        if (!hasError && ctx.errorCode == 0) {
          return Choice.Data
        }
      }
      return Choice.Error
    }
  }

}

// BEGIN USER CODE: Test
// Test 802.11 AST & codec
val ctsExample = MSZ(
  F, F,                   // protocol
  T, F,                   // tpe = control
  F, F, T, T,             // subType = CTS
  F,                      // toDS
  F,                      // fromDS
  F,                      // moreFrag
  F,                      // retry
  F,                      // powerMgmt
  F,                      // moreData
  F,                      // wep
  F,                      // order
  T, T, T, T, T, T, T, T, // duration
  F, F, F, F, F, F, F, F,
  T, T, T, T, T, T, T, T, // address
  F, F, F, F, F, F, F, F,
  T, T, T, T, T, T, T, T,
  F, F, F, F, F, F, F, F,
  T, T, T, T, T, T, T, T,
  T, T, T, T, T, T, T, T,
  F, F, F, F, F, F, F, F, // fcs
  F, F, F, F, F, F, F, F,
  F, F, F, F, F, F, F, F,
  T, T, T, T, T, T, T, T,
)

println(s"ctsExample = $ctsExample, ctsExample.size = ${ctsExample.size}")
println()

val macFrame = BitCodec.MacFrame.empty
val ctsExampleInputContext = Context.create
macFrame.decode(ctsExample, ctsExampleInputContext)

println(s"decode(ctsExample) = $macFrame")
println(s"decode(ctsExample).offset = ${ctsExampleInputContext.offset}")
println(s"decode(ctsExample).errorCode = ${ctsExampleInputContext.errorCode}")
println(s"decode(ctsExample).errorOffset = ${ctsExampleInputContext.errorOffset}")
println()

val ctsExampleOutput = MSZ.create(7981 * 8, F) // this can be any size >= 112 for this particular example
val ctsExampleOutputContext = Context.create
macFrame.encode(ctsExampleOutput, ctsExampleOutputContext)
val ctsExampleCodec = Writer.resultMS(ctsExampleOutput, ctsExampleOutputContext)

println(s"encode(decode(ctxExample)) = $ctsExampleCodec, encode(decode(ctxExample)).size = ${ctsExampleCodec.size}")
println(s"encode(decode(ctsExample)).offset = ${ctsExampleOutputContext.offset}")
println(s"encode(decode(ctsExample)).errorCode = ${ctsExampleOutputContext.errorCode}")
println(s"encode(decode(ctsExample)).errorOffset = ${ctsExampleOutputContext.errorOffset}")

assert(ctsExample == ctsExampleCodec, s"$ctsExample != $ctsExampleCodec")
// END USER CODE: Test