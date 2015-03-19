package lib

object OperatorConfig {
  implicit val defaultExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
