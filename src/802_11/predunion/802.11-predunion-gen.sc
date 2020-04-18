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

  val ERROR_CtsFrameControl: Z = 3

  val ERROR_Cts: Z = 4

  val ERROR_RtsFrameControl: Z = 6

  val ERROR_Rts: Z = 7

  val ERROR_DataFrameControl: Z = 9

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

  def decodeFrame(input: ISZ[B], context: Context): Frame.Type = {
    if (context.offset + 2 > input.size) {
      context.signalError(ERROR_Frame)
    }
    if (context.hasError) {
      return Frame.Management
    }
    val r: Frame.Type = Reader.IS.bleU2(input, context) match {
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
      case Frame.Management => Writer.bleU2(output, context, u2"0")
      case Frame.Control => Writer.bleU2(output, context, u2"1")
      case Frame.Data => Writer.bleU2(output, context, u2"2")
      case Frame.Reserved => Writer.bleU2(output, context, u2"3")
    }
  }

  object CtsFrameControl {

    val maxSize: Z = z"16"

    def empty: MCtsFrameControl = {
      return MCtsFrameControl(u2"0", Frame.Management, u4"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0")
    }

    def decode(input: ISZ[B], context: Context): Option[CtsFrameControl] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[CtsFrameControl]() else Some(r.toImmutable)
    }

  }

  @datatype class CtsFrameControl(
    val protocol: U2,
    val tpe: Frame.Type,
    val subType: U4,
    val toDS: U1,
    val fromDS: U1,
    val moreFrag: U1,
    val retry: U1,
    val powerMgmt: U1,
    val moreData: U1,
    val wep: U1,
    val order: U1
  ) {

    @strictpure def toMutable: MCtsFrameControl = MCtsFrameControl(protocol, tpe, subType, toDS, fromDS, moreFrag, retry, powerMgmt, moreData, wep, order)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MCtsFrameControl(
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

    @strictpure def toImmutable: CtsFrameControl = CtsFrameControl(protocol, tpe, subType, toDS, fromDS, moreFrag, retry, powerMgmt, moreData, wep, order)

    def wellFormed: Z = {


      // BEGIN USER CODE: CtsFrameControl.wellFormed

      // END USER CODE: CtsFrameControl.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      protocol = Reader.IS.bleU2(input, context)
      tpe = decodeFrame(input, context)
      subType = Reader.IS.bleU4(input, context)
      toDS = Reader.IS.bleU1(input, context)
      fromDS = Reader.IS.bleU1(input, context)
      moreFrag = Reader.IS.bleU1(input, context)
      retry = Reader.IS.bleU1(input, context)
      powerMgmt = Reader.IS.bleU1(input, context)
      moreData = Reader.IS.bleU1(input, context)
      wep = Reader.IS.bleU1(input, context)
      order = Reader.IS.bleU1(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleU2(output, context, protocol)
      encodeFrame(output, context, tpe)
      Writer.bleU4(output, context, subType)
      Writer.bleU1(output, context, toDS)
      Writer.bleU1(output, context, fromDS)
      Writer.bleU1(output, context, moreFrag)
      Writer.bleU1(output, context, retry)
      Writer.bleU1(output, context, powerMgmt)
      Writer.bleU1(output, context, moreData)
      Writer.bleU1(output, context, wep)
      Writer.bleU1(output, context, order)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_CtsFrameControl)
      }
    }

  }

  object Cts {

    val maxSize: Z = z"112"

    def empty: MCts = {
      return MCts(CtsFrameControl.empty, MSZ.create(2, u8"0"), MSZ.create(6, u8"0"), u32"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Cts] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Cts]() else Some(r.toImmutable)
    }

  }

  @datatype class Cts(
    val ctsFrameControl: CtsFrameControl,
    val duration: ISZ[U8],
    val receiver: ISZ[U8],
    val fcs: U32
  ) extends MacFrame {

    @strictpure def toMutable: MCts = MCts(ctsFrameControl.toMutable, duration.toMS, receiver.toMS, fcs)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MCts(
    var ctsFrameControl: MCtsFrameControl,
    var duration: MSZ[U8],
    var receiver: MSZ[U8],
    var fcs: U32
  ) extends MMacFrame {

    @strictpure def toImmutable: Cts = Cts(ctsFrameControl.toImmutable, duration.toIS, receiver.toIS, fcs)

    def wellFormed: Z = {

      val wfCtsFrameControl = ctsFrameControl.wellFormed
      if (wfCtsFrameControl != 0) {
        return wfCtsFrameControl
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

    def decode(input: ISZ[B], context: Context): Unit = {
      ctsFrameControl.decode(input, context)
      Reader.IS.beU8S(input, context, duration, 2)
      Reader.IS.beU8S(input, context, receiver, 6)
      fcs = Reader.IS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      ctsFrameControl.encode(output, context)
      Writer.beU8S(output, context, duration)
      Writer.beU8S(output, context, receiver)
      Writer.beU32(output, context, fcs)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Cts)
      }
    }

  }

  object RtsFrameControl {

    val maxSize: Z = z"16"

    def empty: MRtsFrameControl = {
      return MRtsFrameControl(u2"0", Frame.Management, u4"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0")
    }

    def decode(input: ISZ[B], context: Context): Option[RtsFrameControl] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[RtsFrameControl]() else Some(r.toImmutable)
    }

  }

  @datatype class RtsFrameControl(
    val protocol: U2,
    val tpe: Frame.Type,
    val subType: U4,
    val toDS: U1,
    val fromDS: U1,
    val moreFrag: U1,
    val retry: U1,
    val powerMgmt: U1,
    val moreData: U1,
    val wep: U1,
    val order: U1
  ) {

    @strictpure def toMutable: MRtsFrameControl = MRtsFrameControl(protocol, tpe, subType, toDS, fromDS, moreFrag, retry, powerMgmt, moreData, wep, order)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MRtsFrameControl(
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

    @strictpure def toImmutable: RtsFrameControl = RtsFrameControl(protocol, tpe, subType, toDS, fromDS, moreFrag, retry, powerMgmt, moreData, wep, order)

    def wellFormed: Z = {


      // BEGIN USER CODE: RtsFrameControl.wellFormed

      // END USER CODE: RtsFrameControl.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      protocol = Reader.IS.bleU2(input, context)
      tpe = decodeFrame(input, context)
      subType = Reader.IS.bleU4(input, context)
      toDS = Reader.IS.bleU1(input, context)
      fromDS = Reader.IS.bleU1(input, context)
      moreFrag = Reader.IS.bleU1(input, context)
      retry = Reader.IS.bleU1(input, context)
      powerMgmt = Reader.IS.bleU1(input, context)
      moreData = Reader.IS.bleU1(input, context)
      wep = Reader.IS.bleU1(input, context)
      order = Reader.IS.bleU1(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleU2(output, context, protocol)
      encodeFrame(output, context, tpe)
      Writer.bleU4(output, context, subType)
      Writer.bleU1(output, context, toDS)
      Writer.bleU1(output, context, fromDS)
      Writer.bleU1(output, context, moreFrag)
      Writer.bleU1(output, context, retry)
      Writer.bleU1(output, context, powerMgmt)
      Writer.bleU1(output, context, moreData)
      Writer.bleU1(output, context, wep)
      Writer.bleU1(output, context, order)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_RtsFrameControl)
      }
    }

  }

  object Rts {

    val maxSize: Z = z"160"

    def empty: MRts = {
      return MRts(RtsFrameControl.empty, MSZ.create(2, u8"0"), MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), u32"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Rts] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Rts]() else Some(r.toImmutable)
    }

  }

  @datatype class Rts(
    val rtsFrameControl: RtsFrameControl,
    val duration: ISZ[U8],
    val receiver: ISZ[U8],
    val transmitter: ISZ[U8],
    val fcs: U32
  ) extends MacFrame {

    @strictpure def toMutable: MRts = MRts(rtsFrameControl.toMutable, duration.toMS, receiver.toMS, transmitter.toMS, fcs)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MRts(
    var rtsFrameControl: MRtsFrameControl,
    var duration: MSZ[U8],
    var receiver: MSZ[U8],
    var transmitter: MSZ[U8],
    var fcs: U32
  ) extends MMacFrame {

    @strictpure def toImmutable: Rts = Rts(rtsFrameControl.toImmutable, duration.toIS, receiver.toIS, transmitter.toIS, fcs)

    def wellFormed: Z = {

      val wfRtsFrameControl = rtsFrameControl.wellFormed
      if (wfRtsFrameControl != 0) {
        return wfRtsFrameControl
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

    def decode(input: ISZ[B], context: Context): Unit = {
      rtsFrameControl.decode(input, context)
      Reader.IS.beU8S(input, context, duration, 2)
      Reader.IS.beU8S(input, context, receiver, 6)
      Reader.IS.beU8S(input, context, transmitter, 6)
      fcs = Reader.IS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      rtsFrameControl.encode(output, context)
      Writer.beU8S(output, context, duration)
      Writer.beU8S(output, context, receiver)
      Writer.beU8S(output, context, transmitter)
      Writer.beU32(output, context, fcs)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_Rts)
      }
    }

  }

  object DataFrameControl {

    val maxSize: Z = z"16"

    def empty: MDataFrameControl = {
      return MDataFrameControl(u2"0", Frame.Management, u4"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0", u1"0")
    }

    def decode(input: ISZ[B], context: Context): Option[DataFrameControl] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[DataFrameControl]() else Some(r.toImmutable)
    }

  }

  @datatype class DataFrameControl(
    val protocol: U2,
    val tpe: Frame.Type,
    val subType: U4,
    val toDS: U1,
    val fromDS: U1,
    val moreFrag: U1,
    val retry: U1,
    val powerMgmt: U1,
    val moreData: U1,
    val wep: U1,
    val order: U1
  ) {

    @strictpure def toMutable: MDataFrameControl = MDataFrameControl(protocol, tpe, subType, toDS, fromDS, moreFrag, retry, powerMgmt, moreData, wep, order)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MDataFrameControl(
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

    @strictpure def toImmutable: DataFrameControl = DataFrameControl(protocol, tpe, subType, toDS, fromDS, moreFrag, retry, powerMgmt, moreData, wep, order)

    def wellFormed: Z = {


      // BEGIN USER CODE: DataFrameControl.wellFormed

      // END USER CODE: DataFrameControl.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      protocol = Reader.IS.bleU2(input, context)
      tpe = decodeFrame(input, context)
      subType = Reader.IS.bleU4(input, context)
      toDS = Reader.IS.bleU1(input, context)
      fromDS = Reader.IS.bleU1(input, context)
      moreFrag = Reader.IS.bleU1(input, context)
      retry = Reader.IS.bleU1(input, context)
      powerMgmt = Reader.IS.bleU1(input, context)
      moreData = Reader.IS.bleU1(input, context)
      wep = Reader.IS.bleU1(input, context)
      order = Reader.IS.bleU1(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleU2(output, context, protocol)
      encodeFrame(output, context, tpe)
      Writer.bleU4(output, context, subType)
      Writer.bleU1(output, context, toDS)
      Writer.bleU1(output, context, fromDS)
      Writer.bleU1(output, context, moreFrag)
      Writer.bleU1(output, context, retry)
      Writer.bleU1(output, context, powerMgmt)
      Writer.bleU1(output, context, moreData)
      Writer.bleU1(output, context, wep)
      Writer.bleU1(output, context, order)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_DataFrameControl)
      }
    }

  }

  object SeqControl {

    val maxSize: Z = z"16"

    def empty: MSeqControl = {
      return MSeqControl(u4"0", u12"0")
    }

    def decode(input: ISZ[B], context: Context): Option[SeqControl] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[SeqControl]() else Some(r.toImmutable)
    }

  }

  @datatype class SeqControl(
    val fragNumber: U4,
    val seqNumber: U12
  ) {

    @strictpure def toMutable: MSeqControl = MSeqControl(fragNumber, seqNumber)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MSeqControl(
    var fragNumber: U4,
    var seqNumber: U12
  ) extends Runtime.Composite {

    @strictpure def toImmutable: SeqControl = SeqControl(fragNumber, seqNumber)

    def wellFormed: Z = {


      // BEGIN USER CODE: SeqControl.wellFormed
      // ... empty
      // END USER CODE: SeqControl.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      fragNumber = Reader.IS.bleU4(input, context)
      seqNumber = Reader.IS.beU12(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      Writer.bleU4(output, context, fragNumber)
      Writer.beU12(output, context, seqNumber)

      if (context.errorCode == Writer.INSUFFICIENT_BUFFER_SIZE) {
        context.updateErrorCode(ERROR_SeqControl)
      }
    }

  }

  object Data {

    val maxSize: Z = z"-1"

    def empty: MData = {
      return MData(DataFrameControl.empty, MSZ.create(2, u8"0"), MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), MSZ.create(6, u8"0"), SeqControl.empty, MSZ.create(6, u8"0"), MSZ[B](), u32"0")
    }

    def decode(input: ISZ[B], context: Context): Option[Data] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[Data]() else Some(r.toImmutable)
    }

  }

  @datatype class Data(
    val dataFrameControl: DataFrameControl,
    val duration: ISZ[U8],
    val address1: ISZ[U8],
    val address2: ISZ[U8],
    val address3: ISZ[U8],
    val seqControl: SeqControl,
    val address4: ISZ[U8],
    val body: ISZ[B],
    val fcs: U32
  ) extends MacFrame {

    @strictpure def toMutable: MData = MData(dataFrameControl.toMutable, duration.toMS, address1.toMS, address2.toMS, address3.toMS, seqControl.toMutable, address4.toMS, body.toMS, fcs)

    def encode(buffSize: Z, context: Context): Option[ISZ[B]] = {
      val buffer = MSZ.create(buffSize, F)
      toMutable.encode(buffer, context)
      return if (context.hasError) None[ISZ[B]]() else Some(buffer.toIS)
    }

    def wellFormed: Z = {
      return toMutable.wellFormed
    }
  }

  @record class MData(
    var dataFrameControl: MDataFrameControl,
    var duration: MSZ[U8],
    var address1: MSZ[U8],
    var address2: MSZ[U8],
    var address3: MSZ[U8],
    var seqControl: MSeqControl,
    var address4: MSZ[U8],
    var body: MSZ[B],
    var fcs: U32
  ) extends MMacFrame {

    @strictpure def toImmutable: Data = Data(dataFrameControl.toImmutable, duration.toIS, address1.toIS, address2.toIS, address3.toIS, seqControl.toImmutable, address4.toIS, body.toIS, fcs)

    def wellFormed: Z = {

      val wfDataFrameControl = dataFrameControl.wellFormed
      if (wfDataFrameControl != 0) {
        return wfDataFrameControl
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

      val bodySize = sizeOfBody((dataFrameControl.tpe, dataFrameControl.subType))
      if (body.size != bodySize) {
        return ERROR_Data_body
      }

      // BEGIN USER CODE: Data.wellFormed
      // ... empty
      // END USER CODE: Data.wellFormed

      return 0
    }

    def decode(input: ISZ[B], context: Context): Unit = {
      dataFrameControl.decode(input, context)
      Reader.IS.beU8S(input, context, duration, 2)
      Reader.IS.beU8S(input, context, address1, 6)
      Reader.IS.beU8S(input, context, address2, 6)
      Reader.IS.beU8S(input, context, address3, 6)
      seqControl.decode(input, context)
      Reader.IS.beU8S(input, context, address4, 6)
      val bodySize = sizeOfBody((dataFrameControl.tpe, dataFrameControl.subType))
      if (bodySize >= 0) {
        body = MSZ.create(bodySize, F)
        Reader.IS.bleRaw(input, context, body, bodySize)
      } else {
        context.signalError(ERROR_Data_body)
      }
      fcs = Reader.IS.beU32(input, context)

      val wf = wellFormed
      if (wf != 0) {
        context.signalError(wf)
      }
    }

    def encode(output: MSZ[B], context: Context): Unit = {
      dataFrameControl.encode(output, context)
      Writer.beU8S(output, context, duration)
      Writer.beU8S(output, context, address1)
      Writer.beU8S(output, context, address2)
      Writer.beU8S(output, context, address3)
      seqControl.encode(output, context)
      Writer.beU8S(output, context, address4)
      val bodySize = sizeOfBody((dataFrameControl.tpe, dataFrameControl.subType))
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

  @datatype trait MacFrame {
    @strictpure def toMutable: MMacFrame
    def encode(buffSize: Z, context: Context): Option[ISZ[B]]
    def wellFormed: Z
  }

  @record trait MMacFrame extends Runtime.Composite {
    @strictpure def toImmutable: MacFrame
  }

  object MacFrame {

    val maxSize: Z = z"-1"

    def empty: MMacFrame = {
      return Cts.empty
    }

    def decode(input: ISZ[B], context: Context): Option[MacFrame] = {
      val r = empty
      r.decode(input, context)
      return if (context.hasError) None[MacFrame]() else Some(r.toImmutable)
    }

    @enum object Choice {
       'Cts
       'Rts
       'Data
       'Error
    }

    def choose(input: ISZ[B], context: Context): Choice.Type = {
      {
        var ctx = context
        var hasError = F
        if (!hasError) {
          ctx.skip(input.size, 2, -1)
          hasError = !(ctx.errorCode == 0)
        }
        if (!hasError) {
          val temp = Reader.IS.bleU2(input, ctx)
          hasError = !(ctx.errorCode == 0 && temp == u2"1")
        }
        if (!hasError) {
          val temp = Reader.IS.bleU4(input, ctx)
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
          val temp = Reader.IS.bleU2(input, ctx)
          hasError = !(ctx.errorCode == 0 && temp == u2"1")
        }
        if (!hasError) {
          val temp = Reader.IS.bleU4(input, ctx)
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
          val temp = Reader.IS.bleU2(input, ctx)
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
val ctsExample = ISZ(
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

val ctsExampleInputContext = Context.create
val macFrame = BitCodec.MacFrame.decode(ctsExample, ctsExampleInputContext).get

println(s"decode(ctsExample) = $macFrame")
println(s"decode(ctsExample).offset = ${ctsExampleInputContext.offset}")
println(s"decode(ctsExample).errorCode = ${ctsExampleInputContext.errorCode}")
println(s"decode(ctsExample).errorOffset = ${ctsExampleInputContext.errorOffset}")
println()

val ctsExampleOutputContext = Context.create
val ctsExampleOutput = macFrame.encode(7981 * 8, ctsExampleOutputContext).get // this can be any size >= 112 for this particular example
val ctsExampleCodec = Writer.resultIS(ctsExampleOutput, ctsExampleOutputContext)

println(s"encode(decode(ctxExample)) = $ctsExampleCodec, encode(decode(ctxExample)).size = ${ctsExampleCodec.size}")
println(s"encode(decode(ctsExample)).offset = ${ctsExampleOutputContext.offset}")
println(s"encode(decode(ctsExample)).errorCode = ${ctsExampleOutputContext.errorCode}")
println(s"encode(decode(ctsExample)).errorOffset = ${ctsExampleOutputContext.errorOffset}")

assert(ctsExample == ctsExampleCodec, s"$ctsExample != $ctsExampleCodec")
// END USER CODE: Test