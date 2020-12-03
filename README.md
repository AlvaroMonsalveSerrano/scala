# scala

Project with personal tests of the technologies of the Scala context.

For database operations it is necessary to have a MySQL database installed locally.

## Plugins 

### sbt-scalafmt

Definition of the **scalafmt** plugin in the project/plugins.sbt file

``` 
addSbtPlugin ("org.scalameta"% "sbt-scalafmt"% "2.4.2")
``` 

The scalafmt rules are defined in the file **.Scalafmt.conf**

The usage commands are:

+ **sbt scalafmtCheck.-** Verification of the rules in the source files.
+ **sbt scalafmt.-** Formatting of source files.
+ **sbt test:scalafmtCheck.-** Verification of the rules in the test files.
+ **sbt test:scalafmt.-** Formatting the test files.
+ **sbt scalafmtSbtCheck.-** Verification of the rules in the .sbt and project / *. scala files.
+ **sbt scalafmtSbt.-** Formatting the .sbt and project / *. scala files.


### sbt-dependency-graph

Definition of the **dependency-graph** plugin in the project/plugins.sbt file.

``` 
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
``` 

The usage commands are:

+ **sbt dependencyTree.-** List of dependency tree.
+ **sbt dependencyBrowseGraph.-** Graph of the dependency tree in HTML. One page per module.
+ **sbt dependencyBrowseTree.-** List of dependency tree in HTML.
+ **sbt dependencyList.-** List of dependencies.
+ **sbt dependencyLicenseInfo.-** List of dependencies by license.
+ **sbt dependencyStats.-** List of the table with the statistics of each artifact.


### sbt-dependency-updates

Definition of the **dependency-updates** plugin in the project/plugins.sbt file.

``` 
addSbtPlugin("org.jmotor.sbt"   % "sbt-dependency-updates" % "1.2.2")
``` 

The usage commands are:

+ **sbt dependencyUpdates.-** Shows a list of project dependencies and plugins that can be modified.


### sbt-buildinfo

Definition of the **buildinfo** plugin in the project/plugins.sbt file.

```
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
```

The plugin has to be configured as follows:

```
lazy val root = (project in file("."))
  [...]
  .settings(BuildInfoSettings.value)
  [...]
  .enablePlugins(BuildInfoPlugin)
```

In the construction phase of the project is created in the folder ./target/src_managed/main/sbt-buildinfo/BuildInfo.scala


### sbt-explicit-dependencies

Definition of the **explicit-dependencies** plugin in the project/plugins.sbt file.

```
addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.2.16")
```

The usage commands are:

+ **sbt undeclaredCompileDependencies.-** It show undefined library dependencies
+ **sbt undeclaredCompileDependenciesTest.-** It shows library dependencies not defined in the project tests.
+ **sbt unusedCompileDependencies.-** It shows unnecessary project dependencies
+ **sbt unusedCompileDependenciesTest.-** It shows unnecessary project dependencies in project tests.


### sbt-git

Definition of the **git** plugin in the project/plugins.sbt file.

```
addSbtPlugin("com.typesafe.sbt" % "sbt-git"                   % "1.0.0")
```

In the construction phase of the project is created in the folder ./target/src_managed/main/sbt-buildinfo/BuildInfo.scala


### sbt-scoverage

Definition of the **scoverage** plugin in the project/plugins.sbt file.

```
addSbtPlugin("org.scoverage"    % "sbt-scoverage"          % "1.6.1")
```

The usage commands are:

+ **sbt coveratge test.-** Execution of coverage tests.
+ **sbt coverageReport.-** Execution of coverage tests with html reports.


---


# scala

Proyecto con las pruebas personales de las tecnologías del contexto Scala.

Para las operaciones sobre base de datos es necesario tener instalado en local una base d datos MySQL. 

## Plugins 

### sbt-scalafmt


Definición del plugin **scalafmt** en el fichero project/plugins.sbt:

```
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")
```

Las reglas de scalafmt se definen en el fichero **.scalafmt.conf**

Los comandos de uso son:

+ **sbt scalafmtCheck.-** Verificación de las reglas en los ficheros fuentes.
+ **sbt scalafmt.-** Formateo de los ficheros fuentes.
+ **sbt test:scalafmtCheck.-** Verificación de las reglas en los ficheros de test.
+ **sbt test:scalafmt.-** Formateo de los ficheros de test.
+ **sbt scalafmtSbtCheck.-** Verificación de las reglas en los ficheros .sbt y project/*.scala.
+ **sbt scalafmtSbt.-** Formateo de los ficheros .sbt y project/*.scala.

### sbt-dependency-graph

Definición del plugin **dependency-graph** en el fichero project/plugins.sbt:

```
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
```

Los comandos de uso son: 

+ **sbt dependencyTree.-** Listado del árbol de dependencias.
+ **sbt dependencyBrowseGraph.-** Grafo del árbol de dependencias en HTML. Una página por módulo. 
+ **sbt dependencyBrowseTree.-** Listado del árbol de dependencias en HTML.
+ **sbt dependencyList.-** Listado de dependencias.
+ **sbt dependencyLicenseInfo.-** Listado de dependencias por licencia.
+ **sbt dependencyStats.-** Listado de la tabla con las estadísticas de cada artefacto.


### sbt-dependency-updates

Definición del plugin **dependency-graph** en el fichero project/plugins.sbt:

```
addSbtPlugin("org.jmotor.sbt"   % "sbt-dependency-updates" % "1.2.2")
```

Los comandos de uso son: 

+ **sbt dependencyUpdates.-** Muestra una lista de dependencias de proyecto y plugins que pueden ser modificados.

### sbt-buildinfo

Definición del plugin **buildinfo** en el fichero project/plugins.sbt:

```
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
```

El plugin se tiene que configurar como sigue:

```
lazy val root = (project in file("."))
  [...]
  .settings(BuildInfoSettings.value)
  [...]
  .enablePlugins(BuildInfoPlugin)
```

En la fase de construcción del proyecto, se crea en la carpeta ./target/src_managed/main/sbt-buildinfo/BuildInfo.scala

### sbt-explicit-dependencies

Definición del plugin **explicit-dependencies** en el fichero project/plugins.sbt:

```
addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.2.16")
```

Los comandos de uso son: 

+ **sbt undeclaredCompileDependencies.-** Muestra dependencias de librería no definidas
+ **sbt undeclaredCompileDependenciesTest.-** Muestra dependencias de librería no definidas en los test del proyecto.
+ **sbt unusedCompileDependencies.-** Muestra las dependencias innecesarias del proyecto
+ **sbt unusedCompileDependenciesTest.-** Muestra las dependencias innecesarias del proyecto en los test del proyecto.


### sbt-git

Definición del plugin **explicit-dependencies** en el fichero project/plugins.sbt:

```
addSbtPlugin("com.typesafe.sbt" % "sbt-git"                   % "1.0.0")
```

En la fase de construcción del proyecto, se crea en la carpeta ./target/src_managed/main/sbt-buildinfo/BuildInfo.scala


### sbt-scoverage

Definición del plugin **scoverage** en el fichero project/plugins.sbt:

```
addSbtPlugin("org.scoverage"    % "sbt-scoverage"          % "1.6.1")
```

Los comandos de uso son: 

+ **sbt coveratge test.-** Ejecución de los test de cobertura
+ **sbt coverageReport.-** Ejecución de los test de cobertura con informes html.
