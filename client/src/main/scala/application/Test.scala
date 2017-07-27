package application

object Test extends App {

  val a1 = (1, "qwe")
  val a2 = (1, "qwe")

  println(a1.equals(a2))
  println(a1 == a2)

  val i = "d"

  val s = "12345     "

  println(s + i * 10)
}
