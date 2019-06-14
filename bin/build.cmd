::#! 2> /dev/null                                             #
@ 2>/dev/null # 2>nul & echo off & goto BOF                   #
if [ -z ${SIREUM_HOME} ]; then                                #
  echo "Please set SIREUM_HOME env var"                       #
  exit -1                                                     #
fi                                                            #
if [ -f "$0.com" ] && [ "$0.com" -nt "$0" ]; then             #
  exec "$0.com" "$@"                                          #
else                                                          #
  rm -fR "$0.com"                                             #
  exec "${SIREUM_HOME}/bin/sireum" slang run -s -n "$0" "$@"  #
fi                                                            #
:BOF
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
%SIREUM_HOME%\bin\sireum.bat slang run -s "%0" %*
exit /B %errorlevel%
::!#
// #Sireum
import org.sireum._

val homeBin = Os.slashDir
val home = homeBin.up
val homeSrc = home / "src"
val sireumHome = Os.path(Os.env("SIREUM_HOME").get)
val sireum: Os.Path = sireumHome / "bin" / (if (Os.isWin) "sireum.bat" else "sireum")

val specSuffix: String = "-spec.sc"
val genSuffix: String = "-gen"
val genBigSuffix: String = s"-be$genSuffix"
val genLittleSuffix: String = s"-le$genSuffix"

val specs: ISZ[Os.Path] =
  for (spec <- Os.Path.walk(homeSrc, F, F, p => ops.StringOps(p.name).endsWith(specSuffix))) yield spec

val specGensMap: HashSMap[Os.Path, ISZ[Os.Path]] = {
  var r = HashSMap.empty[Os.Path, ISZ[Os.Path]]
  for (spec <- specs) {
    val name = ops.StringOps(spec.name).substring(0, spec.name.size - specSuffix.size)
    val gen = spec.up / s"$name$genSuffix"
    if (gen.exists) {
      r = r + spec ~> ISZ(gen)
    } else {
      val big = spec.up / "big" / s"$name$genBigSuffix"
      val little = spec.up / "little" / s"$name$genLittleSuffix"
      if ((big.up / s"${big.name}.sc").exists && (little.up / s"${little.name}.sc").exists) {
        r = r + spec ~> ISZ(big, little)
      } else {
        r = r + spec ~> ISZ(gen)
      }
    }
  }
  r
}

def gen(): Unit = {
  for (specGens <- specGensMap.entries) {
    val (spec, gens) = specGens

    println(s"Generating bitcodec from $spec ...")
    val big = gens(0)
    val pb = sireum.call(ISZ("tools", "bcgen", "--mode", "script", "--name", big.name,
      "--output-dir", big.up.string, spec.string)).console
    println(st"${(pb.cmds, " ")}".render)
    pb.runCheck()

    if (gens.size == 2) {
      val little = gens(1)
      val pl = sireum.call(ISZ("tools", "bcgen", "--little", "--mode", "script", "--name", little.name,
        "--output-dir", little.up.string, spec.string)).console
      println(st"${(pl.cmds, " ")}".render)
      pl.runCheck()
    }
    println()
  }
}

def run(): Unit = {
  gen()

  for (specGens <- specGensMap.entries) {
    for (gen <- specGens._2) {
      val genPath = s"$gen.sc"
      println(s"Running $genPath ...")
      val p = sireum.call(ISZ("slang", "run", "--no-server", genPath)).console
      println(st"${(p.cmds, " ")}".render)
      p.runCheck()
      println()
    }
  }
}

def runNative(): Unit = {
//  gen()

  for (specGens <- specGensMap.entries) {
    for (gen <- specGens._2) {
      val genPath = s"$gen.sc"

      val c = gen.up / "c"
      val out = gen.up / "out"
      val x = out / (if (Os.isWin) s"${gen.name}.exe" else gen.name)

      c.removeAll()
      out.removeAll()
      out.mkdirAll()

      println(s"Compiling $genPath to C ...")
      val pt = sireum.call(ISZ("slang", "transpilers", "c", "--string-size", "2048",
        "--sequence", "MSZ[org.sireum.B]=63848", "--output-dir", c.string, "--name", gen.name, genPath)).console
      println(st"${(pt.cmds, " ")}".render)
      pt.runCheck()
      println()

      println(s"Compiling executable $x ...")
      val px = Os.proc(ISZ("cmake", "-DCMAKE_BUILD_TYPE=Release", s"..${Os.fileSep}c")).at(out).console
      println(st"${(px.cmds, " ")}".render)
      px.runCheck()
      println()

      val pm = Os.proc(ISZ("make")).at(out).console
      println(st"${(pm.cmds, " ")}".render)
      pm.runCheck()
      println()

      println(s"Running $x ...")
      x.call(ISZ()).console.runCheck()
      println()
      println()
    }
  }
}

def usage(): Unit = {
  println("Usage: ( gen | run | run-native )")
}

if (Os.cliArgs.size == 1) {
  Os.cliArgs(0) match {
    case string"gen" => gen()
    case string"run" => run()
    case string"run-native" => runNative()
    case _ => usage()
  }
} else {
  usage()
}

