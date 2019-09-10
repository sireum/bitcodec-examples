// #Sireum

import org.sireum._
import org.sireum.U4._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._

// Partial spec of 802.11: http://inst.eecs.berkeley.edu/~ee122/sp07/80211.pdf

@enum object Frame {
  'Management // 00
  'Control // 1
  'Data // 2
  'Reserved // 3
}

val frameControl: Spec = Concat("FrameControl", ISZ(
  Bits("protocol", 2),
  Enum("tpe", "Frame"),
  Bits("subType", 4),
  Bits("toDS", 1),
  Bits("fromDS", 1),
  Bits("moreFrag", 1),
  Bits("retry", 1),
  Bits("powerMgmt", 1),
  Bits("moreData", 1),
  Bits("wep", 1),
  Bits("order", 1)
))

val seqControl: Spec = Concat("SeqControl", ISZ(
  Bits("fragNumber", 4),
  Bits("seqNumber", 12)
))

val macHeader: Spec = Concat("MacHeader", ISZ(
  frameControl,
  UBytes("duration", 2),
  Union[(Frame.Type, U4)](
    "HeaderAddress",
    ISZ("frameControl.tpe", "frameControl.subType"),
    p => p match {
      case /* CTS */ (Frame.Control, u4"0xC") => 0
      case /* RTS */ (Frame.Control, u4"0xB") => 1
      case /* Data */ (Frame.Data, _) => 2
      case _ => -1 // error
    },
    ISZ(
      UBytes("receiver", 6),
      Concat("ReceiverTransmitter", ISZ(
        UBytes("receiver", 6),
        UBytes("transmitter", 6)
      )),
      Concat("Data", ISZ(
        UBytes("address1", 6),
        UBytes("address2", 6),
        UBytes("address3", 6),
        seqControl,
        UBytes("address4", 6)
      )),
      // ...
    )
  )
))

val macFrame: Spec.Base =
  Concat("MacFrame", ISZ(
    macHeader,
    Raw[(Frame.Type, U4)](
      "body",
      ISZ("macHeader.frameControl.tpe", "macHeader.frameControl.subType"),
      p => p match {
        case /* CTS */ (Frame.Control, u4"0xC") => 0
        case /* RTS */ (Frame.Control, u4"0xB") => 0
        case _ => -1
      }
    ),
    Bits("fcs", 32),
  ))

println(macFrame.toJSON(T))
