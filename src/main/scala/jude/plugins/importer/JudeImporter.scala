package jude.plugins.importer

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins._
import nsc.transform._

class JudeImporter(val global: Global) extends Plugin {
  import global._

  val name = "importer"
  val description = "imports the content of the jude package"
  val components = List[PluginComponent](Component)

  private object Component extends PluginComponent with TypingTransformers {
    val global: JudeImporter.this.global.type = JudeImporter.this.global
    // val runsAfter = List("parser")
    // Using the Scala Compiler 2.8.x the runsAfter should be written as below
    val runsAfter = List[String]("parser")
    val phaseName = JudeImporter.this.name
    def newPhase(_prev: Phase) = new JudeImporterPhase(_prev)

    class JudeImporterTransformer(unit: CompilationUnit)
        extends TypingTransformer(unit) {
      override def transform(tree: Tree) = tree match {
        case p: PackageDef =>
          val i = q"""import _root_.jude._"""
          val r = p.copy(stats = i :: p.stats)
          super.transform(r)
        case _ =>
          super.transform(tree)
      }
    }

    def newTransformer(unit: CompilationUnit) =
      new JudeImporterTransformer(unit)

    class JudeImporterPhase(prev: Phase) extends StdPhase(prev) {

      type PublicCompilationUnit = CompilationUnit
      override def name = JudeImporter.this.name

      override def apply(unit: CompilationUnit): Unit =
        unit.body = new JudeImporterTransformer(unit).transform(unit.body)

    }
  }
}
