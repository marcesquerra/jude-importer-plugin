import scala.meta._

object Main extends App {
  val program = """
  import foo._

  object Main extends App { print("Hello!") }

  """
  val tree = program.parse[Source].get
  println(tree.structure)
}
