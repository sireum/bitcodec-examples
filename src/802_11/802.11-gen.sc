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

  val ERROR_Receiver: Z = 4

  val ERROR_ReceiverTransmitter: Z = 5

  val ERROR_SeqControl: Z = 6

  val ERROR_Data: Z = 7

  val ERROR_HeaderAddress: Z = 8

  val ERROR_MacHeader: Z = 9

  val ERROR_MacFrame_body: Z = 10

  val ERROR_MacFrame: Z = 11

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

  object Receiver {
    def empty: Receiver = {
      return Receiver(MSZ.create(6, u8"0"))
    }
  }

  @record class Receiver(
    var receiver: MSZ[U8]
  ) extends HeaderAddress {

    def wellFormed: Z = {

      if (receiver.size != 6) {
        return ERROR_Receiver
      }

      // BEGIN USER CODE: Receiver.wellFormed
      // ... empty
      // END USER CODE: Receiver.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      Reader.MS.beU8S(input, context, receiver, 6)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8S(output, context, receiver)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Receiver)
      }
    }

  }

  object ReceiverTransmitter {
    def empty: ReceiverTransmitter = {
      return ReceiverTransmitter(MSZ.create(6, u8"0"), MSZ.create(6, u8"0"))
    }
  }

  @record class ReceiverTransmitter(
    var receiver: MSZ[U8],
    var transmitter: MSZ[U8]
  ) extends HeaderAddress {

    def wellFormed: Z = {

      if (receiver.size != 6) {
        return ERROR_ReceiverTransmitter
      }

      if (transmitter.size != 6) {
        return ERROR_ReceiverTransmitter
      }

      // BEGIN USER CODE: ReceiverTransmitter.wellFormed
      // ... empty
      // END USER CODE: ReceiverTransmitter.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      Reader.MS.beU8S(input, context, receiver, 6)
      Reader.MS.beU8S(input, context, transmitter, 6)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8S(output, context, receiver)
      Writer.beU8S(output, context, transmitter)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_ReceiverTransmitter)
      }
    }

  }

  object SeqControl {
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
    def empty: Data = {
      return Data(MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), SeqControl.empty, MSZ.create(6, u8"0"))
    }
  }

  @record class Data(
    var address1: MSZ[U8],
    var address2: MSZ[U8],
    var address3: MSZ[U8],
    var seqControl: SeqControl,
    var address4: MSZ[U8]
  ) extends HeaderAddress {

    def wellFormed: Z = {

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

      // BEGIN USER CODE: Data.wellFormed
      // ... empty
      // END USER CODE: Data.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      Reader.MS.beU8S(input, context, address1, 6)
      Reader.MS.beU8S(input, context, address2, 6)
      Reader.MS.beU8S(input, context, address3, 6)
      seqControl.decode(input, context)
      Reader.MS.beU8S(input, context, address4, 6)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.beU8S(output, context, address1)
      Writer.beU8S(output, context, address2)
      Writer.beU8S(output, context, address3)
      seqControl.encode(output, context)
      Writer.beU8S(output, context, address4)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Data)
      }
    }

  }

  @record trait HeaderAddress extends Runtime.Composite

  object HeaderAddress {

    @enum object Choice {
       'Receiver
       'ReceiverTransmitter
       'Data
       'Error
    }

    def choose(p: (Frame.Type, U4)): Choice.Type = {
      val r: Z = {
        p match {
          case /* CTS */ (Frame.Control, u4"0xC") => 0
          case /* RTS */ (Frame.Control, u4"0xB") => 1
          case /* Data */ (Frame.Data, _) => 2
          case _ => -1 // error
        }
      }
      r match {
        case z"0" => return Choice.Receiver
        case z"1" => return Choice.ReceiverTransmitter
        case z"2" => return Choice.Data
        case _ =>
      }
      return Choice.Error
    }
  }

  object MacHeader {
    def empty: MacHeader = {
      return MacHeader(FrameControl.empty, MSZ.create(2, u8"0"), Receiver.empty)
    }
  }

  @record class MacHeader(
    var frameControl: FrameControl,
    var duration: MSZ[U8],
    var headerAddress: HeaderAddress
  ) extends Runtime.Composite {

    def wellFormed: Z = {

      val wfFrameControl = frameControl.wellFormed
      if (wfFrameControl != 0) {
        return wfFrameControl
      }

      if (duration.size != 2) {
        return ERROR_MacHeader
      }

      (HeaderAddress.choose((frameControl.tpe, frameControl.subType)), headerAddress) match {
        case (HeaderAddress.Choice.Receiver, _: Receiver) =>
        case (HeaderAddress.Choice.ReceiverTransmitter, _: ReceiverTransmitter) =>
        case (HeaderAddress.Choice.Data, _: Data) =>
        case _ => return ERROR_HeaderAddress
      }

      val wfHeaderAddress = headerAddress.wellFormed
      if (wfHeaderAddress != 0) {
        return wfHeaderAddress
      }

      // BEGIN USER CODE: MacHeader.wellFormed
      // ... empty
      // END USER CODE: MacHeader.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      frameControl.decode(input, context)
      Reader.MS.beU8S(input, context, duration, 2)
      HeaderAddress.choose((frameControl.tpe, frameControl.subType)) match {
        case HeaderAddress.Choice.Receiver => headerAddress = Receiver.empty
        case HeaderAddress.Choice.ReceiverTransmitter => headerAddress = ReceiverTransmitter.empty
        case HeaderAddress.Choice.Data => headerAddress = Data.empty
        case _ => context.signalError(ERROR_HeaderAddress)
      }
      headerAddress.decode(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      frameControl.encode(output, context)
      Writer.beU8S(output, context, duration)
      headerAddress.encode(output, context)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_MacHeader)
      }
    }

  }

  object MacFrame {
    def empty: MacFrame = {
      return MacFrame(MacHeader.empty, MSZ[B](), u32"0")
    }
  }

  @record class MacFrame(
    var macHeader: MacHeader,
    var body: MSZ[B],
    var fcs: U32
  ) extends Runtime.Composite {

    def wellFormed: Z = {

      val wfMacHeader = macHeader.wellFormed
      if (wfMacHeader != 0) {
        return wfMacHeader
      }

      val bodySize = sizeOfBody((macHeader.frameControl.tpe, macHeader.frameControl.subType))
      if (body.size != bodySize) {
        return ERROR_MacFrame_body
      }

      // BEGIN USER CODE: MacFrame.wellFormed
      // ... empty
      // END USER CODE: MacFrame.wellFormed

      return 0
    }

    def decode(input: MSZ[B], context: Context): Unit = {
      macHeader.decode(input, context)
      val bodySize = sizeOfBody((macHeader.frameControl.tpe, macHeader.frameControl.subType))
      if (bodySize >= 0) {
        body = MSZ.create(bodySize, F)
        Reader.MS.bleRaw(input, context, body, bodySize)
      } else {
        context.signalError(ERROR_MacFrame_body)
      }
      fcs = Reader.MS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      macHeader.encode(output, context)
      val bodySize = sizeOfBody((macHeader.frameControl.tpe, macHeader.frameControl.subType))
      if (bodySize >= 0) {
        Writer.bleRaw(output, context, body, bodySize)
      } else {
        context.signalError(ERROR_MacFrame_body)
      }
      Writer.beU32(output, context, fcs)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_MacFrame)
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