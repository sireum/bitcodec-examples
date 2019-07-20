// #Sireum

import org.sireum._
import org.sireum.U4._
import org.sireum.bitcodec.Spec
import org.sireum.bitcodec.Spec._
import org.sireum.bitcodec.Spec.bits

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

val macFrame: Spec =
  PredUnion(
    "MacFrame",
    ISZ(
      PredSpec(
        ISZ(skip(2), bits(2, 1), bits(4, 0xC)),
        Concat("Cts", ISZ(
          frameControl,
          Bytes("duration", 2),
          Bytes("receiver", 6),
          Bits("fcs", 32),
        ))
      ),
      PredSpec(
        ISZ(skip(2), bits(2, 1), bits(4, 0xB)),
        Concat("Rts", ISZ(
          frameControl,
          Bytes("duration", 2),
          Bytes("receiver", 6),
          Bytes("transmitter", 6),
          Bits("fcs", 32),
        ))
      ),
      PredSpec(
        ISZ(skip(2), bits(2, 2)),
        Concat("Data", ISZ(
          frameControl,
          Bytes("duration", 2),
          Bytes("address1", 6),
          Bytes("address2", 6),
          Bytes("address3", 6),
          seqControl,
          Bytes("address4", 6),
          Raw[(Frame.Type, U4)](
            "body",
            ISZ("frameControl.tpe", "frameControl.subType"),
            p => p match {
              case /* CTS */ (Frame.Control, u4"0xC") => 0
              case /* RTS */ (Frame.Control, u4"0xB") => 0
              case _ => -1
            }
          ),
          Bits("fcs", 32),
        ))
      ),
      // ...
    )
  )

println(macFrame.toJSON(T))
