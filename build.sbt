import sbtorgpolicies.model._
import sbtorgpolicies.templates.badges._
import sbtorgpolicies.runnable.syntax._

lazy val fsVersion = Option(sys.props("frees.version")).getOrElse("0.2.0")

lazy val docs = (project in file("."))
  .settings(micrositeSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    name := "freestyle-docs",
    description := "Freestyle Docs and Microsite",
    orgScriptTaskListSetting := guard(scalaBinaryVersion.value == "2.12")("tut".asRunnableItem),
    orgAfterCISuccessTaskListSetting := List(
      orgUpdateDocFiles.asRunnableItem,
      depUpdateDependencyIssues.asRunnableItem
    ) ++ guard(scalaBinaryVersion.value == "2.12" &&
      !version.value.endsWith("-SNAPSHOT"))(defaultPublishMicrosite),
    orgBadgeListSetting := List(
      TravisBadge.apply,
      LicenseBadge.apply,
      // Gitter badge (owner field) can be configured with default value if we migrate it to the frees-io organization
      { info =>
        GitterBadge.apply(info.copy(owner = "47deg", repo = "freestyle"))
      },
      GitHubIssuesBadge.apply
    )
  )
  .settings(
    resolvers += Resolver.bintrayRepo("kailuowang", "maven"),
    libraryDependencies ++= Seq(
      %%("freestyle", fsVersion),
      fsDep("tagless"),
      fsDep("effects"),
      fsDep("async"),
      fsDep("async-monix"),
      fsDep("async-fs2"),
      fsDep("config"),
      fsDep("logging"),
      fsDep("cache-redis"),
      fsDep("doobie"),
      fsDep("fetch"),
      fsDep("fs2"),
      fsDep("http-akka"),
      fsDep("http-finch"),
      fsDep("http-http4s"),
      fsDep("http-play"),
      fsDep("monix"),
      fsDep("slick"),
      fsDep("twitter-util"),
      %%("doobie-h2-cats"),
      %%("http4s-dsl"),
      %%("play"),
      %("h2") % "test"
    )
  )
  .settings(
    scalacOptions in Tut ~= (_ filterNot Set("-Ywarn-unused-import", "-Xlint").contains)
  )
  .disablePlugins(CoursierPlugin)
  .enablePlugins(MicrositesPlugin)

def fsDep(suffix: String): ModuleID = %%(s"freestyle-$suffix", fsVersion) changing ()
